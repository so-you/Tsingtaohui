package com.tsingtaohui.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tsingtaohui.common.enums.OrderStatus;
import com.tsingtaohui.common.enums.TradeMode;
import com.tsingtaohui.mapper.InventoryMapper;
import com.tsingtaohui.mapper.OrderItemMapper;
import com.tsingtaohui.mapper.OrderMapper;
import com.tsingtaohui.mapper.ProductMapper;
import com.tsingtaohui.mapper.UserShipMapper;
import com.tsingtaohui.model.dto.CreateOrderDTO;
import com.tsingtaohui.model.dto.OrderItemDTO;
import com.tsingtaohui.model.entity.InventoryEntity;
import com.tsingtaohui.model.entity.OrderEntity;
import com.tsingtaohui.model.entity.OrderItemEntity;
import com.tsingtaohui.model.entity.ProductEntity;
import com.tsingtaohui.model.entity.UserShipEntity;
import com.tsingtaohui.model.vo.OrderEstimateVO;
import com.tsingtaohui.model.vo.OrderVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private ProductMapper productMapper;
    @Mock
    private InventoryMapper inventoryMapper;
    @Mock
    private UserShipMapper userShipMapper;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderItemMapper orderItemMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void estimateShouldChooseAutoTradeWhenInventoryAndDroneRulesPass() {
        ProductEntity product = product(1L, "SKU-001", true, "12.50", "1.000", "0.0020");
        InventoryEntity inventory = inventory("SKU-001", 10, 0);

        when(productMapper.selectList(ArgumentMatchers.<Wrapper<ProductEntity>>any()))
                .thenReturn(List.of(product));
        when(inventoryMapper.selectList(ArgumentMatchers.<Wrapper<InventoryEntity>>any()))
                .thenReturn(List.of(inventory));

        OrderEstimateVO estimate = orderService.estimate(9L, List.of(new OrderItemDTO(1L, 2)));

        assertThat(estimate.getTradeMode()).isEqualTo(TradeMode.AUTO_TRADE.name());
        assertThat(estimate.isCanAutoTrade()).isTrue();
        assertThat(estimate.getTotalPrice()).isEqualTo("25.00");
        assertThat(estimate.getTotalWeightKg()).isEqualTo("2.000");
        assertThat(estimate.getItems()).hasSize(1);
    }

    @Test
    void createOrderShouldInsertSnapshotItemsAndLockInventory() {
        ProductEntity product = product(1L, "SKU-001", true, "12.50", "1.000", "0.0020");
        InventoryEntity inventory = inventory("SKU-001", 10, 1);
        inventory.setId(100L);
        UserShipEntity ship = new UserShipEntity();
        ship.setShipNo("SHIP-01");
        ship.setShipName("Qingdao Star");
        ship.setShipNationality("CN");
        ship.setImo("IMO1234567");
        ship.setMmsi("412345678");

        when(productMapper.selectList(ArgumentMatchers.<Wrapper<ProductEntity>>any()))
                .thenReturn(List.of(product));
        when(inventoryMapper.selectList(ArgumentMatchers.<Wrapper<InventoryEntity>>any()))
                .thenReturn(List.of(inventory));
        when(userShipMapper.selectOne(ArgumentMatchers.<Wrapper<UserShipEntity>>any()))
                .thenReturn(ship);

        CreateOrderDTO dto = new CreateOrderDTO();
        dto.setItems(List.of(new OrderItemDTO(1L, 2)));
        dto.setConsigneeName("Tom");
        dto.setCabinNo("A-100");
        dto.setContactInfo("tom@example.test");

        OrderVO order = orderService.createOrder(9L, dto);

        ArgumentCaptor<OrderEntity> orderCaptor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(orderMapper).insert(orderCaptor.capture());
        verify(orderItemMapper).insert(any(OrderItemEntity.class));
        verify(inventoryMapper, atLeastOnce()).updateById(inventory);

        OrderEntity inserted = orderCaptor.getValue();
        assertThat(inserted.getTradeMode()).isEqualTo(TradeMode.AUTO_TRADE.name());
        assertThat(inserted.getOrderStatus()).isEqualTo(OrderStatus.CONFIRMED.name());
        assertThat(inserted.getShipNo()).isEqualTo("SHIP-01");
        assertThat(inventory.getAvailableQty()).isEqualTo(8);
        assertThat(inventory.getLockedQty()).isEqualTo(3);
        assertThat(order.getItems()).hasSize(1);
    }

    @Test
    void getAdminMatchingOrdersShouldOnlyReturnPendingMatchingOrders() {
        OrderEntity order = new OrderEntity();
        order.setId(1L);
        order.setOrderNo("TH202605280001");
        order.setUserId(9L);
        order.setTotalPrice(new BigDecimal("25.00"));
        order.setTotalWeightKg(new BigDecimal("2.000"));
        order.setTotalVolumeM3(new BigDecimal("0.0040"));
        order.setTradeMode(TradeMode.MATCHING_ORDER.name());
        order.setOrderStatus(OrderStatus.PENDING_CONFIRM.name());
        order.setConsigneeName("Tom");
        order.setCabinNo("A-100");
        order.setShipNo("SHIP-01");
        order.setShipNationality("CN");
        order.setShippingAgentId(0L);
        order.setShippingAgentName("SELF");

        Page<OrderEntity> page = new Page<>(1, 10);
        page.setRecords(List.of(order));
        page.setTotal(1);
        when(orderMapper.selectPage(any(Page.class), ArgumentMatchers.<Wrapper<OrderEntity>>any()))
                .thenReturn(page);

        OrderVO item = orderService.getAdminMatchingOrders(null, null, 1, 10).getItems().get(0);

        assertThat(item.getTradeMode()).isEqualTo(TradeMode.MATCHING_ORDER.name());
        assertThat(item.getOrderStatus()).isEqualTo(OrderStatus.PENDING_CONFIRM.name());
    }

    private ProductEntity product(Long id, String skuCode, boolean droneDeliverable,
                                  String price, String weightKg, String volumeM3) {
        ProductEntity product = new ProductEntity();
        product.setId(id);
        product.setSkuCode(skuCode);
        product.setNameZh("饮用水");
        product.setNameEn("Water");
        product.setPrice(new BigDecimal(price));
        product.setWeightKg(new BigDecimal(weightKg));
        product.setVolumeM3(new BigDecimal(volumeM3));
        product.setDroneDeliverable(droneDeliverable ? 1 : 0);
        product.setStatus("ON_SALE");
        return product;
    }

    private InventoryEntity inventory(String skuCode, int availableQty, int lockedQty) {
        InventoryEntity inventory = new InventoryEntity();
        inventory.setSkuCode(skuCode);
        inventory.setAvailableQty(availableQty);
        inventory.setLockedQty(lockedQty);
        inventory.setOutboundQty(0);
        return inventory;
    }
}

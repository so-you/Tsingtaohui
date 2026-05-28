package com.tsingtaohui.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tsingtaohui.mapper.OrderMapper;
import com.tsingtaohui.model.entity.OrderEntity;
import com.tsingtaohui.service.IReconciliationService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReconciliationServiceImpl implements IReconciliationService {

    private static final String CSV_HEADER = "订单号,创建时间,完成时间,交易模式,订单状态,船号,船名,船籍," +
            "收货人,舱房号,总金额,总重量(kg),总体积(m³),船代\n";

    private static final byte[] UTF8_BOM = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};

    private final OrderMapper orderMapper;

    public ReconciliationServiceImpl(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Override
    public byte[] exportCsv(String startDate, String endDate) {
        LambdaQueryWrapper<OrderEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(OrderEntity::getOrderStatus,
                List.of("COMPLETED", "OUTBOUND", "IN_DELIVERY", "PENDING_RECEIPT", "PENDING_LOADING"));
        if (StringUtils.hasText(startDate)) {
            LocalDateTime start = LocalDate.parse(startDate.trim()).atStartOfDay();
            wrapper.ge(OrderEntity::getCreatedAt, start);
        }
        if (StringUtils.hasText(endDate)) {
            LocalDateTime end = LocalDate.parse(endDate.trim()).atTime(LocalTime.MAX);
            wrapper.le(OrderEntity::getCreatedAt, end);
        }
        wrapper.orderByAsc(OrderEntity::getCreatedAt);

        List<OrderEntity> orders = orderMapper.selectList(wrapper);

        StringBuilder sb = new StringBuilder(CSV_HEADER);
        for (OrderEntity order : orders) {
            sb.append(csvEscape(order.getOrderNo())).append(',');
            sb.append(order.getCreatedAt() != null ? order.getCreatedAt().toString() : "").append(',');
            sb.append(order.getCompletedAt() != null ? order.getCompletedAt().toString() : "").append(',');
            sb.append(csvEscape(order.getTradeMode())).append(',');
            sb.append(csvEscape(order.getOrderStatus())).append(',');
            sb.append(csvEscape(order.getShipNo())).append(',');
            sb.append(csvEscape(order.getShipName())).append(',');
            sb.append(csvEscape(order.getShipNationality())).append(',');
            sb.append(csvEscape(order.getConsigneeName())).append(',');
            sb.append(csvEscape(order.getCabinNo())).append(',');
            sb.append(order.getTotalPrice() != null ? order.getTotalPrice().toPlainString() : "0").append(',');
            sb.append(order.getTotalWeightKg() != null ? order.getTotalWeightKg().toPlainString() : "0").append(',');
            sb.append(order.getTotalVolumeM3() != null ? order.getTotalVolumeM3().toPlainString() : "0").append(',');
            sb.append(csvEscape(order.getShippingAgentName())).append('\n');
        }

        byte[] csvBytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[UTF8_BOM.length + csvBytes.length];
        System.arraycopy(UTF8_BOM, 0, result, 0, UTF8_BOM.length);
        System.arraycopy(csvBytes, 0, result, UTF8_BOM.length, csvBytes.length);
        return result;
    }

    private String csvEscape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}

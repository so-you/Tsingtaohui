package com.tsingtaohui.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tsingtaohui.common.enums.ErrorCode;
import com.tsingtaohui.common.exception.BusinessException;
import com.tsingtaohui.common.model.PageResult;
import com.tsingtaohui.mapper.ShipMapper;
import com.tsingtaohui.mapper.ShippingAgentMapper;
import com.tsingtaohui.model.dto.CreateShipDTO;
import com.tsingtaohui.model.entity.ShipEntity;
import com.tsingtaohui.model.entity.ShippingAgentEntity;
import com.tsingtaohui.model.vo.ShipAdminVO;
import com.tsingtaohui.model.vo.ShippingAgentVO;
import com.tsingtaohui.service.IShipService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShipServiceImpl implements IShipService {

    private final ShipMapper shipMapper;
    private final ShippingAgentMapper shippingAgentMapper;

    public ShipServiceImpl(ShipMapper shipMapper, ShippingAgentMapper shippingAgentMapper) {
        this.shipMapper = shipMapper;
        this.shippingAgentMapper = shippingAgentMapper;
    }

    @Override
    public PageResult<ShipAdminVO> getShips(String keyword, String nationality, int page, int pageSize) {
        LambdaQueryWrapper<ShipEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            String value = keyword.trim();
            wrapper.and(w -> w
                    .like(ShipEntity::getShipNo, value)
                    .or().like(ShipEntity::getShipName, value)
                    .or().like(ShipEntity::getImo, value)
                    .or().like(ShipEntity::getMmsi, value)
            );
        }
        if (StringUtils.hasText(nationality)) {
            wrapper.eq(ShipEntity::getShipNationality, nationality.trim());
        }
        wrapper.orderByDesc(ShipEntity::getCreatedAt);

        int safePage = Math.max(page, 1);
        int safePageSize = Math.min(Math.max(pageSize, 1), 100);
        Page<ShipEntity> pageResult = shipMapper.selectPage(new Page<>(safePage, safePageSize), wrapper);

        List<ShipAdminVO> items = new ArrayList<>();
        for (ShipEntity entity : pageResult.getRecords()) {
            items.add(toShipAdminVO(entity));
        }
        return new PageResult<>(items, safePage, safePageSize, pageResult.getTotal());
    }

    @Override
    public ShipAdminVO createShip(CreateShipDTO dto) {
        ShipEntity entity = new ShipEntity();
        entity.setShipNo(dto.getShipNo());
        entity.setShipName(dto.getShipName());
        entity.setShipNationality(dto.getShipNationality());
        entity.setImo(dto.getImo());
        entity.setMmsi(dto.getMmsi());
        entity.setCurrentBerth(dto.getCurrentBerth());
        entity.setCurrentAnchorage(dto.getCurrentAnchorage());
        entity.setTargetGps(dto.getTargetGps());
        entity.setLocationSource(dto.getLocationSource() != null ? dto.getLocationSource() : "ADMIN");
        shipMapper.insert(entity);
        return toShipAdminVO(entity);
    }

    @Override
    public ShipAdminVO updateShip(Long shipId, CreateShipDTO dto) {
        ShipEntity entity = shipMapper.selectById(shipId);
        if (entity == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(), "Ship not found");
        }
        if (StringUtils.hasText(dto.getShipNo())) {
            entity.setShipNo(dto.getShipNo());
        }
        entity.setShipName(dto.getShipName());
        if (StringUtils.hasText(dto.getShipNationality())) {
            entity.setShipNationality(dto.getShipNationality());
        }
        entity.setImo(dto.getImo());
        entity.setMmsi(dto.getMmsi());
        entity.setCurrentBerth(dto.getCurrentBerth());
        entity.setCurrentAnchorage(dto.getCurrentAnchorage());
        entity.setTargetGps(dto.getTargetGps());
        if (dto.getLocationSource() != null) {
            entity.setLocationSource(dto.getLocationSource());
        }
        shipMapper.updateById(entity);
        return toShipAdminVO(entity);
    }

    @Override
    public PageResult<ShippingAgentVO> getShippingAgents(String keyword, String status, int page, int pageSize) {
        LambdaQueryWrapper<ShippingAgentEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            String value = keyword.trim();
            wrapper.and(w -> w
                    .like(ShippingAgentEntity::getAgentNameZh, value)
                    .or().like(ShippingAgentEntity::getAgentNameEn, value)
                    .or().like(ShippingAgentEntity::getContactName, value)
            );
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(ShippingAgentEntity::getStatus, status.trim());
        }
        wrapper.orderByDesc(ShippingAgentEntity::getCreatedAt);

        int safePage = Math.max(page, 1);
        int safePageSize = Math.min(Math.max(pageSize, 1), 100);
        Page<ShippingAgentEntity> pageResult = shippingAgentMapper.selectPage(
                new Page<>(safePage, safePageSize), wrapper);

        List<ShippingAgentVO> items = new ArrayList<>();
        for (ShippingAgentEntity entity : pageResult.getRecords()) {
            items.add(toShippingAgentVO(entity));
        }
        return new PageResult<>(items, safePage, safePageSize, pageResult.getTotal());
    }

    private ShipAdminVO toShipAdminVO(ShipEntity entity) {
        ShipAdminVO vo = new ShipAdminVO();
        vo.setId(entity.getId());
        vo.setShipNo(entity.getShipNo());
        vo.setShipName(entity.getShipName());
        vo.setShipNationality(entity.getShipNationality());
        vo.setImo(entity.getImo());
        vo.setMmsi(entity.getMmsi());
        vo.setCurrentBerth(entity.getCurrentBerth());
        vo.setCurrentAnchorage(entity.getCurrentAnchorage());
        vo.setTargetGps(entity.getTargetGps());
        vo.setLocationSource(entity.getLocationSource());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private ShippingAgentVO toShippingAgentVO(ShippingAgentEntity entity) {
        ShippingAgentVO vo = new ShippingAgentVO();
        vo.setId(entity.getId());
        vo.setAgentNameZh(entity.getAgentNameZh());
        vo.setAgentNameEn(entity.getAgentNameEn());
        vo.setContactName(entity.getContactName());
        vo.setContactPhone(entity.getContactPhone());
        vo.setStatus(entity.getStatus());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }
}

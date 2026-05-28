package com.tsingtaohui.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tsingtaohui.mapper.UserMapper;
import com.tsingtaohui.mapper.UserProfileMapper;
import com.tsingtaohui.mapper.UserShipMapper;
import com.tsingtaohui.model.dto.UpdateProfileDTO;
import com.tsingtaohui.model.dto.UpdateShipDTO;
import com.tsingtaohui.model.entity.UserEntity;
import com.tsingtaohui.model.entity.UserProfileEntity;
import com.tsingtaohui.model.entity.UserShipEntity;
import com.tsingtaohui.model.vo.ShipVO;
import com.tsingtaohui.model.vo.UserProfileVO;
import com.tsingtaohui.service.IUserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final UserShipMapper userShipMapper;

    public UserServiceImpl(UserMapper userMapper, UserProfileMapper userProfileMapper, UserShipMapper userShipMapper) {
        this.userMapper = userMapper;
        this.userProfileMapper = userProfileMapper;
        this.userShipMapper = userShipMapper;
    }

    @Override
    public UserProfileVO getMyProfile(Long userId) {
        UserEntity user = userMapper.selectById(userId);
        UserProfileEntity profile = userProfileMapper.selectOne(
                new LambdaQueryWrapper<UserProfileEntity>().eq(UserProfileEntity::getUserId, userId)
        );
        List<UserShipEntity> ships = userShipMapper.selectList(
                new LambdaQueryWrapper<UserShipEntity>()
                        .eq(UserShipEntity::getUserId, userId)
                        .orderByDesc(UserShipEntity::getIsDefault)
        );

        UserProfileVO vo = new UserProfileVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setPreferredLanguage(user.getPreferredLanguage());

        if (profile != null) {
            vo.setDisplayName(profile.getDisplayName());
            vo.setContactPhone(profile.getContactPhone());
            vo.setEmail(profile.getEmail());
            vo.setNationality(profile.getNationality());
        }

        List<ShipVO> shipVOs = new ArrayList<>();
        for (UserShipEntity ship : ships) {
            ShipVO svo = new ShipVO();
            svo.setId(ship.getId());
            svo.setShipNo(ship.getShipNo());
            svo.setShipName(ship.getShipName());
            svo.setShipNationality(ship.getShipNationality());
            svo.setImo(ship.getImo());
            svo.setMmsi(ship.getMmsi());
            svo.setIsDefault(ship.getIsDefault() != null && ship.getIsDefault() == 1);
            shipVOs.add(svo);
        }
        vo.setShips(shipVOs);

        return vo;
    }

    @Override
    public UserProfileVO updateProfile(Long userId, UpdateProfileDTO dto) {
        UserProfileEntity profile = userProfileMapper.selectOne(
                new LambdaQueryWrapper<UserProfileEntity>().eq(UserProfileEntity::getUserId, userId)
        );

        if (profile == null) {
            profile = new UserProfileEntity();
            profile.setUserId(userId);
            profile.setDisplayName(dto.getDisplayName());
            profile.setContactPhone(dto.getContactPhone());
            profile.setEmail(dto.getEmail());
            profile.setNationality(dto.getNationality());
            userProfileMapper.insert(profile);
        } else {
            if (dto.getDisplayName() != null) profile.setDisplayName(dto.getDisplayName());
            if (dto.getContactPhone() != null) profile.setContactPhone(dto.getContactPhone());
            if (dto.getEmail() != null) profile.setEmail(dto.getEmail());
            if (dto.getNationality() != null) profile.setNationality(dto.getNationality());
            userProfileMapper.updateById(profile);
        }

        return getMyProfile(userId);
    }

    @Override
    public ShipVO updateShip(Long userId, UpdateShipDTO dto) {
        // Find existing default ship or create new
        UserShipEntity ship = userShipMapper.selectOne(
                new LambdaQueryWrapper<UserShipEntity>()
                        .eq(UserShipEntity::getUserId, userId)
                        .eq(UserShipEntity::getIsDefault, 1)
        );

        if (ship == null) {
            ship = new UserShipEntity();
            ship.setUserId(userId);
            ship.setIsDefault(1);
        }

        ship.setShipNo(dto.getShipNo());
        ship.setShipName(dto.getShipName());
        ship.setShipNationality(dto.getShipNationality());
        ship.setImo(dto.getImo());
        ship.setMmsi(dto.getMmsi());

        if (ship.getId() == null) {
            userShipMapper.insert(ship);
        } else {
            userShipMapper.updateById(ship);
        }

        ShipVO vo = new ShipVO();
        vo.setId(ship.getId());
        vo.setShipNo(ship.getShipNo());
        vo.setShipName(ship.getShipName());
        vo.setShipNationality(ship.getShipNationality());
        vo.setImo(ship.getImo());
        vo.setMmsi(ship.getMmsi());
        vo.setIsDefault(true);
        return vo;
    }
}

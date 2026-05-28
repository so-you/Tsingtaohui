package com.tsingtaohui.service;

import com.tsingtaohui.model.dto.UpdateProfileDTO;
import com.tsingtaohui.model.dto.UpdateShipDTO;
import com.tsingtaohui.model.vo.UserProfileVO;
import com.tsingtaohui.model.vo.ShipVO;

public interface IUserService {

    UserProfileVO getMyProfile(Long userId);

    UserProfileVO updateProfile(Long userId, UpdateProfileDTO dto);

    ShipVO updateShip(Long userId, UpdateShipDTO dto);
}

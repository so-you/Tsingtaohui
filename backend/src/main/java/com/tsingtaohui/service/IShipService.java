package com.tsingtaohui.service;

import com.tsingtaohui.common.model.PageResult;
import com.tsingtaohui.model.dto.CreateShipDTO;
import com.tsingtaohui.model.vo.ShipAdminVO;
import com.tsingtaohui.model.vo.ShippingAgentVO;

public interface IShipService {

    PageResult<ShipAdminVO> getShips(String keyword, String nationality, int page, int pageSize);

    ShipAdminVO createShip(CreateShipDTO dto);

    ShipAdminVO updateShip(Long shipId, CreateShipDTO dto);

    PageResult<ShippingAgentVO> getShippingAgents(String keyword, String status, int page, int pageSize);
}

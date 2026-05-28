package com.tsingtaohui.service;

import com.tsingtaohui.model.dto.LoginDTO;
import com.tsingtaohui.model.dto.RegisterDTO;
import com.tsingtaohui.model.vo.AuthVO;

public interface IAuthService {

    AuthVO register(RegisterDTO dto);

    AuthVO login(LoginDTO dto);
}

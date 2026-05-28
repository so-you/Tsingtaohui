package com.tsingtaohui.model.vo;

public class AuthVO {

    private String token;
    private UserVO user;

    public AuthVO(String token, UserVO user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public UserVO getUser() {
        return user;
    }
}

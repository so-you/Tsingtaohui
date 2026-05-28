package com.tsingtaohui.model.vo;

public class UserVO {

    private Long id;
    private String username;
    private String userType;
    private String preferredLanguage;

    public UserVO(Long id, String username, String userType, String preferredLanguage) {
        this.id = id;
        this.username = username;
        this.userType = userType;
        this.preferredLanguage = preferredLanguage;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getUserType() {
        return userType;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }
}

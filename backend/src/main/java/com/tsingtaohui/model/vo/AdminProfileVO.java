package com.tsingtaohui.model.vo;

public class AdminProfileVO {

    private Long id;
    private String username;
    private String nickname;
    private String role;
    private String preferredLanguage;

    public AdminProfileVO(Long id, String username, String nickname, String role, String preferredLanguage) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.role = role;
        this.preferredLanguage = preferredLanguage;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getNickname() { return nickname; }
    public String getRole() { return role; }
    public String getPreferredLanguage() { return preferredLanguage; }
}

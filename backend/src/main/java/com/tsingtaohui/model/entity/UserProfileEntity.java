package com.tsingtaohui.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("t_user_profile")
public class UserProfileEntity extends BaseEntity {

    private Long userId;
    private String displayName;
    private String contactPhone;
    private String email;
    private String nationality;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
}

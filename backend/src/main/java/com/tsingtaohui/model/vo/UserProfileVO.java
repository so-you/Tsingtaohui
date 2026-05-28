package com.tsingtaohui.model.vo;

import java.util.List;

public class UserProfileVO {

    private Long id;
    private String username;
    private String displayName;
    private String contactPhone;
    private String email;
    private String nationality;
    private String preferredLanguage;
    private List<ShipVO> ships;

    public UserProfileVO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public String getPreferredLanguage() { return preferredLanguage; }
    public void setPreferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; }
    public List<ShipVO> getShips() { return ships; }
    public void setShips(List<ShipVO> ships) { this.ships = ships; }
}

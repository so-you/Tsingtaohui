package com.tsingtaohui.model.dto;

public class UpdateProfileDTO {

    private String displayName;
    private String contactPhone;
    private String email;
    private String nationality;

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
}

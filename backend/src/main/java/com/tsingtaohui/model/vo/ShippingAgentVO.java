package com.tsingtaohui.model.vo;

import java.time.LocalDateTime;

public class ShippingAgentVO {

    private Long id;
    private String agentNameZh;
    private String agentNameEn;
    private String contactName;
    private String contactPhone;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAgentNameZh() { return agentNameZh; }
    public void setAgentNameZh(String agentNameZh) { this.agentNameZh = agentNameZh; }
    public String getAgentNameEn() { return agentNameEn; }
    public void setAgentNameEn(String agentNameEn) { this.agentNameEn = agentNameEn; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

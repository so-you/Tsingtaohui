package com.tsingtaohui.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("t_shipping_agent")
public class ShippingAgentEntity extends BaseEntity {

    private String agentNameZh;
    private String agentNameEn;
    private String contactName;
    private String contactPhone;
    private String status;

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
}

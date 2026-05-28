package com.tsingtaohui.model.vo;

import java.time.LocalDateTime;

public class RuleConfigVO {

    private Long id;
    private String ruleKey;
    private String ruleNameZh;
    private String ruleNameEn;
    private String ruleValue;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRuleKey() { return ruleKey; }
    public void setRuleKey(String ruleKey) { this.ruleKey = ruleKey; }
    public String getRuleNameZh() { return ruleNameZh; }
    public void setRuleNameZh(String ruleNameZh) { this.ruleNameZh = ruleNameZh; }
    public String getRuleNameEn() { return ruleNameEn; }
    public void setRuleNameEn(String ruleNameEn) { this.ruleNameEn = ruleNameEn; }
    public String getRuleValue() { return ruleValue; }
    public void setRuleValue(String ruleValue) { this.ruleValue = ruleValue; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

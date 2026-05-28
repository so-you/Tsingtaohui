package com.tsingtaohui.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("t_rule_config")
public class RuleConfigEntity extends BaseEntity {

    private String ruleKey;
    private String ruleNameZh;
    private String ruleNameEn;
    private String ruleValue;
    private String description;
    private String status;

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
}

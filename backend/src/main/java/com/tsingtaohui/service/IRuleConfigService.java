package com.tsingtaohui.service;

import com.tsingtaohui.common.model.PageResult;
import com.tsingtaohui.model.dto.UpdateRuleDTO;
import com.tsingtaohui.model.vo.RuleConfigVO;

public interface IRuleConfigService {

    PageResult<RuleConfigVO> getRules(String keyword, String status, int page, int pageSize);

    RuleConfigVO updateRule(Long ruleId, UpdateRuleDTO dto);

    String getRuleValue(String ruleKey);
}

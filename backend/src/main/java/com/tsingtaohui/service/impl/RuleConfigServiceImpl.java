package com.tsingtaohui.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tsingtaohui.common.enums.ErrorCode;
import com.tsingtaohui.common.exception.BusinessException;
import com.tsingtaohui.common.model.PageResult;
import com.tsingtaohui.mapper.RuleConfigMapper;
import com.tsingtaohui.model.dto.UpdateRuleDTO;
import com.tsingtaohui.model.entity.RuleConfigEntity;
import com.tsingtaohui.model.vo.RuleConfigVO;
import com.tsingtaohui.service.IRuleConfigService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class RuleConfigServiceImpl implements IRuleConfigService {

    private static final Set<String> RULE_STATUSES = Set.of("ENABLED", "DISABLED");

    private final RuleConfigMapper ruleConfigMapper;

    public RuleConfigServiceImpl(RuleConfigMapper ruleConfigMapper) {
        this.ruleConfigMapper = ruleConfigMapper;
    }

    @Override
    public PageResult<RuleConfigVO> getRules(String keyword, String status, int page, int pageSize) {
        LambdaQueryWrapper<RuleConfigEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            String value = keyword.trim();
            wrapper.and(w -> w
                    .like(RuleConfigEntity::getRuleKey, value)
                    .or().like(RuleConfigEntity::getRuleNameZh, value)
                    .or().like(RuleConfigEntity::getRuleNameEn, value)
            );
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(RuleConfigEntity::getStatus, status.trim());
        }
        wrapper.orderByAsc(RuleConfigEntity::getRuleKey);

        int safePage = Math.max(page, 1);
        int safePageSize = Math.min(Math.max(pageSize, 1), 100);
        Page<RuleConfigEntity> pageResult = ruleConfigMapper.selectPage(
                new Page<>(safePage, safePageSize), wrapper);

        List<RuleConfigVO> items = new ArrayList<>();
        for (RuleConfigEntity entity : pageResult.getRecords()) {
            items.add(toRuleConfigVO(entity));
        }
        return new PageResult<>(items, safePage, safePageSize, pageResult.getTotal());
    }

    @Override
    public RuleConfigVO updateRule(Long ruleId, UpdateRuleDTO dto) {
        RuleConfigEntity entity = ruleConfigMapper.selectById(ruleId);
        if (entity == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(), "Rule not found");
        }
        if (dto.getRuleValue() != null) {
            entity.setRuleValue(dto.getRuleValue());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (StringUtils.hasText(dto.getStatus())) {
            if (!RULE_STATUSES.contains(dto.getStatus())) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(), "Invalid rule status");
            }
            entity.setStatus(dto.getStatus());
        }
        ruleConfigMapper.updateById(entity);
        return toRuleConfigVO(entity);
    }

    @Override
    public String getRuleValue(String ruleKey) {
        RuleConfigEntity entity = ruleConfigMapper.selectOne(
                new LambdaQueryWrapper<RuleConfigEntity>()
                        .eq(RuleConfigEntity::getRuleKey, ruleKey)
                        .eq(RuleConfigEntity::getStatus, "ENABLED")
        );
        return entity != null ? entity.getRuleValue() : null;
    }

    private RuleConfigVO toRuleConfigVO(RuleConfigEntity entity) {
        RuleConfigVO vo = new RuleConfigVO();
        vo.setId(entity.getId());
        vo.setRuleKey(entity.getRuleKey());
        vo.setRuleNameZh(entity.getRuleNameZh());
        vo.setRuleNameEn(entity.getRuleNameEn());
        vo.setRuleValue(entity.getRuleValue());
        vo.setDescription(entity.getDescription());
        vo.setStatus(entity.getStatus());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }
}

package com.tsingtaohui.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tsingtaohui.model.entity.AuditLogEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLogEntity> {
}

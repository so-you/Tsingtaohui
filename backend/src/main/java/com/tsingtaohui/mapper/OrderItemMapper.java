package com.tsingtaohui.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tsingtaohui.model.entity.OrderItemEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItemEntity> {
}

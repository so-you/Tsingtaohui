package com.tsingtaohui.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.tsingtaohui.common.context.UserContextHolder;
import com.tsingtaohui.common.model.UserContext;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyBatisPlusMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createdAt", LocalDateTime::now, LocalDateTime.class);
        this.strictInsertFill(metaObject, "updatedAt", LocalDateTime::now, LocalDateTime.class);
        this.strictInsertFill(metaObject, "createdBy", () -> getCurrentUsername(), String.class);
        this.strictInsertFill(metaObject, "updatedBy", () -> getCurrentUsername(), String.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime::now, LocalDateTime.class);
        this.strictUpdateFill(metaObject, "updatedBy", () -> getCurrentUsername(), String.class);
    }

    private String getCurrentUsername() {
        UserContext ctx = UserContextHolder.get();
        return ctx != null ? ctx.getUsername() : "system";
    }
}

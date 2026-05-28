package com.tsingtaohui.common.context;

import com.tsingtaohui.common.model.UserContext;

public class UserContextHolder {

    private static final ThreadLocal<UserContext> CONTEXT = new ThreadLocal<>();

    public static void set(UserContext context) {
        CONTEXT.set(context);
    }

    public static UserContext get() {
        return CONTEXT.get();
    }

    public static Long getUserId() {
        UserContext ctx = CONTEXT.get();
        return ctx != null ? ctx.getUserId() : null;
    }

    public static void clear() {
        CONTEXT.remove();
    }
}

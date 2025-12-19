package com.yuhang.demo.common.enums;

import lombok.Getter;

@Getter
public enum RoleConstants {
    ADMIN(1L, "ROLE_ADMIN"),
    USER(2L, "ROLE_USER");

    private final Long id;
    private final String roleKey;

    RoleConstants(Long id, String roleKey) {
        this.id = id;
        this.roleKey = roleKey;
    }
}

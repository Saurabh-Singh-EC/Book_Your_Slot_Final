package com.codeWithSrb.bookyourslot.enumeration;

public enum RolePermission {
    ROLE_USER("USER:READ,USER:WRITE,USER:DELETE,USER:CREATE"),
    ROLE_ADMIN("ADMIN:READ,ADMIN:WRITE,ADMIN:DELETE,ADMIN:CREATE");

    private String permission;

    RolePermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
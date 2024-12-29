package com.codeWithSrb.BookYourSlot.Enumeration;

public enum RolePermission {
    USER("USER:READ,USER:WRITE,USER:DELETE,USER:CREATE"),
    ADMIN("ADMIN:READ,ADMIN:WRITE,ADMIN:DELETE,ADMIN:CREATE");

    private String permission;

    RolePermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
package com.codeWithSrb.BookYourSlot.Enumeration;

public enum RolePermission {
    USER("READ"),
    ADMIN("READ, WRITE");

    private String permission;

    RolePermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
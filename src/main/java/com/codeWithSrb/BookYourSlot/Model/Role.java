package com.codeWithSrb.BookYourSlot.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "Role")
public class Role {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique=true)
    private String name;

    private String permission;

    public Role() {
    }

    public Role(int id, String name, String permission) {
        this.id = id;
        this.name = name;
        this.permission = permission;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}

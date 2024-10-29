package com.codeWithSrb.BookYourSlot.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique=true)
    private String name;

    private String permission;

    @OneToMany(mappedBy = "role")
    private List<UserRole> userRole;

    public Role(String name, String permission) {
        this.name = name;
        this.permission = permission;
    }
}

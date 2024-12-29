package com.codeWithSrb.BookYourSlot.Model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "user_role")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "fk_user_id", unique = true)
    private UserInfo userInfo;


    @ManyToOne
    @JoinColumn(name = "fk_role_id")
    private Role role;

    public UserRole(UserInfo userInfo, Role role) {
        this.userInfo = userInfo;
        this.role = role;
    }
}

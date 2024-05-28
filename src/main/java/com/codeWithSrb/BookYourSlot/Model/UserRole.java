package com.codeWithSrb.BookYourSlot.Model;


import jakarta.persistence.*;

@Entity
@Table(name = "user_role")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fkUserId")
    private UserInfo userInfo;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fkRoleId")
    private Role role;

    public UserRole() {
    }

    public UserRole(UserInfo userInfo, Role role) {
        this.userInfo = userInfo;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}

package com.example.bagrot_work.models;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private PlayerAppearance appearance;
    private Integer currentlevel;

    public User() {}

    public User(String id, String firstname, String lastname, String username, String password, Integer currentlevel) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.currentlevel = currentlevel;
        appearance = new PlayerAppearance(0,0);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getCurrentlevel() {
        return currentlevel;
    }

    public void setCurrentlevel(Integer currentlevel) {
        this.currentlevel = currentlevel;
    }
    public void levelUp() {
        this.currentlevel++;
    }

    public PlayerAppearance getAppearance() {
        return appearance;
    }

    public void setAppearance(PlayerAppearance appearance) {
        this.appearance = appearance;
    }


}

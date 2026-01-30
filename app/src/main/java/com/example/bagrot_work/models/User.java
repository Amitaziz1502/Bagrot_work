package com.example.bagrot_work.models;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {
    private String id;
    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private boolean isAdmin;
    private Skins appearance;
    private Integer currentlevel;
    public User(){}

    public User(String id, String firstname, String lastname, String username, String password, Integer currentlevel, boolean isAdmin, Skins appearance) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
        this.currentlevel = currentlevel;
        this.appearance = appearance;
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
        if (this.currentlevel < 5) {
            this.currentlevel++;
        }
    }
    public boolean isAdmin() {
        return isAdmin;
    }
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
    public Skins getAppearance() {return appearance;}
    public void setAppearance(Skins appearance) {
        this.appearance = appearance;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

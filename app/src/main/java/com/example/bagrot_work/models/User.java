package com.example.bagrot_work.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class User implements Serializable {
    private String id;
    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private boolean isAdmin;
    private Abilities appearance;
    private Integer currentlevel;
    private ArrayList<Abilities> collectedAbilities;
    private int walletBalance;
    private boolean mainAdmin;
    private ArrayList<GameLevel.CoinData> collectedCoins;

    public User(){
        collectedCoins = new ArrayList<>();
        collectedAbilities = new ArrayList<>();
    }

    public User(String id, String firstname, String lastname, String username, String password, Integer currentlevel,
                boolean isAdmin, Abilities appearance, boolean mainAdmin, ArrayList<GameLevel.CoinData> collectedCoins, int walletBalance, ArrayList<Abilities> collectedAbilities) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
        this.currentlevel = currentlevel;
        this.appearance = appearance;
        this.mainAdmin = mainAdmin;
        this.collectedCoins = collectedCoins;
        this.collectedAbilities = collectedAbilities;
        this.walletBalance = walletBalance;
    }

    public String getId() {return id;}

    public void setId(String id) {this.id = id;}

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

    public boolean isAdmin() {
        return isAdmin;
    }
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
    public Abilities getAppearance() {return appearance;}
    public void setAppearance(Abilities appearance) {
        this.appearance = appearance;
    }
    public boolean isMainAdmin() {
        return mainAdmin;
    }



    public ArrayList<GameLevel.CoinData> getCollectedCoins() {
        return collectedCoins;
    }

    public void setCollectedCoins(ArrayList<GameLevel.CoinData> collectedCoins) {
        this.collectedCoins = collectedCoins;
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

    @Exclude
    public int getTotalCoins() {
        return collectedCoins.size();
    }

    public int getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(int walletBalance) {
        this.walletBalance = walletBalance;
    }

    public ArrayList<Abilities> getCollectedAbilities() {
        if (this.collectedAbilities == null)
            this.collectedAbilities = new ArrayList<>();
        return collectedAbilities;
    }

    public void setCollectedAbilities(ArrayList<Abilities> collectedAbilities) {
        this.collectedAbilities = collectedAbilities;
    }

    public boolean isInAbilityList(Abilities ability) {
        if (collectedAbilities == null || ability == null) {
            return false;
        }
        return collectedAbilities.contains(ability);
    }
}

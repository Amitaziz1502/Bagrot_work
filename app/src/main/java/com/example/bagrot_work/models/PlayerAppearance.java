package com.example.bagrot_work.models;

public class PlayerAppearance {
    private int skin;
    private  int playercolor;


    public PlayerAppearance() {
        this.skin = 0;
        this.playercolor = 0;
    }
    public PlayerAppearance(int skin, int playercolor) {
        this.skin = skin;
        this.playercolor = playercolor;
    }

    public int getSkin() {
        return skin;
    }

    public void setSkin(int skin) {
        this.skin = skin;
    }

    public int getPlayercolor() {
        return playercolor;
    }

    public void setPlayercolor(int playercolor) {
        this.playercolor = playercolor;
    }
}

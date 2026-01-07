package com.example.bagrot_work.models;

import android.graphics.Rect;

import androidx.annotation.NonNull;

import com.example.bagrot_work.screens.GameView;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;

public class GameLevel {
    public List<RectData> platforms;
    public List<RectData> spikes;
    public List<RectData> checkpoints;
    public List<RectData> coins;
    public List<FloatingTextData> floatingTexts;
    public float worldWidth;
    public long levelTimeMillis;

    public GameLevel() {
        this.platforms = new ArrayList<>();
        this.spikes = new ArrayList<>();
        this.checkpoints = new ArrayList<>();
        this.floatingTexts = new ArrayList<>();
    }

    public static class RectData {
        public int left, top, right, bottom;

        public RectData() {}

        public RectData(int l, int t, int r, int b) {
            this.left = l; this.top = t; this.right = r; this.bottom = b;
        }

        public Rect toRect() {
            return new Rect(left, top, right, bottom);
        }
    }

    public static class FloatingTextData {
        public String text;
        public float x, y;
        public FloatingTextData() {}
        public FloatingTextData(String text, float x, float y) {
            this.text = text;
            this.x = x;
            this.y = y;
        }
    }


    @NonNull
    @Exclude
    public static GameLevel getLevel(int levelNumber, int floorLevel) {
        GameLevel level = new GameLevel();
        switch(levelNumber){
            case 1:
                level.worldWidth = 20000;
                level.levelTimeMillis = 120000;
                //Texts
                level.floatingTexts.add(new FloatingTextData("Welcome to my game! ", 400, floorLevel - 500));
                level.floatingTexts.add(new FloatingTextData("To move u can use the arrows... ", 700, floorLevel - 400));
                level.floatingTexts.add(new FloatingTextData("GO TO THE PLATFORM!!! ", 2400, floorLevel - 400));
                level.floatingTexts.add(new FloatingTextData("U can use the right side of the screen to jump... ", 3600, floorLevel - 500));
                level.floatingTexts.add(new FloatingTextData("Be careful don't touch the spikes!! ", 5500, floorLevel - 500));
                level.floatingTexts.add(new FloatingTextData("THE CAT MIGHT EXPLODE!!! ", 5500, floorLevel - 400));
                level.floatingTexts.add(new FloatingTextData("Go collect the checkpoint  ", 8000, floorLevel - 400));
                level.floatingTexts.add(new FloatingTextData("Now you're place will be saved! ", 9800, floorLevel - 400));
                level.floatingTexts.add(new FloatingTextData("notice the time on your top left screen! ", 10600, floorLevel - 400));
                level.floatingTexts.add(new FloatingTextData("Make sure to finish the level ", 11000, floorLevel - 400));






                //Platforms
                level.platforms.add(new RectData(3600, floorLevel-100, 4000, floorLevel));

                //Spikes
                level.spikes.add(new RectData(6100, floorLevel - 100, 6150, floorLevel));
                level.spikes.add(new RectData(6700, floorLevel - 100, 6750, floorLevel));
                level.spikes.add(new RectData(6700, floorLevel - 100, 6750, floorLevel));
                level.spikes.add(new RectData(7300, floorLevel - 100, 7350, floorLevel));
                level.spikes.add(new RectData(10000, floorLevel - 100, 10100, floorLevel));


                //Checkpoint
                level.checkpoints.add(new RectData(9000, floorLevel - 200, 9100, floorLevel ));



                break;

            case 2:
                level.worldWidth = 22000;
                level.levelTimeMillis = 120000;

                //Platforms
                level.platforms.add(new RectData(1900, 600, 2200, 650));
                level.platforms.add(new RectData(2500, 450, 2800, 500));
                level.platforms.add(new RectData(3100, 300, 4500, 350));
                level.platforms.add(new RectData(4500, 300, 4600, 750));
                level.platforms.add(new RectData(6300, floorLevel - 100, 6400, floorLevel));
                level.platforms.add(new RectData(6700, floorLevel - 300, 6800, floorLevel));
                level.platforms.add(new RectData(7100, floorLevel - 500, 7200, floorLevel));
                level.platforms.add(new RectData(7500, floorLevel - 700, 7600, floorLevel));
                level.platforms.add(new RectData(8000, floorLevel - 750, 8100, floorLevel - 700));
                level.platforms.add(new RectData(8500, floorLevel - 750, 8600, floorLevel - 700));
                level.platforms.add(new RectData(9000, floorLevel - 750, 9100, floorLevel - 700));
                level.platforms.add(new RectData(9500, floorLevel - 650, 9800, floorLevel - 600));
                level.platforms.add(new RectData(10100, floorLevel - 550, 10400, floorLevel - 500));
                level.platforms.add(new RectData(10700, floorLevel - 450, 11000, floorLevel - 400));
                level.platforms.add(new RectData(11300, floorLevel - 350, 11600, floorLevel - 300));
                level.platforms.add(new RectData(11900, floorLevel - 250, 12200, floorLevel - 200));
                level.platforms.add(new RectData(12500, floorLevel - 150, 12800, floorLevel - 100));
                level.platforms.add(new RectData(15000, floorLevel - 150, 15400, floorLevel - 100));
                level.platforms.add(new RectData(15900, floorLevel - 150, 16300, floorLevel - 100));
                level.platforms.add(new RectData(16800, floorLevel - 150, 17200, floorLevel - 100));
                level.platforms.add(new RectData(17700, floorLevel - 150, 18000, floorLevel - 100));

                //Spikes
                level.spikes.add(new RectData(1200, floorLevel - 100, 1300, floorLevel));
                level.spikes.add(new RectData(2200, floorLevel - 100, 3400, floorLevel));
                level.spikes.add(new RectData(3500, 200, 3600, 350));
                level.spikes.add(new RectData(3900, 200, 4000, 350));
                level.spikes.add(new RectData(4400, 200, 4600, 350));
                level.spikes.add(new RectData(6800, floorLevel - 100, 7100, floorLevel));
                level.spikes.add(new RectData(7200, floorLevel - 100, 7500, floorLevel));
                level.spikes.add(new RectData(7600, floorLevel - 100, 12200, floorLevel));
                level.spikes.add(new RectData(13300, floorLevel - 100, 13500, floorLevel));
                level.spikes.add(new RectData(13800, floorLevel - 100, 14000, floorLevel));
                level.spikes.add(new RectData(14300, floorLevel - 100, 14500, floorLevel));
                level.spikes.add(new RectData(15500, floorLevel - 100, 15800, floorLevel));
                level.spikes.add(new RectData(16400, floorLevel - 100, 16700, floorLevel));
                level.spikes.add(new RectData(17300, floorLevel - 100, 17600, floorLevel));

                // Checkpoints
                level.checkpoints.add(new RectData(10200, floorLevel - 700, 10300, floorLevel - 550));

                // Texts
                level.floatingTexts.add(new FloatingTextData("The owner is not here! ", 18600, floorLevel - 400));
                level.floatingTexts.add(new FloatingTextData("Maybe you can find him in the next neighborhood? ", 19500, floorLevel - 300));
                break;

        }

        return level;
    }
}



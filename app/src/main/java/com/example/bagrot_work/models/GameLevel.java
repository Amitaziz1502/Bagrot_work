package com.example.bagrot_work.models;

import android.graphics.Rect;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameLevel {
    public List<RectData> platforms;
    public List<RectData> spikes;
    public List<RectData> checkpoints;
    public List<CoinData> coins;
    public List<FloatingTextData> floatingTexts;
    public List<MovingObstecleData> movingPlatforms;
    public List<MovingObstecleData> movingSpikes;

    public float worldWidth;
    public long levelTimeMillis;

    public GameLevel() {
        this.platforms = new ArrayList<>();
        this.spikes = new ArrayList<>();
        this.checkpoints = new ArrayList<>();
        this.floatingTexts = new ArrayList<>();
        this.movingPlatforms = new ArrayList<>();
        this.movingSpikes = new ArrayList<>();
        this.coins = new ArrayList<>();

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
    public static class CoinData {
        public int left, top, right, bottom;
        public int id;

        public CoinData() {}

        public CoinData(int id, int left, int top, int right, int bottom) {
            this.id = id;
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        public Rect toRect() {
            return new Rect(left, top, right, bottom);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            CoinData coinData = (CoinData) o;
            return id == coinData.id;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }
    }

    public static class MovingObstecleData {
        public int x, y, width, height, rangeX, rangeY;
        public float speed;
        public boolean isTriggerBased;

        public MovingObstecleData() {}

        public MovingObstecleData(int x, int y, int width, int height, int rangeX, float speed,int rangeY,boolean isTriggerBased) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.rangeX = rangeX;
            this.rangeY = rangeY;
            this.speed = speed;
            this.isTriggerBased=isTriggerBased;
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
                level.worldWidth = 14500;
                level.levelTimeMillis = 120000;

                level.coins.add(new CoinData(1,500, floorLevel - 300, 600, floorLevel - 200));
                level.coins.add(new CoinData(2,2400, floorLevel - 300, 2500, floorLevel - 200));
                level.coins.add(new CoinData(3,3000, floorLevel - 300, 3100, floorLevel - 200));
                level.coins.add(new CoinData(4,3700, floorLevel - 350, 3800, floorLevel - 250));
                level.coins.add(new CoinData(5,6100, floorLevel - 350, 6200, floorLevel - 250));
                level.coins.add(new CoinData(6,6700, floorLevel - 350, 6800, floorLevel - 250));
                level.coins.add(new CoinData(7,7300, floorLevel - 350, 7400, floorLevel - 250));
                level.coins.add(new CoinData(8,10000, floorLevel - 300, 10100, floorLevel - 200));
                level.coins.add(new CoinData(9,12000, floorLevel-100 , 12100, floorLevel ));
                level.coins.add(new CoinData(10,13000, floorLevel-100 , 13100, floorLevel ));

                //Texts
                level.floatingTexts.add(new FloatingTextData("Welcome to my game! ", 400, floorLevel - 500));
                level.floatingTexts.add(new FloatingTextData("To move u can use the arrows... ", 700, floorLevel - 400));
                level.floatingTexts.add(new FloatingTextData("GO TO THE PLATFORM!!! ", 2400, floorLevel - 400));
                level.floatingTexts.add(new FloatingTextData("U can use the right side of the screen to jump... ", 3600, floorLevel - 500));
                level.floatingTexts.add(new FloatingTextData("Be careful don't touch the spikes!! ", 5500, floorLevel - 500));
                level.floatingTexts.add(new FloatingTextData("YOUR CAT MIGHT EXPLODE!!! ", 5500, floorLevel - 400));
                level.floatingTexts.add(new FloatingTextData("Go collect the checkpoint  ", 8000, floorLevel - 400));
                level.floatingTexts.add(new FloatingTextData("Now you're place will be saved! ", 9800, floorLevel - 400));
                level.floatingTexts.add(new FloatingTextData("You can check by interacting with the spike... ", 9900, floorLevel - 300));
                level.floatingTexts.add(new FloatingTextData("notice the time on your top left screen! ", 11500, floorLevel - 400));
                level.floatingTexts.add(new FloatingTextData("Make sure to finish the level before its over... ", 12500, floorLevel - 300));
                level.floatingTexts.add(new FloatingTextData("And... ", 13950, floorLevel - 400));
                level.floatingTexts.add(new FloatingTextData("That's it! ", 14000, floorLevel - 300));
                level.floatingTexts.add(new FloatingTextData("GO ENJOY THE GAME!!! ", 14200, floorLevel - 200));

                //Platforms
                level.platforms.add(new RectData(3600, floorLevel-100, 4000, floorLevel));

                //Spikes
                level.spikes.add(new RectData(6100, floorLevel - 100, 6150, floorLevel));
                level.spikes.add(new RectData(6700, floorLevel - 100, 6750, floorLevel));
                level.spikes.add(new RectData(7300, floorLevel - 100, 7350, floorLevel));
                level.spikes.add(new RectData(10000, floorLevel - 100, 10100, floorLevel));


                //Checkpoint
                level.checkpoints.add(new RectData(9000, floorLevel - 200, 9100, floorLevel ));

                break;

            case 2:
                level.worldWidth = 20000;
                level.levelTimeMillis = 120000;

                level.coins.add(new CoinData(11,2000, floorLevel-300, 2100, floorLevel-200));
                level.coins.add(new CoinData(12,2600, floorLevel-450, 2700, floorLevel-350));
                level.coins.add(new CoinData(13,3900, floorLevel-700, 4000, floorLevel-600));
                level.coins.add(new CoinData(14,4900, floorLevel-400, 5000, floorLevel-300));
                level.coins.add(new CoinData(15,7100, floorLevel-700, 7200, floorLevel-600));
                level.coins.add(new CoinData(16,8500, floorLevel-950, 8600, floorLevel-850));
                level.coins.add(new CoinData(17,13850, floorLevel-300, 13950, floorLevel-200));
                level.coins.add(new CoinData(18,15600, floorLevel-500, 15700, floorLevel-400));
                level.coins.add(new CoinData(19,16500, floorLevel-500, 16600, floorLevel-400));
                level.coins.add(new CoinData(20,17400, floorLevel-500, 17500, floorLevel-400));



                //Platforms
                level.platforms.add(new RectData(1900, 600, 2200, 650));
                level.platforms.add(new RectData(2500, 450, 2800, 500));
                level.platforms.add(new RectData(3100, 300, 4500, 350));
                level.platforms.add(new RectData(4500, 300, 4600, 750));//
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
            case 3:
                level.worldWidth = 20000;
                level.levelTimeMillis = 120000;

                //checkpoints
                level.checkpoints.add(new RectData(9800, floorLevel - 100, 9900, floorLevel ));
                level.checkpoints.add(new RectData(14500, floorLevel - 1500, 14600, floorLevel - 1400 ));

                level.coins.add(new CoinData(21,1000, floorLevel-400, 1100, floorLevel-300));
                level.coins.add(new CoinData(22,2000, floorLevel-500, 2100, floorLevel-400));
                level.coins.add(new CoinData(23,3900, floorLevel-700, 4000, floorLevel-600));
                level.coins.add(new CoinData(24,5000, floorLevel-600, 5100, floorLevel-500));
                level.coins.add(new CoinData(25,6200, floorLevel-500, 6300, floorLevel-400));
                level.coins.add(new CoinData(26,9150, floorLevel-600, 9250, floorLevel-500));
                level.coins.add(new CoinData(27,13500, floorLevel-600, 13600, floorLevel-500));
                level.coins.add(new CoinData(28,13500, floorLevel-1100, 13600, floorLevel-1000));
                level.coins.add(new CoinData(29,16850, floorLevel-1200, 16950, floorLevel-1100));
                level.coins.add(new CoinData(30,18400, floorLevel-1000, 18500, floorLevel-900));



                //Spikes
                level.spikes.add(new RectData(500, floorLevel - 100, 9000, floorLevel));
                level.spikes.add(new RectData(3000, floorLevel - 500, 3200, floorLevel - 400));
                level.spikes.add(new RectData(3900, floorLevel - 500, 4000, floorLevel - 400));
                level.spikes.add(new RectData(5000, floorLevel - 400, 5100, floorLevel - 300));
                level.spikes.add(new RectData(5600, floorLevel - 400, 5700, floorLevel - 300));
                level.spikes.add(new RectData(6200, floorLevel - 400, 6300, floorLevel - 300));
                level.spikes.add(new RectData(10600, floorLevel - 100, 15600, floorLevel));
                level.spikes.add(new RectData(12600, floorLevel - 500, 12700, floorLevel-400));
                //floor1
                level.spikes.add(new RectData(13200, floorLevel - 500, 13300, floorLevel-400));
                level.spikes.add(new RectData(13800, floorLevel - 500, 13800, floorLevel-400));
                level.spikes.add(new RectData(13800, floorLevel - 500, 13900, floorLevel-400));
                //floor2
                level.spikes.add(new RectData(13100, floorLevel - 1000, 13200, floorLevel-900));
                level.spikes.add(new RectData(13900, floorLevel - 1000, 14000, floorLevel-900));
                level.spikes.add(new RectData(15600, floorLevel - 100, 20000, floorLevel));


                //Moving platforms
                level.movingPlatforms.add(new MovingObstecleData(400, floorLevel - 200, 500, 50, 500,6f, 0,false));
                level.movingPlatforms.add(new MovingObstecleData(1600, floorLevel - 300, 300, 50, 700, 6f,0,false));
                level.movingPlatforms.add(new MovingObstecleData(4100, floorLevel - 300, 2200, 50, 700, 6f,0,false));
                level.movingPlatforms.add(new MovingObstecleData(12000, floorLevel - 400, 2500, 50, 500, 6f,0,false));
                level.movingPlatforms.add(new MovingObstecleData(12300, floorLevel - 900, 2000, 50, 700, 8f,0,false));
                level.movingPlatforms.add(new MovingObstecleData(12300, floorLevel - 1400, 1000, 50, 700, 10f,0,false));


                //Platforms
                level.platforms.add(new RectData(2800, floorLevel - 400, 4000, floorLevel - 350));
                level.platforms.add(new RectData(6900, floorLevel - 500, 7300, floorLevel - 450));
                level.platforms.add(new RectData(7600, floorLevel - 400, 8000, floorLevel - 350));
                level.platforms.add(new RectData(8300, floorLevel - 300, 8700, floorLevel - 250));
                level.platforms.add(new RectData(9000, floorLevel - 200, 9400, floorLevel - 150));
                level.platforms.add(new RectData(10600, floorLevel - 200, 11000, floorLevel - 150));
                level.platforms.add(new RectData(11300, floorLevel - 300, 11700, floorLevel - 250));
                level.platforms.add(new RectData(14900, floorLevel - 650, 15300, floorLevel - 600));
                level.platforms.add(new RectData(11800, floorLevel - 1150, 12100, floorLevel - 1100));
                level.platforms.add(new RectData(15500, floorLevel - 1400, 15600, floorLevel));
                level.platforms.add(new RectData(14000, floorLevel - 1400, 15600, floorLevel-1350));
                level.platforms.add(new RectData(16100, floorLevel - 1200, 16300, floorLevel-1150));
                level.platforms.add(new RectData(16800, floorLevel - 1100, 17000, floorLevel-1050));//
                level.platforms.add(new RectData(17500, floorLevel - 1000, 17700, floorLevel-950));
                level.platforms.add(new RectData(18200, floorLevel - 900, 18700, floorLevel-850));
                level.platforms.add(new RectData(19200, floorLevel - 800, 23000, floorLevel-750));


                //Floating text
                level.floatingTexts.add(new FloatingTextData("You are doing great! ", 9800, floorLevel -500));
                level.floatingTexts.add(new FloatingTextData("Don't get all tired now... ", 10000, floorLevel - 400));
                level.floatingTexts.add(new FloatingTextData("Holy shit... ", 15000, floorLevel - 1500));
                level.floatingTexts.add(new FloatingTextData("God job!", 15100, floorLevel - 1450));
                level.floatingTexts.add(new FloatingTextData("Amazing!", 19600, floorLevel - 1000));
                level.floatingTexts.add(new FloatingTextData("He is not here tho...", 19700, floorLevel - 900));
                break;

            case 4:
                level.worldWidth = 19500;
                level.levelTimeMillis = 120000;

                //platforms
                level.platforms.add(new RectData(500, floorLevel - 100, 600, floorLevel));
                level.platforms.add(new RectData(900, floorLevel - 300, 1000, floorLevel));
                level.platforms.add(new RectData(1300, floorLevel - 500, 1400, floorLevel));
                level.platforms.add(new RectData(1700, floorLevel - 700, 4500, floorLevel-650));
                level.platforms.add(new RectData(9200, floorLevel - 800, 9400, floorLevel-750));
                level.platforms.add(new RectData(9900, floorLevel - 600, 10100, floorLevel-550));
                level.platforms.add(new RectData(10600, floorLevel - 400, 10800, floorLevel-350));
                level.platforms.add(new RectData(11300, floorLevel - 200, 11500, floorLevel-150));
                level.platforms.add(new RectData(12400, floorLevel - 200, 12500, floorLevel));
                level.platforms.add(new RectData(12900, floorLevel - 400, 13000, floorLevel));
                level.platforms.add(new RectData(13400, floorLevel - 600, 13500, floorLevel));
                level.platforms.add(new RectData(13900, floorLevel - 600, 14300, floorLevel-550));
                level.platforms.add(new RectData(14700, floorLevel - 700, 15100, floorLevel-650));
                level.platforms.add(new RectData(15500, floorLevel - 600, 15900, floorLevel-550));
                level.platforms.add(new RectData(16200, floorLevel - 850, 16500, floorLevel-800));
                level.platforms.add(new RectData(14700, floorLevel - 1150, 16000, floorLevel-1100));
                level.platforms.add(new RectData(14000, floorLevel - 1350, 14400, floorLevel-1300));
                level.platforms.add(new RectData(14700, floorLevel - 1600, 16000, floorLevel-1550));
                level.platforms.add(new RectData(16400, floorLevel - 1500, 16600, floorLevel-1450));
                level.platforms.add(new RectData(17000, floorLevel - 1600, 17200, floorLevel-1550));
                level.platforms.add(new RectData(17600, floorLevel - 1600, 17800, floorLevel-1550));
                level.platforms.add(new RectData(18200, floorLevel - 1600, 21000, floorLevel-1550));

                //coins
                level.coins.add(new CoinData(31,900, floorLevel-500, 1000, floorLevel-400));
                level.coins.add(new CoinData(32,2200, floorLevel-900, 2300, floorLevel-800));
                level.coins.add(new CoinData(33,3400, floorLevel-900, 3500, floorLevel-800));
                level.coins.add(new CoinData(34,6500, floorLevel-1300, 6600, floorLevel-1200));
                level.coins.add(new CoinData(35,9950, floorLevel-700, 10050, floorLevel-600));
                level.coins.add(new CoinData(36,12900, floorLevel-700, 13000, floorLevel-600));
                level.coins.add(new CoinData(37,14050, floorLevel-900, 14150, floorLevel-800));
                level.coins.add(new CoinData(38,15700, floorLevel-1400, 15800, floorLevel-1300));
                level.coins.add(new CoinData(39,15000, floorLevel-1400, 15100, floorLevel-1300));
                level.coins.add(new CoinData(40,17650, floorLevel-1700, 17750, floorLevel-1600));


                //spikes
                level.spikes.add(new RectData(600, floorLevel - 100, 11500, floorLevel));
                level.spikes.add(new RectData(6500, floorLevel-1100, 6600, floorLevel-1000));
                level.spikes.add(new RectData(7100, floorLevel-1100, 7200, floorLevel-1000));
                level.spikes.add(new RectData(7800, floorLevel-1100, 7900, floorLevel-1000));
                level.spikes.add(new RectData(12500, floorLevel - 100, 16500, floorLevel));
                level.spikes.add(new RectData(15100, floorLevel - 1700, 15200, floorLevel - 1600));
                level.spikes.add(new RectData(15500, floorLevel - 1700, 15600, floorLevel - 1600));


                //movingSpikes
                level.movingSpikes.add(new MovingObstecleData(1700, floorLevel - 800, 200, 200, 1300, 8f,0,false));
                level.movingSpikes.add(new MovingObstecleData(1700, floorLevel - 800, 200, 200, 1300, 12f,0,false));
                level.movingSpikes.add(new MovingObstecleData(3200, floorLevel - 800, 200, 200, 1300 ,15f,0,false));
                level.movingSpikes.add(new MovingObstecleData(13900, floorLevel - 675, 100, 100, 400, 10f,0,false));
                level.movingSpikes.add(new MovingObstecleData(14700, floorLevel - 775, 100, 100, 400, 10f,0,false));
                level.movingSpikes.add(new MovingObstecleData(15500, floorLevel - 675, 100, 100, 400, 10f,0,false));
                level.movingSpikes.add(new MovingObstecleData(14700, floorLevel - 1250, 100, 100, 1300, 15f,0,false));


                //movingPlatforms
                level.movingPlatforms.add(new MovingObstecleData(4800, floorLevel - 900, 500, 50, 500, 6f,0,false));
                level.movingPlatforms.add(new MovingObstecleData(6000, floorLevel - 1000, 2300, 50, 500, 8f,0,false));


                //checkpoints
                level.checkpoints.add(new RectData(11900, floorLevel - 100, 12000, floorLevel ));

                //Floating text
                level.floatingTexts.add(new FloatingTextData("Those moving spikes were cool right\uD83D\uDE38? ", 12100, floorLevel -400));
                level.floatingTexts.add(new FloatingTextData("The next level is insane! ", 19000, floorLevel - 1800));
                break;

            case 5:
                level.worldWidth = 4500;
                level.levelTimeMillis = 200000;

                //platforms
                level.platforms.add(new RectData(700, floorLevel - 9000, 750, floorLevel-100));
                level.platforms.add(new RectData(2800, floorLevel - 8000, 2850, floorLevel+100));
                level.platforms.add(new RectData(1000, floorLevel , 1200, floorLevel+50));
                level.platforms.add(new RectData(1400, floorLevel - 100, 1600, floorLevel-50));
                level.platforms.add(new RectData(1800, floorLevel - 200, 2000, floorLevel-150));
                level.platforms.add(new RectData(2200, floorLevel - 300, 2400, floorLevel-250));
                level.platforms.add(new RectData(2000, floorLevel - 800, 2400, floorLevel-750));
                level.platforms.add(new RectData(2200, floorLevel - 1800, 2800, floorLevel-1750));
                level.platforms.add(new RectData(2200, floorLevel - 2400, 2500, floorLevel-2350));
                level.platforms.add(new RectData(1700, floorLevel - 2550, 2000, floorLevel-2500));
                level.platforms.add(new RectData(1900, floorLevel - 6500, 2800, floorLevel-6450));
                level.platforms.add(new RectData(2400, floorLevel - 7000, 2500, floorLevel-6950));
                level.platforms.add(new RectData(2000, floorLevel - 7200, 2100, floorLevel-7150));
                level.platforms.add(new RectData(1500, floorLevel - 7050, 1600, floorLevel-7000));
                level.platforms.add(new RectData(1100, floorLevel - 7200, 1200, floorLevel-7150));
                level.platforms.add(new RectData(1300, floorLevel - 7650, 2800, floorLevel-7600));
                level.platforms.add(new RectData(2600, floorLevel - 7800, 2800, floorLevel-7750));
                level.platforms.add(new RectData(2800, floorLevel - 8000, 5500, floorLevel-7950));


                //coins
                level.coins.add(new CoinData(41,2150, floorLevel-1050, 2250, floorLevel-950));
                level.coins.add(new CoinData(42,1000, floorLevel-1200, 1100, floorLevel-1100));
                level.coins.add(new CoinData(43,2300, floorLevel-2000, 2400, floorLevel-1900));
                level.coins.add(new CoinData(44,1500, floorLevel-3300, 1600, floorLevel-3200));
                level.coins.add(new CoinData(45,900, floorLevel-4000, 1000, floorLevel-3900));
                level.coins.add(new CoinData(46,1600, floorLevel-5200, 1700, floorLevel-5100));
                level.coins.add(new CoinData(47,900, floorLevel-6300, 1000, floorLevel-6200));
                level.coins.add(new CoinData(48,2100, floorLevel-6800, 2200, floorLevel-6700));
                level.coins.add(new CoinData(49,1500, floorLevel-7200, 1600, floorLevel-6700));
                level.coins.add(new CoinData(50,2050, floorLevel-7900, 2150, floorLevel-7800));



                //spikes
                level.spikes.add(new RectData(2150, floorLevel - 875, 2225, floorLevel - 800));
                level.spikes.add(new RectData(900, floorLevel - 3200, 1200, floorLevel - 3100));
                level.spikes.add(new RectData(1300, floorLevel - 3900, 1600, floorLevel - 3800));
                level.spikes.add(new RectData(900, floorLevel - 4300, 1100, floorLevel - 4200));
                level.spikes.add(new RectData(1600, floorLevel - 4300, 1700, floorLevel - 4200));
                level.spikes.add(new RectData(1200, floorLevel - 4700, 1500, floorLevel - 4600));//
                level.spikes.add(new RectData(1000, floorLevel - 5100, 1100, floorLevel - 5000));
                level.spikes.add(new RectData(1500, floorLevel - 5100, 1600, floorLevel - 5000));
                level.spikes.add(new RectData(900, floorLevel - 5600, 1500, floorLevel - 5500));
                level.spikes.add(new RectData(1100, floorLevel - 6200, 1700, floorLevel - 6100));
                level.spikes.add(new RectData(2000, floorLevel - 6575, 2025, floorLevel - 6550));
                level.spikes.add(new RectData(2300, floorLevel - 6575, 2325, floorLevel - 6550));


                //Floating text
                level.floatingTexts.add(new FloatingTextData("hurry up! ", 400, floorLevel -150));
                level.floatingTexts.add(new FloatingTextData("good job! ", 3000, floorLevel -8400));
                level.floatingTexts.add(new FloatingTextData("maybe you will find him in the next world? ", 3400, floorLevel -8300));



                //Moving spikes
                level.movingSpikes.add(new MovingObstecleData(-500, floorLevel-300 , 500, 500, 3000, 1f,0,false));
                level.movingSpikes.add(new MovingObstecleData(850, floorLevel+400 , 1950, 1950, 0, 3f,10000,false));
                level.movingSpikes.add(new MovingObstecleData(2200, floorLevel - 1900 , 150, 150, 600, 14f,0,false));
                level.movingSpikes.add(new MovingObstecleData(2100, floorLevel - 2450 , 50, 50, 300, 8f,0,false));
                level.movingSpikes.add(new MovingObstecleData(1700, floorLevel - 2600 , 50, 50, 300, 8f,0,false));
                level.movingSpikes.add(new MovingObstecleData(1300, floorLevel - 7750 , 100, 100, 1500, 20f,0,false));


                //Moving platform
                level.movingPlatforms.add(new MovingObstecleData(2600, floorLevel - 350, 200, 50, 0, 6f,300,false));
                level.movingPlatforms.add(new MovingObstecleData(1400, floorLevel - 900, 200, 50, 300, 6f,0,false));
                level.movingPlatforms.add(new MovingObstecleData(950, floorLevel - 900, 200, 50, 0, 6f,300,false));
                level.movingPlatforms.add(new MovingObstecleData(1200, floorLevel - 1200, 200, 50, 200, 4f,300,false));
                level.movingPlatforms.add(new MovingObstecleData(1700, floorLevel - 1350, 200, 50, 300, 4f,300,false));
                level.movingPlatforms.add(new MovingObstecleData(2600, floorLevel - 1950, 200, 50, 0, 6f,300,false));
                level.movingPlatforms.add(new MovingObstecleData(900, floorLevel - 2550, 800, 50, 0, 8f,4000,true));
                level.movingPlatforms.add(new MovingObstecleData(2700, floorLevel - 6600, 100, 50, 0, 8f,400,true));
                level.movingPlatforms.add(new MovingObstecleData(900, floorLevel - 7300, 200, 50, 0, 8f,500,true));


                break;
        }

        return level;
    }
}



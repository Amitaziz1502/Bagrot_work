package com.example.bagrot_work.screens;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import com.example.bagrot_work.R;

import java.util.ArrayList;
import java.util.List;


public class GameView extends SurfaceView implements Runnable {

    private Thread gameThread;
    private boolean isPlaying;
    private SurfaceHolder surfaceHolder;
    private Paint paint;

    // Player properties
    private float playerX = 200;
    private float playerY = 500;
    private float playerSize = 150;
    private float velocityY = 0;
    private float gravity = 2f;
    private boolean isMoving = false;
    private boolean isJumping = false;
    private float worldWidth = 22000;

    // Movement
    private float scrollSpeed = 5;
    private float worldOffsetX = 0;
    private float worldOffsetY = 0;
    private float moveSpeed = 15f;

    // Movement controls
    private boolean movingLeft = false;
    private boolean movingRight = false;

    // Cached bitmaps
    private Bitmap backgroundImage;
    private Bitmap scaledBackground;
    private Bitmap characterImage;
    private Bitmap spikeimage;
    private int floor_color;
    private int main_color;

    //Spikes settings
    private List<Rect> spikes = new ArrayList<>();
    private BitmapShader spikeShader;

    // If player reacts with the spike
    private List<Particle> particles = new ArrayList<>();
    private boolean isDead = false;

    //WorldOffset settings
    private float backgroundScroll;

    //Platform settings
    private List<Rect> platforms = new ArrayList<>();

    //Player Collision with obstacles
    private Rect playerRect = new Rect();
    private Rect spriteSrcRect = new Rect();
    private Rect spriteDstRect = new Rect();

    //cat running animation
    private Bitmap spriteSheet;
    private int frameCount = 5;
    private int currentFrame = 0;
    private long lastFrameTime = 0;
    private int frameDuration = 100; //animation speed


    //cat idle animation
    private Bitmap idleSpriteSheet;
    private int idleFrameCount = 2; //
    private int idleCurrentFrame = 0;
    private long lastIdleFrameTime = 0;

    //cat jump animation
    private Bitmap jumpSpriteSheet;
    private int jumpFrameCount = 6;
    private int jumpCurrentFrame = 0;
    private long lastJumpFrameTime = 0;


    // FPS control
    private final int FPS = 60;
    private final int FRAME_TIME = 1000 / FPS;

    //Clock settings
    private long gameTimeMillis = 120000;
    private long timerStartMillis;
    private long timeLeftMillis;
    private String timeText = "02:00:00";
    private boolean gameStarted = false;
    private Paint textPaint;
    private long lastTimeUpdate = 0;
    private int textFinalTime ;



    // Checkpoint properties
    private float savedCheckpointX = 200;
    private float savedCheckpointY = 500;
    private List<Rect> checkpoints = new ArrayList<>();
    private Bitmap checkpointImage;
    private Bitmap checkpointImageBefore;
    private Bitmap checkpointImageAfter;
    private List<Boolean> checkpointActivated = new ArrayList<>();
    private float flagY;

    //Text properties
    private List<FloatingText> floatingTexts = new ArrayList<>();
    private Paint floatingTextPaint;

    //Ending scene
    private boolean isLevelEnding = false;
    private int blackAlpha = 0;


    //checking what level your in
    private int currentLevel = 1;
    public void setLevel(int level) {
        this.currentLevel = level;
        if (getWidth() > 0) {
            createPlatforms();
        }
    }


    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        //Stuff
        surfaceHolder = getHolder();
        paint = new Paint();
        paint.setAntiAlias(false);// Fps push
        paint.setFilterBitmap(false); // Make the pictures smoother
        paint.setDither(false); // Saving processing time
        floor_color = Color.parseColor("#0F2A55");
        main_color =Color.parseColor("#353C6F");// Setting the color one time

        //Clock
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTypeface(Typeface.MONOSPACE);
        textPaint.setTextSize(70);
        textPaint.setFakeBoldText(true);
        textPaint.setAntiAlias(true);
        textFinalTime = Color.parseColor("#FF3131");
        textPaint.setShadowLayer(5, 2, 2, Color.BLACK);
        timeLeftMillis = gameTimeMillis;

        //Text
        floatingTextPaint = new Paint();
        floatingTextPaint.setColor(Color.WHITE);
        floatingTextPaint.setTextSize(60);
        floatingTextPaint.setFakeBoldText(true);
        floatingTextPaint.setAntiAlias(true);
        floatingTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        loadBitmaps();
        createPlatforms();
        scaleBackgroundIfNeeded();
    }

    private void loadBitmaps() {
        if (backgroundImage == null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565; // Saves 50% of the power
            options.inScaled = true;
            backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.background_img, options);

        }

        if (spriteSheet == null) {
            spriteSheet = BitmapFactory.decodeResource(getResources(), R.drawable.cat_run_spritesheet);

        }

        if (spikeimage == null) {
            Bitmap originalspike = BitmapFactory.decodeResource(getResources(), R.drawable.up_arrow__1_);
            spikeimage = Bitmap.createScaledBitmap(originalspike, 100, 100, false);
            spikeShader = new BitmapShader(spikeimage, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
        }
        if (idleSpriteSheet == null) {
            idleSpriteSheet = BitmapFactory.decodeResource(getResources(), R.drawable.idle_cat_spritesheet);
        }
        if (jumpSpriteSheet == null) {
            jumpSpriteSheet = BitmapFactory.decodeResource(getResources(), R.drawable.cat_jump_animation);
        }
        if(checkpointImageBefore == null){
            checkpointImageBefore = BitmapFactory.decodeResource(getResources(), R.drawable.flag1);

        }
        if(checkpointImageAfter == null){
            checkpointImageAfter = BitmapFactory.decodeResource(getResources(), R.drawable.flag2);

        }

    }

    private void scaleBackgroundIfNeeded() {
        if (backgroundImage == null) return;

        if (scaledBackground == null && getWidth() > 0 && getHeight() > 0) {
            int newHeight = getHeight()+150;
            int newWidth = getWidth()+300;
            scaledBackground = Bitmap.createScaledBitmap(backgroundImage, newWidth, newHeight, false);
        }
    }

    @Override
    public void run() {
        long startTime;
        long timeTaken;
        long sleepTime;

        while (isPlaying) {
            startTime = System.currentTimeMillis();

            update();
            draw();

            timeTaken = System.currentTimeMillis() - startTime;
            sleepTime = FRAME_TIME - timeTaken;

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }
            }
        }
    }

    private void update() {
        long time = System.currentTimeMillis();

        if (!isDead) {
            //Checking if player has reached the end of the level
            if (playerX >= 20000) {
                isLevelEnding = true;
                gameStarted = false;
            }

            if (isLevelEnding) {
                //Automatic run
                playerX += moveSpeed;
                movingRight = true;
                movingLeft = false;

                if (blackAlpha < 255) {
                    blackAlpha += 2;
                } else {
                    Intent intent = new Intent(getContext(), LevelsActivity.class);
                    getContext().startActivity(intent);
                    if (getContext() instanceof Activity) {
                        ((Activity) getContext()).finish();
                    }
                }
            }
            else {

                velocityY += gravity;
                playerY += velocityY;

                if (movingLeft) playerX -= moveSpeed;
                if (movingRight) playerX += moveSpeed;

                playerRect.set((int)playerX + 20, (int)playerY + 20, (int)(playerX + playerSize) - 20, (int)(playerY + playerSize));

                boolean onGround = false;

                // Floor check
                float groundY = getHeight() - 200 - playerSize + 15;
                if (playerY >= groundY) {
                    playerY = groundY;
                    velocityY = 0;
                    onGround = true;
                }

                // Platform check
                for (Rect platform : platforms) {
                    if (platform.right < playerX - 500 || platform.left > playerX + 500) continue;

                    if (Rect.intersects(playerRect, platform)) {
                        if (velocityY >= 0 && (playerY + playerSize) <= (platform.top + velocityY + 15)) {
                            playerY = platform.top - playerSize;
                            velocityY = 0;
                            onGround = true;
                        }
                        else if (velocityY < 0 && playerY >= (platform.bottom + velocityY - 15)) {
                            playerY = platform.bottom;
                            velocityY = 0;
                        }
                        else {
                            if (playerX + playerSize > platform.left && playerX < platform.left) {
                                playerX = platform.left - playerSize;
                            } else if (playerX < platform.right && playerX + playerSize > platform.right) {
                                playerX = platform.right;
                            }
                        }
                    }
                }

                isJumping = !onGround;

                // Spike check
                for (Rect spike : spikes) {
                    if (playerX + playerSize > spike.left - 200 && playerX < spike.right + 200) {
                        if (Rect.intersects(playerRect, spike)) {
                            triggerDeath();
                            break;
                        }
                    }
                }

                // Checkpoint check
                for (int i = 0; i < checkpoints.size(); i++) {
                    Rect cp = checkpoints.get(i);
                    if (Rect.intersects(playerRect, cp)) {
                        savedCheckpointX = cp.left;
                        savedCheckpointY = cp.top - playerSize + 10;
                        checkpointActivated.set(i, true);
                    }
                }

                // Time properties
                if (gameStarted) {
                    timeLeftMillis -= FRAME_TIME;
                    if (timeLeftMillis <= 10000) {
                        textPaint.setColor(textFinalTime);
                    }
                    if (timeLeftMillis <= 0) {
                        timeLeftMillis = 0;
                        isDead = true;
                        particles.clear();
                        for (int i = 0; i < 20; i++) {
                            particles.add(new Particle(playerX + playerSize/2, playerY + playerSize/2));
                        }
                        playerX = 200;
                        playerY = 500;
                        savedCheckpointX = playerX;
                        savedCheckpointY = playerY;
                        for (int i = 0; i < checkpointActivated.size(); i++) {
                            checkpointActivated.set(i, false);
                        }
                        timeLeftMillis = 120000;
                        isDead = false;
                        textPaint.setColor(Color.WHITE);
                    }
                }

                int minutes = (int) (timeLeftMillis / 60000);
                int seconds = (int) (timeLeftMillis % 60000) / 1000;
                int millis  = (int) (timeLeftMillis % 1000) / 10;
                timeText = String.format("%02d:%02d:%02d", minutes, seconds, millis);

                updateCamera();
            }
        }

        updateParticles();
    }


    private void triggerDeath() {
        isDead = true;
        particles.clear();
        for (int i = 0; i < 30; i++) {
            particles.add(new Particle(playerX + playerSize/2, playerY + playerSize/2));
        }
    }

    private void updateCamera() {
        worldOffsetX = (playerX > (float) getWidth() / 2) ? playerX - (float) getWidth() / 2 : 0;
        worldOffsetY = (playerY < (float) getHeight() / 2) ? (float) getHeight() / 2 - playerY : 0;
        if (playerX < 0) playerX = 0;
        if (playerX > worldWidth - playerSize) playerX = worldWidth - playerSize;
    }

    private void updateParticles() {
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle p = particles.get(i);
            p.update();
            if (p.alpha <= 0) particles.remove(i);
        }
        if (isDead && particles.isEmpty()) resetPlayer();
    }

    // Functions that help restore the player
    private void resetPlayer() {
        playerX = savedCheckpointX;
        playerY = savedCheckpointY;
        velocityY = 0;
        worldOffsetX = Math.max(0, playerX - (float) getWidth() / 2);
        isDead = false;
        particles.clear(); // Just in case removing the particles

        //Resetting clock
        gameStarted = false;
        if (timeLeftMillis > 10000) {
            textPaint.setColor(Color.WHITE);
        }
    }


    private void draw() {
        if (!surfaceHolder.getSurface().isValid()) return;
        Canvas canvas = surfaceHolder.lockCanvas();
        if (canvas == null) return;

        try {
            // Drawing background
            float backgroundscroll = worldOffsetX > 200 ? 200 : worldOffsetX;
            if (scaledBackground != null) {
                canvas.drawBitmap(scaledBackground, -backgroundscroll, -150, paint);
            } else {
                canvas.drawColor(Color.BLACK);
            }

            // Setting world
            canvas.save();
            canvas.translate(-worldOffsetX, worldOffsetY);

            // Drawing floor + obstecals
            paint.setAlpha(255);
            paint.setColor(floor_color);
            canvas.drawRect(worldOffsetX, getHeight() - 200, worldOffsetX + getWidth(), getHeight(), paint);
            for (Rect spike : spikes) {
                if (spike.right > worldOffsetX && spike.left < worldOffsetX + getWidth()) {

                    int startX = (int) Math.max(spike.left, (Math.floor(worldOffsetX / 100) * 100));
                    int endX = (int) Math.min(spike.right, worldOffsetX + getWidth());

                    for (int x = startX; x < endX; x += 100) {
                        canvas.drawBitmap(spikeimage, x, spike.top, null);
                    }
                }
            }

            // Drawing particles ( using in death  )
            int size = particles.size();
            for (int i = 0; i < size; i++) {
                Particle p = particles.get(i);
                paint.setAlpha(p.alpha);
                canvas.drawRect(p.x, p.y, p.x + 8, p.y + 8, paint);
            }
            paint.setAlpha(255);

            // Drawing player animation (both sides)
            if (!isDead) {
                canvas.save();

                if (movingLeft) {
                    canvas.scale(-1, 1, playerX + playerSize / 2, playerY + playerSize / 2);
                }

                Bitmap currentSheet;
                int currentTotalFrames;
                int frameToDraw;
                long time = System.currentTimeMillis();

                if (isJumping) {
                    // Jump mode
                    currentSheet = jumpSpriteSheet;
                    currentTotalFrames = jumpFrameCount;

                    if (time - lastJumpFrameTime > 80) { //

                        if (jumpCurrentFrame < jumpFrameCount - 1) {
                            jumpCurrentFrame++;
                        }
                        lastJumpFrameTime = time;
                    }
                    frameToDraw = jumpCurrentFrame;

                } else if (movingLeft || movingRight) {
                    //Running mode
                    currentSheet = spriteSheet;
                    currentTotalFrames = frameCount;
                    jumpCurrentFrame = 0;

                    if (time - lastFrameTime > frameDuration) {
                        currentFrame = (currentFrame + 1) % frameCount;
                        lastFrameTime = time;
                    }
                    frameToDraw = currentFrame;

                } else {
                    //Idle mode

                    currentSheet = idleSpriteSheet;
                    currentTotalFrames = idleFrameCount;
                    jumpCurrentFrame = 0;

                    if (time - lastIdleFrameTime > 250 && gameStarted) {
                        idleCurrentFrame = (idleCurrentFrame + 1) % idleFrameCount;
                        lastIdleFrameTime = time;
                    }
                    frameToDraw = idleCurrentFrame;
                }
                //Drawing character
                if (currentSheet != null) {
                    int fWidth = currentSheet.getWidth() / currentTotalFrames;
                    int fHeight = currentSheet.getHeight();
                    spriteSrcRect.set(frameToDraw * fWidth, 0, (frameToDraw + 1) * fWidth, fHeight);
                    spriteDstRect.set((int)playerX, (int)playerY, (int)(playerX + playerSize), (int)(playerY + playerSize));

                    canvas.drawBitmap(currentSheet, spriteSrcRect, spriteDstRect, paint);
                }

                canvas.restore();
            }

            // Drawing platforms
            paint.setColor(Color.GRAY);
            for (Rect platform : platforms) {
                if (platform.right > worldOffsetX && platform.left < worldOffsetX + getWidth()) {
                    canvas.drawRect(platform, paint);
                }
            }
            //Drawing flag
            for (int i = 0; i < checkpoints.size(); i++) {
                Rect cp = checkpoints.get(i);

                if (cp.right > worldOffsetX && cp.left < worldOffsetX + getWidth()) {
                    Bitmap flag = checkpointActivated.get(i) ? checkpointImageAfter : checkpointImageBefore;

                    if (flag != null) {
                        canvas.drawBitmap(flag, cp.left, cp.bottom - flag.getHeight(), paint);
                    }
                }
            }

            //Drawing text
            for (FloatingText ft : floatingTexts) {
                if (ft.x > worldOffsetX - 500 && ft.x < worldOffsetX + getWidth() + 500) {
                    canvas.drawText(ft.text, ft.x, ft.y, floatingTextPaint);
                }
            }

            paint.setColor(main_color);
            canvas.restore();
            //Drawing clock
            canvas.drawText(timeText, 50, 100, textPaint);

            //Drawing the black out
            if (blackAlpha > 0) {
                paint.setColor(Color.BLACK);
                paint.setAlpha(blackAlpha);
                canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
                paint.setAlpha(255);
            }

        } finally {
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }
    public void resume() {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void pause() {
        isPlaying = false;
        try {
            if (gameThread != null) gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void cleanup() {
        if (backgroundImage != null) {
            backgroundImage.recycle();
            backgroundImage = null;
        }
        if (scaledBackground != null) {
            scaledBackground.recycle();
            scaledBackground = null;
        }
        if (characterImage != null) {
            characterImage.recycle();
            characterImage = null;
        }
    }

    // Player controls
    public void jump() {
        if (!isJumping) {
            velocityY = -35;
            gameStarted = true;
            isJumping = true;
        }
    }

    public void moveLeft(boolean moving) {
        movingLeft = moving;
        if (moving) gameStarted = true;
    }

    public void moveRight(boolean moving) {
        movingRight = moving;
        if (moving) gameStarted = true;
    }

    public void moveForward() {
        scrollSpeed = 10;
    }

    public void moveBackward() {
        scrollSpeed = -5;
    }

    public void stopMovement() {
        scrollSpeed = 5;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getX() > (float) getWidth() / 2) {
                jump();
            }
        }
        return true;
    }
    class Particle {
        float x, y, vx, vy;
        int alpha = 255;

        Particle(float x, float y) {
            this.x = x;
            this.y = y;
            // Random speed to all directions
            this.vx = (float) (Math.random() * 20 - 10);
            this.vy = (float) (Math.random() * 20 - 10);
        }

        void update() {
            x += vx;
            y += vy;
            alpha -= 5; // The particle is slowly disapering
        }
    }

    public void showPausePopup() {
        gameStarted=false;
        ((Activity) getContext()).runOnUiThread(() -> {
            View customView = LayoutInflater.from(getContext()).inflate(R.layout.pop_up, null);

            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setView(customView)
                    .setCancelable(false)
                    .create();

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            ImageButton btnPlay = customView.findViewById(R.id.continue_popup);
            btnPlay.setOnClickListener(v -> {
                isPlaying = true;
                dialog.dismiss();
            });


            ImageButton btnExit = customView.findViewById(R.id.exit_popup);
            btnExit.setOnClickListener(v -> {
                Intent exit_intent = new Intent(getContext(), LevelsActivity.class);
                getContext().startActivity(exit_intent);
            });

            dialog.show();
        });
    }
    class FloatingText {
        String text;
        float x, y;

        public FloatingText(String text, float x, float y) {
            this.text = text;
            this.x = x;
            this.y = y;
        }
    }

    private void createPlatforms() {
        int floorLevel = getHeight() - 200;
        platforms.clear();
        spikes.clear();
        floatingTexts.clear();
        checkpoints.clear();
        checkpointActivated.clear();
        switch (currentLevel){
            case 1:
                //Platforms
                // 1 platform
                platforms.add(new Rect(1900, 600, 2200, 650));
                // 2 platform
                platforms.add(new Rect(2500, 450, 2800, 500));
                // 3 platform
                platforms.add(new Rect(3100, 300, 4500, 350));
                //4 platform
                platforms.add(new Rect(4500, 300, 4600, 750));
                //5 platform
                platforms.add(new Rect(6300, floorLevel  - 100, 6400, floorLevel));
                //6 platform
                platforms.add(new Rect(6700,floorLevel  - 300, 6800, floorLevel));
                //7 platform
                platforms.add(new Rect(7100,floorLevel  - 500, 7200, floorLevel));
                //8 platform
                platforms.add(new Rect(7500,floorLevel  - 700, 7600, floorLevel));
                //9 platform
                platforms.add(new Rect(8000,floorLevel  - 750, 8100, floorLevel-700));
                //10 platform
                platforms.add(new Rect(8500,floorLevel  - 750, 8600, floorLevel-700));
                //11 platform
                platforms.add(new Rect(9000,floorLevel  - 750, 9100, floorLevel-700));
                //12 platform
                platforms.add(new Rect(9500,floorLevel  - 650, 9800, floorLevel-600));
                //13 platform
                platforms.add(new Rect(10100,floorLevel  - 550, 10400, floorLevel-500));
                //14 platform
                platforms.add(new Rect(10700,floorLevel  - 450, 11000, floorLevel-400));
                //15 platform
                platforms.add(new Rect(11300,floorLevel  - 350, 11600, floorLevel-300));
                //16 platform
                platforms.add(new Rect(11900,floorLevel  - 250, 12200, floorLevel-200));
                //17 platform
                platforms.add(new Rect(12500,floorLevel  - 150, 12800, floorLevel-100));
                //18 platform
                platforms.add(new Rect(15000,floorLevel  - 150, 15400, floorLevel-100));
                //19 platform
                platforms.add(new Rect(15900,floorLevel  - 150, 16300, floorLevel-100));

                platforms.add(new Rect(16800,floorLevel  - 150, 17200, floorLevel-100));

                platforms.add(new Rect(17700,floorLevel  - 150, 18000, floorLevel-100));


                //Spikes
                spikes.add(new Rect(1200, floorLevel  - 100, 1300, floorLevel));
                spikes.add(new Rect(2200, floorLevel-100, 3400, floorLevel));
                spikes.add(new Rect(3500, 200, 3600, 350));
                spikes.add(new Rect(3900, 200, 4000, 350));
                spikes.add(new Rect(4400, 200, 4600, 350));
                spikes.add(new Rect(6800, floorLevel  - 100, 7100, floorLevel));
                spikes.add(new Rect(7200, floorLevel  - 100, 7500, floorLevel));
                spikes.add(new Rect(7600, floorLevel  - 100, 12200, floorLevel));
                spikes.add(new Rect(13300, floorLevel  - 100, 13500, floorLevel));
                spikes.add(new Rect(13800, floorLevel  - 100, 14000, floorLevel));
                spikes.add(new Rect(14300, floorLevel  - 100, 14500, floorLevel));
                spikes.add(new Rect(15500, floorLevel  - 100, 15800, floorLevel));
                spikes.add(new Rect(16400, floorLevel  - 100, 16700, floorLevel));
                spikes.add(new Rect(17300, floorLevel  - 100, 17600, floorLevel));


                //Checkpoints
                checkpoints.clear();
                checkpointActivated.clear();
                //First Checkpoint
                checkpoints.add(new Rect(10200, floorLevel  - 700, 10300, floorLevel-550));
                checkpointActivated.add(false);

                //Texts
                floatingTexts.clear();
                floatingTexts.add(new FloatingText("The owner is not here! ", 18600, floorLevel - 400));
                floatingTexts.add(new FloatingText("Maybe you can find him in the next neighborhood? ", 19500, floorLevel - 300));

        }



    }


}

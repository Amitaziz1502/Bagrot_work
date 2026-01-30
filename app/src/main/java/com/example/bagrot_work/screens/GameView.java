package com.example.bagrot_work.screens;

import static java.lang.Thread.sleep;

import android.animation.ObjectAnimator;
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
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.bagrot_work.R;
import com.example.bagrot_work.models.GameLevel;
import com.example.bagrot_work.models.Skins;
import com.example.bagrot_work.models.User;
import com.example.bagrot_work.services.DatabaseService;
import com.example.bagrot_work.utils.SharedPreferencesUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;


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
    private float moveSpeed = 13f;

    // Movement controls
    private boolean movingLeft = false;
    private boolean movingRight = false;

    // Cached bitmaps
    private Bitmap backgroundImage;
    private Bitmap backgroundImage2;
    private Bitmap backgroundImage3;
    private Bitmap backgroundImage4;
    private Bitmap backgroundImage5;
    private Bitmap scaledBackground;
    private Bitmap scaledBackground2;
    private Bitmap scaledBackground3;
    private Bitmap scaledBackground4;
    private Bitmap scaledBackground5;


    private Bitmap characterImage;
    private Bitmap spikeimage;
    private int floor_color1;
    private int floor_color2;
    private int floor_color3;
    private int floor_color4;
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

    //Moving platforms
    private List<MovingObstecle> movingPlatforms = new ArrayList<>();


    //checking what level your in
    private int currentLevel = 1;

    // Coins properties
    private List<Coin> coins = new ArrayList<>();
    private Bitmap coinImage;
    private Paint coinPaint;
    private int coinsCollected = 0;
    private int totalCoinsInLevel = 0;
    private int rotationAngle = 0;
    private int yOffset = 0;
    private int alpha = 255;
    private boolean isCollected = false;
    private boolean isVisible = true;

    //Moving Spikes
    private List<MovingObstecle> movingSpikes = new ArrayList<>();
    private Bitmap movingSpikeImg;
    float rotationAngleSpike = 0;
    float rotationSpeed = 3f;

    private boolean endPopupShown = false;

    private Skins skin;


    public void setLevel(int level) {
        this.currentLevel = level;
        loadLevelFromCloud(level);

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
        floor_color1 = Color.parseColor("#0F2A55");
        floor_color2 = Color.parseColor("#122D28");
        floor_color3 = Color.parseColor("#ECB681");
        floor_color4= Color.parseColor("#132952");
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

        //coin
        coinPaint = new Paint();
        coinPaint.setFilterBitmap(true);
        coinPaint.setAntiAlias(true);

    }

    public Skins getSkin() {
        return skin;
    }

    public void setSkin(Skins skin) {
        this.skin = skin;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        loadBitmaps();
        createPlatforms();
        scaleBackground();
    }

    private void loadBitmaps() {
        if (backgroundImage == null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565; // Saves 50% of the power
            options.inScaled = true;
            backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.background_img, options);

        }
        if(backgroundImage2 == null){
            backgroundImage2 = BitmapFactory.decodeResource(getResources(), R.drawable.landscape_image);

        }
        if(backgroundImage3 == null){
            backgroundImage3 = BitmapFactory.decodeResource(getResources(), R.drawable.wildwest);

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
        if(coinImage == null){
            coinImage = BitmapFactory.decodeResource(getResources(), R.drawable.coin);

        }
        if(movingSpikeImg==null){
            movingSpikeImg = BitmapFactory.decodeResource(getResources(), R.drawable.moving_spike);
        }
        if(backgroundImage4==null){
            backgroundImage4 = BitmapFactory.decodeResource(getResources(), R.drawable.background_castle);
        }
        if(backgroundImage5==null){
            backgroundImage5 = BitmapFactory.decodeResource(getResources(), R.drawable.backgroundingfive);
        }

    }


    private void updateLevelInFirebase(String userId, int newLevel) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(userId);
        ref.child("currentlevel").setValue(newLevel);
    }

    private void scaleBackground() {
        if (backgroundImage == null || backgroundImage2==null || backgroundImage3==null || backgroundImage4==null|| backgroundImage5==null) return;

        if (scaledBackground == null && getWidth() > 0 && getHeight() > 0 || scaledBackground2 == null && getWidth() > 0 && getHeight() > 0 || scaledBackground3 == null && getWidth() > 0 && getHeight() > 0) {
            int newHeight = getHeight()+150;
            int newWidth = getWidth()+300;
            scaledBackground = Bitmap.createScaledBitmap(backgroundImage, newWidth, newHeight, false);
            scaledBackground2 = Bitmap.createScaledBitmap(backgroundImage2, newWidth, newHeight, false);
            scaledBackground3 = Bitmap.createScaledBitmap(backgroundImage3, newWidth, newHeight, false);
            scaledBackground4 = Bitmap.createScaledBitmap(backgroundImage4, newWidth, newHeight, false);
            scaledBackground5 = Bitmap.createScaledBitmap(backgroundImage5, newWidth, newHeight, false);

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
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }
            }
        }
    }
    private void isPlayerVerified() {

            User currentUser = SharedPreferencesUtil.getUser(getContext());

            if (currentUser != null) {
                if (currentLevel == currentUser.getCurrentlevel() && currentLevel < 5) {

                    currentUser.setCurrentlevel(currentLevel + 1);
                    SharedPreferencesUtil.saveUser(getContext(), currentUser);
                    DatabaseService.getInstance().updateUser(currentUser.getId(),
                            new UnaryOperator<User>() {
                                @Override
                                public User apply(User user) {
                                    if (user == null) return user;
                                    user.setCurrentlevel(currentUser.getCurrentlevel());
                                    return user;
                                }
                            },
                    new DatabaseService.DatabaseCallback<Void>() {
                        @Override
                        public void onCompleted(Void object) {
                            Log.d("DatabaseService", "Level updated successfully in Firebase");
                        }

                        @Override
                        public void onFailed(Exception e) {
                            Log.e("DatabaseService", "Failed to update level", e);
                        }
                    });
                }
            }
    }

    private void update() {
        long time = System.currentTimeMillis();

        if (!isDead) {
            //Checking if player has reached the end of the level
            if (!endPopupShown && playerX >= worldWidth - playerSize) {
                endPopupShown = true;
                isLevelEnding = true;
                gameStarted = false;
                isPlayerVerified();
                showEndPopup();
            }

            if (isLevelEnding) {
                //Automatic run
                playerX += moveSpeed;
                movingRight = true;
                movingLeft = false;

                if (blackAlpha < 255) {
                    blackAlpha += 2;
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
                if(currentLevel==5){
                    float groundY = getHeight() - 100 - playerSize + 15;
                    if (playerY >= groundY) {
                        playerY = groundY;
                        velocityY = 0;
                        onGround = true;
                    }
                }
                else{
                    float groundY = getHeight() - 200 - playerSize + 15;
                    if (playerY >= groundY) {
                        playerY = groundY;
                        velocityY = 0;
                        onGround = true;
                    }
                }

                // Platform check
                for (Rect platform : platforms) {
                    if (platform.right < playerX - 500 || platform.left > playerX + 500) continue;

                    if (Rect.intersects(playerRect, platform)) {

                        //checking top
                        if (velocityY >= 0 && (playerY + playerSize) <= (platform.top + velocityY + 20)) {
                            playerY = platform.top - playerSize;
                            velocityY = 0;
                            onGround = true;
                        }

                        //checking bottom
                        else if (velocityY < 0 && playerY + 20 > platform.bottom + velocityY) {
                            playerY = platform.bottom;
                            velocityY = 0;
                        }

                        else {
                            // left side
                            if (playerX + playerSize > platform.left && playerX < platform.left) {
                                playerX = platform.left - playerSize;
                            }
                            //right side
                            else if (playerX < platform.right && playerX + playerSize > platform.right) {
                                playerX = platform.right;
                            }
                        }
                    }
                }

                // Checking moving platforms
                for (MovingObstecle mp : movingPlatforms) {
                    mp.update();

                    playerRect.set((int)playerX + 20, (int)playerY + 20, (int)(playerX + playerSize) - 20, (int)(playerY + playerSize));
                    if (Rect.intersects(playerRect, mp.rect)) {

                        // Landing on top of the platform
                        // Landing on top of the platform
                        if (velocityY >= 0 && (playerY + playerSize) <= (mp.rect.top + velocityY + 15)) {
                            mp.isTriggered = true;
                            playerY = mp.rect.top - playerSize;
                            velocityY = 0;
                            onGround = true;

                            // Moving the player with the platform
                            if (mp.endX > mp.startX) {
                                if (mp.movingForward) {
                                    playerX += mp.speed;
                                } else {
                                    playerX -= mp.speed;
                                }
                            }

                            if (mp.endY != mp.startY) {
                                if (mp.movingUp) {
                                    playerY -= mp.speed;
                                } else {
                                    playerY += mp.speed;
                                }
                            }
                        }

                        //collision with bottom
                        else if (velocityY < 0 && playerY >= (mp.rect.bottom + velocityY - 15)) {
                            playerY = mp.rect.bottom;
                            velocityY = 0;
                        }

                        //collision with the sides
                        else {
                            if (playerX + playerSize > mp.rect.left && playerX < mp.rect.left) {
                                playerX = mp.rect.left - playerSize;
                            }
                            else if (playerX < mp.rect.right && playerX + playerSize > mp.rect.right) {
                                playerX = mp.rect.right;
                            }
                        }
                    }
                }

                //moving spikes
                playerRect.set((int)playerX + 20, (int)playerY + 20, (int)(playerX + playerSize) - 20, (int)(playerY + playerSize));

                // moving spikes round check
                for (MovingObstecle ms : movingSpikes) {
                    ms.update();

                    float playerCenterX = playerX + playerSize / 2f;
                    float playerCenterY = playerY + playerSize / 2f;

                    float spikeCenterX = ms.rect.centerX();
                    float spikeCenterY = ms.rect.centerY();

                    float dx = playerCenterX - spikeCenterX;
                    float dy = playerCenterY - spikeCenterY;
                    double distance = Math.sqrt(dx * dx + dy * dy);

                    float collisionRadius = (ms.width / 2f);

                    if (distance < collisionRadius) {
                        triggerDeath();
                        break;
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

                // Coin collision and update
                for (Coin coin : coins) {
                    if (coin.isVisible && !coin.isCollected) {
                        if (Rect.intersects(playerRect, coin.position)) {
                            coin.isCollected = true;
                            coinsCollected++;
                        }
                    }
                    coin.update();
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

        for (MovingObstecle mp : movingPlatforms) {
            mp.reset();
        }
        for (MovingObstecle ms : movingSpikes) {
            ms.reset();
        }

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
            if (scaledBackground != null && currentLevel==1) {
                canvas.drawBitmap(scaledBackground, -backgroundscroll, -150, paint);
            }
            else if(scaledBackground2 != null && currentLevel==2){
                canvas.drawBitmap(scaledBackground2, -backgroundscroll, -150, paint);
            }
            else if (scaledBackground3 != null && currentLevel==3) {
                canvas.drawBitmap(scaledBackground3, -backgroundscroll, -150, paint);
            }
            else if (scaledBackground4 != null && currentLevel==4) {
                canvas.drawBitmap(scaledBackground4, -backgroundscroll, -150, paint);
            }
            else if (scaledBackground4 != null && currentLevel==5) {
                canvas.drawBitmap(scaledBackground5, -backgroundscroll, -150, paint);
            }
            else {
                canvas.drawColor(Color.BLACK);
            }

            // Setting world
            canvas.save();
            canvas.translate(-worldOffsetX, worldOffsetY);


            // Drawing floor + obstecals
            paint.setAlpha(255);
            if(currentLevel==1){
                paint.setColor(floor_color1);
                canvas.drawRect(worldOffsetX, getHeight() - 200, worldOffsetX + getWidth(), getHeight(), paint);
            }
            else if(currentLevel==2){
                paint.setColor(floor_color2);
                canvas.drawRect(worldOffsetX, getHeight() - 200, worldOffsetX + getWidth(), getHeight(), paint);

            }
            else if(currentLevel==3){
                paint.setColor(floor_color3);
                canvas.drawRect(worldOffsetX, getHeight() - 200, worldOffsetX + getWidth(), getHeight(), paint);

            }
            else if(currentLevel==4){
                paint.setColor(floor_color2);
                canvas.drawRect(worldOffsetX, getHeight() - 200, worldOffsetX + getWidth(), getHeight(), paint);

            }
            else if(currentLevel==5){
                paint.setColor(floor_color4);
                canvas.drawRect(worldOffsetX, getHeight() - 100, worldOffsetX + getWidth(), getHeight(), paint);
                playerSize = 125;
            }

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

            if (coins != null && coinImage != null) {
                for (Coin coin : coins) {
                    if (!coin.isVisible || coin.position.right < worldOffsetX || coin.position.left > worldOffsetX + getWidth()) continue;

                    canvas.save();
                    coinPaint.setAlpha(coin.alpha);

                    float centerX = coin.position.centerX();
                    float centerY = coin.position.centerY() + coin.yOffset;

                    canvas.rotate(coin.rotationAngle, centerX, centerY);

                    Rect drawRect = new Rect(
                            coin.position.left,
                            (int)(coin.position.top + coin.yOffset),
                            coin.position.right,
                            (int)(coin.position.bottom + coin.yOffset)
                    );

                    canvas.drawBitmap(coinImage, null, drawRect, coinPaint);
                    canvas.restore();
                }
                coinPaint.setAlpha(255);
            }

            // Drawing player animation cat
            if (!isDead && skin == Skins.cat) {
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
            if (!isDead && skin == Skins.dog) {
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

            //Drawing moving platforms
            paint.setColor(Color.BLUE);
            for (MovingObstecle mp : movingPlatforms) {
                if (mp.rect.right > worldOffsetX && mp.rect.left < worldOffsetX + getWidth()) {
                    canvas.drawRect(mp.rect, paint);
                }
            }

            //Drawing moving spikes
            for (MovingObstecle ms : movingSpikes) {
                boolean shouldDraw = (currentLevel == 5) ||
                        (ms.rect.right > worldOffsetX && ms.rect.left < worldOffsetX + getWidth());

                if (shouldDraw) {
                    canvas.save();

                    float cx = ms.rect.centerX();
                    float cy = ms.rect.centerY();

                    canvas.rotate(ms.rotationAngle, cx, cy);

                    canvas.drawBitmap(movingSpikeImg, null, ms.rect, null);

                    canvas.restore();
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
            if(currentLevel!=5){
                canvas.drawText(timeText, 50, 100, textPaint);
            }


            //Score
            if (coinImage != null) {
                int xPos = getWidth() - 700;
                int yPos = getHeight()-850;
                canvas.drawBitmap(coinImage, null, new Rect(xPos, yPos - 60, xPos + 60, yPos), null);
                String scoreText = coinsCollected + " / " + totalCoinsInLevel;
                canvas.drawText(scoreText, xPos + 100, yPos, textPaint);
            }

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
    public void showEndPopup(){
        gameStarted=false;
        ((Activity) getContext()).runOnUiThread(() -> {
            View customView = LayoutInflater.from(getContext()).inflate(R.layout.pop_up_end_lvl, null);
            AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(customView).setCancelable(false).create();

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            ImageButton btnNext = customView.findViewById(R.id.nextlvl);
            btnNext.setOnClickListener(v -> {
                User currentUser = SharedPreferencesUtil.getUser(getContext());
                if (currentUser != null) {
                    if (currentLevel == currentUser.getCurrentlevel() && currentLevel < 5) {
                        int nextLevel = currentLevel + 1;
                        currentUser.setCurrentlevel(nextLevel);
                        SharedPreferencesUtil.saveUser(getContext(), currentUser);
                        DatabaseService.getInstance().updateUser(currentUser.getId(), new UnaryOperator<User>() {
                            @Override
                            public User apply(User user) {
                                if (user == null) return null;
                                user.setCurrentlevel(nextLevel);
                                return user;
                            }
                        }, new DatabaseService.DatabaseCallback<Void>() {
                            @Override
                            public void onCompleted(Void v) {

                            }

                            @Override
                            public void onFailed(Exception e) {

                            }
                        });
                    }
                }
                customView.setScaleX(0.5f);
                customView.setScaleY(0.5f);
                customView.setAlpha(0f);
                switch(currentLevel){
                    case 1:
                        Intent goToLevelTwo = new Intent(getContext(), LevelTwoActivity.class);
                        getContext().startActivity(goToLevelTwo);
                        if (getContext() instanceof Activity) {
                            ((Activity) getContext()).finish();
                        }
                        break;

                    case 2:
                        Intent goToLevelThree = new Intent(getContext(), LevelThreeActivity.class);
                        getContext().startActivity(goToLevelThree);
                        if (getContext() instanceof Activity) {
                            ((Activity) getContext()).finish();
                        }
                        break;
                    case 3:
                        Intent goToLevelFour = new Intent(getContext(), LevelFourActivity.class);
                        getContext().startActivity(goToLevelFour);
                        if (getContext() instanceof Activity) {
                            ((Activity) getContext()).finish();
                        }
                        break;
                    case 4:
                        Intent goToLevelFive = new Intent(getContext(), LevelFiveActivity.class);
                        getContext().startActivity(goToLevelFive);
                        if (getContext() instanceof Activity) {
                            ((Activity) getContext()).finish();
                        }
                        break;
                    case 5:
                        Intent goToLevelHome = new Intent(getContext(), LevelsActivity.class);
                        getContext().startActivity(goToLevelHome);
                        if (getContext() instanceof Activity) {
                            ((Activity) getContext()).finish();
                        }
                        break;

                }
            });

            ImageButton btnExit = customView.findViewById(R.id.exit_popup);
            btnExit.setOnClickListener(v -> {
                Intent exit_intent = new Intent(getContext(), LevelsActivity.class);
                getContext().startActivity(exit_intent);
                if (getContext() instanceof Activity) {
                    ((Activity) getContext()).finish();
                }
            });


            dialog.show();
            customView.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(1000)
                    .setInterpolator(new OvershootInterpolator())
                    .start();
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
        GameLevel data = GameLevel.getLevel(currentLevel, floorLevel);

        if (data != null) {
            this.platforms.clear();
            for (GameLevel.RectData rd : data.platforms) {
                this.platforms.add(rd.toRect());
            }

            this.spikes.clear();
            for (GameLevel.RectData sd : data.spikes) {
                this.spikes.add(sd.toRect());
            }

            this.checkpoints.clear();
            for (GameLevel.RectData cd : data.checkpoints) {
                this.checkpoints.add(cd.toRect());
            }

            this.worldWidth = data.worldWidth;
            this.timeLeftMillis = data.levelTimeMillis;

            this.floatingTexts.clear();
            for (GameLevel.FloatingTextData ft : data.floatingTexts) {
                this.floatingTexts.add(new FloatingText(ft.text, ft.x, ft.y));
            }

            movingPlatforms.clear();
            if (data.movingPlatforms != null) {
                for (GameLevel.MovingObstecleData mpd : data.movingPlatforms) {
                    movingPlatforms.add(new MovingObstecle(mpd.x, mpd.y, mpd.width, mpd.height, mpd.rangeX, mpd.rangeY, mpd.speed,mpd.isTriggerBased));                }
            }

            movingSpikes.clear();
            if (data.movingSpikes != null) {
                for (GameLevel.MovingObstecleData msd : data.movingSpikes) {
                    movingSpikes.add(new MovingObstecle(msd.x, msd.y, msd.width, msd.height, msd.rangeX, msd.rangeY, msd.speed,msd.isTriggerBased));
                }
            }

            checkpointActivated.clear();
            for (Rect r : checkpoints) checkpointActivated.add(false);
        }
    }
    private void loadLevelFromCloud(int levelNumber) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("levels").child("level_" + levelNumber);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                GameLevel data = snapshot.getValue(GameLevel.class);
                if (data != null) {
                    platforms.clear();
                    for (GameLevel.RectData rd : data.platforms)
                        platforms.add(rd.toRect());

                    spikes.clear();
                    for (GameLevel.RectData sd : data.spikes)
                        spikes.add(sd.toRect());

                    checkpoints.clear();
                    if (data.checkpoints != null) {
                        for (GameLevel.RectData cd : data.checkpoints)
                            checkpoints.add(cd.toRect());
                    }

                    floatingTexts.clear();
                    if (data.floatingTexts != null) {
                        for (GameLevel.FloatingTextData ft : data.floatingTexts) {
                            floatingTexts.add(new FloatingText(ft.text, ft.x, ft.y));
                        }
                    }

                    movingPlatforms.clear();
                    if (data.movingPlatforms != null) {
                        for (GameLevel.MovingObstecleData mpd : data.movingPlatforms) {
                            // הוספת mpd.isTriggerBased בסוף
                            movingPlatforms.add(new MovingObstecle(mpd.x, mpd.y, mpd.width, mpd.height,
                                    mpd.rangeX, mpd.rangeY, mpd.speed, mpd.isTriggerBased));
                        }
                    }
                    movingSpikes.clear();
                    if (data.movingSpikes != null) {
                        for (GameLevel.MovingObstecleData msd : data.movingSpikes) {
                            movingPlatforms.add(new MovingObstecle(msd.x, msd.y, msd.width, msd.height, msd.rangeX,0, msd.speed,msd.isTriggerBased));
                        }
                    }

                    coins.clear();
                    if (data.coins != null) {
                        for (GameLevel.RectData cr : data.coins) {
                            coins.add(new Coin(cr.toRect()));
                        }
                    }
                    totalCoinsInLevel = coins.size();
                    coinsCollected = 0;

                    checkpointActivated.clear();
                    for (Rect r : checkpoints) checkpointActivated.add(false);

                    worldWidth = data.worldWidth;
                    timeLeftMillis = data.levelTimeMillis;

                    postInvalidate();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("DB", "Failed to load level", error.toException());
            }
        });
    }

    private class MovingObstecle {
        Rect rect;
        float currentX, startX, endX;
        float currentY, startY, endY;
        float speed;
        int width, height;
        boolean movingForward = true;
        boolean movingUp = true;
        boolean isTriggerBased = false;
        boolean isTriggered = false;

        public float rotationAngle = 0f;
        public float rotationSpeed = 5f;

        public MovingObstecle(int x, int y, int width, int height, int rangeX, int rangeY, float speed, boolean isTriggerBased) {
            this.startX = x;
            this.currentX = x;
            this.endX = x + rangeX;
            this.isTriggerBased = isTriggerBased;
            if (!isTriggerBased) {
                this.isTriggered = true;
            }

            this.startY = y;
            this.currentY = y;
            this.endY = y - rangeY;

            this.speed = speed;
            this.width = width;
            this.height = height;
            this.rect = new Rect(x, y, x + width, y + height);
        }

        public void update() {
            if (!isTriggered) return;

            if (endX > startX) {
                if (movingForward) {
                    currentX += speed;
                    if (currentX >= endX) movingForward = false;
                } else {
                    currentX -= speed;
                    if (currentX <= startX) movingForward = true;
                }
            }

            if (startY != endY) {
                if (movingUp) {
                    currentY -= speed;
                    if (currentY <= endY) movingUp = false;
                } else {
                    currentY += speed;
                    if (currentY >= startY) movingUp = true;
                }
            }

            rotationAngle = (rotationAngle + rotationSpeed) % 360;

            rect.left = (int) currentX;
            rect.top = (int) currentY;
            rect.right = (int) (currentX + width);
            rect.bottom = (int) (currentY + height);
        }
        public void reset() {
            this.currentX = startX;
            this.currentY = startY;
            this.movingForward = true;
            this.movingUp = true;
            this.rotationAngle = 0f;

            if (isTriggerBased) {
                isTriggered = false;
            } else {
                isTriggered = true;
            }

            rect.left = (int) currentX;
            rect.top = (int) currentY;
            rect.right = (int) (currentX + width);
            rect.bottom = (int) (currentY + height);

        }
    }
    class Coin {
        Rect position;
        float rotationAngle = 0;
        float yOffset = 0;
        int alpha = 255;
        boolean isCollected = false;
        boolean isVisible = true;

        public Coin(Rect position) {
            this.position = position;
        }

        void update() {
            if (isCollected && isVisible) {
                rotationAngle += 15;
                yOffset -= 5;
                alpha -= 10;

                if (alpha <= 0) {
                    alpha = 0;
                    isVisible = false;
                }
            }
        }

        void reset() {
            rotationAngle = 0;
            yOffset = 0;
            alpha = 255;
            isCollected = false;
            isVisible = true;
        }
    }
}


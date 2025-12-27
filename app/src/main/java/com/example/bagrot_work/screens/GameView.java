package com.example.bagrot_work.screens;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

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
    private float playerSize = 200;
    private float velocityY = 0;
    private float gravity = 2f;
    private boolean isMoving = false;
    private boolean isJumping = false;

    // Movement
    private float scrollSpeed = 5;
    private float worldOffset = 0;
    private float moveSpeed = 15f;

    // Movement controls
    private boolean movingLeft = false;
    private boolean movingRight = false;

    // Cached bitmaps
    private Bitmap backgroundImage;
    private Bitmap scaledBackground;
    private Bitmap characterImage;
    private Bitmap spikeimage;

    //Spikes settings
    private List<Rect> spikes = new ArrayList<>();

    // If player reacts with the spike
    private List<Particle> particles = new ArrayList<>();
    private boolean isDead = false;

    //WorldOffset settings
    private float backgroundScroll;

    //Platform settings
    private List<Rect> platforms = new ArrayList<>();

    //Player Collision with obstacles
    private Rect playerRect = new Rect();

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

    // FPS control
    private final int FPS = 60;
    private final int FRAME_TIME = 1000 / FPS;

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        surfaceHolder = getHolder();
        paint = new Paint();
        paint.setAntiAlias(true);// Making the drawing smoother
        paint.setFilterBitmap(false); // Make the pictures smoother
        paint.setAntiAlias(false);//Giving a nice fps push
        paint.setColor(Color.parseColor("#353C6F"));// Setting the color one time
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
            backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.backroundhorizentle, options);

        }

        if (spriteSheet == null) {
            spriteSheet = BitmapFactory.decodeResource(getResources(), R.drawable.cat_run_spritesheet);

        }

        if (spikeimage == null) {
            Bitmap originalspike = BitmapFactory.decodeResource(getResources(), R.drawable.up_arrow__1_);
            spikeimage = Bitmap.createScaledBitmap(originalspike, 100, 100, false);
        }
        if (idleSpriteSheet == null) {
            idleSpriteSheet = BitmapFactory.decodeResource(getResources(), R.drawable.idle_cat_spritesheet);
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
        // Restoring character only if he's alive
        if (!isDead) {
            // Gravity
            velocityY += gravity;
            playerY += velocityY;

            // Horizontal movement
            if (movingLeft) playerX -= moveSpeed;
            if (movingRight) playerX += moveSpeed;

            // Checking if player is on the floor
            float groundY = getHeight() - 200 - playerSize + 15;
            if (playerY >= groundY) {
                playerY = groundY;
                velocityY = 0;
                isJumping = false;
            }

            // checking Collision with the spike
            playerRect.set((int)playerX, (int)playerY, (int)(playerX + playerSize), (int)(playerY + playerSize));

            for (Rect spike : spikes) {
                // Checking if the player is 500px from the spike, so they will only check the spikes which in eye reach
                if (Math.abs(spike.left - playerX) < 1000) {
                    if (Rect.intersects(playerRect, spike)) {
                        isDead = true;
                        particles.clear();
                        for (int i = 0; i < 30; i++) {
                            particles.add(new Particle(playerX + playerSize/2, playerY + playerSize/2));
                        }
                        break;
                    }
                }
            }


            //Collision with the platforms
            for (Rect platform : platforms) {
                if (Rect.intersects(playerRect, platform)) {
                    //If the player tries to go throw the platform from the bottom
                    if (velocityY < 0 && playerY < platform.bottom && playerY > platform.top) {
                        playerY = platform.bottom;
                        velocityY = 0; // Start falling instantly
                    }
                    // Unavailing the player to go throw the platform from the top
                    if (velocityY > 0 && (playerY + playerSize) <= (platform.top + velocityY + 15)) {
                        playerY = platform.top - playerSize;
                        velocityY = 0;
                        isJumping = false;
                    }
                    else {
                        // If the player trying to go from the left side of the platform
                        if (playerX + playerSize > platform.left && playerX < platform.left) {
                            playerX = platform.left - playerSize;
                        }
                        // If the player trying to go from the right side of the platform
                        else if (playerX < platform.right && playerX + playerSize > platform.right) {
                            playerX = platform.right;
                        }
                    }
                }
            }

        }

        // Updating the exploding particles
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle p = particles.get(i);
            p.update();

            // When the particle is gone I remove him
            if (p.alpha <= 0) {
                particles.remove(i);
            }
        }

        // Auto restore when the particles are gone
        if (isDead && particles.isEmpty()) {
            resetPlayer();
        }

        // World-offset control
        if (playerX > (float) getWidth() / 2) {
            worldOffset = playerX - (float) getWidth() / 2;
        } else {
            worldOffset = 0;
        }

        // Setting the players boundaries
        if (playerX < 0) playerX = 0;
        if (playerX > 10000 - playerSize) playerX = 10000 - playerSize;

    }

    // Functions that help restore the player
    private void resetPlayer() {
        playerX = 200;
        playerY = 500;
        velocityY = 0;
        worldOffset = 0;
        isDead = false;
        particles.clear(); // Just in case removing the particles
    }

    private void draw() {
        if (!surfaceHolder.getSurface().isValid()) return;
        Canvas canvas = surfaceHolder.lockCanvas();
        if (canvas == null) return;

        try {
            // Drawing background
            float backgroundscroll = worldOffset > 200 ? 200 : worldOffset;
            if (scaledBackground != null) {
                canvas.drawBitmap(scaledBackground, -backgroundscroll, -150, paint);
            } else {
                canvas.drawColor(Color.BLACK);
            }

            // Setting world
            canvas.save();
            canvas.translate(-worldOffset, 0);

            // Drawing floor + obstecals
            canvas.drawRect(worldOffset, getHeight() - 200, worldOffset + getWidth(), getHeight(), paint);
            for (Rect spike : spikes) {
                if (spike.right > worldOffset && spike.left < worldOffset + getWidth()) {
                    for (int x = spike.left; x < spike.right; x += 100) {
                        if (x + 100 > worldOffset && x < worldOffset + getWidth()) {
                            canvas.drawBitmap(spikeimage, x, spike.top, null);
                        }
                    }
                }
            }

            // Drawing particles ( using in death  )
            for (int i = 0; i < particles.size(); i++) {
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

                if (movingLeft || movingRight) {
                    // Running mode
                    currentSheet = spriteSheet;
                    currentTotalFrames = frameCount;

                    long time = System.currentTimeMillis();
                    if (time - lastFrameTime > frameDuration) {
                        currentFrame = (currentFrame + 1) % frameCount;
                        lastFrameTime = time;
                    }
                    frameToDraw = currentFrame;
                } else {
                    // Idle mode
                    currentSheet = idleSpriteSheet;
                    currentTotalFrames = idleFrameCount;

                    long time = System.currentTimeMillis();
                    if (time - lastIdleFrameTime > 250) {
                        idleCurrentFrame = (idleCurrentFrame + 1) % idleFrameCount;
                        lastIdleFrameTime = time;
                    }
                    frameToDraw = idleCurrentFrame;
                }

                if (currentSheet != null) {
                    int fWidth = currentSheet.getWidth() / currentTotalFrames;
                    int fHeight = currentSheet.getHeight();

                    Rect src = new Rect(frameToDraw * fWidth, 0, (frameToDraw + 1) * fWidth, fHeight);
                    Rect dst = new Rect((int)playerX, (int)playerY, (int)(playerX + playerSize), (int)(playerY + playerSize));

                    canvas.drawBitmap(currentSheet, src, dst, paint);
                }

                canvas.restore();
            }

            // Drawing platforms
            paint.setColor(Color.GRAY);
            for (Rect platform : platforms) {
                if (platform.right > worldOffset && platform.left < worldOffset + getWidth()) {
                    canvas.drawRect(platform, paint);
                }
            }
            paint.setColor(Color.parseColor("#353C6F"));

            canvas.restore();

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
            isJumping = true;
        }
    }

    public void moveLeft(boolean moving) {
        movingLeft = moving;
    }

    public void moveRight(boolean moving) {
        movingRight = moving;
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
    private void createPlatforms() {


        platforms.clear();
        spikes.clear();

        //Platforms
        // First platform
        platforms.add(new Rect(1900, 600, 2200, 650));
        // Second platform
        platforms.add(new Rect(2500, 450, 2800, 500));
        // Third platform
        platforms.add(new Rect(3100, 300, 3400, 350));

        //Spikes
        spikes.add(new Rect(1200, 653, 1250, 753));
        spikes.add(new Rect(2200, 653, 3400, 753));



    }

}

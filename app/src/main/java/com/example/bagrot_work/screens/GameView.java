package com.example.bagrot_work.screens;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.bagrot_work.R;

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
    private boolean isJumping = false;

    // Movement
    private float scrollSpeed = 5;
    private float worldOffset = 0;
    private float moveSpeed = 10f;

    // Movement controls
    private boolean movingLeft = false;
    private boolean movingRight = false;

    // Cached bitmaps
    private Bitmap backgroundImage;
    private Bitmap scaledBackground;
    private Bitmap characterImage;

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
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        loadBitmaps();
        scaleBackgroundIfNeeded();
    }

    private void loadBitmaps() {
        if (backgroundImage == null) {
            backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.backroundhorizentle);
        }

        if (characterImage == null) {
            Bitmap originalCharacter = BitmapFactory.decodeResource(getResources(), R.drawable.gamecharacterimprove);
            characterImage = Bitmap.createScaledBitmap(originalCharacter, (int) playerSize, (int) playerSize, false);
            if (originalCharacter != characterImage) {
                originalCharacter.recycle();
            }
        }
    }

    private void scaleBackgroundIfNeeded() {
        if (backgroundImage == null) return;

        if (scaledBackground == null && getWidth() > 0 && getHeight() > 0) {
            scaledBackground = Bitmap.createScaledBitmap(backgroundImage, getWidth(), getHeight(), false);
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
                    e.printStackTrace();
                }
            }
        }
    }

    private void update() {
        // Apply gravity
        velocityY += gravity;
        playerY += velocityY;

        // Ground collision
        float groundY = getHeight() - 300 - playerSize;
        if (playerY >= groundY) {
            playerY = groundY;
            velocityY = 0;
            isJumping = false;
        }

        // Horizontal movement
        if (movingLeft) playerX -= moveSpeed;
        if (movingRight) playerX += moveSpeed;

        if (playerX > (float) getWidth() / 2) {
            worldOffset = playerX - (float) getWidth() / 2;
        }else{
            worldOffset=0;
        }

        // Keep player in bounds
        if (playerX < 0) playerX = 0;
        if (playerX > 10000 - playerSize) playerX = 10000 - playerSize;

    }

    private void draw() {
        if (!surfaceHolder.getSurface().isValid()) return;

        Canvas canvas = surfaceHolder.lockCanvas();
        if (canvas == null) return;

        try {
            if (scaledBackground != null) {
                canvas.drawBitmap(scaledBackground, 0, 0, paint);
            } else {
                canvas.drawColor(Color.BLACK);
            }

            canvas.save();

            canvas.translate(-worldOffset, 0);

            paint.setColor(Color.parseColor("#353C6F"));
            canvas.drawRect(0, getHeight() - 300, 10000, getHeight(), paint);

            if (characterImage != null) {
                canvas.drawBitmap(characterImage, playerX, playerY, null);
            }

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
            if (event.getX() > getWidth() / 2) {
                jump();
            }
        }
        return true;
    }
}

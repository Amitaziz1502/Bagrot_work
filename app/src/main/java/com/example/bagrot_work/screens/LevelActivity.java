package com.example.bagrot_work.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.bagrot_work.R;
import com.example.bagrot_work.models.GameLevel;
import com.example.bagrot_work.models.User;
import com.example.bagrot_work.services.DatabaseService;
import com.example.bagrot_work.utils.SharedPreferencesUtil;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.HashSet;
import java.util.Set;
import java.util.function.UnaryOperator;

public class LevelActivity extends BaseActivity implements View.OnClickListener {
    private GameView gameView;
    private ImageButton moveRight, moveLeft, exit;
    private ImageView[] hearts;
    private DrawerLayout drawerLayout;
    private LinearLayout drawerMenu;
    User user;

    int level;
    public static InterstitialAd mInterstitialAd;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_level);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        hearts = new ImageView[]{
                findViewById(R.id.heart1),
                findViewById(R.id.heart2),
                findViewById(R.id.heart3),
                findViewById(R.id.heart4),
                findViewById(R.id.heart5)
        };

        level = getIntent().getIntExtra("LEVEL", 1);
        gameView = findViewById(R.id.gameView);

        // Drawer setup
        drawerLayout = findViewById(R.id.drawerLayout);
        drawerMenu = findViewById(R.id.drawerMenu);

        // Pause game when drawer opens, resume when it closes
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                gameView.pause();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                gameView.resume();
            }
        });

        // Drawer menu buttons
        findViewById(R.id.menuContinue).setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.END);
            // onDrawerClosed will resume the game
        });

        findViewById(R.id.menuRetry).setOnClickListener(v -> {
            Intent intent = new Intent(this, LevelActivity.class);
            intent.putExtra("LEVEL", level);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.menuExit).setOnClickListener(v -> {
            startActivity(new Intent(this, LevelsActivity.class));
            finish();
        });

        user = SharedPreferencesUtil.getUser(LevelActivity.this);
        gameView.setAbility(user.getAppearance());
        gameView.setHearts(hearts);
        gameView.setEvents(new GameView.OnGameViewEvents() {
            @Override
            public void onGameFinish() {
                databaseService.updateUser(user.getId(), new UnaryOperator<User>() {
                    @Override
                    public User apply(User serverUser) {
                        if (serverUser == null) return null;
                        serverUser.setCollectedCoins(user.getCollectedCoins());
                        serverUser.setWalletBalance(user.getCollectedCoins().size());
                        return serverUser;
                    }
                }, new DatabaseService.DatabaseCallback<Void>() {
                    @Override
                    public void onCompleted(Void v) {
                        SharedPreferencesUtil.saveUser(LevelActivity.this, user);
                    }

                    @Override
                    public void onFailed(Exception e) {}
                });
            }

            @Override
            public void onCoinCollected(GameLevel.CoinData coin) {
                user.getCollectedCoins().add(coin);
            }

            @Override
            public boolean shouldShowCoin(GameLevel.CoinData coin) {
                Set<GameLevel.CoinData> coinDataSet = new HashSet<>(user.getCollectedCoins());
                return !coinDataSet.contains(coin);
            }
        });

        moveRight = findViewById(R.id.move_right);
        moveLeft = findViewById(R.id.move_left);
        exit = findViewById(R.id.btn_exit);
        exit.setOnClickListener(this);

        moveRight.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                gameView.moveRight(true);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                gameView.moveRight(false);
            }
            return true;
        });

        moveLeft.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                gameView.moveLeft(true);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                gameView.moveLeft(false);
            }
            return true;
        });

        databaseService.getLevel(level, new DatabaseService.DatabaseCallback<GameLevel>() {
            @Override
            public void onCompleted(GameLevel gameLevel) {
                gameView.setLevel(level, gameLevel);
            }

            @Override
            public void onFailed(Exception e) {}
        });
        //remove when done
        databaseService.getLevel(level, new DatabaseService.DatabaseCallback<GameLevel>() {
            @Override
            public void onCompleted(GameLevel gameLevel) {
                // הוסף את זה זמנית
                Log.d("LevelDebug", "movingSpikes count: " + (gameLevel.movingSpikes != null ? gameLevel.movingSpikes.size() : "null"));
                if (gameLevel.movingSpikes != null) {
                    for (GameLevel.MovingObstecleData ms : gameLevel.movingSpikes) {
                        Log.d("LevelDebug", "spike: x=" + ms.x + " y=" + ms.y + " w=" + ms.width + " h=" + ms.height + " rangeY=" + ms.rangeY);
                    }
                }
                gameView.setLevel(level, gameLevel);
            }

            @Override
            public void onFailed(Exception e) {}
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!drawerLayout.isDrawerOpen(Gravity.END)) {
            gameView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_exit) {
            // Open drawer and pause game
            drawerLayout.openDrawer(Gravity.END);
        }
    }
}
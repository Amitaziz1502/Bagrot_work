package com.example.bagrot_work.screens;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bagrot_work.R;
import com.example.bagrot_work.models.GameLevel;
import com.example.bagrot_work.models.User;
import com.example.bagrot_work.services.DatabaseService;
import com.example.bagrot_work.utils.SharedPreferencesUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.function.UnaryOperator;

public class LevelActivity extends BaseActivity implements View.OnClickListener {
    private GameView gameView;
    private ImageButton moveRight, moveLeft, exit;
    User user;

    int level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_level);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        level = getIntent().getIntExtra("LEVEL", 1);

        gameView = findViewById(R.id.gameView);

        user = SharedPreferencesUtil.getUser(LevelActivity.this);
        gameView.setAbility(user.getAppearance());
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
                    public void onFailed(Exception e) {

                    }
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
        moveRight= findViewById(R.id.move_right);
        moveLeft= findViewById(R.id.move_left);
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
            public void onFailed(Exception e) {

            }
        });


    }
    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() ==  R.id.btn_exit){
            gameView.showPausePopup();

        }
    }

}
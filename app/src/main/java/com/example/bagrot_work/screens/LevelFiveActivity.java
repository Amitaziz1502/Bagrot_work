package com.example.bagrot_work.screens;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bagrot_work.R;
import com.example.bagrot_work.utils.SharedPreferencesUtil;

public class LevelFiveActivity extends BaseActivity implements View.OnClickListener {
    private GameView gameView;
    private ImageButton moveRight, moveLeft, exit;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_level_one);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        gameView = findViewById(R.id.gameView);
        gameView.setLevel(5);
        gameView.setAbility(SharedPreferencesUtil.getUser(this).getAppearance());
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
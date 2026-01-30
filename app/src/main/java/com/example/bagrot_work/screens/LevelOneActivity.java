package com.example.bagrot_work.screens;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import com.example.bagrot_work.R;
import com.example.bagrot_work.utils.SharedPreferencesUtil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LevelOneActivity extends BaseActivity implements View.OnClickListener {
    private GameView gameView;
    private ImageButton moveRight, moveLeft, exit;

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
        gameView.setLevel(1);
        gameView.setSkin(SharedPreferencesUtil.getUser(this).getAppearance());
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
        if(v.getId() == R.id.btn_exit) {
            gameView.showPausePopup();
        }
    }

}
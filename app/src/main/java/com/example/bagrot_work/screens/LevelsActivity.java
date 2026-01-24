package com.example.bagrot_work.screens;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bagrot_work.R;
import com.example.bagrot_work.utils.SharedPreferencesUtil;

public class LevelsActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_levels);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageButton btnFloat = findViewById(R.id.btnLevel1);
        ImageButton btnFloat2 = findViewById(R.id.btnLevel2);
        ImageButton btnFloat3 = findViewById(R.id.btnLevel3);
        ImageButton btnFloat4 = findViewById(R.id.btnLevel4);
        ImageButton btnFloat5 = findViewById(R.id.btnLevel5);
        ImageButton btnReturnHome = findViewById(R.id.btnGoHome);
        btnReturnHome.setOnClickListener(this);
        btnFloat.setOnClickListener(this);
        btnFloat2.setOnClickListener(this);
        btnFloat3.setOnClickListener(this);
        btnFloat4.setOnClickListener(this);
        btnFloat5.setOnClickListener(this);



        TextView level1_txt,level2_txt,level3_txt,level4_txt,level5_txt;
        level1_txt = findViewById(R.id.tvLevel1);
        level2_txt = findViewById(R.id.tvLevel2);
        level3_txt = findViewById(R.id.tvLevel3);
        level4_txt = findViewById(R.id.tvLevel4);
        level5_txt = findViewById(R.id.tvLevel5);


        startOneFloatingAnimation(btnFloat, btnFloat3, btnFloat5,level1_txt,level3_txt,level5_txt);
        startTwoFloatingAnimation(btnFloat2,btnFloat4,level2_txt,level4_txt);


    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnLevel1){
            Intent goToLevel1 = new Intent(LevelsActivity.this, LevelOneActivity.class);
            startActivity(goToLevel1);
        }

        if(v.getId() == R.id.btnLevel2){
            Intent goToLevel2 = new Intent(LevelsActivity.this, LevelTwoActivity.class);
            startActivity(goToLevel2);

        }
        if(v.getId() == R.id.btnLevel3){
            Intent goToLevel3 = new Intent(LevelsActivity.this, LevelThreeActivity.class);
            startActivity(goToLevel3);

        }
        if(v.getId() == R.id.btnLevel4){
            Intent goToLevel4 = new Intent(LevelsActivity.this, LevelFourActivity.class);
            startActivity(goToLevel4);

        }
        if(v.getId() == R.id.btnLevel5){
            Intent goToLevel5 = new Intent(LevelsActivity.this, LevelFiveActivity.class);
            startActivity(goToLevel5);

        }
        if(v.getId() == R.id.btnGoHome){
            Intent ReturnHome = new Intent(LevelsActivity.this, Home_page.class);
            startActivity(ReturnHome);

        }
    }
    private void startOneFloatingAnimation(View... buttons) {
        for (View btn : buttons) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(btn, "translationY", 0f, -30f);
            animator.setDuration(1000);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.start();
        }
    }
    private void startTwoFloatingAnimation(View... buttons) {
        for (View btn : buttons) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(btn, "translationY", 0f, -30f);
            animator.setDuration(2000);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.start();
        }
    }
}
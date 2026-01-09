package com.example.bagrot_work.screens;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bagrot_work.R;

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

        startFloatingAnimation(btnFloat, btnFloat2, btnFloat3, btnFloat4, btnFloat5);



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
        if(v.getId() == R.id.btnGoHome){
            Intent ReturnHome = new Intent(LevelsActivity.this, Home_page.class);
            startActivity(ReturnHome);

        }
    }
    private void startFloatingAnimation(View... buttons) {
        for (View btn : buttons) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(btn, "translationY", 0f, -30f);
            animator.setDuration(1000);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.start();
        }
    }
}
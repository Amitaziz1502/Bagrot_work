package com.example.bagrot_work.screens;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static java.security.AccessController.getContext;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bagrot_work.R;
import com.example.bagrot_work.models.User;
import com.example.bagrot_work.utils.SharedPreferencesUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LevelsActivity extends BaseActivity implements View.OnClickListener {

    ImageButton btnFloat, btnFloat2, btnFloat3, btnFloat4, btnFloat5,btnGoHome;
    TextView level1_txt, level2_txt, level3_txt, level4_txt, level5_txt;
    int currentLvl=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);
        btnFloat  = findViewById(R.id.btnLevel1);
        btnFloat2 = findViewById(R.id.btnLevel2);
        btnFloat3 = findViewById(R.id.btnLevel3);
        btnFloat4 = findViewById(R.id.btnLevel4);
        btnFloat5 = findViewById(R.id.btnLevel5);
        btnGoHome = findViewById(R.id.btnGoHome);

        level1_txt = findViewById(R.id.tvLevel1);
        level2_txt = findViewById(R.id.tvLevel2);
        level3_txt = findViewById(R.id.tvLevel3);
        level4_txt = findViewById(R.id.tvLevel4);
        level5_txt = findViewById(R.id.tvLevel5);

        btnFloat.setOnClickListener(this);
        btnFloat2.setOnClickListener(this);
        btnFloat3.setOnClickListener(this);
        btnFloat4.setOnClickListener(this);
        btnFloat5.setOnClickListener(this);
        btnGoHome.setOnClickListener(this);


        User currentUser = SharedPreferencesUtil.getUser(this);

        if (currentUser != null) {
            currentLvl = currentUser.getCurrentlevel();
        } else {
            currentLvl = 1;
        }
        setupLevelButtons(currentLvl);

        //animations will only apply to unlocked buttons
        startOneFloatingAnimation(btnFloat, btnFloat3, btnFloat5, level1_txt, level3_txt, level5_txt);
        startTwoFloatingAnimation(btnFloat2, btnFloat4, level2_txt, level4_txt);
    }

    @Override
    public void onClick(View v) {
        if (!v.isEnabled()) {
            Toast.makeText(this, "bro finish the level before you enter the next one", Toast.LENGTH_SHORT).show();
            return;
        }

        if (v.getId() == R.id.btnLevel1) {
            startActivity(new Intent(this, LevelOneActivity.class));
        }
        else if (v.getId() == R.id.btnLevel2) {
            startActivity(new Intent(this, LevelTwoActivity.class));
        }
        else if (v.getId() == R.id.btnLevel3) {
            startActivity(new Intent(this, LevelThreeActivity.class));
        }
        else if (v.getId() == R.id.btnLevel4) {
            startActivity(new Intent(this, LevelFourActivity.class));
        }
        else if (v.getId() == R.id.btnLevel5) {
            startActivity(new Intent(this, LevelFiveActivity.class));
        }
        else if (v.getId() == R.id.btnGoHome) {
            startActivity(new Intent(this, Home_page.class));
        }
    }

    //levels logic
    private void setupLevelButtons(int currentLvl) {
        btnFloat.setEnabled(true);

        checkLevel(btnFloat2, level2_txt, 2, currentLvl);
        checkLevel(btnFloat3, level3_txt, 3, currentLvl);
        checkLevel(btnFloat4, level4_txt, 4, currentLvl);
        checkLevel(btnFloat5, level5_txt, 5, currentLvl);
    }
    private void checkLevel(ImageButton btn, TextView txt, int levelNum, int userLevel) {
        if (userLevel < levelNum) {
            btn.setEnabled(false);
            btn.setImageResource(R.drawable.lock);
            txt.setText(""); //
        } else {
            btn.setEnabled(true);

        }
    }

    //animations
    private void startOneFloatingAnimation(View... views) {
        for (View v : views) {
            if (!v.isEnabled()) continue;

            ObjectAnimator animator =
                    ObjectAnimator.ofFloat(v, "translationY", 0f, -30f);
            animator.setDuration(1000);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.start();
        }
    }

    private void startTwoFloatingAnimation(View... views) {
        for (View v : views) {
            if (!v.isEnabled()) continue;

            ObjectAnimator animator =
                    ObjectAnimator.ofFloat(v, "translationY", 0f, -30f);
            animator.setDuration(2000);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.start();
        }
    }
}

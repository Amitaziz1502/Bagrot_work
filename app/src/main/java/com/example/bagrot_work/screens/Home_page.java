package com.example.bagrot_work.screens;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bagrot_work.R;
import com.example.bagrot_work.models.User;
import com.example.bagrot_work.utils.SharedPreferencesUtil;

public class Home_page extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        User user = SharedPreferencesUtil.getUser(Home_page.this);

        ImageButton floatingButton = findViewById(R.id.floatingButton);
        ObjectAnimator animator = ObjectAnimator.ofFloat(floatingButton, "translationY", 0f, -30f);
        animator.setDuration(1000);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();

        ImageButton signOutBtn = findViewById(R.id.sign_out);
        signOutBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_edit_user){
            Intent intent = new Intent(Home_page.this, Register.class);
            startActivity(register_link);

        }
        if (v.getId() == R.id.sign_out) {
            signOut();
        }
    }

    private void signOut() {
        Log.d(TAG, "Sign out button clicked");
        SharedPreferencesUtil.signOutUser(Home_page.this);

        Log.d(TAG, "User signed out, redirecting to MainActivity");
        Intent landingIntent = new Intent(Home_page.this, MainActivity.class);
        landingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(landingIntent);
    }
}
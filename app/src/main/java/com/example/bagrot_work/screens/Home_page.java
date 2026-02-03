package com.example.bagrot_work.screens;

import static android.content.ContentValues.TAG;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bagrot_work.R;
import com.example.bagrot_work.models.User;
import com.example.bagrot_work.services.DatabaseService;
import com.example.bagrot_work.utils.SharedPreferencesUtil;
import com.example.bagrot_work.utils.Validator;

public class Home_page extends BaseActivity implements View.OnClickListener {
    private TextView tvUserDisplayName;
    private ImageButton btnAdmin,btnStore;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            ;
            return insets;
        });

        user = SharedPreferencesUtil.getUser(Home_page.this);

        databaseService.getUser(user.getId(), new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User updatedUser) {
                user = updatedUser;
                SharedPreferencesUtil.saveUser(Home_page.this, updatedUser);
                fullNameDisplay();
                hideAdminButton();
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
        btnAdmin = findViewById(R.id.btnAdminPage);
        btnStore = findViewById(R.id.btn_store);
        btnAdmin.setOnClickListener(this);
        btnStore.setOnClickListener(this);


        ImageButton btnToLevelMap = findViewById(R.id.btnToLevelMap);
        ObjectAnimator animator = ObjectAnimator.ofFloat(btnToLevelMap, "translationY", 0f, -30f);
        animator.setDuration(1000);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();

        ImageButton btnEditUser= findViewById(R.id.btn_edit_user);
        ImageButton signOutBtn = findViewById(R.id.sign_out);
        btnToLevelMap = findViewById(R.id.btnToLevelMap);
        signOutBtn.setOnClickListener(this);
        btnEditUser.setOnClickListener(this);
        btnToLevelMap.setOnClickListener(this);
        tvUserDisplayName = findViewById(R.id.tvUserDisplayName);
        btnAdmin = findViewById(R.id.btnAdminPage);
        fullNameDisplay();
        hideAdminButton();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_edit_user){
            Intent intent = new Intent(Home_page.this, EditUser.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.sign_out) {
            signOut();
        }
        if (v.getId() == R.id.btnToLevelMap){
            Intent goToLevelMap = new Intent(Home_page.this, LevelsActivity.class);
            startActivity(goToLevelMap);
        }
        if (v.getId() == R.id.btnAdminPage){
            Intent goToAdmin = new Intent(Home_page.this, AdminActivity.class);
            startActivity(goToAdmin);
        }
        if (v.getId() == R.id.btn_store){
            Intent goToStore = new Intent(Home_page.this, AbilityStore.class);
            startActivity(goToStore);
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
    private void fullNameDisplay(){
        String fullName = "Hello " + user.getFirstname();
        tvUserDisplayName.setText(fullName);

    }
    private void hideAdminButton(){
        Log.d(TAG, "User admin status: " + user.isAdmin());
        Log.d(TAG, "Button visibility before: " + btnAdmin.getVisibility());

        if(!user.isAdmin()){
            btnAdmin.setVisibility(View.GONE);
        } else {
            btnAdmin.setVisibility(View.VISIBLE); // Add this!
        }

        Log.d(TAG, "Button visibility after: " + btnAdmin.getVisibility());

    }
}
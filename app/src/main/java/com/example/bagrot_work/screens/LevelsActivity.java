package com.example.bagrot_work.screens;

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
        ImageButton btnFloat6 = findViewById(R.id.btnLevel6);
        ImageButton btnFloat7 = findViewById(R.id.btnLevel7);
        ImageButton btnFloat8 = findViewById(R.id.btnLevel8);
        ImageButton btnFloat9 = findViewById(R.id.btnLevel9);
        ImageButton btnFloat10 = findViewById(R.id.btnLevel10);
        ImageButton btnReturnHome = findViewById(R.id.btnGoHome);
        btnReturnHome.setOnClickListener(this);
        btnFloat.setOnClickListener(this);
        btnFloat2.setOnClickListener(this);


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
        if(v.getId() == R.id.btnGoHome){
            Intent ReturnHome = new Intent(LevelsActivity.this, Home_page.class);
            startActivity(ReturnHome);

        }
    }
}
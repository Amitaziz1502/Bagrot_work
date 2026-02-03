package com.example.bagrot_work.screens;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bagrot_work.R;
import com.example.bagrot_work.adapters.AbilityAdapter;
import com.example.bagrot_work.models.Abilities;

import java.util.Arrays;
import java.util.List;

public class AbilityStore extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ability_store);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recyclerView = findViewById(R.id.ability_Recycle);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        List<Abilities> abilities = Arrays.asList(
                Abilities.fastRun,
                Abilities.doubleJump,
                Abilities.defaultOp
        );

        AbilityAdapter adapter = new AbilityAdapter(this, abilities, ability -> {
            Toast.makeText(this, "Selected: " + ability.name, Toast.LENGTH_SHORT).show();
        });

        recyclerView.setAdapter(adapter);
    }
}



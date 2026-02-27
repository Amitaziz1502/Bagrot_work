package com.example.bagrot_work.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bagrot_work.R;
import com.example.bagrot_work.adapters.AbilityAdapter;
import com.example.bagrot_work.models.Abilities;
import com.example.bagrot_work.models.GameLevel;
import com.example.bagrot_work.models.User;
import com.example.bagrot_work.services.DatabaseService;
import com.example.bagrot_work.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

public class AbilityStore extends BaseActivity implements View.OnClickListener {
    ImageButton exit;
    User user;
    AbilityAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ability_store);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        user = SharedPreferencesUtil.getUser(AbilityStore.this);
        RecyclerView recyclerView = findViewById(R.id.ability_Recycle);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        exit = findViewById(R.id.btn_go_home);
        exit.setOnClickListener(this);

        List<Abilities> abilities = Arrays.asList(
                Abilities.fastRun,
                Abilities.doubleJump,
                Abilities.defaultOp
        );

         adapter = new AbilityAdapter(this, abilities, new AbilityAdapter.OnAbilityClickListener() {
            @Override
            public void onAbilityClick(Abilities ability) {
                if (!user.isInAbilityList(ability))
                    buyAbility(ability);
                else {
                    changeAppearance(ability);
                }

            }
        }, user);

        recyclerView.setAdapter(adapter);
    }

    private void changeAppearance(Abilities ability) {
        DatabaseService.getInstance().updateUser(user.getId(), new UnaryOperator<User>() {
            @Override
            public User apply(User user) {
                if (user != null) {
                    user.setAppearance(ability);
                }
                return user;
            }
        }, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void v) {
                user.setAppearance(ability);
                SharedPreferencesUtil.saveUser(AbilityStore.this, user);
                Toast.makeText(AbilityStore.this, "Success!!!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }

    private void buyAbility(Abilities ability) {
        if (ability.price > user.getWalletBalance()) {
            Toast.makeText(AbilityStore.this, "Not enough coins!", Toast.LENGTH_SHORT).show();
            return;
        }

        UnaryOperator<User> updateLogic = currentUser -> {
            if (currentUser != null) {
                currentUser.setWalletBalance(currentUser.getWalletBalance() - ability.price);
                currentUser.getCollectedAbilities().add(ability);
            }
            return currentUser;
        };
        DatabaseService.getInstance().updateUser(user.getId(), updateLogic, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                user.setWalletBalance(user.getWalletBalance() - ability.price);
                user.getCollectedAbilities().add(ability);
                SharedPreferencesUtil.saveUser(AbilityStore.this, user);
                adapter.notifyDataSetChanged();
                Toast.makeText(AbilityStore.this, "Purchase successful!", Toast.LENGTH_SHORT).show();
                changeAppearance(ability);

            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(AbilityStore.this, "Purchase failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_go_home) {
            Intent GoHome = new Intent(AbilityStore.this, Home_page.class);
            startActivity(GoHome);
        }

    }
}



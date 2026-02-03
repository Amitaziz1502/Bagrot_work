package com.example.bagrot_work.screens;

import android.app.SharedElementCallback;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bagrot_work.R;
import com.example.bagrot_work.adapters.UserAdapter;
import com.example.bagrot_work.models.User;
import com.example.bagrot_work.services.DatabaseService;
import com.example.bagrot_work.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class AdminActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private UserAdapter adapter;
    ImageButton btnGoHome;
    TextView adminTAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        btnGoHome = findViewById(R.id.btn_go_home);
        btnGoHome.setOnClickListener(this);
        adminTAG = findViewById(R.id.main_tag);
        recyclerView = findViewById(R.id.admin_recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UserAdapter(new UserAdapter.OnUserClickListener() {

            @Override
            public void onUserClick(User user) {
            }

            @Override
            public void onLongUserClick(User user) {
            }

            @Override
            public void onTrashClick(User user) {
                if (user.isMainAdmin()) {
                    Toast.makeText(AdminActivity.this, "u cant delete the main admin", Toast.LENGTH_SHORT).show();
                    return;
                }

                deleteUserFromDatabase(user);
                adapter.removeUser(user);
            }

            @Override
            public void onManagerClick(User user) {
                if (user.isMainAdmin()) {
                    Toast.makeText(AdminActivity.this, "u cant change the access mode of an admin", Toast.LENGTH_SHORT).show();
                    return;
                }

                setUserAdmin(user, !user.isAdmin());
            }


        });
        recyclerView.setAdapter(adapter);
        loadUsers();
    }

    private void deleteUserFromDatabase(User user) {

        DatabaseService.getInstance().deleteUser(user.getId(), new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void v) {
                Log.d("trash", "Succeed");
            }

            @Override
            public void onFailed(Exception e) {
                Log.d("trash", "Failed");


            }
        });
    }

    private void setUserAdmin(User user, boolean flag) {

        DatabaseService.getInstance().updateUser(user.getId(), new UnaryOperator<User>() {
                    @Override
                    public User apply(User u) {
                        if (u == null) return null;
                        u.setAdmin(flag);
                        return u;
                    }
                }, new DatabaseService.DatabaseCallback<Void>() {
                    @Override
                    public void onCompleted(Void object) {
                        Log.d("ADMIN", "updated");
                        user.setAdmin(flag);
                        adapter.updateUser(user);
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Log.e("ADMIN", "failed", e);
                    }
                }
        );
    }
    private void loadUsers() {
        DatabaseService.getInstance().getUserList(
                new DatabaseService.DatabaseCallback<List<User>>() {
                    @Override
                    public void onCompleted(List<User> users) {
                        adapter.setUserList(users);
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Log.e("ADMIN", "load users failed", e);
                    }
                }
        );
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_go_home){
            Intent GoHome = new Intent(AdminActivity.this, Home_page.class);
            startActivity(GoHome);
        }
    }

}




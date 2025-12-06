package com.example.bagrot_work.screens;
/*
import static com.google.firebase.database.core.operation.OperationSource.Source.User;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bagrot_work.R;
import com.example.bagrot_work.adapters.UserAdapter;
import com.example.bagrot_work.models.User;
import com.example.bagrot_work.services.DatabaseService;

import java.util.List;

public class AdminActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "UsersListActivity";
    private UserAdapter userAdapter;
    private TextView tvUserCount;
    private RecyclerView usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usersList = findViewById(R.id.usersList);
        tvUserCount = findViewById(R.id.tvUserCount);
        usersList.setLayoutManager(new LinearLayoutManager(this));


        userAdapter = new UserAdapter(new UserAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(User user) {
                Log.d(TAG, "User clicked: " + user);
                Intent intent = new Intent(AdminActivity.this, UserProfileActivity.class); // שינוי שם המחלקה
                intent.putExtra("USER_UID", user.getId());
                startActivity(intent);
            }

            @Override
            public void onLongUserClick(User user) {
                Log.d(TAG, "User long clicked: " + user);
            }
        });
        usersList.setAdapter(userAdapter);


    }
    @Override
    protected void onResume() {
        super.onResume();
         databaseService.getUserList(new DatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(List<User> users) {
                userAdapter.setUserList(users);
                tvUserCount.setText("Total users: " + users.size());
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to get users list", e);
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
*/


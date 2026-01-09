package com.example.bagrot_work.screens;

import static android.content.ContentValues.TAG;
import static android.opengl.ETC1.isValid;

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
import com.google.android.material.textfield.TextInputLayout;

public class EditUser extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "EditUser";

    private TextInputLayout etUserFirstName, etUserLastName, etUsername, etUserPassword;
    private ImageButton btnUpdateProfile, btnReturnHome;
    private String selectedUid;
    private User selectedUser;
    private boolean isCurrentUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        // -------------------------
        // Edge-to-Edge setup (system bars)
        // -------------------------
        View root = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // -------------------------
        // Get current user and selected user UID
        // -------------------------
        selectedUid = getIntent().getStringExtra("USER_UID");
        User currentUser = SharedPreferencesUtil.getUser(this);
        if (currentUser == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (selectedUid == null) {
            selectedUid = currentUser.getId();
        }

        isCurrentUser = selectedUid.equals(currentUser.getId());

        // Check if user is authorized
        if (!currentUser.isAdmin() && !isCurrentUser) {
            Toast.makeText(this, "You are not authorized to view this profile", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Selected user UID: " + selectedUid);

        // -------------------------
        // Find views
        // -------------------------
        etUserFirstName = findViewById(R.id.editFirstNameLayout);
        etUserLastName = findViewById(R.id.editLastNameLayout);
        etUsername = findViewById(R.id.editUsername_input2);
        etUserPassword = findViewById(R.id.editPassword_input_layout2);
        btnUpdateProfile = findViewById(R.id.btnUpdate);
        btnReturnHome = findViewById(R.id.btnReturnHome);

        btnUpdateProfile.setOnClickListener(this);
        btnReturnHome.setOnClickListener(this);

        // Hide return home button if viewing another user
        if (!isCurrentUser) {
            btnReturnHome.setVisibility(View.GONE);
        }

        // Load user profile from database
        showUserProfile();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnUpdate) {
            updateUserProfile();
        } else if (id == R.id.btnReturnHome) {
            startActivity(new Intent(EditUser.this, Home_page.class));
        }
    }

    private void showUserProfile() {
        databaseService.getUser(selectedUid, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                selectedUser = user;
                if (user != null) {
                    etUserFirstName.getEditText().setText(user.getFirstname());
                    etUserLastName.getEditText().setText(user.getLastname());
                    etUsername.getEditText().setText(user.getUsername());
                    etUserPassword.getEditText().setText(user.getPassword());
                }
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Error getting user profile", e);
                Toast.makeText(EditUser.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserProfile() {
        if (selectedUser == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String firstName = etUserFirstName.getEditText().getText().toString();
        String lastName = etUserLastName.getEditText().getText().toString();
        String username = etUsername.getEditText().getText().toString();
        String password = etUserPassword.getEditText().getText().toString();

        if (!isValid(firstName, lastName, username, password)) {
            return;
        }

        // Update user object
        selectedUser.setFirstname(firstName);
        selectedUser.setLastname(lastName);
        selectedUser.setUsername(username);
        selectedUser.setPassword(password);

        // Only allow current user to update their profile, or admins
        if (!isCurrentUser && !selectedUser.isAdmin()) {
            Toast.makeText(this, "You can only update your own profile", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update in database
        updateUserInDatabase(selectedUser);
    }

    private void updateUserInDatabase(User user) {
        databaseService.updateUser(user, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void result) {
                Toast.makeText(EditUser.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                showUserProfile();
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Error updating user profile", e);
                Toast.makeText(EditUser.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValid(String firstName, String lastName, String username, String password) {
        if (!Validator.islNameValid(firstName)) {
            etUserFirstName.setError("First name is required");
            etUserFirstName.requestFocus();
            return false;
        }
        if (!Validator.isfNameValid(lastName)) {
            etUserLastName.setError("Last name is required");
            etUserLastName.requestFocus();
            return false;
        }
        if (!Validator.isUsernameValid(username)) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return false;
        }
        if (!Validator.isPasswordValid(password)) {
            etUserPassword.setError("Password is required");
            etUserPassword.requestFocus();
            return false;
        }
        return true;
    }
}

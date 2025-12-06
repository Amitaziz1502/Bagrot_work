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
    private TextInputLayout etUserFirstName, etUserLastName, etUsername, etUserPassword;
    private ImageButton btnUpdateProfile, btnReturnHome;
    String selectedUid;
    User selectedUser;
    boolean isCurrentUser = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        selectedUid = getIntent().getStringExtra("USER_UID");
        User currentUser = SharedPreferencesUtil.getUser(this);
        assert currentUser != null;

        if (selectedUid == null) {
            selectedUid = currentUser.getId();
        }
        isCurrentUser = selectedUid.equals(currentUser.getId());
        if (!currentUser.isAdmin()) {
            // If the user is not an admin and the selected user is not the current user
            // then finish the activity
            Toast.makeText(this, "You are not authorized to view this profile", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Selected user: " + selectedUid);

        etUserFirstName = findViewById(R.id.editFirstNameLayout);
        etUserLastName = findViewById(R.id.editLastNameLayout);
        etUsername = findViewById(R.id.editUsername_input2);
        etUserPassword = findViewById(R.id.editPassword_input_layout2);
        btnUpdateProfile = findViewById(R.id.btnUpdate);
        btnReturnHome = findViewById(R.id.btnReturnHome);
        btnUpdateProfile.setOnClickListener(this);
        btnReturnHome.setOnClickListener(this);

        // if the user is not the current user, hide the sign out button
        if (!isCurrentUser) {
            btnReturnHome.setVisibility(View.GONE);
        }

        showUserProfile();
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnUpdate) {
            updateUserProfile();
            return;
        }
        if(v.getId() == R.id.btnReturnHome) {
            Intent btnReturnHome = new Intent(EditUser.this, Home_page.class);
            startActivity(btnReturnHome);
        }
    }
    private void showUserProfile() {
        // Get the user data from database
        databaseService.getUser(selectedUid, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                selectedUser = user;
                // Set the user data to the EditText fields
                etUserFirstName.getEditText().setText(user.getFirstname());
                etUserLastName.getEditText().setText(user.getLastname());
                etUsername.getEditText().setText(user.getUsername());
                etUserPassword.getEditText().setText(user.getPassword());

            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Error getting user profile", e);
            }
        });

    }
    private void updateUserProfile() {
        if (selectedUser == null) {
            Log.e(TAG, "User not found");
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            return;
        }
        // Get the updated user data from the EditText fields
        String firstName = etUserFirstName.getEditText().getText().toString();
        String lastName = etUserLastName.getEditText().getText().toString();
        String username = etUsername.getEditText().getText().toString();
        String password = etUserPassword.getEditText().getText().toString();

        if (!isValid(firstName, lastName, username, password)) {
            Log.e(TAG, "Invalid input");
            return;
        }

        // Update the user object
        selectedUser.setFirstname(firstName);
        selectedUser.setLastname(lastName);
        selectedUser.setUsername(username);
        selectedUser.setPassword(password);

        // Update the user data in the authentication
        Log.d(TAG, "Updating user profile");
        Log.d(TAG, "Selected user UID: " + selectedUser.getId());
        Log.d(TAG, "Is current user: " + isCurrentUser);
        Log.d(TAG, "User username: " + selectedUser.getUsername());
        Log.d(TAG, "User password: " + selectedUser.getPassword());



        if (!isCurrentUser && !selectedUser.isAdmin()) {
            Log.e(TAG, "Only the current user can update their profile");
            Toast.makeText(this, "You can only update your own profile", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (isCurrentUser) {
            updateUserInDatabase(selectedUser);
        }
        else if (selectedUser.isAdmin()) {
            // update the user in the database
            updateUserInDatabase(selectedUser);
        }
    }
    private void updateUserInDatabase(User user) {
        Log.d(TAG, "Updating user in database: " + user.getId());
        databaseService.updateUser(user, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void result) {
                Log.d(TAG, "User profile updated successfully");
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

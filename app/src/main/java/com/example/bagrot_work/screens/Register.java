package com.example.bagrot_work.screens;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bagrot_work.R;
import com.example.bagrot_work.models.User;
import com.example.bagrot_work.services.DatabaseService;
import com.example.bagrot_work.utils.SharedPreferencesUtil;
import com.example.bagrot_work.utils.Validator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.annotations.NotNull;

import java.util.Objects;

public class Register extends BaseActivity implements View.OnClickListener {
    private TextInputLayout etfName, etlName,etPassword,etUsername;
    private ImageButton enter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        etfName = findViewById(R.id.firstNameLayout);
        etlName = findViewById(R.id.lastNameLayout);
        etUsername = findViewById(R.id.username_input2);
        etPassword = findViewById(R.id.password_input_layout2);
        enter = findViewById(R.id.enterBtn);
        enter.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == enter.getId()){
            Log.d(TAG, "onClick: Register button clicked");
            String fName = Objects.requireNonNull(etfName.getEditText()).getText().toString();
            String lName = Objects.requireNonNull(etlName.getEditText()).getText().toString();
            String Username = Objects.requireNonNull(etUsername.getEditText()).getText().toString();
            String Password = Objects.requireNonNull(etPassword.getEditText()).getText().toString();

            Log.d(TAG, "onClick: First name:: " + fName);
            Log.d(TAG, "onClick: Last name: " + lName);
            Log.d(TAG, "onClick: Username: " + Username);
            Log.d(TAG, "onClick: Password: " + Password);

            Log.d(TAG, "onClick: Validating input...");
            if (!checkInput(fName, lName, Username, Password)) {
                return;
            }
            Log.d(TAG, "onClick: Registering user...");
            registerUser(fName, lName, Username, Password);

        }
    }
    private boolean checkInput(String fName, String lName, String Username, String Password) {

        if (!Validator.isfNameValid(fName)) {
            Log.e(TAG, "checkInput: your first name has to be at least 2 characters long");
            etfName.setError("your first name has to be at least 2 characters long");
            etfName.requestFocus();
            return false;
        }

        if (!Validator.islNameValid(lName)) {
            Log.e(TAG, "checkInput: your last name has to be at least 2 characters long");
            etlName.setError("your last name has to be at least 2 characters long");
            etlName.requestFocus();
            return false;
        }

        if (!Validator.isPasswordValid(Password)) {
            Log.e(TAG, "checkInput: Password must be at least 6 characters long");
            etPassword.setError("Password must be at least 6 characters long");
            etPassword.requestFocus();
            return false;
        }

        if (!Validator.isUsernameValid(Username)) {
            Log.e(TAG, "checkInput: Username have to be at least 3 characters long");
            etUsername.setError("Username have to be at least 3 characters long");
            etUsername.requestFocus();
            return false;
        }


        Log.d(TAG, "checkInput: Input is valid");
        return true;
    }
    private void registerUser(String fName, String lName, String Username, String Password) {
        DatabaseService databaseService = DatabaseService.getInstance();
        Log.d(TAG, "registerUser: Registering user...");
        String uid = databaseService.generateUserId();
        User user = new User(uid, fName, lName, Username,Password, 0);
        databaseService.checkIfUsernameExists(user.getUsername(), new DatabaseService.DatabaseCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean exist) {
                if (exist) {
                    Toast.makeText(Register.this, "שלום, זאת הודעה!", Toast.LENGTH_SHORT).show();
                } else {
                    addUserToDB(databaseService, user);
                }
            }

            @Override
            public void onFailed(Exception e) {

                Log.e(TAG, "createUserInDatabase: Failed to create user", e);
                /// show error message to user
                Toast.makeText(Register.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                /// sign out the user if failed to register
                SharedPreferencesUtil.signOutUser(Register.this);
            }
        });


    }

    private void addUserToDB(DatabaseService databaseService, User user) {
        databaseService.createNewUser(user, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void v) {
                Log.d(TAG, "createUserInDatabase: User created successfully");
                /// save the user to shared preferences
                SharedPreferencesUtil.saveUser(Register.this, user);
                Log.d(TAG, "createUserInDatabase: Redirecting to MainActivity");
                /// Redirect to MainActivity and clear back stack to prevent user from going back to register screen
                Intent mainIntent = new Intent(Register.this, MainActivity.class);
                /// clear the back stack (clear history) and start the MainActivity
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "createUserInDatabase: Failed to create user", e);
                /// show error message to user
                Toast.makeText(Register.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                /// sign out the user if failed to register
                SharedPreferencesUtil.signOutUser(Register.this);

            }
        });
    }
}
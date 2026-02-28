package com.example.bagrot_work.screens;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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

public class MainActivity extends BaseActivity implements View.OnClickListener{
    private TextInputLayout usernameLayout, passwordLayout;
    private ImageButton btnLogin;
    private static final String TAG = "MainActivity";
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.start_game_id), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        for (int i = 1; i <= 4; i++) {
            DatabaseService.getInstance().CreateNewLevel(i, 750);
        }
        DatabaseService.getInstance().CreateNewLevel(5, 650);


        if (SharedPreferencesUtil.isUserLoggedIn(MainActivity.this)) {

            Intent intent = new Intent(MainActivity.this, Home_page.class);
            startActivity(intent);
            finish();
            return;
        }



        TextView register = findViewById(R.id.register_text);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register_link = new Intent(MainActivity.this, Register.class);
                startActivity(register_link);
            }

        });
        usernameLayout = findViewById(R.id.username_input);
        passwordLayout = findViewById(R.id.password_input_layout);
        btnLogin = findViewById(R.id.enter_icon);
        btnLogin.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == btnLogin.getId()) {
            Log.d(TAG, "onClick: Login button clicked");

            /// get the Username and password entered by the user
            String Username = usernameLayout.getEditText().getText().toString();
            String password = passwordLayout.getEditText().getText().toString();

            /// log the Username and password
            Log.d(TAG, "onClick: Username: " + Username);
            Log.d(TAG, "onClick: Password: " + password);

            Log.d(TAG, "onClick: Validating input...");
            /// Validate input
            if (!checkInput(Username, password)) {
                /// stop if input is invalid
                return;
            }

            Log.d(TAG, "onClick: Logging in user...");

            /// Login user
            loginUser(Username, password);
        } else if (v.getId() == btnLogin.getId()) {
            /// Navigate to Register Activity
            Intent registerIntent = new Intent(MainActivity.this, Register.class);
            startActivity(registerIntent);
        }
    }
    private void loginUser(String Username, String password) {
        databaseService.getUserByUsernameAndPassword(Username, password, new DatabaseService.DatabaseCallback<User>() {
            /// Callback method called when the operation is completed
            ///
            /// @param user the user object that is logged in
            @Override
            public void onCompleted(User user) {
                if (user == null) {
                    passwordLayout.setError("Invalid Password");
                    usernameLayout.setError("Invalid Username");
                    return;
                }
                Log.d(TAG, "onCompleted: User logged in: " + user.toString());
                /// save the user data to shared preferences
                SharedPreferencesUtil.saveUser(MainActivity.this, user);
                /// Redirect to main activity and clear back stack to prevent user from going back to login screen
                Intent mainIntent = new Intent(MainActivity.this, Home_page.class);
                /// Clear the back stack (clear history) and start the MainActivity
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "onFailed: Failed to retrieve user data", e);
                /// Show error message to user
                passwordLayout.setError("Invalid Username or password");
                passwordLayout.requestFocus();
                /// Sign out the user if failed to retrieve user data
                /// This is to prevent the user from being logged in again
            }
        });
    }
    private boolean checkInput(String Username, String Password) {

        if (!Validator.isUsernameValid(Username)) {
            Log.e(TAG, "checkInput: Username have to be at least 3 characters long");
            usernameLayout.setError("Username have to be at least 3 characters long");
            usernameLayout.requestFocus();
            return false;
        }


        if (!Validator.isPasswordValid(Password)) {
            Log.e(TAG, "checkInput: Password must be at least 6 characters long");
            passwordLayout.setError("Password must be at least 6 characters long");
            passwordLayout.requestFocus();
            return false;
        }


        Log.d(TAG, "checkInput: Input is valid");
        return true;
    }
}
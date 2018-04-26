package com.se_project.movie_db;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private EditText NameText;
    private EditText EmailText;
    private EditText PasswordText;
    private EditText ConfirmPasswordText;
    private Button CreateAccount;
    private Button SignIn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null) {
            startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        NameText = findViewById(R.id.nameEditText);
        EmailText = findViewById(R.id.emailEditText);
        PasswordText = findViewById(R.id.passwordEditText);
        ConfirmPasswordText = findViewById(R.id.confirmPasswordEditText);

        CreateAccount = findViewById(R.id.sign_up_activ);
        SignIn = findViewById(R.id.sign_in_activ);

        CreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name = NameText.getText().toString();
                String Email = EmailText.getText().toString();
                String Password = PasswordText.getText().toString();
                String confirmPassword = ConfirmPasswordText.getText().toString();

                if(Password.length() < 8) {
                    Toast.makeText(SignUpActivity.this, "Password too short", Toast.LENGTH_LONG).show();
                }else {
                    if(confirmPassword.equals(Password)) {
                        setCreateAccount(Email, Password, Name);
                    }else {
                        Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
                finish();
            }
        });
    }

    private void setCreateAccount(String email, String password, final String name) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            String user_id = mAuth.getCurrentUser().getUid();
                            startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
                            finish();
                        }else {
                            Toast.makeText(SignUpActivity.this, "Could not create Account", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}

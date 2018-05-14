package com.ardasatata.testmap_pedagangapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends Activity {

    private ImageView buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignup;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();


        if (firebaseAuth.getCurrentUser() != null) {

            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            finish();
        }

        editTextEmail = (EditText) findViewById(R.id.loginEmail);
        editTextPassword = (EditText) findViewById(R.id.loginPassword);
        buttonSignIn = (ImageView) findViewById(R.id.loginSignIn);
        textViewSignup = (TextView) findViewById(R.id.LoginSignUp);

        progressDialog = new ProgressDialog(this);

        //attaching click listener
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();
                if(TextUtils.isEmpty(email)){

                    Toast.makeText(LoginActivity.this,"Please enter email", Toast.LENGTH_LONG).show();

                }
                else if(TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this,"Please enter password",Toast.LENGTH_LONG).show();

                }
                else if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                }
                else {


                    userLogin(email,password);
                }

            }
        });

        textViewSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                finish();
            }
        });

    }

    private void userLogin(String email, String password) {

        progressDialog.setMessage("Loging in Please Wait...");
        progressDialog.show();

        //logging in the user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        //if the task is successfull
                        if (task.isSuccessful()) {
                            //start the profile activity
                            finish();
                            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                        }
                    }
                });

    }

}

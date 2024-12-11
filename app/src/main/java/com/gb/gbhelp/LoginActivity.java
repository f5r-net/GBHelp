package com.gb.gbhelp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends BaseActivity {
    Button loginBtn;
    EditText emailET,passET;
    TextView registerTV;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (GB.getSharedBool(this,GB.IS_LOGIN)){
            GB.startFeedsActivity(LoginActivity.this,FirebaseAuth.getInstance().getUid());
            finish();
        }
        setContentView(R.layout.login);
        mAuth = FirebaseAuth.getInstance();

        loginBtn = findViewById(R.id.login);
        registerTV = findViewById(R.id.register);

        emailET = findViewById(R.id.email);
        passET = findViewById(R.id.password);
        passET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                GB.passwordAlignment(LoginActivity.this,passET,s);
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        registerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            }
        });
    }

    private void loginUser(){
        String email = emailET.getText().toString();
        String password = passET.getText().toString();

        if (TextUtils.isEmpty(email)){
            emailET.setError("Email cannot be empty");
            emailET.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            passET.setError("Password cannot be empty");
            passET.requestFocus();
        }else{
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, task.getResult().getUser().getDisplayName()+" logged in successfully", Toast.LENGTH_SHORT).show();
                        GB.saveSharedString(LoginActivity.this,GB.NICKNAME,task.getResult().getUser().getDisplayName());
                        GB.saveSharedString(LoginActivity.this,GB.USER_ID,FirebaseAuth.getInstance().getUid());
                        GB.saveSharedBool(LoginActivity.this,GB.IS_LOGIN,true);
                        GB.startFeedsActivity(LoginActivity.this,FirebaseAuth.getInstance().getUid());
                        finish();
                    }else{
                        Toast.makeText(LoginActivity.this, "Log in Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        GB.saveSharedBool(LoginActivity.this,GB.IS_LOGIN,false);
                    }
                }
            });
        }
    }
    public void hideKeyboard(View view) {
        if (view == null) {
            return;
        }
        try {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && !imm.isActive()) {
                return;
            }
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            if (passET.isFocused()) {
                passET.clearFocus();
                return;
            }
            if (emailET.isFocused())
            emailET.clearFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

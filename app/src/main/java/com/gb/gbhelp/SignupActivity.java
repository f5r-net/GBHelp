package com.gb.gbhelp;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends BaseActivity {
    Button registerBtn;
    EditText nickNameET,emailET,passET;
    FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        registerBtn = findViewById(R.id.register);
        nickNameET = findViewById(R.id.nickname);
        emailET = findViewById(R.id.email);
        passET = findViewById(R.id.password);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        passET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                GB.passwordAlignment(SignupActivity.this,passET,s);
            }
        });
    }

    private void register() {
        String email = emailET.getText().toString();
        String password = passET.getText().toString();
        String nickName = nickNameET.getText().toString();

        if (TextUtils.isEmpty(nickName)){
            nickNameET.setError("");
            nickNameET.requestFocus();
        }else if (TextUtils.isEmpty(email)){
            emailET.setError("");
            emailET.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            passET.setError("");
            passET.requestFocus();
        } else{
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(nickName).build();
                        FirebaseUser firebaseUser  = FirebaseAuth.getInstance().getCurrentUser();
                        firebaseUser.updateProfile(userProfileChangeRequest);

                        UserModel userModel = new UserModel();
                        userModel.setUserId(FirebaseAuth.getInstance().getUid());
                        userModel.setUserEmail(email);
                        userModel.setNickName(nickName);
                        userModel.setUserPass(password);
                        databaseReference.child(FirebaseAuth.getInstance().getUid()).setValue(userModel);
                        GB.saveSharedString(SignupActivity.this,GB.NICKNAME,nickName);
                        GB.startFeedsActivity(SignupActivity.this,FirebaseAuth.getInstance().getUid());

                        GB.saveSharedString(SignupActivity.this,GB.USER_ID,FirebaseAuth.getInstance().getUid());
                        GB.saveSharedBool(SignupActivity.this,GB.IS_LOGIN,true);
                        Toast.makeText(SignupActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        GB.saveSharedBool(SignupActivity.this,GB.IS_LOGIN,false);
                        Toast.makeText(SignupActivity.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
            if (emailET.isFocused()) {
                emailET.clearFocus();
                return;
            }
            if (passET.isFocused()) {
                passET.clearFocus();
                return;
            }
            if (nickNameET.isFocused()) {
                nickNameET.clearFocus();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

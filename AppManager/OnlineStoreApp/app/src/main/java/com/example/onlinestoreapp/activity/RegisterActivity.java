package com.example.onlinestoreapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.onlinestoreapp.R;
import com.example.onlinestoreapp.retrofit.ApiStore;
import com.example.onlinestoreapp.retrofit.RetrofitClient;
import com.example.onlinestoreapp.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RegisterActivity extends AppCompatActivity {
    TextView email, pass, repass, mobile, username;
    AppCompatButton button;
    ApiStore apiStore;
    FirebaseAuth firebaseAuth;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        initView();
        initControl();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initControl() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dangKi();
            }
        });
    }

    private void dangKi(){
        String str_email = email.getText().toString().trim();
        String str_pass = pass.getText().toString().trim();
        String str_repass = repass.getText().toString().trim();
        String str_mobile = mobile.getText().toString().trim();
        String str_username = username.getText().toString().trim();
        if (TextUtils.isEmpty(str_email)){
            Toast.makeText(getApplicationContext(), "You haven't input email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_username)){
            Toast.makeText(getApplicationContext(), "You haven't input username", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_pass)){
            Toast.makeText(getApplicationContext(), "You haven't input password", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_repass)){
            Toast.makeText(getApplicationContext(), "You haven't input reset password", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_mobile)){
            Toast.makeText(getApplicationContext(), "You haven't input mobile phone", Toast.LENGTH_SHORT).show();
        } else {
            if (str_pass.equals(str_repass)){
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.createUserWithEmailAndPassword(str_email, str_pass)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    if (user != null){
                                        postData(str_email,  str_pass,  str_username,  str_mobile, user.getUid());
                                    }
                                }else{
                                    Toast.makeText(getApplicationContext(), "Email has existed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }else {
                Toast.makeText(getApplicationContext(), "Password do not match", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void postData(String str_email, String str_pass, String str_username, String str_mobile, String uid){
        //post data
        compositeDisposable.add(apiStore.dangKi(str_email, str_pass, str_username, str_mobile, uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if (userModel.isSuccess()){
                                Utils.user_current.setEmail(str_email);
                                Utils.user_current.setPassword(str_pass);
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void initView() {
        apiStore = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiStore.class);
        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        pass = findViewById(R.id.pass);
        repass = findViewById(R.id.repass);
        mobile = findViewById(R.id.mobile);
        button = findViewById(R.id.btndangki);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
package com.example.ui_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity{


    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 100;

    private FirebaseAuth firebaseAuth; //파이어베이스 인증
    private EditText email, pwd; //로그인 Email, Pwd 변수
    private GoogleSignInClient mGoogleSignInClient; //구글 로그인 인증
    private FirebaseFirestore databaseReference; //firestore 데이터베이스


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference = FirebaseFirestore.getInstance();
        Button btnLogin = findViewById(R.id.loginButton);
        email=findViewById(R.id.emailToLogin);
        pwd=findViewById(R.id.pwdToLogin);



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그인 요청하기
                String strEmail = email.getText().toString();
                String strPWD = pwd.getText().toString();

                firebaseAuth.signInWithEmailAndPassword(strEmail, strPWD).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //로그인이 성공했을 때, Main 화면으로 이동
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            //로그인 실패했을 때 메세지 출력
                            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        //회원가입 버튼 누르면 회원가입 창으로 이동
        Button btnSignup = findViewById(R.id.signButton);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SingUpActivity.class);
                startActivity(intent);
            }
        });


        //구글 로그인 인증 준비
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) //무시해도 돌아감
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        SignInButton btnGoogle = findViewById(R.id.googleLogin);

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });


    }

    @Override
    //구글 인증후 결과를 받아내는 함수
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("why", "Google sign in failed", e);
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        //로그인 성공시 getCurrentUser로 유저정보를 얻을 수 있음
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            //유저 정보 얻기
                            UserAccount account = new UserAccount();
                            account.setIdToken(firebaseUser.getUid());
                            account.setEmailId(firebaseUser.getEmail());



                            //유저 정보 객체 생성
                            Map<String, Object> user = new HashMap<>();
                            user.put("email", account.getEmailId());
                            user.put("UserId", account.getIdToken());
                            //파이어 스토어는 collection - document - field 구조
                            //collection 이름 지정, document 이름 지정, field 값은 set으로 삽입
                            databaseReference.collection("UserAccount")
                                    .document(account.getEmailId())
                                    .set(user)
                                    //db에 유저정보가 들어가고 회원가입이 성공했으면 메세지 출력
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(LoginActivity.this, "로그인 성공!", Toast.LENGTH_LONG).show();
                                        }
                                    });


                            //회원가입이 완료됐으면, Login 화면으로 이동
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
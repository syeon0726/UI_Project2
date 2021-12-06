package com.example.ui_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SingUpActivity extends AppCompatActivity {


    private FirebaseAuth firebaseAuth; //파이어베이스 인증
    private FirebaseFirestore databaseReference; //실시간 데이터베이스
    private EditText email, pwd; //회원가입 Email, Pwd 변수
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        firebaseAuth = FirebaseAuth.getInstance(); //파이어베이스 인증 객체
        databaseReference = FirebaseFirestore.getInstance(); //파이어스토어 객체체
        //입력필드 ID 가져오기
        email = findViewById(R.id.registerEmail);
        pwd = findViewById(R.id.registerPWD);
        btnRegister = findViewById(R.id.registerButton);

        //회원가입 시작
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //입력한 이메일, PWD를 String 변환
                String strEmail = email.getText().toString();
                String strPWD = pwd.getText().toString();

                //Firebase 인증 시작
                //입력받은 Email, PWD를 매개변수로 사용용
               firebaseAuth.createUserWithEmailAndPassword(strEmail, strPWD).addOnCompleteListener(SingUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){ //회원가입이 성공했을 때
                            //현재 입력한 회원정보를 유저정보로 가져오기
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            UserAccount account = new UserAccount();
                            account.setIdToken(firebaseUser.getUid());
                            account.setEmailId(firebaseUser.getEmail());
                            account.setPassword(strPWD);

                            //회원정보를 firestore에 저장
                            //DB저장에 용이하게 Map 자료구조 사용
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
                                            Toast.makeText(SingUpActivity.this, "회원가입 성공!", Toast.LENGTH_LONG).show();
                                        }
                                    });
                            //회원가입이 완료됐으면, Login 화면으로 이동
                            Intent intent = new Intent(SingUpActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();


                        }
                        else{
                            Toast.makeText(SingUpActivity.this, "회원가입 실패!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}
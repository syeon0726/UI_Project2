package com.example.ui_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Button btnLogout;
    private FirebaseAuth firebaseAuth;
    private Button btnWordBook;
    private Button btnNote;
    private Button btnTTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogout=findViewById(R.id.logout);
        firebaseAuth=FirebaseAuth.getInstance();
        btnWordBook=findViewById(R.id.word);
        btnNote =findViewById(R.id.note);
        btnTTS = findViewById(R.id.tts);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnWordBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,WordBookActivity.class);
                startActivity(intent);

            }
        });

        btnNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MyNoteActivity.class);
                startActivity(intent);
            }
        });

        btnTTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TTSActivity.class);
                startActivity(intent);
            }
        });

    }
    public void onBtnClick(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                "https://endic.naver.com/popManager.nhn?m=search&query=XXX%20%EC%B6%9C%EC%B2%98:%20https://flystone.tistory.com/15%20[MomO]"));
        startActivity(intent);
    }

}

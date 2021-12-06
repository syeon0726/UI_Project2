package com.example.ui_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WordBookActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_book);

        ListView wordSelect=findViewById(R.id.wordSelect);

        List<String> list = new ArrayList<>();
        list.add("wordbook");
        list.add("100개의 동사로 외우는 숙어");
        list.add("왕초보 탈출 기초 단어");
        list.add("필수암기 중학생 단어");


        ArrayAdapter<String> adapter= new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,list);
        wordSelect.setAdapter(adapter);

        wordSelect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = new Intent(WordBookActivity.this,ShowWordListActivity.class);
                it.putExtra("it_listData",list.get(position));
                startActivity(it);
            }
        });














    }
}
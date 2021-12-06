package com.example.ui_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MyNoteActivity extends AppCompatActivity {
    EditText inputTerm,inputDefinition; //사용자로부터 영단어와 뜻을 입력받음.
    Button myWordListBtn; //단어 추가 다이얼로그를 띄우는 버튼
    Button addWordBtn,deleteBtn; //단어 추가 다이얼로그를 띄웠을때 추가,취소 버튼
    Dialog wordInputDialog; //단어 추가 다이얼로그

    // Dialog wordModifyDialog; //단어 수정 다이얼로그
    //Button modifyFinishedBtn,deleteModifyBtn;//단어 수정 다이얼로그를 띄웟을때 수정,취소 버튼
    //EditText modiftyTerm,modifyDefinition; //사용자로부터 영단어와 뜻을 입력받음.
    //Button deleteMyWordBtn,modifyMyWordBtn; //단어 삭제,수정을 위한 버튼튼

    String term,definition; //사용자 입력 용어,뜻

    private CustomMyWordAdapter customMyWordAdapter; //커스텀리스트뷰어댑터
    private ListView listExcel; //사용자한테 보여질 리스트뷰


    private Map<String, String> wordDB = new HashMap<>(); //사용자 입력 단어 DB
    private Map<String, String> getWordDB; //출력을 위한 DB 맵
    private FirebaseAuth firebaseAuth; //로그인 인증

    private FirebaseFirestore databaseReference; //파이어스토어 객체




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_note);

        wordDB=new HashMap<>(); //단어를 DB에 저장하기 위한 Map

        customMyWordAdapter=new CustomMyWordAdapter();

        listExcel=findViewById(R.id.wordMyExcel);
        listExcel.setAdapter(customMyWordAdapter);

        //단어 입력 다이얼로그
        myWordListBtn=findViewById(R.id.myWordListBtn);
        wordInputDialog=new Dialog(MyNoteActivity.this);
        wordInputDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        wordInputDialog.setContentView(R.layout.custom_word_input_dialog);

        //deleteMyWordBtn=findViewById(R.id.deleteMyWordBtn);
        //modifyMyWordBtn=findViewById(R.id.modifyMyWordBtn);

        //단어 수정 다이얼로그
        //wordModifyDialog=new Dialog(MyNoteActivity.this);
        //wordModifyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //wordInputDialog.setContentView(R.layout.custom_word_modification_dialog);


        myWordListBtn.setOnClickListener(new View.OnClickListener() { //단어 추가 버튼에 대한 이벤트 처리
            @Override
            public void onClick(View v) {
                showWordInputDialog();
            }
        });


        initMyWordList();

    }

    public void showWordInputDialog() {
        wordInputDialog.show();

        addWordBtn=wordInputDialog.findViewById(R.id.addWordBtn);
        deleteBtn=wordInputDialog.findViewById(R.id.deleteBtn);
        inputTerm=wordInputDialog.findViewById(R.id.inputTerm);
        inputDefinition=wordInputDialog.findViewById(R.id.inputDefi);

        databaseReference=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();



        //단어, 뜻 입력후 추가 버튼 눌렀을 경우
        addWordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                term=inputTerm.getText().toString();
                definition=inputDefinition.getText().toString();
                customMyWordAdapter.addItem(new Word(term,definition));
                customMyWordAdapter.notifyDataSetChanged();

                inputTerm.setText("");
                inputDefinition.setText("");

                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser(); //현재 유저 정보를 얻기 위함
                wordDB.put(term, definition); //영단어를 key값으로, 뜻을 값으로 집어넣기

                //현재 유저 정보를 기반으로 Word 콜렉션 생성, 'term' Document 생성, DB저장
                databaseReference.collection("UserAccount").document(firebaseUser.getEmail()).collection("Word").document(term).set(wordDB);


                wordInputDialog.dismiss();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputTerm.setText("");
                inputDefinition.setText("");

                wordInputDialog.dismiss();
            }
        });



    }
    //단어DB를 LogCat에 출력시켜주는 함수.
    public void printListWord() {

        //파이어스토어 인증 객체 및 DB객체
        databaseReference=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        CollectionReference collectionReference=databaseReference.collection("Word");

        //UserAccount를 읽은 후 해당 계정에 저장된 단어 DB를 불러와서 Log에 출력.
        databaseReference.collection("UserAccount").document(firebaseUser.getEmail()).collection("Word").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){                     ;
                    Log.d("connetDB", "wow"+task.getResult());
                    getWordDB = new HashMap<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("successfulLoadData", document.getId() + " => " +document.getString(document.getId()));
                    }
                } else {
                    Log.d("failData", "Error getting documents: ", task.getException());
                }
            }
        });

    }

    public void initMyWordList(){ //단어DB에 저장되있는 단어를 우선적으로 myWordlList에 띄우는 함수

        //파이어스토어 인증 객체 및 DB객체
        databaseReference=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        CollectionReference collectionReference=databaseReference.collection("Word");

        databaseReference.collection("UserAccount").document(firebaseUser.getEmail()).collection("Word").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){                     ;
                    Log.d("connetDB", "wow"+task.getResult());
                    getWordDB = new HashMap<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //Log.d("successfulLoadData", document.getId() + " => " +document.getString(document.getId()));
                        customMyWordAdapter.addItem(new Word(document.getId(), document.getString(document.getId())));
                        customMyWordAdapter.notifyDataSetChanged();

                    }
                } else {
                    Log.d("failData", "Error getting documents: ", task.getException());
                }
            }
        });


    }






}
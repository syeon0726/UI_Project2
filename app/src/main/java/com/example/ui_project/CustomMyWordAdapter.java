package com.example.ui_project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomMyWordAdapter extends BaseAdapter {
    private ArrayList<Word> myItems= new ArrayList<>();



    @Override
    public int getCount() {
        return myItems.size();
    }

    @Override
    public Object getItem(int position) {
        return myItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CustomMyWordAdapter.CustomMyViewHolder holder;

        if(convertView==null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_my_word_list,null,false);

            holder=new CustomMyWordAdapter.CustomMyViewHolder();
            holder.textTerm=convertView.findViewById(R.id.mytopText);
            holder.textDefinition=convertView.findViewById(R.id.mybottomText);
            holder.modifyTermBtn=convertView.findViewById(R.id.modifyMyWordBtn);
            holder.deleteTermBtn=convertView.findViewById(R.id.deleteMyWordBtn);
            holder.modifyDefi=convertView.findViewById(R.id.modifyDefi);
            holder.modifyTerm=convertView.findViewById(R.id.modifyTerm);


            convertView.setTag(holder);

        }
        else{
            holder=(CustomMyWordAdapter.CustomMyViewHolder) convertView.getTag();
        }

        Word word= myItems.get(position);

        holder.textTerm.setText(word.getTerm());
        holder.textDefinition.setText(word.getDefinition());


        //myWorldListView?????? ITEM ??????,
        //???????????? DB?????? ????????????????????? ?????? ???????????? ????????????
        holder.deleteTermBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myItems.remove(position);

                notifyDataSetChanged();

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                
                //?????? ????????? ????????? ?????? ???????????? ?????? ?????? ??????(word.getTerm())??? DB?????? ????????? ???????????? ??????.
                firebaseFirestore.collection("UserAccount").document(firebaseUser.getEmail()).collection("Word").document(word.getTerm()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            //?????? ????????? ??????????????? ??????!
                            public void onSuccess(Void unused) {
                                Log.d("deleteDB", "Successfully delete the word!");
                            }
                        })
                        //?????? ?????? ??????
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("why failed?", "Error deleting document", e);
                            }
                        });
            }
        });

        holder.modifyTermBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Word word = myItems.get(position);
                Log.d("orgWord", word.getTerm());
                Log.d("newWord", holder.modifyTerm.getText().toString());

                String originWord = word.getTerm();


                if(holder.modifyTermBtn.getText()=="????????????"){
                    holder.modifyTerm.setVisibility(View.GONE);
                    holder.modifyDefi.setVisibility(View.GONE);


                    holder.textTerm.setText(holder.modifyTerm.getText().toString());
                    holder.textDefinition.setText(holder.modifyDefi.getText().toString());

                    word.setTerm(holder.modifyTerm.getText().toString());
                    word.setDefinition(holder.modifyDefi.getText().toString());
                    notifyDataSetChanged();

                    Map<String, Object> modifyWord = new HashMap();
                    String term = holder.modifyTerm.getText().toString();
                    modifyWord.put(term,holder.modifyDefi.getText().toString());

                    holder.modifyTerm.setText("");
                    holder.modifyDefi.setText("");



                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                    firebaseFirestore.collection("UserAccount").document(firebaseUser.getEmail()).collection("Word")
                            .document(originWord).delete();

                    firebaseFirestore.collection("UserAccount").document(firebaseUser.getEmail()).collection("Word")
                            .document(term).set(modifyWord)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("updateDB", "update new DB");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("failedUpdate", "fail update the DB",e);
                                }
                            });






                    holder.modifyTermBtn.setText("??????");



                }
                else{
                    holder.modifyTermBtn.setText("????????????");
                    holder.modifyTerm.setVisibility(View.VISIBLE);
                    holder.modifyDefi.setVisibility(View.VISIBLE);


                }

            }
        });



        return convertView;
    }

    class CustomMyViewHolder{
        TextView textTerm;
        TextView  textDefinition;
        Button modifyTermBtn;
        Button deleteTermBtn;
        EditText modifyDefi;
        EditText modifyTerm;
    }
    public void addItem(Word word){
        myItems.add(word);

    }
}

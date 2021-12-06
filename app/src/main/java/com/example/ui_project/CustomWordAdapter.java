package com.example.ui_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomWordAdapter extends BaseAdapter {
    private ArrayList<Word> items= new ArrayList<>();
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CustomViewHolder holder;

        if(convertView==null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_word_list,null,false);

            holder=new CustomViewHolder();
            holder.textTerm=convertView.findViewById(R.id.topText);
            holder.textDefinition=convertView.findViewById(R.id.bottomText);

            convertView.setTag(holder);

        }
        else{
            holder=(CustomViewHolder) convertView.getTag();
        }

        Word word= items.get(position);
        
        holder.textTerm.setText(word.getTerm());
        holder.textDefinition.setText(word.getDefinition());
        
        return convertView;
                
    }

    class CustomViewHolder{
        TextView textTerm;
        TextView  textDefinition;
    }
    
    public void addItem(Word word){
        items.add(word);

    }


}

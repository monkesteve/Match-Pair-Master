package com.example.assignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
    @NonNull
    Context context;
    String[][] mData;
    int i = 0;

    private OnRecyclerViewClickListener listener;

    public MyAdapter(Context context, String[][] mData){
        this.context = context;
        this.mData = mData;
    }

    public void setItemClickListener(OnRecyclerViewClickListener itemClickListener){
        listener = itemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitems, parent, false);
        if(listener != null){
            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    listener.onItemClickListener(view);
                }
            });
        }
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if(i % 4 == 0){
            holder.container.setBackgroundColor(ContextCompat.getColor(context, R.color.itemBackground1));
            i++;
        }else if(i % 4 == 1){
            holder.container.setBackgroundColor(ContextCompat.getColor(context, R.color.itemBackground2));
            i++;
        }else if(i % 4 == 2){
            holder.container.setBackgroundColor(ContextCompat.getColor(context, R.color.itemBackground3));
            i++;
        }else if(i % 4 == 3){
            holder.container.setBackgroundColor(ContextCompat.getColor(context, R.color.itemBackground4));
            i++;
        }


        for(int i = 0; i < holder.txtItem.length; i++){
            holder.txtItem[i].setTextColor(ContextCompat.getColor(context,R.color.itemFontColor));
            holder.txtItem[i].setText(mData[position][i]);
        }
    }

    @Override
    public int getItemCount() {
        return mData.length;
    }
}
package com.hxm.itemdecoration;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by hxm on 2018/7/16
 * 描述：
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHolder> {
    private AppCompatActivity activity;

    public ItemAdapter(AppCompatActivity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        holder.textView.setText("item"+position);
        holder.textView.setBackgroundColor(Color.parseColor("#3F51B5"));
//        if ((position&1)==1){
//            holder.textView.setBackgroundColor(Color.parseColor("#FF4081"));
//        }else {
//            holder.textView.setBackgroundColor(Color.parseColor("#3F51B5"));
//        }
//        holder.textView.setBackgroundColor(Color.parseColor("#59B4654F"));
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return 19;
    }

    static class ItemHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public ItemHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv);
        }
    }
}

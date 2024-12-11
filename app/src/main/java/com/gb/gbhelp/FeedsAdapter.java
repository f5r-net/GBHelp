package com.gb.gbhelp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class FeedsAdapter extends RecyclerView.Adapter<FeedsAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<FeedsModel> feedsModelArrayList;
    public FeedsAdapter(Context context){
        this.context = context;
        this.feedsModelArrayList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.feeds_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FeedsModel feedsModel = feedsModelArrayList.get(position);
        holder.title.setText(GB.getText(context,feedsModel,true));
        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandFeed(feedsModel);
            }
        });
      //  holder.btnStatus.setColorFilter(Color.parseColor("#5E0FEC"));
    }

    @Override
    public int getItemCount() {
        return feedsModelArrayList.size();
    }

    private void expandFeed(FeedsModel feedsModel) {
        Intent intent = new Intent(context,FeedsExpand.class);
        intent.putExtra("feedsModel", feedsModel);
        context.startActivity(intent);
    }
    public void clear(){
        feedsModelArrayList.clear();
        notifyDataSetChanged();
    }

    public void add(ArrayList<FeedsModel> hashMap){
        feedsModelArrayList = hashMap;
        GB.printLog("test3/"+ feedsModelArrayList.size());
        notifyDataSetChanged();
        Collections.sort(feedsModelArrayList);
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title;
      //  ImageView btnStatus;
        LinearLayout viewHolder;

        public MyViewHolder(View view){
            super(view);
            title = view.findViewById(R.id.title);
         //   btnStatus = view.findViewById(R.id.btn_status);
            viewHolder = view.findViewById(R.id.viewHolder);
        }

    }
}

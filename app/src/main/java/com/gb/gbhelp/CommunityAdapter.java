package com.gb.gbhelp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<CommunityModel> communityModelArrayList;
    public CommunityAdapter(Context context){
        this.context = context;
        this.communityModelArrayList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comunity_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CommunityModel communityModel = communityModelArrayList.get(position);
        holder.msg.setText(communityModel.getNickName());
        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChat(communityModel.getSenderId(), communityModel.getNickName(),communityModel.getTag());
            }
        });
        holder.btnStatus.setColorFilter(Color.parseColor("#5E0FEC"));
    }

    @Override
    public int getItemCount() {
        return communityModelArrayList.size();
    }

    public void openChat(String id,String nickname,String tag) {
        GB.startChatActivity(context,id,nickname,tag);
    }
    public void clear(){
        communityModelArrayList.clear();
        notifyDataSetChanged();
    }

    public void add(ArrayList<CommunityModel> hashMap){
        communityModelArrayList = hashMap;
        GB.printLog("add/"+ communityModelArrayList.size());
        notifyDataSetChanged();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView msg;
        ImageView btnStatus;
        LinearLayout viewHolder;

        public MyViewHolder(View view){
            super(view);
            msg = view.findViewById(R.id.sender);
            btnStatus = view.findViewById(R.id.btn_status);
            viewHolder = view.findViewById(R.id.viewHolder);
        }

    }
}

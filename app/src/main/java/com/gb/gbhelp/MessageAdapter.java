package com.gb.gbhelp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    private Context context;
    private List<MessageModel> messageModelList;
    private PopupMenu menu;

    public MessageAdapter(Context context) {
        this.context = context;
        this.messageModelList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MessageModel messageModel = messageModelList.get(position);

        if (messageModel.getSenderId().equals(GB.getSharedString(context,GB.USER_ID))) {
            holder.viewStub.setLayoutResource(R.layout.conversation_row_text_right);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.END;
            holder.viewStub.setLayoutParams(params);
        }else
            holder.viewStub.setLayoutResource(R.layout.conversation_row_text_left);

        if (holder.viewStub.getParent() == null) {
            return;
        }
        View inflated = holder.viewStub.inflate();
        TextView message_text = inflated.findViewById(R.id.message_text);
        TextView message_date = inflated.findViewById(R.id.date);
        LinearLayout mainLayout = inflated.findViewById(R.id.main_layout);
        popupMenu(context,mainLayout,messageModel);
        if (messageModel.getType().equals("1")) {
            String mediaUrl = messageModel.getMessage().split(",,")[0];
            String mediaLink = messageModel.getMessage().split(",,")[1];
            boolean mediaExits = false;
            File serverMediaFile = new File(mediaUrl);

            String downloadedMediaPath = context.getFilesDir() + "/Media/" + serverMediaFile.getName();
            File downloadedMediaFile = new File(downloadedMediaPath);

            if (serverMediaFile.exists())
                mediaExits = true;

            if (mediaExits) {
                GB.printLog("MessageAdapter/mediaUrl exits");
                Bitmap bitmap = BitmapFactory.decodeFile(mediaUrl);
                holder.media.setImageBitmap(bitmap);
            } else if (downloadedMediaFile.exists()) {
                GB.printLog("MessageAdapter/downloadedMediaPath exits");
                Bitmap bitmap = BitmapFactory.decodeFile(downloadedMediaPath);
                holder.media.setImageBitmap(bitmap);
            } else {
                GB.printLog("MessageAdapter/download media btn");
                holder.downloadMedia.setVisibility(View.VISIBLE);
                holder.downloadMedia.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.progressDownloadMedia.setVisibility(View.VISIBLE);
                        holder.downloadMedia.setVisibility(View.GONE);
                        MediaManager mediaManager = new MediaManager(context, mediaLink, serverMediaFile.getName()) {
                            @Override
                            public void onDownloadComplete() {
                                if (downloadedMediaFile.exists()) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(downloadedMediaPath);
                                    holder.media.setImageBitmap(bitmap);
                                }
                            }
                        };
                        mediaManager.start();
                    }
                });
            }
            holder.viewStub.setVisibility(View.GONE);
            holder.media.setVisibility(View.VISIBLE);
            boolean finalMediaExits = mediaExits;
            holder.media.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MediaViewer.class);
                    if (finalMediaExits) {
                        intent.putExtra("imageUri", mediaUrl);
                        intent.putExtra("viewImage", "viewImage");
                    } else if (downloadedMediaFile.exists()) {
                        intent.putExtra("imageUri", downloadedMediaPath);
                        intent.putExtra("viewImage", "viewImage");
                    }
                    context.startActivity(intent);
                }
            });

        } else {
            message_text.setText(messageModel.getMessage());
            message_date.setText(longToTime(messageModel));
            holder.viewStub.setVisibility(View.VISIBLE);
            holder.media.setVisibility(View.GONE);
        }

        GB.printLog("message/" + messageModel.getMessage());
    }

    @Override
    public int getItemCount() {
        return messageModelList.size();
    }

    public void clear() {
        messageModelList.clear();
        notifyDataSetChanged();
    }

    public void add(MessageModel messageModel) {
        messageModelList.add(messageModel);
        sort();
    }

    void sort() {
        Collections.sort(messageModelList, new SortMessages());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ViewStub viewStub;
        ImageView media;
        ImageButton downloadMedia;
        ProgressBar progressDownloadMedia;

        public MyViewHolder(View view) {
            super(view);

            media = view.findViewById(R.id.media);
            downloadMedia = view.findViewById(R.id.downloadMedia);
            progressDownloadMedia = view.findViewById(R.id.progressDownloadMedia);
            viewStub =  view.findViewById(R.id.conversation_row_text);
        }

    }
    private void popupMenu(Context context, LinearLayout mainLayout,MessageModel messageModel){

        mainLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                menu = new PopupMenu(context, v);
                menu.getMenu().add(0, 0, 0, R.string.copy);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        GB.printLog("ChatActivity" + "/onMenuItemClick/id" + item.getItemId());
                        if (item.getItemId() == 0){
                            GB.copyMessage(context,messageModel.getMessage());
                            return true;
                        }
                        return false;
                    }
                });
                menu.show();
                return false;
            }
        });

    }
    private String longToTime(MessageModel messageModel){
        long longTime = Long.parseLong(messageModel.getTimeStamp());

        Date msgDate = new Date(longTime);

        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
        return dateFormat.format(msgDate);
    }
    class SortMessages implements Comparator<MessageModel> {
        @Override
        public int compare(MessageModel a, MessageModel b) {
            long modified;
            modified = Long.parseLong(a.getTimeStamp()) - Long.parseLong(b.getTimeStamp());
            if (modified > 0) {
                return 1;
            } else if (modified == 0) {
                return 0;
            } else {
                return -1;
            }
        }
    }

}

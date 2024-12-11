package com.gb.gbhelp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URISyntaxException;
import java.util.UUID;

public class ChatActivity extends BaseActivity {
    ImageButton sendBtn, mediaBtn;
    EditText entry;
    RecyclerView recyclerView;

    public String nickname,tag,id;
    DatabaseReference databaseReferenceSender;
    String senderRoom;
    MessageAdapter messageAdapter;
    Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        sendBtn = findViewById(R.id.send);
        mediaBtn = findViewById(R.id.camera_btn);
        entry = findViewById(R.id.entry);

        messageAdapter = new MessageAdapter(this);
        recyclerView = findViewById(R.id.messages_list);

        recyclerView.setAdapter(messageAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom <= oldBottom) {
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.smoothScrollToPosition(bottom);
                        }
                    }, 100);
                }
            }
        });
        id = getIntent().getStringExtra("userId");
        nickname = getIntent().getStringExtra("nickname");
        tag = getIntent().getStringExtra("tag");
        if (tag.equals(GB.CHAT_TAG_HELP))
            entry.setHint(R.string.your_q);
        else
            entry.setHint(R.string.your_sug);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(nickname);
        if (GB.isAdmin()) {
            senderRoom = id + "TOOO" + "sxXwxKeElhR5Gz9La5EcvlVm0O63";
        } else {
            senderRoom = FirebaseAuth.getInstance().getUid() + "TOOO" + id;
        }
        GB.printLog("ChatActivity" + "/senderRoom" + senderRoom);
        databaseReferenceSender = FirebaseDatabase.getInstance().getReference(GB.DATABASE_COMMUNITY).child(senderRoom);

        databaseReferenceSender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GB.printLog("ChatActivity" + "/onDataChange");
                messageAdapter.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    GB.printLog("ChatActivity" + "/onDataChange/databaseReferenceSender/messageModel=" + messageModel.getMessage());
                    if (tag.equals(GB.CHAT_TAG_HELP) && messageModel.getTag().equals(GB.CHAT_TAG_HELP)) {
                        messageAdapter.add(messageModel);
                    } else if (tag.equals(GB.CHAT_TAG_SUGGESTION) && messageModel.getTag().equals(GB.CHAT_TAG_SUGGESTION)) {
                        messageAdapter.add(messageModel);
                    }
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = entry.getText().toString();
                if (TextUtils.isEmpty(message))
                    return;
                if (TextUtils.isEmpty(message.trim()))
                    return;
                sendMessage(message, "0");
            }
        });
        mediaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage();
            }
        });
    }

    private void sendMessage(String message, String type) {
        String messageId = UUID.randomUUID().toString();
        MessageModel messageModel = new MessageModel();
        messageModel.setMessage(message);
        messageModel.setTimeStamp(String.valueOf(System.currentTimeMillis()));
        messageModel.setMessageId(messageId);
        messageModel.setType(type);
        messageModel.setTag(tag);
        messageModel.setStatus("received");
        messageModel.setNickName(GB.getSharedString(this, GB.NICKNAME));
        messageModel.setSenderId(FirebaseAuth.getInstance().getUid());

        messageAdapter.add(messageModel);
        messageAdapter.notifyDataSetChanged();
        databaseReferenceSender.child(messageId).setValue(messageModel);
        entry.setText("");
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && data != null && data.getData() != null) {
            imageUri = data.getData();

            Intent intent = new Intent(this, MediaViewer.class);
            intent.putExtra("imageUri", imageUri.toString());
            startActivityForResult(intent, 0);
        } else if (resultCode == GB.MEDIA_UPLOADED) {
            GB.printLog("ChatActivity/result=7");

            String videoPath = null;
            try {
                videoPath = PathUtil.getPath(this, imageUri);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(videoPath))
                videoPath = " null";
            String uploadedMediaUrl = data.getStringExtra("mediaUrl");
            sendMessage(videoPath +",,"+ uploadedMediaUrl,"1");
        }
    }
}

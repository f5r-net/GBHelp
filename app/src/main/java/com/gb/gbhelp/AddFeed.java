package com.gb.gbhelp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AddFeed extends BaseActivity {
    private PagerMediaAdapter pagerMediaAdapter, pagerMediaAdapterAr;
    private EditText etTitle, etSummery,etTitleAR, etSummeryAr;
    private CheckBox tagCheckBox;
    DatabaseReference databaseReferenceFeeds;
    private final HashMap<String, ArrayList<String>> feedsMediaUrls = new HashMap<>();
    private final HashMap<String, ArrayList<String>> feedsMediaName = new HashMap<>();
    private ProgressBar progressBar;
    private final String tag = "AddFeed";
    private final Object fixingObject = new Object();
    private final ArrayList<String> urls = new ArrayList<>();
    private final ArrayList<String> names = new ArrayList<>();
    private final ArrayList<String> urlsAr = new ArrayList<>();
    private final ArrayList<String> namesAr = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_feed);
        etTitle = findViewById(R.id.titleEt);
        etSummery = findViewById(R.id.summeryEt);
        etTitleAR = findViewById(R.id.titleArEt);
        etSummeryAr = findViewById(R.id.summeryEtAr);
        progressBar = findViewById(R.id.progressBar);
        tagCheckBox = findViewById(R.id.tagCheckBox);
        RecyclerView feedsMediaRV = findViewById(R.id.feedsMediaList);
        RecyclerView feedsMediaRVAr = findViewById(R.id.feedsMediaListAr);

        feedsMediaRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        feedsMediaRVAr.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        pagerMediaAdapter = new PagerMediaAdapter(this, selected, true, GB.FEEDS_SUPPORT_LANG);
        pagerMediaAdapterAr = new PagerMediaAdapter(this, selectedAr, true, GB.FEEDS_SUPPORT_LANG_AR);

        feedsMediaRV.setAdapter(pagerMediaAdapter);
        feedsMediaRVAr.setAdapter(pagerMediaAdapterAr);
        findViewById(R.id.addMedia).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(GB.SELECT_FEED_MEDIA);
            }
        });
        findViewById(R.id.addMediaAr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(GB.SELECT_FEED_MEDIA_AR);
            }
        });
        findViewById(R.id.postFeed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                for (int i2 = 0; i2 < 2; i2++) {
                    if (i2 == 0)
                    makePostFeed(pagerMediaAdapter,GB.FEEDS_SUPPORT_LANG);
                    else
                        makePostFeed(pagerMediaAdapterAr,GB.FEEDS_SUPPORT_LANG_AR);

                }*/

                makePostFeed(pagerMediaAdapter,pagerMediaAdapterAr);
            }
        });
        databaseReferenceFeeds = FirebaseDatabase.getInstance().getReference(GB.DATABASE_FEEDS);
    }

    private void makePostFeed(PagerMediaAdapter adapter,PagerMediaAdapter pagerMediaAdapterAr) {
        progressBar.setVisibility(View.VISIBLE);
        class thread extends java.lang.Thread {
            @Override
            public void run() {

                synchronized (fixingObject) {
                    int size = adapter.getArrayList().size();
                    GB.printLog(tag + "/makePostFeed/mediaUriArrayList size=" + size);
                    if (size > 0) {
                        GB.printLog(tag + "/makePostFeed/mediaUriArrayList=" + adapter.getArrayList().toString());
                        for (int j = 0; j < size; j++) {
                            boolean uploadAr = false;
                            if (j + 1 == size) {
                                uploadImage((Uri) adapter.getArrayList().get(j), GB.FEEDS_SUPPORT_LANG, false);
                                uploadAr = true;
                            } else {
                                uploadImage((Uri) adapter.getArrayList().get(j), GB.FEEDS_SUPPORT_LANG, false);
                            }
                            try {
                                fixingObject.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (uploadAr){
                                threadAR t = new threadAR();
                                t.start();
                            }
                        }
                    } else {
                        GB.printLog(tag + "/makePostFeed/no media=");
                        postFeed(GB.FEEDS_SUPPORT_LANG);
                    }
                }
            }



            class threadAR extends java.lang.Thread {
                @Override
                public void run() {

                    synchronized (fixingObject) {
                        int size = pagerMediaAdapterAr.getArrayList().size();
                        GB.printLog(tag + "/makePostFeed/pagerMediaAdapterAr size=" + size);
                        if (size > 0) {
                            GB.printLog(tag + "/makePostFeed/pagerMediaAdapterAr=" + pagerMediaAdapterAr.getArrayList().toString());
                            for (int j = 0; j < size; j++) {
                                if (j + 1 == size) {
                                    uploadImage((Uri) pagerMediaAdapterAr.getArrayList().get(j), GB.FEEDS_SUPPORT_LANG_AR, true);
                                } else {
                                    uploadImage((Uri) pagerMediaAdapterAr.getArrayList().get(j), GB.FEEDS_SUPPORT_LANG_AR, false);
                                }
                                try {
                                    fixingObject.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            GB.printLog(tag + "/makePostFeed/no media=");
                            postFeed(GB.FEEDS_SUPPORT_LANG_AR);
                        }
                    }
                }
            }

            private void uploadImage(Uri uri, String lang, boolean isLast) {
                GB.printLog(tag + "/uploadImage/isLast=" + isLast);
                String fileName = GB.getUploadFileName();
                StorageReference storageReference = FirebaseStorage.getInstance().getReference("feedsMedia/" + fileName);
                storageReference.putFile(uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        GB.printLog(AddFeed.this, "lang/"+lang+",onSuccess/uri/" + uri.toString());
                                        if (lang.equals(GB.FEEDS_SUPPORT_LANG_AR)){
                                            urlsAr.add(uri.toString());
                                            namesAr.add(String.valueOf(System.currentTimeMillis()));
                                            feedsMediaUrls.put(lang, urlsAr);
                                            feedsMediaName.put(lang, namesAr);
                                        }else{
                                            urls.add(uri.toString());
                                            names.add(String.valueOf(System.currentTimeMillis()));
                                            feedsMediaUrls.put(lang, urls);
                                            feedsMediaName.put(lang, names);
                                        }

                                        if (isLast) {
                                            postFeed(lang);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                        synchronized (fixingObject) {
                                            fixingObject.notify();
                                        }
                                        finish();
                                    }
                                });
                                synchronized (fixingObject) {
                                    fixingObject.notify();
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                synchronized (fixingObject) {
                                    fixingObject.notify();
                                }
                                Toast.makeText(AddFeed.this, "Failed to Upload/" + e.getMessage(), Toast.LENGTH_SHORT).show();


                            }
                        });
            }
        }

        thread t = new thread();
        t.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GB.SELECT_FEED_MEDIA && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            pagerMediaAdapter.add(imageUri);
            return;
        }
        if (requestCode == GB.SELECT_FEED_MEDIA_AR && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            pagerMediaAdapterAr.add(imageUri);
        }
    }

    private void postFeed(String lang) {
        if (!lang.equals(GB.FEEDS_SUPPORT_LANG_AR))
            return;

        GB.printLog(tag + "/postFeed/" + feedsMediaUrls.get(GB.FEEDS_SUPPORT_LANG));

        String title = etTitle.getText().toString();
        String summery = etSummery.getText().toString();

        String titleAr = etTitleAR.getText().toString();
        String summeryAr = etSummeryAr.getText().toString();
        if (TextUtils.isEmpty(title)) {
            etTitle.setError("");
            return;
        }
        if (TextUtils.isEmpty(summery)) {
            etSummery.setError("");
            return;
        }
        if (TextUtils.isEmpty(titleAr)) {
            etTitleAR.setError("");
            return;
        }
        if (TextUtils.isEmpty(summeryAr)) {
            etSummeryAr.setError("");
            return;
        }
        String timestamp = String.valueOf(System.currentTimeMillis());
        FeedsModel feedsModel = new FeedsModel();

        feedsModel.setTitle(title);
        feedsModel.setSummery(summery);
        feedsModel.setTitleAR(titleAr);
        feedsModel.setSummeryAR(summeryAr);

        feedsModel.setTimestamp(timestamp);

        feedsModel.setUrls(String.join(",", feedsMediaUrls.get(GB.FEEDS_SUPPORT_LANG)));
        feedsModel.setFileName(String.join(",", feedsMediaName.get(GB.FEEDS_SUPPORT_LANG)));

        feedsModel.setUrlsAR(String.join(",",feedsMediaUrls.get(GB.FEEDS_SUPPORT_LANG_AR)));
        feedsModel.setFileNameAR(String.join(",",feedsMediaName.get(GB.FEEDS_SUPPORT_LANG_AR)));

        if (tagCheckBox.isChecked())
            feedsModel.setTag(GB.FEEDS_TAG_IMPORTANT);
        databaseReferenceFeeds.child(timestamp).setValue(feedsModel);

        progressBar.setVisibility(View.GONE);
        setResult(7);
        finish();
    }

    PagerMediaAdapter.OnItemSelected selected = new PagerMediaAdapter.OnItemSelected() {
        @Override
        public void onMediaSelected(Uri uri, String path) {

            pagerMediaAdapter.getArrayList().remove(uri);
            pagerMediaAdapter.notifyDataSetChanged();
        }
    };
    PagerMediaAdapter.OnItemSelected selectedAr = new PagerMediaAdapter.OnItemSelected() {
        @Override
        public void onMediaSelected(Uri uri, String path) {

            pagerMediaAdapterAr.getArrayList().remove(uri);
            pagerMediaAdapterAr.notifyDataSetChanged();
        }
    };

    private void selectImage(int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, requestCode);
    }

    public void hideKeyboard(View view) {
        if (view == null) {
            return;
        }
        try {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && !imm.isActive()) {
                return;
            }
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

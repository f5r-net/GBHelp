package com.gb.gbhelp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FeedsExpand extends BaseActivity implements PagerMediaAdapter.OnItemSelected {
    private PagerMediaAdapter pagerMediaAdapter;
    private RecyclerView feedsMediaRV;
    public TextView summery,title;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feeds_expander);
        Intent intent = getIntent();
        FeedsModel feedsModel = (FeedsModel) intent.getSerializableExtra("feedsModel");
        title = findViewById(R.id.title);
        title.setText(GB.getText(this,feedsModel,true));

        summery = findViewById(R.id.summery);
        summery.setText(GB.getText(this,feedsModel,false));


        feedsMediaRV = findViewById(R.id.feedsMediaList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        feedsMediaRV.setLayoutManager(linearLayoutManager);
        pagerMediaAdapter = new PagerMediaAdapter(this, this,false, "");
        String[] urls =getUrls(this,feedsModel).split(",");
        for (int i = 0; i < urls.length; i++) {
            pagerMediaAdapter.add(urls[i]);
        }
        setAdapter();
       /* boolean needToDownload = false;
        for (int i = 0; i < strings.length; i++) {
            File file = new File(getFilesDir() + "/Media/"+feedsModel.getFileName().split(",,")[i]);
            if (file.exists()) {
                uploadFeedMediaAdapter.add(file);
            }else {
                needToDownload = true;
                break;
            }
        }
        if (needToDownload){
            DownloadFeedsMedia downloadFeedsMedia = new DownloadFeedsMedia(this,feedsModel.getFileName().split(",,"));
            downloadFeedsMedia.execute(feedsModel.getUrls().split(",,"));
        }else
            setAdapter();*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pagerMediaAdapter.shutdown();
    }

    @Override
    public void onMediaSelected(Uri uri, String path) {

    }
    protected void setAdapter(){
        feedsMediaRV.setAdapter(pagerMediaAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.expand_feed,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_translate){
            GB.startTranslation(this,title,summery);
        }
        return super.onOptionsItemSelected(item);
    }
    private String getUrls(Context context,FeedsModel feedsModel){
        return GB.getLanguage(context) ? feedsModel.getUrlsAR() : feedsModel.getUrls();
    }
}

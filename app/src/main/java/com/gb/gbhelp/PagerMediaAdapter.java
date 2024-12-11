package com.gb.gbhelp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gb.gbhelp.mediaview.ImageOverlayView;
import com.gb.gbhelp.mediaview.ImageViewer;
import com.gb.gbhelp.mediaview.StylingOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public class PagerMediaAdapter extends RecyclerView.Adapter<PagerMediaAdapter.ViewHolder> {

    private final Context activity;
    private final List<Object> mediaUriArrayList = new ArrayList<>();
    private final OnItemSelected mOnItemSelected;
    private final boolean exORim;
    private final Picasso picassoInstance;
    private final String lang;
    public PagerMediaAdapter(Context activity, OnItemSelected onItemSelected, boolean exORim, String lang) {
        this.activity = activity;
        mOnItemSelected = onItemSelected;
        this.exORim = exORim;
        this.lang = lang;
        VideoRequestHandler videoRequestHandler = new VideoRequestHandler();
        picassoInstance = new Picasso.Builder(this.activity.getApplicationContext()).addRequestHandler(videoRequestHandler).build();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_feeds_media_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (exORim) {
            Uri uri = (Uri) getArrayList().get(position);
            holder.feedMediaIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemSelected.onMediaSelected(uri,"");
                }
            });
            holder.feedMediaIV.setImageURI(uri);
            holder.progressBar.setVisibility(View.GONE);
        } else {

            GB.printLog("PagerMediaAdapter/onBindViewHolder/mediaUrl="+ getArrayList().get(position));
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    GB.printLog("PagerMediaAdapter/onBitmapLoaded");
                    holder.feedMediaIV.setImageBitmap(bitmap);
                    holder.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    GB.printLog("PagerMediaAdapter/onBitmapFailed/"+e.getMessage());
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    GB.printLog("PagerMediaAdapter/onPrepareLoad");
                }
            };
            holder.feedMediaIV.setTag(target);
            picassoInstance.load((String) getArrayList().get(position)).into(target);
            holder.feedMediaIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPicker(position);
                }
            });

        }
}
public void shutdown(){
    GB.printLog("PagerMediaAdapter/shutdown");
  //  picassoInstance.shutdown();
}
    @Override
    public int getItemCount() {
        return getArrayList().size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void add(Uri path) {
        getArrayList().add(path);
        notifyDataSetChanged();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void add(String strings) {
        getArrayList().add((strings));
        notifyDataSetChanged();
    }
    public List<Object> getArrayList(){
        return  mediaUriArrayList;
    }
    public interface OnItemSelected {
    void onMediaSelected(Uri uri,String path);
}


    protected void showPicker(int startPosition) {
        StylingOptions options;
        ImageOverlayView overlayView;
        options = new StylingOptions();
        ImageViewer.Builder builder = new ImageViewer.Builder(this.activity, getArrayList())
                .setStartPosition(startPosition)
           //     .setOnDismissListener(getDismissListener())
                .hideStatusBar(options.get(StylingOptions.Property.HIDE_STATUS_BAR)).allowSwipeToDismiss(options.get(StylingOptions.Property.SWIPE_TO_DISMISS));
        if (options.get(StylingOptions.Property.SHOW_OVERLAY)) {
            overlayView = new ImageOverlayView(this.activity);
            builder.setOverlayView(overlayView);//.setImageChangeListener(getImageChangeListener());
        }
        builder.show();
    }
class ViewHolder extends RecyclerView.ViewHolder {
    ImageView feedMediaIV;
    ProgressBar progressBar;

    ViewHolder(View itemView) {
        super(itemView);
        feedMediaIV = itemView.findViewById(R.id.feedMedia);
        progressBar = itemView.findViewById(R.id.progressBar);

    }
}
}

package com.gb.gbhelp.mediaview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentContainerView;
import androidx.viewpager.widget.PagerAdapter;

import com.gb.gbhelp.GB;
import com.gb.gbhelp.R;
import com.gb.gbhelp.VideoRequestHandler;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {
    private static final String TAG = ViewPagerAdapter.class.getName();
    private Context activity;
    private Picasso picassoInstance;
    private VideoRequestHandler videoRequestHandler;
    private List<Object> storiesDataArrayList;

    ViewPagerAdapter(Context viewStoryActivity, List<Object> storiesDataArrayList) {
        activity = viewStoryActivity;
        videoRequestHandler = new VideoRequestHandler();
        picassoInstance = new Picasso.Builder(activity.getApplicationContext()).addRequestHandler(videoRequestHandler).build();
        this.storiesDataArrayList = storiesDataArrayList;
    }

    @Override
    public int getCount() {
        return storiesDataArrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (layoutInflater != null) {
            View view = layoutInflater.inflate(R.layout.custom_view_pager, container, false);
            ImageView imageView = view.findViewById(R.id.imageview);
            ProgressBar progressBar = view.findViewById(R.id.progressBar);
            String filePath = (String) storiesDataArrayList.get(position);
           /* if (filePath.endsWith("mp4")) {
                picassoInstance.load(videoRequestHandler.SCHEME_VIDEO + ":" + new File(filePath)).into(imageView);
            } else {*/
                picassoInstance.load(filePath/*new File(filePath)*/).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        GB.printLog("aa/onBitmapLoaded");
                        imageView.setImageBitmap(bitmap);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        GB.printLog("aa/onBitmapFailed");
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        GB.printLog("aa/onPrepareLoad");

                    }
                });
          //  }

            container.addView(view);
            return view;
        }
        return container;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (object instanceof LinearLayout)
        container.removeView((LinearLayout) object);
        else
            container.removeView((FrameLayout) object);
    }

}

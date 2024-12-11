package com.gb.gbhelp.mediaview;

import android.content.Context;

import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import android.widget.RelativeLayout;

import com.gb.gbhelp.GB;
import com.gb.gbhelp.R;


import java.io.File;
import java.util.ArrayList;

public class ImageOverlayView extends RelativeLayout implements View.OnClickListener {

   // public LinearLayout iconVideoLinear;
    private String path;

    public ImageOverlayView(Context context) {
        super(context);
        init();
    }

    public ImageOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private void init() {
        View view = inflate(getContext(), R.layout.view_image_overlay, this);
     //   ImageView fabDownload = view.findViewById(R.id.downloadImage);
      //  ImageView fabRotate = view.findViewById(R.id.story_overlay_fab_rotate);
      //  ImageView fabShare = view.findViewById(R.id.shareImage);
      //  iconVideoLinear = view.findViewById(R.id.story_overlay_linear_video);

       // fabShare.setOnClickListener(this);
      //  fabRotate.setOnClickListener(this);
     //   fabDownload.setOnClickListener(this);

        //iconVideoLinear.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.story_overlay_fab_rotate:
                GB.printLog("rotate");
                //   rotate();
                break;*//*
            case R.id.downloadImage:
                GB.printLog("download media");
                if (getPath().endsWith("jpg")) {
                  //  GB.saveMedia(getContext(), new File(getPath()), "image/*");
                } else if (getPath().endsWith("mp4")) {
                   // GB.saveMedia(getContext(), new File(getPath()), "video/*");
                }
                break;
            case R.id.shareImage:
                ArrayList<Uri> arrayList = new ArrayList<>();
                arrayList.add(Uri.fromFile(new File(getPath())));
               // GB.ShareMedia(getContext(), arrayList);
               // GB.printLog("share media");
                break;*/
           /* case R.id.story_overlay_linear_video:
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setDataAndType(Uri.parse(getPath()), "video/*");
                getContext().startActivity(intent);
                GB.printLog("Play media");
                break;*/
            default:
        }
    }/*
    public void rotate() {
    Bitmap bitmap = BitmapFactory.decodeFile(getPath());
    Matrix matrix = new Matrix();
    matrix.postRotate(280);
    Bitmap bb = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
    fabShare.setImageBitmap(bb);
    }*/
}

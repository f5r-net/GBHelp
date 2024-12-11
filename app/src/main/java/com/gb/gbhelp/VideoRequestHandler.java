package com.gb.gbhelp;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;


public class VideoRequestHandler extends RequestHandler {
    public String SCHEME_VIDEO = "video";

    @Override
    public boolean canHandleRequest(Request b) {
        String c = b.uri.getScheme();
        return (SCHEME_VIDEO.equals(c));
    }

    @Override
    public Result load(Request d, int u) {
        Bitmap v = ThumbnailUtils.createVideoThumbnail(d.uri.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
        return new Result(v, Picasso.LoadedFrom.DISK);
    }
}

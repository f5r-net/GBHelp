package com.gb.gbhelp.mediaview;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;


public class ImageViewer implements OnDismissListener, DialogInterface.OnKeyListener {

    private Builder builder;
    private AlertDialog dialog;
    private ImageViewerView viewer;

    private ImageViewer(Builder builder, List<Object> storiesDataArrayList) {
        this.builder = builder;
        viewer = new ImageViewerView(builder.context);
        viewer.allowSwipeToDismiss(builder.isSwipeToDismissAllowed);
        viewer.setOnDismissListener(this);
        viewer.setBackgroundColor(builder.backgroundColor);
        viewer.setContainerPadding(builder.containerPaddingPixels);
        viewer.setUrls(builder.startPosition, storiesDataArrayList);
    }

    public void show() {
        dialog.show();
    }

    private void createDialog() {

        viewer.setOverlayView(builder.overlayView);
        viewer.setPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (builder.imageChangeListener != null) {
                    builder.imageChangeListener.onImageChange(position);
                }
            }
        });

        dialog = new AlertDialog.Builder(builder.context, getDialogStyle())
                .setView(viewer)
                .setOnKeyListener(this)
                .create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (builder.onDismissListener != null) {
                    builder.onDismissListener.onDismiss();
                }
            }
        });
    }

    @Override
    public void onDismiss() {
        dialog.dismiss();
    }

    /**
     * Resets image on {@literal KeyEvent.KEYCODE_BACK} to normal scale if needed, otherwise - hide the viewer.
     */
    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP && !event.isCanceled()) {
            if (viewer.isScaled()) {
                viewer.resetScale();
            } else {
                dialog.cancel();
            }
        }
        return true;
    }

    private @StyleRes
    int getDialogStyle() {
        return builder.shouldStatusBarHide ? android.R.style.Theme_Translucent_NoTitleBar_Fullscreen : android.R.style.Theme_Translucent_NoTitleBar;
    }

    public interface OnImageChangeListener {
        void onImageChange(int position);
    }

    public interface OnDismissListener {
        void onDismiss();
    }

    public static class Builder {

        private Context context;
        private @ColorInt
        int backgroundColor = Color.BLACK;
        private int startPosition;
        private OnImageChangeListener imageChangeListener;
        private OnDismissListener onDismissListener;
        private View overlayView;
        private int[] containerPaddingPixels = new int[4];
        private boolean shouldStatusBarHide = true;
        private boolean isSwipeToDismissAllowed = true;
        private List<Object> storiesDataArrayList;

        public Builder(Context context, List<Object> storiesDataArrayList) {
            this.context = context;
            this.storiesDataArrayList = storiesDataArrayList;
        }

        public Builder setStartPosition(int position) {
            this.startPosition = position;
            return this;
        }

        public void setImageChangeListener(OnImageChangeListener imageChangeListener) {
            this.imageChangeListener = imageChangeListener;
        }

        public Builder setOverlayView(View view) {
            this.overlayView = view;
            return this;
        }

        public Builder hideStatusBar(boolean shouldHide) {
            this.shouldStatusBarHide = shouldHide;
            return this;
        }

        public Builder allowSwipeToDismiss(boolean value) {
            this.isSwipeToDismissAllowed = value;
            return this;
        }

        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            this.onDismissListener = onDismissListener;
            return this;
        }

        public ImageViewer build() {
            return new ImageViewer(this, storiesDataArrayList);
        }

        void createDialog(ImageViewer imageViewer) {
            imageViewer.createDialog();
        }

        public ImageViewer show() {
            ImageViewer dialog = build();
            createDialog(dialog);
            dialog.show();
            return dialog;
        }
    }
}

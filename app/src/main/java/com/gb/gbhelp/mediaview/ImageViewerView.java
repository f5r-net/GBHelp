package com.gb.gbhelp.mediaview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.core.view.GestureDetectorCompat;
import androidx.viewpager.widget.ViewPager;

import com.gb.gbhelp.GB;
import com.gb.gbhelp.R;

import java.util.ArrayList;
import java.util.List;

public class ImageViewerView extends RelativeLayout implements OnDismissListener, SwipeToDismissListener.OnViewMoveListener {

    public ViewPagerAdapter viewPagerAdapter;
    private View backgroundView;
    private MultiTouchViewPager viewPager;
    private SwipeDirectionDetector directionDetector;
    private ScaleGestureDetector scaleDetector;
    private ViewPager.OnPageChangeListener pageChangeListener;
    private GestureDetectorCompat gestureDetector;

    private ViewGroup dismissContainer;
    private SwipeToDismissListener swipeDismissListener;
    private View overlayView;

    private SwipeDirectionDetector.Direction direction;

    private boolean wasScaled;
    private OnDismissListener onDismissListener;
    private boolean isOverlayWasClicked;

    private boolean isSwipeToDismissAllowed = true;

    public ImageViewerView(Context context) {
        super(context);
        init();
    }

    public ImageViewerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageViewerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setUrls(int startPosition, List<Object> storiesDataArrayList) {
        viewPagerAdapter = new ViewPagerAdapter(getContext(), storiesDataArrayList);
        viewPager.setAdapter(viewPagerAdapter);
        setStartPosition(startPosition);
    }

    @Override
    public void setBackgroundColor(int color) {
        findViewById(R.id.backgroundView)
                .setBackgroundColor(color);
    }

    public void setOverlayView(View view) {
        this.overlayView = view;
        if (overlayView != null) {
            dismissContainer.addView(view);
        }
    }

    public void allowSwipeToDismiss(boolean allowSwipeToDismiss) {
        this.isSwipeToDismissAllowed = allowSwipeToDismiss;
    }

    public void setContainerPadding(int[] paddingPixels) {
        viewPager.setPadding(
                paddingPixels[0],
                paddingPixels[1],
                paddingPixels[2],
                paddingPixels[3]);
    }

    private void init() {
        inflate(getContext(), R.layout.image_viewer, this);

        backgroundView = findViewById(R.id.backgroundView);
        viewPager = findViewById(R.id.pager);

        dismissContainer = findViewById(R.id.container);
        swipeDismissListener = new SwipeToDismissListener(findViewById(R.id.dismissView), this, this);
        dismissContainer.setOnTouchListener(swipeDismissListener);

        directionDetector = new SwipeDirectionDetector(getContext()) {
            @Override
            public void onDirectionDetected(Direction direction) {
                ImageViewerView.this.direction = direction;
            }
        };

        scaleDetector = new ScaleGestureDetector(getContext(),
                new ScaleGestureDetector.SimpleOnScaleGestureListener());

        gestureDetector = new GestureDetectorCompat(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (viewPager.isScrolled()) {
                    onClick(e, isOverlayWasClicked);
                }
                return false;
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        GB.printLog("dispatchTouchEvent");
        onUpDownEvent(event);

        if (direction == null) {
            if (scaleDetector.isInProgress() || event.getPointerCount() > 1) {
                wasScaled = true;
                return viewPager.dispatchTouchEvent(event);
            }
        }

        //    if (!adapter.isScaled(pager.getCurrentItem())) {
        directionDetector.onTouchEvent(event);
        if (direction != null) {
            switch (direction) {
                case UP:
                case DOWN:
                    if (isSwipeToDismissAllowed && !wasScaled && viewPager.isScrolled()) {
                        return swipeDismissListener.onTouch(dismissContainer, event);
                    } else break;
                case LEFT:
                case RIGHT:
                    return viewPager.dispatchTouchEvent(event);
            }
            //   }
            return true;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onDismiss() {
        if (onDismissListener != null) {
            onDismissListener.onDismiss();
        }
    }

    @Override
    public void onViewMove(float translationY, int translationLimit) {
        GB.printLog("onViewMove");
        float alpha = 1.0f - (1.0f / translationLimit / 4) * Math.abs(translationY);
        backgroundView.setAlpha(alpha);
        if (overlayView != null) overlayView.setAlpha(alpha);
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public void resetScale() {
        GB.printLog("resetScale");
        //  adapter.resetScale(pager.getCurrentItem());
    }

    public boolean isScaled() {
        return false;//adapter.isScaled(pager.getCurrentItem());
    }

    public void setPageChangeListener(ViewPager.OnPageChangeListener pageChangeListener) {
        viewPager.removeOnPageChangeListener(this.pageChangeListener);
        this.pageChangeListener = pageChangeListener;
        viewPager.addOnPageChangeListener(pageChangeListener);
        pageChangeListener.onPageSelected(viewPager.getCurrentItem());
    }

    private void setStartPosition(int position) {
        viewPager.setCurrentItem(position);
    }

    private void onUpDownEvent(MotionEvent event) {
        GB.printLog("onUpDownEvent");
        if (event.getAction() == MotionEvent.ACTION_UP) {
            onActionUp(event);
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            onActionDown(event);
        }

        scaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
    }

    private void onActionDown(MotionEvent event) {
        GB.printLog("onActionDown");
        direction = null;
        wasScaled = false;
        viewPager.dispatchTouchEvent(event);
        swipeDismissListener.onTouch(dismissContainer, event);
        isOverlayWasClicked = dispatchOverlayTouch(event);
    }

    private void onActionUp(MotionEvent event) {
        GB.printLog("onActionUp");
        swipeDismissListener.onTouch(dismissContainer, event);
        viewPager.dispatchTouchEvent(event);
        isOverlayWasClicked = dispatchOverlayTouch(event);
    }

    private void onClick(MotionEvent event, boolean isOverlayWasClicked) {
        GB.printLog("onClick");
        if (overlayView != null && !isOverlayWasClicked) {
            AnimationUtils.animateVisibility(overlayView);
            super.dispatchTouchEvent(event);
        }
    }

    private boolean dispatchOverlayTouch(MotionEvent event) {
        GB.printLog("dispatchOverlayTouch");
        return overlayView != null
                && overlayView.getVisibility() == VISIBLE
                && overlayView.dispatchTouchEvent(event);
    }

}

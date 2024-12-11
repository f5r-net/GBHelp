package com.gb.gbhelp.mediaview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Scroller;

public class TouchImage extends androidx.appcompat.widget.AppCompatImageView {

    public static String A = "a";
    public static String C = "c";
    public static String D = "d";
    private Context context;
    private c fling;
    private float[] m;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleDetector;
    private boolean maintainZoomAfterSetImage;
    private float matchViewHeight;
    private float matchViewWidth;
    private Matrix matrix;
    private float maxScale;
    private float minScale;
    private float normalizedScale;
    private float prevMatchViewHeight;
    private float prevMatchViewWidth;
    private Matrix prevMatrix;
    private int prevViewHeight;
    private int prevViewWidth;
    private boolean setImageCalledRecenterImage;
    private State state;
    private float superMaxScale;
    private float superMinScale;
    private int viewHeight;
    private int viewWidth;

    public TouchImage(Context context) {
        super(context);
        sharedConstructing(context);
    }

    public TouchImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructing(context);
    }

    public TouchImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        sharedConstructing(context);
    }

    @TargetApi(16)
    private void compatPostOnAnimation(Runnable runnable) {
        if (VERSION.SDK_INT >= 16) {
            //		postOnAnimation(runnable);
        } else {
            postDelayed(runnable, 16);
        }
    }

    private void fitImageToView() {
        Drawable drawable = getDrawable();
        if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0 || matrix == null || prevMatrix == null) {
        } else {
            int drawableWidth = drawable.getIntrinsicWidth();
            int drawableHeight = drawable.getIntrinsicHeight();
            float scale = Math.min(((float) viewWidth) / ((float) drawableWidth), ((float) viewHeight) / ((float) drawableHeight));
            float redundantYSpace = ((float) viewHeight) - (((float) drawableHeight) * scale);
            float redundantXSpace = ((float) viewWidth) - (((float) drawableWidth) * scale);
            matchViewWidth = ((float) viewWidth) - redundantXSpace;
            matchViewHeight = ((float) viewHeight) - redundantYSpace;
            if (normalizedScale == 1.0f || setImageCalledRecenterImage) {
                matrix.setScale(scale, scale);
                matrix.postTranslate(redundantXSpace / 2.0f, redundantYSpace / 2.0f);
                normalizedScale = 1.0f;
                setImageCalledRecenterImage = false;
            } else {
                prevMatrix.getValues(m);
                m[0] = (matchViewWidth / ((float) drawableWidth)) * normalizedScale;
                m[4] = (matchViewHeight / ((float) drawableHeight)) * normalizedScale;
                translateMatrixAfterRotate(2, m[2], prevMatchViewWidth * normalizedScale, getImageWidth(), prevViewWidth, viewWidth, drawableWidth);
                translateMatrixAfterRotate(5, m[5], prevMatchViewHeight * normalizedScale, getImageHeight(), prevViewHeight, viewHeight, drawableHeight);
                matrix.setValues(m);
            }
            setImageMatrix(matrix);
        }
    }

    private void fixScaleTrans() {
        fixTrans();
        matrix.getValues(m);
        if (getImageWidth() < ((float) viewWidth)) {
            m[2] = (((float) viewWidth) - getImageWidth()) / 2.0f;
        }
        if (getImageHeight() < ((float) viewHeight)) {
            m[5] = (((float) viewHeight) - getImageHeight()) / 2.0f;
        }
        matrix.setValues(m);
    }

    private float getFixDragTrans(float delta, float viewSize, float contentSize) {
        if (contentSize <= viewSize) {
            delta = 0.0f;
        }
        return delta;
    }

    private float getFixTrans(float trans, float viewSize, float contentSize) {
        float minTrans;
        float maxTrans;
        if (contentSize <= viewSize) {
            minTrans = 0.0f;
            maxTrans = viewSize - contentSize;
        } else {
            minTrans = viewSize - contentSize;
            maxTrans = 0.0f;
        }
        if (trans < minTrans) {
            return (-trans) + minTrans;
        } else if (trans > maxTrans) {
            return (-trans) + maxTrans;
        } else {
            return 0.0f;
        }
    }

    private float getImageHeight() {
        return matchViewHeight * normalizedScale;
    }

    private float getImageWidth() {
        return matchViewWidth * normalizedScale;
    }

    private void savePreviousImageValues() {
        if (matrix != null) {
            matrix.getValues(m);
            prevMatrix.setValues(m);
            prevMatchViewHeight = matchViewHeight;
            prevMatchViewWidth = matchViewWidth;
            prevViewHeight = viewHeight;
            prevViewWidth = viewWidth;
        }
    }

    private void scaleImage(float deltaScale, float focusX, float focusY, boolean stretchImageToSuper) {
        float lowerScale;
        float upperScale;
        if (stretchImageToSuper) {
            lowerScale = superMinScale;
            upperScale = superMaxScale;
        } else {
            lowerScale = minScale;
            upperScale = maxScale;
        }
        float origScale = normalizedScale;
        normalizedScale *= deltaScale;
        if (normalizedScale > upperScale) {
            normalizedScale = upperScale;
            deltaScale = upperScale / origScale;
        } else if (normalizedScale < lowerScale) {
            normalizedScale = lowerScale;
            deltaScale = lowerScale / origScale;
        }
        matrix.postScale(deltaScale, deltaScale, focusX, focusY);
        fixScaleTrans();
    }

    private void setImageCalled() {
        if (!maintainZoomAfterSetImage) {
            setImageCalledRecenterImage = true;
        }
    }

    private void setState(State state) {
        this.state = state;
    }

    private int setViewSize(int mode, int size, int drawableWidth) {
        switch (mode) {
            case -2147483648:
                return Math.min(drawableWidth, size);
            case 0:
                return drawableWidth;
            case 1073741824:
                return size;
        }
        return size;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void sharedConstructing(Context context) {
        super.setClickable(true);
        this.context = context;
        mScaleDetector = new ScaleGestureDetector(context, new e(this, null));
        mGestureDetector = new GestureDetector(context, new d(this, null));
        matrix = new Matrix();
        prevMatrix = new Matrix();
        m = new float[9];
        normalizedScale = 1.0f;
        minScale = 1.0f;
        maxScale = 3.0f;
        superMinScale = 0.75f * minScale;
        superMaxScale = 1.25f * maxScale;
        maintainZoomAfterSetImage = true;
        setImageMatrix(matrix);
        setScaleType(ScaleType.MATRIX);
        setState(State.NONE);
        setOnTouchListener(new f(this, null));
    }

    private PointF transformCoordBitmapToTouch(float bx, float by) {
        matrix.getValues(m);
        return new PointF(m[2] + (getImageWidth() * (bx / ((float) getDrawable().getIntrinsicWidth()))), m[5] + (getImageHeight() * (by / ((float) getDrawable().getIntrinsicHeight()))));
    }

    private PointF transformCoordTouchToBitmap(float x, float y, boolean clipToBitmap) {
        matrix.getValues(m);
        float origW = (float) getDrawable().getIntrinsicWidth();
        float origH = (float) getDrawable().getIntrinsicHeight();
        float finalX = ((x - m[2]) * origW) / getImageWidth();
        float finalY = ((y - m[5]) * origH) / getImageHeight();
        if (clipToBitmap) {
            finalX = Math.min(Math.max(x, 0.0f), origW);
            finalY = Math.min(Math.max(y, 0.0f), origH);
        }
        return new PointF(finalX, finalY);
    }

    private void translateMatrixAfterRotate(int axis, float trans, float prevImageSize, float imageSize, int prevViewSize, int viewSize, int drawableSize) {
        if (imageSize < ((float) viewSize)) {
            m[axis] = (((float) viewSize) - (((float) drawableSize) * m[0])) * 0.5f;
        } else if (trans > 0.0f) {
            m[axis] = -((imageSize - ((float) viewSize)) * 0.5f);
        } else {
            m[axis] = -((((Math.abs(trans) + (((float) prevViewSize) * 0.5f)) / prevImageSize) * imageSize) - (((float) viewSize) * 0.5f));
        }
    }

    public void fixTrans() {
        matrix.getValues(m);
        float fixTransX = getFixTrans(m[2], (float) viewWidth, getImageWidth());
        float fixTransY = getFixTrans(m[5], (float) viewHeight, getImageHeight());
        if (fixTransX != 0.0f || fixTransY != 0.0f) {
            matrix.postTranslate(fixTransX, fixTransY);
        }
    }

    public float getCurrentZoom() {
        return normalizedScale;
    }

    public PointF getDrawablePointFromTouchPoint(float x, float y) {
        return transformCoordTouchToBitmap(x, y, true);
    }

    public PointF getDrawablePointFromTouchPoint(PointF p) {
        return transformCoordTouchToBitmap(p.x, p.y, true);
    }

    public float getMaxZoom() {
        return maxScale;
    }

    public void setMaxZoom(float max) {
        maxScale = max;
        superMaxScale = 1.25f * maxScale;
    }

    public float getMinZoom() {
        return minScale;
    }

    public void setMinZoom(float min) {
        minScale = min;
        superMinScale = 0.75f * minScale;
    }

    public void maintainZoomAfterSetImage(boolean maintainZoom) {
        maintainZoomAfterSetImage = maintainZoom;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable drawable = getDrawable();
        if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
            setMeasuredDimension(0, 0);
        } else {
            viewWidth = setViewSize(MeasureSpec.getMode(widthMeasureSpec), MeasureSpec.getSize(widthMeasureSpec), drawable.getIntrinsicWidth());
            viewHeight = setViewSize(MeasureSpec.getMode(heightMeasureSpec), MeasureSpec.getSize(heightMeasureSpec), drawable.getIntrinsicHeight());
            setMeasuredDimension(viewWidth, viewHeight);
            fitImageToView();
        }
    }

    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            normalizedScale = bundle.getFloat("saveScale");
            m = bundle.getFloatArray("matrix");
            prevMatrix.setValues(m);
            prevMatchViewHeight = bundle.getFloat("matchViewHeight");
            prevMatchViewWidth = bundle.getFloat("matchViewWidth");
            prevViewHeight = bundle.getInt("viewHeight");
            prevViewWidth = bundle.getInt("viewWidth");
            super.onRestoreInstanceState(bundle.getParcelable("instanceState"));
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putFloat("saveScale", normalizedScale);
        bundle.putFloat("matchViewHeight", matchViewHeight);
        bundle.putFloat("matchViewWidth", matchViewWidth);
        bundle.putInt("viewWidth", viewWidth);
        bundle.putInt("viewHeight", viewHeight);
        matrix.getValues(m);
        bundle.putFloatArray("matrix", m);
        return bundle;
    }

    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        setImageCalled();
        savePreviousImageValues();
        fitImageToView();
    }

    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        setImageCalled();
        savePreviousImageValues();
        fitImageToView();
    }

    public void setImageResource(int resId) {
        super.setImageResource(resId);
        setImageCalled();
        savePreviousImageValues();
        fitImageToView();
    }

    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        setImageCalled();
        savePreviousImageValues();
        fitImageToView();
    }

    public enum State {
        NONE,
        DRAG,
        ZOOM,
        FLING,
        ANIMATE_ZOOM;


        static {

        }
    }

    private class b implements Runnable {

        final /* synthetic */ TouchImage this$0;
        private float bitmapX;
        private float bitmapY;
        private PointF endTouch;
        private AccelerateDecelerateInterpolator interpolator;
        private long startTime;
        private PointF startTouch;
        private float startZoom;
        private boolean stretchImageToSuper;
        private float targetZoom;

        b(TouchImage r5_TouchImageView, float targetZoom, float focusX, float focusY, boolean stretchImageToSuper) {
            super();
            this$0 = r5_TouchImageView;
            interpolator = new AccelerateDecelerateInterpolator();
            r5_TouchImageView.setState(State.ANIMATE_ZOOM);
            startTime = System.currentTimeMillis();
            startZoom = r5_TouchImageView.normalizedScale;
            this.targetZoom = targetZoom;
            this.stretchImageToSuper = stretchImageToSuper;
            PointF bitmapPoint = r5_TouchImageView.transformCoordTouchToBitmap(focusX, focusY, false);
            bitmapX = bitmapPoint.x;
            bitmapY = bitmapPoint.y;
            startTouch = r5_TouchImageView.transformCoordBitmapToTouch(bitmapX, bitmapY);
            endTouch = new PointF((float) (r5_TouchImageView.viewWidth / 2), (float) (r5_TouchImageView.viewHeight / 2));
        }

        private float calculateDeltaScale(float t) {
            return (startZoom + ((targetZoom - startZoom) * t)) / this$0.normalizedScale;
        }

        private float interpolate() {
            return interpolator.getInterpolation(Math.min(1.0f, ((float) (System.currentTimeMillis() - startTime)) / 500.0f));
        }

        private void translateImageToCenterTouchPosition(float t) {
            PointF curr = this$0.transformCoordBitmapToTouch(bitmapX, bitmapY);
            this$0.matrix.postTranslate((startTouch.x + ((endTouch.x - startTouch.x) * t)) - curr.x, (startTouch.y + ((endTouch.y - startTouch.y) * t)) - curr.y);
        }

        public void run() {
            float t = interpolate();
            this$0.scaleImage(calculateDeltaScale(t), bitmapX, bitmapY, stretchImageToSuper);
            translateImageToCenterTouchPosition(t);
            this$0.fixScaleTrans();
            this$0.setImageMatrix(this$0.matrix);
            if (t < 1.0f) {
                this$0.compatPostOnAnimation(this);
            } else {
                this$0.setState(State.NONE);
            }
        }
    }

    private class c implements Runnable {
        final /* synthetic */ TouchImage this$0;
        int currX;
        int currY;
        Scroller scroller;

        c(TouchImage r10_TouchImageView, int velocityX, int velocityY) {
            int minX;
            int maxX;
            int minY;
            int maxY;
            this$0 = r10_TouchImageView;
            r10_TouchImageView.setState(State.FLING);
            scroller = new Scroller(r10_TouchImageView.context);
            r10_TouchImageView.matrix.getValues(r10_TouchImageView.m);
            int startX = (int) r10_TouchImageView.m[2];
            int startY = (int) r10_TouchImageView.m[5];
            if (r10_TouchImageView.getImageWidth() > ((float) r10_TouchImageView.viewWidth)) {
                minX = r10_TouchImageView.viewWidth - ((int) r10_TouchImageView.getImageWidth());
                maxX = 0;
            } else {
                maxX = startX;
                minX = startX;
            }
            if (r10_TouchImageView.getImageHeight() > ((float) r10_TouchImageView.viewHeight)) {
                minY = r10_TouchImageView.viewHeight - ((int) r10_TouchImageView.getImageHeight());
                maxY = 0;
            } else {
                maxY = startY;
                minY = startY;
            }
            scroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
            currX = startX;
            currY = startY;
        }

        public void cancelFling() {
            if (scroller != null) {
                this$0.setState(State.NONE);
                scroller.forceFinished(true);
            }
        }

        public void run() {
            if (scroller.isFinished()) {
                scroller = null;
            } else if (scroller.computeScrollOffset()) {
                int newX = scroller.getCurrX();
                int newY = scroller.getCurrY();
                currX = newX;
                currY = newY;
                this$0.matrix.postTranslate((float) (newX - currX), (float) (newY - currY));
                this$0.fixTrans();
                this$0.setImageMatrix(this$0.matrix);
                this$0.compatPostOnAnimation(this);
            }
        }
    }

    private class d extends SimpleOnGestureListener {
        final /* synthetic */ TouchImage this$0;

        private d(TouchImage r1_TouchImageView) {
            super();
            this$0 = r1_TouchImageView;
        }

        /* synthetic */ d(TouchImage r1_TouchImageView, d r2_TouchImageView_GestureListener) {
            this(r1_TouchImageView);
        }

        public boolean onDoubleTap(MotionEvent e) {
            if (this$0.state == State.NONE) {
                float targetZoom;
                if (this$0.normalizedScale == this$0.minScale) {
                    targetZoom = this$0.maxScale;
                } else {
                    targetZoom = this$0.minScale;
                }
                this$0.compatPostOnAnimation(new b(this$0, targetZoom, e.getX(), e.getY(), false));
                return true;
            } else {
                return false;
            }
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (this$0.fling != null) {
                this$0.fling.cancelFling();
            }
            this$0.fling = new c(this$0, (int) velocityX, (int) velocityY);
            this$0.compatPostOnAnimation(this$0.fling);
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        public void onLongPress(MotionEvent e) {
            this$0.performLongClick();
        }

        public boolean onSingleTapConfirmed(MotionEvent e) {
            return this$0.performClick();
        }
    }

    private class e extends SimpleOnScaleGestureListener {
        final /* synthetic */ TouchImage this$0;

        private e(TouchImage r1_TouchImageView) {
            super();
            this$0 = r1_TouchImageView;
        }

        /* synthetic */ e(TouchImage r1_TouchImageView, e r2_TouchImageView_ScaleListener) {
            this(r1_TouchImageView);
        }

        public boolean onScale(ScaleGestureDetector detector) {
            this$0.scaleImage(detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY(), true);
            return true;
        }

        public boolean onScaleBegin(ScaleGestureDetector detector) {
            this$0.setState(State.ZOOM);
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
            this$0.setState(State.NONE);
            boolean animateToZoomBoundary = false;
            float targetZoom = this$0.normalizedScale;
            if (this$0.normalizedScale > this$0.maxScale) {
                targetZoom = this$0.maxScale;
                animateToZoomBoundary = true;
            } else if (this$0.normalizedScale < this$0.minScale) {
                targetZoom = this$0.minScale;
                animateToZoomBoundary = true;
            }
            if (animateToZoomBoundary) {
                this$0.compatPostOnAnimation(new b(this$0, targetZoom, (float) (this$0.viewWidth / 2), (float) (this$0.viewHeight / 2), true));
            }
        }
    }

    private class f implements OnTouchListener {
        final /* synthetic */ TouchImage this$0;
        private PointF last;

        private f(TouchImage r2_TouchImageView) {
            super();
            this$0 = r2_TouchImageView;
            last = new PointF();
        }

        /* synthetic */ f(TouchImage r1_TouchImageView, f r2_TouchImageView_TouchImageViewListener) {
            this(r1_TouchImageView);
        }

        @SuppressLint("ClickableViewAccessibility")
        public boolean onTouch(View v, MotionEvent event) {
            this$0.mScaleDetector.onTouchEvent(event);
            this$0.mGestureDetector.onTouchEvent(event);
            PointF curr = new PointF(event.getX(), event.getY());
            if (this$0.state == State.NONE || this$0.state == State.DRAG || this$0.state == State.FLING) {
                switch (event.getAction()) {
                    case 0:
                        last.set(curr);
                        if (this$0.fling != null) {
                            this$0.fling.cancelFling();
                        }
                        this$0.setState(State.DRAG);
                        break;
                    case 1:
                    case 6:
                        this$0.setState(State.NONE);
                        break;
                    case 2:
                        if (this$0.state == State.DRAG) {
                            this$0.matrix.postTranslate(this$0.getFixDragTrans(curr.x - last.x, (float) this$0.viewWidth, this$0.getImageWidth()), this$0.getFixDragTrans(curr.y - last.y, (float) this$0.viewHeight, this$0.getImageHeight()));
                            this$0.fixTrans();
                            last.set(curr.x, curr.y);
                        }
                        break;
                }
            } else {
                this$0.setImageMatrix(this$0.matrix);
                return true;
            }
            this$0.setImageMatrix(this$0.matrix);
            return true;
        }
    }
}

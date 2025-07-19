package com.neew.browser;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * Custom view that displays a TV-style cursor with auto-hide functionality.
 * The cursor can be configured to automatically hide after a period of inactivity.
 */
public class TvCursorView extends View {
    private Paint fillPaint;
    private Paint borderPaint;
    private Path arrowPath;
    private int cursorSize = 40; // Size of the cursor in pixels

    private Paint progressPaint;
    private Path progressPath;
    private PathMeasure pathMeasure;
    private float progress = 0; // 0 to 100
    private boolean isProgressVisible = false;
    
    // Auto-hide functionality
    private static final long CURSOR_HIDE_DELAY_MS = 3000; // 3 seconds
    private final Handler hideHandler = new Handler(Looper.getMainLooper());
    private final Runnable hideRunnable = this::hideCursor;
    private boolean isAutoHideEnabled = false;
    private static final String TAG = "TvCursorView";

    public TvCursorView(Context context) {
        super(context);
        init();
    }

    public TvCursorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TvCursorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Initialize the cursor view with default settings.
     */
    private void init() {
        Log.d(TAG, "Initializing TvCursorView");
        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(3f); // Border width

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(5f); // Make progress border thicker
        progressPaint.setColor(Color.parseColor("#2196F3")); // Use the primary blue color
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        // Detect current theme and set colors
        int nightModeFlags = getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            // Dark mode
            fillPaint.setColor(Color.WHITE);
            borderPaint.setColor(Color.BLACK);
        } else {
            // Light mode
            fillPaint.setColor(Color.BLACK);
            borderPaint.setColor(Color.WHITE);
        }

        arrowPath = new Path();
        progressPath = new Path();
        pathMeasure = new PathMeasure();
        updateArrowPath();
    }

    /**
     * Update the cursor's arrow path based on current size.
     */
    private void updateArrowPath() {
        float halfSize = cursorSize / 2f;
        arrowPath.reset();
        arrowPath.moveTo(0, -halfSize); // Top point
        arrowPath.lineTo(halfSize, halfSize); // Bottom right
        arrowPath.lineTo(-halfSize, halfSize); // Bottom left
        arrowPath.close();
        // Update PathMeasure with the new path
        pathMeasure.setPath(arrowPath, true);
    }

    /**
     * Show the cursor and reset the auto-hide timer if enabled
     */
    public void showCursor() {
        Log.d(TAG, "Showing cursor");
        setVisibility(View.VISIBLE);
        resetHideTimer();
    }

    /**
     * Hide the cursor immediately
     */
    public void hideCursor() {
        Log.d(TAG, "Hiding cursor");
        setVisibility(View.INVISIBLE);
    }

    /**
     * Reset the auto-hide timer
     */
    public void resetHideTimer() {
        if (isAutoHideEnabled) {
            hideHandler.removeCallbacks(hideRunnable);
            hideHandler.postDelayed(hideRunnable, CURSOR_HIDE_DELAY_MS);
            Log.d(TAG, "Reset cursor hide timer");
        }
    }

    /**
     * Enable or disable auto-hide functionality
     * @param enabled true to enable auto-hide, false to disable
     */
    public void setAutoHideEnabled(boolean enabled) {
        if (isAutoHideEnabled != enabled) {
            isAutoHideEnabled = enabled;
            if (enabled) {
                resetHideTimer();
                Log.d(TAG, "Auto-hide enabled");
            } else {
                hideHandler.removeCallbacks(hideRunnable);
                setVisibility(View.VISIBLE);
                Log.d(TAG, "Auto-hide disabled");
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getVisibility() != View.VISIBLE) {
            return; // Don't draw if cursor is hidden
        }
        
        canvas.save();
        canvas.translate(getWidth() / 2f, getHeight() / 2f);

        // Draw border first
        canvas.drawPath(arrowPath, borderPaint);
        // Draw fill on top
        canvas.drawPath(arrowPath, fillPaint);

        if (isProgressVisible) {
            float length = pathMeasure.getLength();
            float stop = length * (progress / 100f);
            progressPath.reset();
            pathMeasure.getSegment(0, stop, progressPath, true);
            canvas.drawPath(progressPath, progressPaint);
        }

        canvas.restore();
    }

    public void setCursorSize(int size) {
        cursorSize = size;
        updateArrowPath();
        invalidate();
    }

    public void setProgress(int progress) {
        if (progress >= 0 && progress <= 100) {
            this.progress = progress;
            if(isProgressVisible) invalidate();
        }
    }

    public void showProgress() {
        isProgressVisible = true;
        this.progress = 0; // Reset progress when shown
        invalidate();
    }

    public void hideProgress() {
        isProgressVisible = false;
        invalidate();
    }
}
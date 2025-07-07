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

import androidx.annotation.Nullable;

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

    private void init() {
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
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
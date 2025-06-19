package com.neew.browser;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.content.res.Configuration;

public class TvCursorView extends View {
    private Paint fillPaint;
    private Paint borderPaint;
    private Path arrowPath;
    private int cursorSize = 40; // Size of the cursor in pixels

    public TvCursorView(Context context) {
        super(context);
        init();
    }

    public TvCursorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(3f); // Border width

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

        // Create arrow path
        arrowPath = new Path();
        updateArrowPath();
    }

    private void updateArrowPath() {
        float halfSize = cursorSize / 2f;
        arrowPath.reset();
        arrowPath.moveTo(0, -halfSize); // Top point
        arrowPath.lineTo(halfSize, halfSize); // Bottom right
        arrowPath.lineTo(-halfSize, halfSize); // Bottom left
        arrowPath.close();
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
        
        canvas.restore();
    }

    public void setCursorSize(int size) {
        cursorSize = size;
        updateArrowPath();
        invalidate();
    }
} 
package com.neew.browser;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class TvCursorView extends View {
    private Paint paint;
    private Path arrowPath;
    private int cursorColor = Color.WHITE;
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
        paint = new Paint();
        paint.setColor(cursorColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

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
        // Draw the arrow centered in the view
        canvas.save();
        canvas.translate(getWidth() / 2f, getHeight() / 2f);
        canvas.drawPath(arrowPath, paint);
        canvas.restore();
    }

    public void setCursorColor(int color) {
        cursorColor = color;
        paint.setColor(color);
        invalidate();
    }

    public void setCursorSize(int size) {
        cursorSize = size;
        updateArrowPath();
        invalidate();
    }
} 
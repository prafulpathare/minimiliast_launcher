package com.minimal.launcher;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class BatteryView extends View {

    private float batteryLevel = 50f;
    private Paint trackPaint;
    private Paint barPaint;

    public BatteryView(Context context) { super(context); init(); }
    public BatteryView(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    public BatteryView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); init(); }

    private void init() {
        trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeCap(Paint.Cap.ROUND);
        trackPaint.setColor(0xFFF0F0F0);

        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barPaint.setStyle(Paint.Style.STROKE);
        barPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setBatteryLevel(float level) {
        this.batteryLevel = level;
        if (batteryLevel < 20f) {
            barPaint.setColor(0xFFFF3B30); // red
        } else if (batteryLevel < 75f) {
            barPaint.setColor(0xFFFF9500); // orange
        } else {
            barPaint.setColor(0xFF30D158); // green
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = getWidth();
        float h = getHeight();
        float strokeWidth = h;
        float halfStroke  = strokeWidth / 2f;

        trackPaint.setStrokeWidth(strokeWidth);
        barPaint.setStrokeWidth(strokeWidth);

        float y = h / 2f;
        float startX = halfStroke;
        float endX   = w - halfStroke;
        float fillX  = startX + (endX - startX) * (batteryLevel / 100f);

        // track
        canvas.drawLine(startX, y, endX, y, trackPaint);
        // bar
        if (batteryLevel > 0) {
            canvas.drawLine(startX, y, fillX, y, barPaint);
        }
    }
}
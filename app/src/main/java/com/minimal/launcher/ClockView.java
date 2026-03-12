package com.minimal.launcher;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;

public class ClockView extends View {

    private Paint bgCirclePaint;
    private Paint progressPaint;
    private Paint timePaint;
    private Paint datePaint;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            invalidate();
            handler.postDelayed(this, 1000);
        }
    };

    public ClockView(Context context) {
        super(context);
        init();
    }

    public ClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Grey background ring
        bgCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgCirclePaint.setStyle(Paint.Style.STROKE);
//        bgCirclePaint.setColor(0xFF444444);
        bgCirclePaint.setColor(0xFF1A1A1A);
        bgCirclePaint.setStrokeCap(Paint.Cap.BUTT);

        // White progress arc
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setColor(0xFFFFFFFF);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        // Time text
        timePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        timePaint.setColor(0xFFFFFFFF);
        timePaint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        timePaint.setTextAlign(Paint.Align.CENTER);

        // Date text
        datePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        datePaint.setColor(0xFF888888);
        datePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        datePaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        handler.post(ticker);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacks(ticker);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();
        float cx = w / 2f;
        float cy = h / 2f;
        float radius = Math.min(cx, cy);
        float strokeWidth = radius * 0.02f;
        float arcInset = strokeWidth / 2f + 4f;

        bgCirclePaint.setStrokeWidth(strokeWidth);
        progressPaint.setStrokeWidth(strokeWidth);
        timePaint.setTextSize(radius * 0.30f);
        datePaint.setTextSize(radius * 0.18f);

        // Get current time
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        // Progress: total seconds elapsed in day out of 86400
        int hour12 = hour % 12;
        float totalSeconds = hour12 * 3600 + minute * 60 + second;
        float fraction = totalSeconds / 43200f;
        float sweepAngle = fraction * 360f;

        RectF oval = new RectF(arcInset, arcInset, w - arcInset, h - arcInset);

        // Draw grey full circle
        canvas.drawArc(oval, -90f, 360f, false, bgCirclePaint);

        // Draw white arc clockwise from top
        if (sweepAngle > 0) {
            canvas.drawArc(oval, -90f, sweepAngle, false, progressPaint);
        }

        // Format strings
        String hourStr = String.format("%02d", hour);
        String minStr = String.format("%02d", minute);
        String secStr = String.format("%02d", second);
        String dateStr = String.format("%02d/%02d/%04d", day, month, year);

        Paint hourPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hourPaint.setColor(0xFFFFFFFF);
        hourPaint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        hourPaint.setTextAlign(Paint.Align.LEFT);
        hourPaint.setTextSize(radius * 0.30f);

        Paint minPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        minPaint.setColor(0xFF888888);
        minPaint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        minPaint.setTextAlign(Paint.Align.LEFT);
        minPaint.setTextSize(radius * 0.30f);

        Paint secPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        secPaint.setColor(0xFF555555);
        secPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        secPaint.setTextAlign(Paint.Align.LEFT);
        secPaint.setTextSize(radius * 0.30f);
        float gap = radius * 0.03f;

        float hourWidth = hourPaint.measureText(hourStr);
        float minWidth  = minPaint.measureText(minStr);
        float secWidth  = secPaint.measureText(secStr);
        float totalWidth = hourWidth + minWidth + secWidth + gap * 2;

        float startX = cx - totalWidth / 2f;

        Paint.FontMetrics fm = hourPaint.getFontMetrics();
        float textHeight = fm.descent - fm.ascent;
        float dateTextSize = radius * 0.15f;
        datePaint.setTextSize(dateTextSize);
        float blockSpacing = radius * 0.08f;
        float blockHeight = textHeight + blockSpacing + dateTextSize;

        float timeY = cy - blockHeight / 2f - fm.ascent;
        float dateY = timeY + fm.descent + blockSpacing + dateTextSize;

        canvas.drawText(hourStr, startX,                                   timeY, hourPaint);
        canvas.drawText(minStr,  startX + hourWidth + gap,                 timeY, minPaint);
        canvas.drawText(secStr,  startX + hourWidth + gap + minWidth + gap, timeY, secPaint);

        canvas.drawText(dateStr, cx, dateY, datePaint);
    }
}
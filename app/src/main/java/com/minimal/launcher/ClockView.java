package com.minimal.launcher;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;

public class ClockView extends View {

    private Paint datePaint;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private static final String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};

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
        datePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        datePaint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL));
        datePaint.setTextAlign(Paint.Align.CENTER);
        applyTheme();
    }

    public void applyTheme() {
        datePaint.setColor(isDark() ? 0xFF888888 : 0xFF666666);
        invalidate();
    }

    private boolean isDark() {
        int nightMode = getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightMode == Configuration.UI_MODE_NIGHT_YES;
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

        float paddingLeft = 20f;

        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        boolean dark = isDark();
        float timeTextSize = getHeight() * 0.6f;
        float dateTextSize = getHeight() * 0.18f;
        float gap = getWidth() * 0.015f;

        Paint hourPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hourPaint.setColor(dark ? 0xFFFFFFFF : 0xFF000000);
        hourPaint.setTypeface(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? Typeface.create(Typeface.DEFAULT, 900, false) : Typeface.DEFAULT_BOLD);
        hourPaint.setTextAlign(Paint.Align.LEFT);
        hourPaint.setTextSize(timeTextSize);

        Paint minPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        minPaint.setColor(dark ? 0xFF888888 : 0xFF666666);
        minPaint.setTypeface(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? Typeface.create(Typeface.DEFAULT, 600, false) : Typeface.DEFAULT_BOLD);
        minPaint.setTextAlign(Paint.Align.LEFT);
        minPaint.setTextSize(timeTextSize);

        Paint secPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        secPaint.setColor(dark ? 0xFF555555 : 0xFFDDDDDD);
        secPaint.setTypeface(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? Typeface.create(Typeface.DEFAULT, 300, false) : Typeface.DEFAULT);
        secPaint.setTextAlign(Paint.Align.LEFT);
        secPaint.setTextSize(timeTextSize);

        String hourStr = String.format("%02d", hour);
        String minStr = String.format("%02d", minute);
        String secStr = String.format("%02d", second);
        String dateStr = String.format("%02d %s %04d", day, months[month - 1], year);


        float hourWidth = hourPaint.measureText(hourStr);
        float minWidth = minPaint.measureText(minStr);

        Paint.FontMetrics fm = hourPaint.getFontMetrics();
        Paint.FontMetrics secFm = secPaint.getFontMetrics();
        float timeY = -fm.ascent;
        float secOffset = (fm.descent - fm.ascent) / 2f - (secFm.descent - secFm.ascent) / 2f;

        canvas.drawText(hourStr, paddingLeft, timeY, hourPaint);
        canvas.drawText(minStr, paddingLeft + hourWidth + gap, timeY, minPaint);
        canvas.drawText(secStr, paddingLeft + hourWidth + (float) (gap * 0.5) + minWidth + gap, timeY - secOffset, secPaint);

        datePaint.setTextSize(dateTextSize);
        datePaint.setTextAlign(Paint.Align.LEFT);
        datePaint.setTypeface(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? Typeface.create(Typeface.DEFAULT, 500, false) : Typeface.DEFAULT);

        canvas.drawText(dateStr, paddingLeft,  20 + timeY + (-fm.ascent) * 0.15f + dateTextSize, datePaint);
    }
}
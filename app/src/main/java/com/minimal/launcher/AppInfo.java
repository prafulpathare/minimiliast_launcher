package com.minimal.launcher;

import android.graphics.drawable.Drawable;

public class AppInfo {
    public final String label;
    public final String packageName;
    public final String activityName;
    public Drawable icon;

    public AppInfo(String label, String packageName, String activityName, Drawable icon) {
        this.label        = label;
        this.packageName  = packageName;
        this.activityName = activityName;
        this.icon         = icon;
    }
}
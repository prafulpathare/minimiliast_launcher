package com.minimal.launcher;

public class AppInfo {
    public final String label;
    public final String packageName;
    public final String activityName;

    public AppInfo(String label, String packageName, String activityName) {
        this.label        = label;
        this.packageName  = packageName;
        this.activityName = activityName;
    }
}
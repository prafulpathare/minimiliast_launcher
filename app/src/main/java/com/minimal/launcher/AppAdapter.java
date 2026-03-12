package com.minimal.launcher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class AppAdapter {

    private final Context context;
    private final LayoutInflater inflater;
    private List<AppInfo> apps;
    private int textColor = 0xFFDDDDDD;
    private OnAppClickListener listener;

    public interface OnAppClickListener {
        void onAppClick(AppInfo app);
    }

    public AppAdapter(Context context, List<AppInfo> apps) {
        this.context  = context;
        this.apps     = apps;
        this.inflater = LayoutInflater.from(context);
    }

    public void setTextColor(int color) {
        this.textColor = color;
    }

    public void setOnAppClickListener(OnAppClickListener listener) {
        this.listener = listener;
    }

    public void updateList(List<AppInfo> newApps, LinearLayout container) {
        this.apps = newApps;
        rebind(container);
    }

    public void rebind(LinearLayout container) {
        container.removeAllViews();

        // Add spacer to push items to center if fewer than fill screen
        container.setGravity(android.view.Gravity.CENTER);

        for (AppInfo app : apps) {
            View item = inflater.inflate(R.layout.item_app, container, false);

            ImageView icon = item.findViewById(R.id.app_icon);
            TextView  name = item.findViewById(R.id.app_name);

            icon.setImageDrawable(app.icon);
            name.setText(app.label);
            name.setTextColor(textColor);

            item.setOnClickListener(v -> {
                if (listener != null) listener.onAppClick(app);
            });

            container.addView(item);
        }
    }

    public int getCount() {
        return apps.size();
    }

    public AppInfo getItem(int index) {
        return apps.get(index);
    }
}
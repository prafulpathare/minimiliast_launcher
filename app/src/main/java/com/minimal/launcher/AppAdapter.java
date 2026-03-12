package com.minimal.launcher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AppAdapter extends ArrayAdapter<AppInfo> {

    private final LayoutInflater inflater;
    private int textColor = 0xFFDDDDDD;

    public AppAdapter(Context context, List<AppInfo> apps) {
        super(context, 0, apps);
        inflater = LayoutInflater.from(context);
    }

    public void setTextColor(int color) {
        this.textColor = color;
    }

    public void updateList(List<AppInfo> apps) {
        clear();
        addAll(apps);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_app, parent, false);
            holder = new ViewHolder();
            holder.nameText = convertView.findViewById(R.id.app_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AppInfo app = getItem(position);
        if (app != null) {
            holder.nameText.setText(app.label);
            holder.nameText.setTextColor(textColor);
        }
        return convertView;
    }

    static class ViewHolder {
        TextView nameText;
    }
}
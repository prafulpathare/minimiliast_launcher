package com.minimal.launcher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LauncherActivity extends Activity {

    private ListView listView;
    private EditText searchBox;
    private AppAdapter adapter;
    private List<AppInfo> allApps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        listView  = findViewById(R.id.app_list);
        searchBox = findViewById(R.id.search_box);

        loadApps();

        adapter = new AppAdapter(this, new ArrayList<>(allApps));
        listView.setAdapter(adapter);

        applyTheme();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            AppInfo app = adapter.getItem(position);
            if (app != null) launchApp(app);
        });

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterApps(s.toString());
            }
        });

        searchBox.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                if (adapter.getCount() > 0) launchApp(adapter.getItem(0));
                return true;
            }
            return false;
        });

        searchBox.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void loadApps() {
        PackageManager pm = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL);

        allApps.clear();
        for (ResolveInfo info : resolveInfos) {
            String label       = info.loadLabel(pm).toString().trim();
            String packageName = info.activityInfo.packageName;
            if (!packageName.equals(getPackageName())) {
                allApps.add(new AppInfo(label, packageName, info.activityInfo.name));
            }
        }
        Collections.sort(allApps, (a, b) -> a.label.compareToIgnoreCase(b.label));
    }

    private void filterApps(String query) {
        List<AppInfo> filtered = new ArrayList<>();
        String lower = query.toLowerCase().trim();
        for (AppInfo app : allApps) {
            if (app.label.toLowerCase().contains(lower)) filtered.add(app);
        }
        adapter.updateList(filtered);

        if (filtered.size() == 1 && !lower.isEmpty()) {
            launchApp(filtered.get(0));
            searchBox.setText("");
        }
    }

    private void launchApp(AppInfo app) {
        try {
            Intent launch = new Intent(Intent.ACTION_MAIN);
            launch.addCategory(Intent.CATEGORY_LAUNCHER);
            launch.setClassName(app.packageName, app.activityName);
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(launch);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open " + app.label, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isDarkTheme() {
        int nightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        return nightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    private void applyTheme() {
        boolean dark    = isDarkTheme();
        int bg          = dark ? 0xFF000000 : 0xFFFFFFFF;
        int textCol     = dark ? 0xFFDDDDDD : 0xFF111111;
        int hintCol     = dark ? 0xFF555555 : 0xFFAAAAAA;
        int searchBg    = dark ? 0xFF111111 : 0xFFEEEEEE;

        findViewById(R.id.root_layout).setBackgroundColor(bg);
        findViewById(R.id.app_list).setBackgroundColor(bg);
        searchBox.setTextColor(textCol);
        searchBox.setHintTextColor(hintCol);
        adapter.setTextColor(textCol);
        adapter.notifyDataSetChanged();

        ClockView clockView = findViewById(R.id.clock_view);
        clockView.applyTheme();
    }

    @Override
    public void onBackPressed() {
        // suppress — this is the home screen
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyTheme();
        loadApps();
        String q = searchBox.getText().toString();
        if (q.isEmpty()) adapter.updateList(new ArrayList<>(allApps));
        else filterApps(q);
        searchBox.requestFocus();
    }
}

package com.cleanyourtv.launcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    private static final int SLOT_COUNT = 7;
    private final String[] packages = new String[SLOT_COUNT];
    private final String[] labels = new String[SLOT_COUNT];
    private SharedPreferences preferences;
    private LinearLayout dock;

    private static class TvApp {
        String packageName;
        String label;

        TvApp(String packageName, String label) {
            this.packageName = packageName;
            this.label = label;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences("CleanYourTV", MODE_PRIVATE);

        for (int i = 0; i < SLOT_COUNT; i++) {
            packages[i] = preferences.getString("package_" + i, "");
            labels[i] = preferences.getString("label_" + i, "");
        }

        buildScreen();
    }

    private void buildScreen() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40, 35, 40, 30);
        root.setBackgroundColor(Color.rgb(12, 15, 27));

        TextView title = new TextView(this);
        title.setText("CleanYourTV");
        title.setTextSize(34);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.WHITE);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Tu entretenimiento, organizado.");
        subtitle.setTextSize(17);
        subtitle.setTextColor(Color.LTGRAY);
        root.addView(subtitle);

        root.addView(new Space(this),
                new LinearLayout.LayoutParams(1, 0, 1));

        dock = new LinearLayout(this);
        dock.setGravity(Gravity.CENTER);
        dock.setOrientation(LinearLayout.HORIZONTAL);

        for (int i = 0; i < SLOT_COUNT; i++) {
            final int slotIndex = i;

            TextView slot = new TextView(this);
            slot.setText(packages[i].isEmpty() ? "+" : labels[i]);
            slot.setTextSize(16);
            slot.setTypeface(null, Typeface.BOLD);
            slot.setTextColor(Color.WHITE);
            slot.setGravity(Gravity.CENTER);
            slot.setFocusable(true);
            slot.setFocusableInTouchMode(true);
            slot.setPadding(5, 5, 5, 5);
            slot.setMaxLines(2);

            GradientDrawable background = new GradientDrawable();
            background.setColor(Color.rgb(35, 42, 60));
            background.setCornerRadius(16);
            slot.setBackground(background);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(130, 90);
            params.setMargins(7, 0, 7, 0);
            dock.addView(slot, params);

            slot.setOnClickListener(v -> {
                if (packages[slotIndex].isEmpty()) {
                    showAppPicker(slotIndex);
                } else {
                    launchApp(slotIndex);
                }
            });

            slot.setOnLongClickListener(v -> {
                showAppPicker(slotIndex);
                return true;
            });
        }

        root.addView(dock);
        setContentView(root);
    }

    private List<TvApp> getInstalledApps() {
        Map<String, TvApp> appMap = new LinkedHashMap<>();
        PackageManager pm = getPackageManager();

        Intent tvIntent = new Intent(Intent.ACTION_MAIN);
        tvIntent.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);

        Intent phoneIntent = new Intent(Intent.ACTION_MAIN);
        phoneIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        addAppsToMap(pm.queryIntentActivities(tvIntent, 0), appMap);
        addAppsToMap(pm.queryIntentActivities(phoneIntent, 0), appMap);

        appMap.remove(getPackageName());

        List<TvApp> apps = new ArrayList<>(appMap.values());
        apps.sort((a, b) -> a.label.compareToIgnoreCase(b.label));

        return apps;
    }

    private void addAppsToMap(
            List<ResolveInfo> results,
            Map<String, TvApp> appMap) {

        for (ResolveInfo info : results) {
            String packageName =
                    info.activityInfo.applicationInfo.packageName;

            String label = info.loadLabel(getPackageManager()).toString();

            if (!appMap.containsKey(packageName)) {
                appMap.put(packageName, new TvApp(packageName, label));
            }
        }
    }

    private void showAppPicker(int slotIndex) {
        List<TvApp> apps = getInstalledApps();

        if (apps.isEmpty()) {
            Toast.makeText(this, "No se encontraron aplicaciones",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> names = new ArrayList<>();
        for (TvApp app : apps) {
            names.add(app.label);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                names
        );

        new AlertDialog.Builder(this)
                .setTitle("Elegir aplicación")
                .setAdapter(adapter, (dialog, which) -> {
                    TvApp selected = apps.get(which);

                    packages[slotIndex] = selected.packageName;
                    labels[slotIndex] = selected.label;

                    preferences.edit()
                            .putString("package_" + slotIndex,
                                    selected.packageName)
                            .putString("label_" + slotIndex,
                                    selected.label)
                            .apply();

                    buildScreen();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void launchApp(int slotIndex) {
        String packageName = packages[slotIndex];

        try {
            Intent launchIntent =
                    getPackageManager().getLaunchIntentForPackage(packageName);

            if (launchIntent != null) {
                startActivity(launchIntent);
            } else {
                Toast.makeText(this, "No se puede abrir esta aplicación",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "No se pudo abrir la aplicación",
                    Toast.LENGTH_SHORT).show();
        }
    }
}


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
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends Activity {

    private static final int SLOT_COUNT = 7;
    private final String[] packages = new String[SLOT_COUNT];
    private final String[] labels = new String[SLOT_COUNT];

    private SharedPreferences preferences;
    private LinearLayout dock;
    private TextView clock;
    private TextView date;
    private final Handler handler = new Handler();

    private final Runnable clockUpdater = new Runnable() {
        @Override
        public void run() {
            if (clock != null) {
                clock.setText(new SimpleDateFormat(
                        "HH:mm", Locale.getDefault()).format(new Date()));

                date.setText(new SimpleDateFormat(
                        "EEEE, d 'de' MMMM",
                        new Locale("es", "AR")).format(new Date()));

                handler.postDelayed(this, 30000);
            }
        }
    };

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

    private GradientDrawable gradient(int color, int stroke) {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(color);
        bg.setCornerRadius(22);
        if (stroke != 0) {
            bg.setStroke(3, stroke);
        }
        return bg;
    }

    private void buildScreen() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(42, 28, 42, 28);

        GradientDrawable background = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        Color.rgb(10, 16, 35),
                        Color.rgb(25, 30, 55),
                        Color.rgb(12, 15, 27)
                });
        root.setBackground(background);

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout heading = new LinearLayout(this);
        heading.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(this);
        title.setText("CleanYourTV");
        title.setTextSize(34);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.WHITE);
        heading.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("TU ENTRETENIMIENTO, A TU MANERA");
        subtitle.setTextSize(12);
        subtitle.setTextColor(Color.rgb(160, 175, 205));
        heading.addView(subtitle);

        header.addView(heading, new LinearLayout.LayoutParams(
                0, -2, 1));

        LinearLayout timeBox = new LinearLayout(this);
        timeBox.setGravity(Gravity.END);
        timeBox.setOrientation(LinearLayout.VERTICAL);

        clock = new TextView(this);
        clock.setTextSize(30);
        clock.setTypeface(null, Typeface.BOLD);
        clock.setTextColor(Color.WHITE);
        clock.setGravity(Gravity.END);
        timeBox.addView(clock);

        date = new TextView(this);
        date.setTextSize(13);
        date.setTextColor(Color.LTGRAY);
        date.setGravity(Gravity.END);
        timeBox.addView(date);

        header.addView(timeBox);
        root.addView(header);

        root.addView(new Space(this),
                new LinearLayout.LayoutParams(1, 0, 1));

        TextView section = new TextView(this);
        section.setText("MIS APLICACIONES");
        section.setTextSize(16);
        section.setTypeface(null, Typeface.BOLD);
        section.setTextColor(Color.WHITE);
        section.setPadding(0, 0, 0, 16);
        root.addView(section);

        dock = new LinearLayout(this);
        dock.setGravity(Gravity.CENTER);
        dock.setOrientation(LinearLayout.HORIZONTAL);

        for (int i = 0; i < SLOT_COUNT; i++) {
            final int index = i;

            TextView slot = new TextView(this);
            slot.setText(packages[i].isEmpty() ? "+" : labels[i]);
            slot.setTextSize(packages[i].isEmpty() ? 34 : 17);
            slot.setTypeface(null, Typeface.BOLD);
            slot.setTextColor(Color.WHITE);
            slot.setGravity(Gravity.CENTER);
            slot.setPadding(8, 8, 8, 8);
            slot.setMaxLines(3);
            slot.setFocusable(true);
            slot.setFocusableInTouchMode(true);

            slot.setBackground(gradient(
                    Color.rgb(35, 45, 72), 0));

            slot.setOnFocusChangeListener((v, focused) -> {
                v.setBackground(gradient(
                        focused ? Color.rgb(48, 75, 120)
                                : Color.rgb(35, 45, 72),
                        focused ? Color.rgb(120, 180, 255) : 0));
                v.setScaleX(focused ? 1.06f : 1f);
                v.setScaleY(focused ? 1.06f : 1f);
            });

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(145, 112);
            params.setMargins(5, 5, 5, 5);
            dock.addView(slot, params);

            slot.setOnClickListener(v -> {
                if (packages[index].isEmpty()) {
                    showAppPicker(index);
                } else {
                    launchApp(index);
                }
            });

            slot.setOnLongClickListener(v -> {
                showAppPicker(index);
                return true;
            });
        }

        root.addView(dock);

        TextView footer = new TextView(this);
        footer.setText("Seleccioná una tarjeta para abrir tus aplicaciones");
        footer.setTextSize(12);
        footer.setTextColor(Color.rgb(145, 160, 190));
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0, 18, 0, 0);
        root.addView(footer);

        setContentView(root);
        handler.removeCallbacks(clockUpdater);
        clockUpdater.run();

        if (dock.getChildCount() > 0) {
            dock.getChildAt(0).requestFocus();
        }
    }

    private List<TvApp> getInstalledApps() {
        Map<String, TvApp> map = new LinkedHashMap<>();
        PackageManager pm = getPackageManager();

        Intent tv = new Intent(Intent.ACTION_MAIN);
        tv.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);

        Intent normal = new Intent(Intent.ACTION_MAIN);
        normal.addCategory(Intent.CATEGORY_LAUNCHER);

        addApps(pm.queryIntentActivities(tv, 0), map);
        addApps(pm.queryIntentActivities(normal, 0), map);

        map.remove(getPackageName());

        List<TvApp> apps = new ArrayList<>(map.values());
        apps.sort((a, b) ->
                a.label.compareToIgnoreCase(b.label));

        return apps;
    }

    private void addApps(List<ResolveInfo> results,
                         Map<String, TvApp> map) {
        for (ResolveInfo info : results) {
            String pkg =
                    info.activityInfo.applicationInfo.packageName;
            String label =
                    info.loadLabel(getPackageManager()).toString();

            if (!map.containsKey(pkg)) {
                map.put(pkg, new TvApp(pkg, label));
            }
        }
    }

    private void showAppPicker(int index) {
        List<TvApp> apps = getInstalledApps();

        if (apps.isEmpty()) {
            Toast.makeText(this, "No hay aplicaciones disponibles",
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
                names);

        new AlertDialog.Builder(this)
                .setTitle("Elegir aplicación")
                .setAdapter(adapter, (dialog, which) -> {
                    TvApp selected = apps.get(which);

                    packages[index] = selected.packageName;
                    labels[index] = selected.label;

                    preferences.edit()
                            .putString("package_" + index,
                                    selected.packageName)
                            .putString("label_" + index,
                                    selected.label)
                            .apply();

                    buildScreen();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void launchApp(int index) {
        Intent intent = getPackageManager()
                .getLaunchIntentForPackage(packages[index]);

        if (intent != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No se puede abrir esta aplicación",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(clockUpdater);
        super.onDestroy();
    }
}

package com.cleanyourtv.launcher;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Space;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(45, 35, 45, 30);
        root.setBackgroundColor(Color.rgb(12, 15, 27));

        TextView title = new TextView(this);
        title.setText("CleanYourTV");
        title.setTextSize(34);
        title.setTextColor(Color.WHITE);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Tu entretenimiento, organizado.");
        subtitle.setTextSize(17);
        subtitle.setTextColor(Color.LTGRAY);
        root.addView(subtitle);

        root.addView(new Space(this), new LinearLayout.LayoutParams(
            1, 0, 1
        ));

        LinearLayout dock = new LinearLayout(this);
        dock.setGravity(Gravity.CENTER);
        dock.setOrientation(LinearLayout.HORIZONTAL);

        for (int i = 1; i <= 7; i++) {
            TextView slot = new TextView(this);
            slot.setText("+");
            slot.setTextSize(28);
            slot.setTextColor(Color.WHITE);
            slot.setGravity(Gravity.CENTER);
            slot.setBackgroundColor(Color.rgb(35, 42, 60));

            LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(90, 90);
            params.setMargins(8, 0, 8, 0);

            dock.addView(slot, params);
        }

        root.addView(dock);
        setContentView(root);
    }
}

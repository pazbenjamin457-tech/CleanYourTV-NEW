package com.cleanyourtv.launcher;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(Color.rgb(15, 18, 30));
        layout.setPadding(40, 40, 40, 40);

        TextView title = new TextView(this);
        title.setText("CleanYourTV");
        title.setTextSize(36);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);

        layout.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Tu TV, a tu manera");
        subtitle.setTextSize(18);
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setGravity(Gravity.CENTER);

        layout.addView(subtitle);

        setContentView(layout);
    }
          }

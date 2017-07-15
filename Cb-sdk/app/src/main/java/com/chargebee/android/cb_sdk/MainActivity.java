package com.chargebee.android.cb_sdk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {
    @InjectView(R.id.launch)
    Button launch;
    @InjectView(R.id.standard)
    Button std;
    @InjectView(R.id.pro)
    Button pro;
    @InjectView(R.id.enterprise)
    Button ent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        launch.setOnClickListener(getOnClickListener("launch"));
        std.setOnClickListener(getOnClickListener("std"));
        pro.setOnClickListener(getOnClickListener("pro"));
        ent.setOnClickListener(getOnClickListener("ent"));
    }

    private OnClickListener getOnClickListener(final String plan) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), SignupActivity.class);
                i.putExtra("plan", plan);
                startActivity(i);
            }
        };
    }
}

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

public class ChoosePlan extends AppCompatActivity {
    @InjectView(R.id.launch)
    Button launch;
    @InjectView(R.id.standard)
    Button std;
    @InjectView(R.id.pro)
    Button pro;
    @InjectView(R.id.enterprise)
    Button ent;

    private static final int REQUEST_SIGNUP = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_plan);
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
                startActivityForResult(i, REQUEST_SIGNUP);
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                setResult(RESULT_OK, null);
                this.finish();
            }
        }
    }
}

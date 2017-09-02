package com.chargebee.android.cb_sdk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ChoosePlan extends AppCompatActivity {
    @InjectView(R.id.monthly)
    LinearLayout monthly;
    @InjectView(R.id.half_yearly)
    LinearLayout halfYearly;
    @InjectView(R.id.yearly)
    LinearLayout yearly;

    private static final int REQUEST_SIGNUP = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_plan);
        ButterKnife.inject(this);
        monthly.setOnClickListener(getOnClickListener("monthly"));
        halfYearly.setOnClickListener(getOnClickListener("half_yearly"));
        yearly.setOnClickListener(getOnClickListener("yearly"));
    }

    private OnClickListener getOnClickListener(final String planId) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), SignupActivity.class);
                i.putExtra("plan_id", planId);
                startActivityForResult(i, REQUEST_SIGNUP);
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, null);
                finish();
            }
        }
    }
}

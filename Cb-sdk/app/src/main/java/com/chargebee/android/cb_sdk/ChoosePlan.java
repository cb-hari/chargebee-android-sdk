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
    @InjectView(R.id.goods)
    LinearLayout goods;
    @InjectView(R.id.saas)
    LinearLayout saas;
    @InjectView(R.id.services)
    LinearLayout services;

    private static final int REQUEST_SIGNUP = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_plan);
        ButterKnife.inject(this);
        goods.setOnClickListener(getOnClickListener("Physical Goods"));
        saas.setOnClickListener(getOnClickListener("Saas"));
        services.setOnClickListener(getOnClickListener("Services"));
    }

    private OnClickListener getOnClickListener(final String businessType) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), SignupActivity.class);
                i.putExtra("business_type", businessType);
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

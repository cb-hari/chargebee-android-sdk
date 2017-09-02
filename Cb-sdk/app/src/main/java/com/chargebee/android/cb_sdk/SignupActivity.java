package com.chargebee.android.cb_sdk;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chargebee.android.sdk.Environment;
import com.chargebee.android.sdk.Result;
import com.chargebee.android.sdk.models.Subscription;
import com.chargebee.android.sdk.models.enums.AutoCollection;

import java.io.IOException;
import java.net.MalformedURLException;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SignupActivity extends AppCompatActivity {
    @InjectView(R.id.input_email)
    EditText _emailText;
    @InjectView(R.id.input_password)
    EditText _passwordText;
    @InjectView(R.id.btn_signup)
    Button _signupButton;
    @InjectView(R.id.link_login)
    TextView _loginLink;

    String planId = null;

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);
        planId = getIntent().getStringExtra("plan_id");
        Toast.makeText(getApplicationContext(), planId, Toast.LENGTH_SHORT).show();
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, null);
                finish();
            }
        });
    }

    public void signup() {
        if (!validate()) {
            onSignupFailed();
            return;
        }
        _signupButton.setEnabled(false);

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // create subscription
        sendPostRequest(email, password);
    }

    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getBaseContext(), "Invalid email", Toast.LENGTH_LONG).show();
            return false;
        }
        if (password == null || password.isEmpty()) {
            Toast.makeText(getBaseContext(), "Please enter password", Toast.LENGTH_LONG).show();
            return false;
        }
        if (password.length() > 20) {
            Toast.makeText(getBaseContext(), "Password cannot be more than 20 characters", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void sendPostRequest(String email, String pwd) {
        new PostClass(this).setEmail(email).setPwd(pwd).execute();
    }


    private class PostClass extends AsyncTask<String, Void, Result> {
        private final Context context;
        private String email;
        private String pwd;

        public PostClass(Context c) {
            this.context = c;
        }

        public PostClass setEmail(String email) {
            this.email = email;
            return this;
        }

        public PostClass setPwd(String pwd) {
            this.pwd = pwd;
            return this;
        }

        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Creating subscription");
            progress.show();
        }

        @Override
        protected Result doInBackground(String... params) {
            try {
                Environment.configure("dubai-test", "test_rRubfcusj7MdOUMMd2AakeJwPckSgAbQS");
                //fetch customer with email and if present dont create sub
                final Result result = Subscription.create()
                        .planId(planId)
                        .customerEmail(this.email)
                        .param("customer[cf_password]", this.pwd)
                        .autoCollection(AutoCollection.OFF)
                        .request();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Result result) {
            progress.dismiss();
            onSignupSuccess();
        }

    }
}

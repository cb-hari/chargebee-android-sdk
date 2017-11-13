package com.chargebee.android.cb_sdk;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chargebee.android.sdk.Environment;
import com.chargebee.android.sdk.Result;
import com.chargebee.android.sdk.models.Subscription;
import com.chargebee.android.sdk.models.enums.AutoCollection;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends AppCompatActivity {
    private static final int CHOOSE_PLAN = 0;
    private ProgressDialog progress;

    @InjectView(R.id.input_email)
    EditText _emailText;
    @InjectView(R.id.input_password)
    EditText _passwordText;
    @InjectView(R.id.btn_login)
    Button _loginButton;
    @InjectView(R.id.link_signup)
    TextView _signupLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        Environment.configure("hpv3-test", "test_E3WOdWcurmxofIsHQkxCRgZzQIb4cuXI0K");
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                login();
                openWebview();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChoosePlan.class);
                startActivityForResult(intent, CHOOSE_PLAN);
            }
        });
    }

    private void openWebview() {
        Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
        intent.putExtra("url","https://hpv3-test.chargebee.com/pages/v3/WfrTErHFcNKu7mMF2OgCyhVG26j7qlCH/cart");
        startActivity(intent);
    }

    public void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        }
        _loginButton.setEnabled(false);
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        sendGetRequest(email, password);
    }

    public void sendGetRequest(String email, String pwd) {
        new GetClass(this).setEmail(email).setPwd(pwd).execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_PLAN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Account created. Login Now.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        Toast.makeText(getApplicationContext(), "Login success", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public void onLoginFailed() {
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplicationContext(), "Enter valid email address", Toast.LENGTH_LONG).show();
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

    private class GetClass extends AsyncTask<String, Void, Result> {
        private final Context context;
        private String email;
        private String pwd;

        public GetClass(Context c) {
            this.context = c;
        }

        public GetClass setEmail(String email) {
            this.email = email;
            return this;
        }

        public GetClass setPwd(String pwd) {
            this.pwd = pwd;
            return this;
        }

        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Logging in...");
            progress.show();
        }

        @Override
        protected Result doInBackground(String... params) {
            try {
                //fetch customer with email and validate with pwd
                final Result result = null;
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Result result) {
            progress.dismiss();
            onLoginSuccess();
        }

    }
}

package com.chargebee.android.cb_sdk;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by cb-hariprasath on 18/07/17.
 */

public class ChargebeeUtil {

    public static void createSubscription(String email) {
        String params = "";
        request("https://dubai-test.chargebee.com/api/v2/subscriptions", params);
    }

    private static void request(String url, String params) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            StringBuffer response = new StringBuffer();

            @Override
            protected String doInBackground(String... params) {
                try {
                    HttpURLConnection con = (HttpURLConnection) new URL(params[0]).openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("User-Agent", "Mozilla/5.0");
                    con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                    con.setRequestProperty("Authorization",

                            getAuthValue());
                    con.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                    wr.writeBytes(params[1]);
                    wr.flush();
                    wr.close();
                    int responseCode = con.getResponseCode();
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    Log.d("--resp", response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("--ex", e.getLocalizedMessage());
                }
                return response.toString();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Log.d("--res", result);
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(url, params);
    }

    private static String getAuthValue() throws Exception {
        String enc = Base64.encodeToString("test_rRubfcusj7MdOUMMd2AakeJwPckSgAbQS".getBytes("UTF-8"), Base64.DEFAULT);
        return "Basic " + enc;
    }
}

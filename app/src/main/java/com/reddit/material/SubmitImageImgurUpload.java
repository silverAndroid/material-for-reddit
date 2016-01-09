package com.reddit.material;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Scanner;

/**
 * Created by Rushil Perera on 1/9/2016.
 */
public class SubmitImageImgurUpload extends AsyncTask<InputStream, Void, String> {

    private static final String TAG = "SubmitImageImgurUpload";
    private final String title;
    private final String subreddit;
    private final Activity activity;
    private ProgressDialog dialog;

    public SubmitImageImgurUpload(String title, String subreddit, Activity activity) {
        this.title = title;
        this.subreddit = subreddit;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(activity, R.style.DialogTheme);
        dialog.setMessage("Uploading image to Imgur...");
        dialog.show();
    }

    @Override
    protected String doInBackground(InputStream... params) {
        uploadToImgur(params[0]);
        return null;
    }

    @Override
    protected void onPostExecute(String response) {
        dialog.dismiss();
        if (response != null) {
            ConnectionSingleton.getInstance().post(activity, title, response, subreddit, "link");
        }
    }

    private String uploadToImgur(InputStream inputStream) {
        HttpURLConnection conn = null;
        InputStream responseIn = null;

        try {
            conn = (HttpURLConnection) new java.net.URL("https://api.imgur.com/3/image.json").openConnection();
            conn.setDoOutput(true);

            conn.setRequestProperty("Authorization", "Client-ID " + APIKey.getInstance().getAPIKey(APIKey
                    .IMGUR_CLIENT_ID_KEY));

            OutputStream out = conn.getOutputStream();
            copy(inputStream, out);
            out.flush();
            out.close();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                responseIn = conn.getInputStream();
                return onInput(responseIn);
            } else {
                Log.i(TAG, "responseCode=" + conn.getResponseCode());
                responseIn = conn.getErrorStream();
                StringBuilder sb = new StringBuilder();
                Scanner scanner = new Scanner(responseIn);
                while (scanner.hasNext()) {
                    sb.append(scanner.next());
                }
                Log.i(TAG, "error response: " + sb.toString());
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error during POST", ex);
        } finally {
            try {
                if (responseIn != null) {
                    responseIn.close();
                }
            } catch (Exception ignore) {
            }
            try {
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception ignore) {
            }
            try {
                inputStream.close();
            } catch (Exception ignore) {
            }
        }
        return null;
    }

    private void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[8192];
        int n;
        while (-1 != (n = inputStream.read(buffer))) {
            outputStream.write(buffer, 0, n);
        }
    }

    protected String onInput(InputStream in) throws Exception {
        StringBuilder sb = new StringBuilder();
        Scanner scanner = new Scanner(in);
        while (scanner.hasNext()) {
            sb.append(scanner.next());
        }

        Log.i(TAG, "onInput: " + sb.toString());
        JSONObject root = new JSONObject(sb.toString());
        String id = root.getJSONObject("data").getString("id");

        return "http://imgur.com/" + id;
    }
}

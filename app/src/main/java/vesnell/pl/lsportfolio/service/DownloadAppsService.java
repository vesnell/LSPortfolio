package vesnell.pl.lsportfolio.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import vesnell.pl.lsportfolio.R;
import vesnell.pl.lsportfolio.database.model.Project;
import vesnell.pl.lsportfolio.json.JsonTags;
import vesnell.pl.lsportfolio.utils.Resources;

public class DownloadAppsService extends IntentService {

    public static final String URL = "url";
    public static final String RECEIVER = "receiver";
    public static final String DOWNLOAD_TYPE = "downloadType";
    public static final String RESULT = "results";

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    public static final String APPS_PREFERENCES = "appsPreferences" ;
    public static final String MD5_APPS_PREFERENCES = "md5";

    private SharedPreferences sharedpreferences;
    private Project project;

    public DownloadAppsService() {
        super(DownloadAppsService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        sharedpreferences = getSharedPreferences(APPS_PREFERENCES, Context.MODE_PRIVATE);

        final ResultReceiver receiver = intent.getParcelableExtra(RECEIVER);
        DownloadType downloadType = (DownloadType) intent.getSerializableExtra(DOWNLOAD_TYPE);
        String url = intent.getStringExtra(URL);

        Bundle bundle = new Bundle();

        if (!TextUtils.isEmpty(url)) {
            //service is running
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);

            try {
                Object results = downloadNewData(url, downloadType);

                //send result back to activity
                switch (downloadType) {
                    case APPS:
                        ArrayList<Project> projects = (ArrayList<Project>) results;
                        bundle.putSerializable(RESULT, projects);
                        break;
                    case DETAILS:

                        break;
                }
                receiver.send(STATUS_FINISHED, bundle);
            } catch (Exception e) {

                //send error back to activity
                bundle.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, bundle);
            }
        }
        this.stopSelf();
    }

    private Object downloadNewData(String requestUrl, DownloadType downloadType) throws IOException, DownloadException {
        InputStream inputStream;
        HttpURLConnection urlConnection;

        java.net.URL url = new URL(requestUrl);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setRequestMethod("GET");
        int statusCode = urlConnection.getResponseCode();

        //statusCode=200? OK
        if (statusCode == 200) {
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            String response = convertInputStreamToString(inputStream);

            switch (downloadType) {
                case APPS:
                    String quizMD5 = sharedpreferences.getString(MD5_APPS_PREFERENCES, null);
                    String md5FromResponse = getMD5(response);
                    if (!md5FromResponse.equals(quizMD5)) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(MD5_APPS_PREFERENCES, md5FromResponse);
                        editor.apply();
                        return parseResult(response, downloadType);
                    }
                    break;
                case DETAILS:
                    return parseResult(response, downloadType);
            }
            return null;
        } else {
            throw new DownloadException(Resources.getString(R.string.error_download_data));
        }
    }

    private Object parseResult(String result, DownloadType downloadType) {
        switch (downloadType) {
            case APPS:
                ArrayList<Project> quizzes = new ArrayList<Project>();
                try {
                    JSONObject response = new JSONObject(result);
                    JSONObject data = response.optJSONObject(JsonTags.DATA);
                    JSONArray items = data.optJSONArray(JsonTags.PORTFOLIO_ARR);

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.optJSONObject(i);
                        Project quiz = new Project(item);
                        quizzes.add(quiz);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return quizzes;
            case DETAILS:

                return null;
        }
        return null;
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";

        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        inputStream.close();
        return result;
    }

    public String getMD5(String s) {
        try {
            //create md5 hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            //create hex string
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public class DownloadException extends Exception {

        public DownloadException(String message) {
            super(message);
        }

        public DownloadException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public enum DownloadType {
        APPS,
        DETAILS;
    }
}

package vesnell.pl.lsportfolio.service;

import android.app.IntentService;
import android.content.Intent;
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
import java.util.ArrayList;
import java.util.List;

import vesnell.pl.lsportfolio.R;
import vesnell.pl.lsportfolio.model.Image;
import vesnell.pl.lsportfolio.model.Project;
import vesnell.pl.lsportfolio.model.ProjectDetails;
import vesnell.pl.lsportfolio.model.Store;
import vesnell.pl.lsportfolio.json.JsonTags;
import vesnell.pl.lsportfolio.utils.Resources;

public class DownloadService extends IntentService {

    public static final String URL = "url";
    public static final String RECEIVER = "receiver";
    public static final String DOWNLOAD_TYPE = "downloadType";
    public static final String RESULT = "results";

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    private Project project;

    public DownloadService() {
        super(DownloadService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final ResultReceiver receiver = intent.getParcelableExtra(RECEIVER);
        DownloadType downloadType = (DownloadType) intent.getSerializableExtra(DOWNLOAD_TYPE);
        String url = intent.getStringExtra(URL);
        if (downloadType == DownloadType.DETAILS) {
            project = (Project) intent.getSerializableExtra(Project.NAME);
        }
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
                        ProjectDetails projectDetails = (ProjectDetails) results;
                        bundle.putSerializable(RESULT, projectDetails);
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
            return parseResult(response, downloadType);
        } else {
            throw new DownloadException(Resources.getString(R.string.error_download_data));
        }
    }

    private Object parseResult(String result, DownloadType downloadType) {
        switch (downloadType) {
            case APPS:
                ArrayList<Project> projects = new ArrayList<Project>();
                try {
                    JSONObject response = new JSONObject(result);
                    JSONObject data = response.optJSONObject(JsonTags.DATA);
                    JSONArray items = data.optJSONArray(JsonTags.PORTFOLIO_ARR);

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.optJSONObject(i);
                        Project project = new Project(item);
                        projects.add(project);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return projects;
            case DETAILS:
                ProjectDetails projectDetails = null;
                try {
                    JSONObject response = new JSONObject(result);
                    JSONObject data = response.optJSONObject(JsonTags.DATA);
                    projectDetails = new ProjectDetails(data, project);
                    JSONArray urls = data.optJSONArray(JsonTags.GALLERY);
                    List<Image> images = new ArrayList<Image>();
                    for (int i = 0; i < urls.length(); i++) {
                        String url = urls.optString(i);
                        Image image = new Image(url, projectDetails);
                        images.add(image);
                    }
                    JSONArray links = data.optJSONArray(JsonTags.LINK_ARR);
                    List<Store> stores = new ArrayList<Store>();
                    for (int i = 0; i < links.length(); i++) {
                        JSONObject link = links.optJSONObject(i);
                        Store store = new Store(link, projectDetails);
                        stores.add(store);
                    }
                    projectDetails.setImages(images);
                    projectDetails.setStores(stores);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return projectDetails;
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

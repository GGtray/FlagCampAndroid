package com.example.flagcamp.Utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public final class QueryUtils {
    private QueryUtils() {

    }

    public static ArrayList<Job> extractJobs(String StringUrl) {
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(createUrl(StringUrl));
        } catch (IOException e) {

        }
        ArrayList<Job> jobs = new ArrayList<>();
        if (jsonResponse == null || jsonResponse.isEmpty()) {
            return jobs;
        }
        try {
            JSONArray features = new JSONArray(jsonResponse);
            for (int i = 0; i < features.length(); i++) {
                JSONObject feature = features.getJSONObject(i);
                String id = feature.getString("id");
                String company = feature.getString("company");
                String location = feature.getString("location");
                String title = feature.getString("title");
                String description = feature.getString("description");
                String companyLogoUrl = feature.getString("company_logo");
                String jobType = feature.getString("type");
                String detailUrl = feature.getString("url");
                String postDate = feature.getString("created_at");
                String applyUrl = feature.getString("how_to_apply");
                jobs.add(new Job(id, company, location, title, description, companyLogoUrl, jobType, detailUrl, postDate, applyUrl));
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the job JSON results", e);
        }

        // Return the list of jobs.
        return jobs;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.d("aaaa", "aaaaaaaa");

            }
        } catch (IOException e) {

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            reader.close();
        }
        return output.toString();
    }
}

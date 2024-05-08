package com.studybear.cdj.myapplication; /**
 * Created by Javon06 on 3/13/2015.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;
import android.widget.TextView;

public class BackgroundDownloadTask extends AsyncTask<String, Void, String> {

    public TextView viewToUpdate;

    public BackgroundDownloadTask(TextView tv) {
        // reference back to the activity these methods are called from
        viewToUpdate = tv;
    }

    /*
     * These two methods are implemented for the interface, doInBackground is passed in
     * arguments from execute() call in the MainMenu activity
     */
    @Override
    protected String doInBackground(String... urls) {
        try {
            return pullServerData(urls[0]);
        } catch (IOException e) {
            return "Unable to pull data, error.";
        }
    }

    /*
     * Is run on the success of doInBackground()
     */
    @Override
    protected void onPostExecute(String results) {
        viewToUpdate.setText(results);
    }

    /*
     * Currently just scrapes webpage for lines
     * needs an update for future versions
     */
    public String pullServerData(String stringURL) throws IOException {
        URL url = new URL(stringURL);
        URLConnection connection = url.openConnection();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream()));
        String pageText = "";
        String currentLine;

        while ((currentLine = in.readLine()) != null)
            pageText += currentLine;
        in.close();

        // page is currently formatted so that each div is a different patient
       // String[] parsed = pageText.split(":");
        return pageText; //parsed[0];  // returns a small string for an example now
    }
}




    /*
    Add to activity to pull code
    */
// network access must be run asynchronously (not on the "main" execution thread)


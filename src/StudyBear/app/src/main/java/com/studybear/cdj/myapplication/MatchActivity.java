package com.studybear.cdj.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MatchActivity extends ActionBarActivity {

    private final float MIN_DISTANCE = 300;
    private float x1;
    private float x2;
    private float y1;
    private float y2;

    public NetworkController networkRequest;
    public NavigationBarController navBar;
    public String username;
    public String matchUser;

    public TextView matchName;
    public TextView matchUserName;
    public TextView matchBio;
    public TextView matchUniversity;
    public TextView classes;

    public JSONArray matchList;
    public int matchIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        networkRequest = NetworkController.getInstance(getApplicationContext());
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        navBar = new NavigationBarController(this, username);
        ImageButton activeIcon = (ImageButton) findViewById(R.id.matchButton);
        activeIcon.setImageResource(R.drawable.matcha);

        String url = getResources().getString(R.string.server_address) + "?rtype=getMatches&username=" + username;

        matchName = (TextView) findViewById(R.id.nameTextView);
        matchUserName = (TextView) findViewById(R.id.userNameTextView);
        matchBio = (TextView) findViewById(R.id.bioTextView);
        matchUniversity = (TextView) findViewById(R.id.universityTextView);
        classes = (TextView) findViewById(R.id.Classes);

        matchIndex = 0;
        getMatches(url);

    }

    private void getMatches(String url) {
        JsonObjectRequest matchRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject json) {
                try {
                    matchList = json.getJSONArray("userList");
                    displayMatch();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getBaseContext(), "Error retrieving matches from server", Toast.LENGTH_LONG).show();
            }
        });
        networkRequest.addToRequestQueue(matchRequest);
    }

    private void displayMatch() {
        if (matchIndex < matchList.length()) {
            try {
                JSONObject matchedUser = matchList.getJSONObject(matchIndex);
                matchIndex++;
                matchName.setText(matchedUser.getString("firstName") + " " + matchedUser.getString("lastName"));
                matchUserName.setText("Username:  " + matchedUser.getString("userName"));
                matchBio.setText(matchedUser.getString("biography"));
                matchUniversity.setText(matchedUser.getString("universityName"));
                matchUser = matchedUser.getString("userName");
                classes.setText("");
                getClasses(matchUser);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getBaseContext(), "Out of matches, please try again!", Toast.LENGTH_LONG).show();
        }
    }

    public void onBlock() {
        sendBlockRequest(matchUserName.getText().toString());
        displayMatch();
    }

    private void onStudy() {
        sendMatchResponse(matchUserName.getText().toString(), "study");

        // start a message to them
        Intent intent = new Intent(this, NewMessage.class);
        intent.putExtra("username", username);
        intent.putExtra("fillTo", matchUserName.getText().toString().substring("Username:  ".length()).replaceAll(" ", "%20"));
        startActivity(intent);
        finish();
    }

    private void onPass() {
        sendMatchResponse(matchUserName.getText().toString(), "pass");
        displayMatch();
    }

    // detecting swipes
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1= event.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                y2 = event.getY();
                float deltaX = x2 - x1;
                float deltaY = y2 - y1;

                // prioritize left and right swipes over top and bottom swipes
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // Left to Right swipe action
                    if (x2 > x1) {
                        //Toast.makeText(this, "Left to Right swipe [Next]", Toast.LENGTH_SHORT).show();
                        onStudy();
                    }

                    // Right to left swipe action
                    else {
                        //Toast.makeText(this, "Right to Left swipe [Previous]", Toast.LENGTH_SHORT).show();
                        onPass();
                    }

                } else {
                    if (Math.abs(deltaY) > MIN_DISTANCE) {

                        // bottom to top swipe
                        if (y1 > y2) {
                            onBlock();
                            //Toast.makeText(this, "Bottom to Top swipe [Previous]", Toast.LENGTH_SHORT).show();
                        }

                        // top to bottom swipe
                        else {
                            //Toast.makeText(this, "Top to Bottom swipe [Previous]", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void sendMatchResponse(String otherUser, String response) {
        String url = getResources().getString(R.string.server_address) + "index.php?rtype=sendMatchResponse&username=" + username.replaceAll(" ", "%20") + "&otheruser=" + otherUser.replaceAll(" ", "%20") + "&response=" + response.replaceAll(" ", "%20");
        JsonObjectRequest matchResponse = new JsonObjectRequest(Request.Method.GET, url, null, null, null);
        networkRequest.addToRequestQueue(matchResponse);
    }

    private void sendBlockRequest(String otherUser) {
        String url = getResources().getString(R.string.server_address) + "index.php?rtype=sendBlockRequest&username=" + username.replaceAll(" ", "%20") + "&otheruser=" + otherUser.substring("Username:  ".length()).replaceAll(" ", "%20");
        Log.d("blockcheck", "block request called with " + url);
        JsonObjectRequest blockRequest = new JsonObjectRequest(Request.Method.GET, url, null, null, null);
        networkRequest.addToRequestQueue(blockRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_match, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_settings:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
                        .show();
                break;
            // action with ID action_settings was selected
            case R.id.action_logout:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
    }

    public void getClasses (String match){
        String url = getResources().getString(R.string.server_address) + "?rtype=getMatchesClasses&uname="+match;

        JsonObjectRequest getMatchesClasses = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject json) {
                try
                {
                    if(!json.isNull("classList")) {

                        JSONArray classList = json.getJSONArray("classList");
                        StringBuilder classListString = new StringBuilder();
                        JSONObject classItem;
                        String classItemString;

                        for (int i = 0; i < classList.length(); i++) {
                            classItem = classList.getJSONObject(i);
                            classItemString = classItem.getString("classId") + ": " + classItem.getString("className") + "\n" + classItem.getString("professorLname") + ", " + classItem.getString("professorFname");

                            if (i + 1 == classList.length())
                                classListString.append(classItemString);
                            else
                                classListString.append(classItemString + "\n\n");
                        }
                        classes.setText(classListString.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        networkRequest.addToRequestQueue(getMatchesClasses);
    }


}

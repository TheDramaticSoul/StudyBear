package com.studybear.cdj.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class inboxActivity extends ActionBarActivity {
    public NetworkController networkRequest;
    public NavigationBarController navigationBar;
    public String username;
    public ArrayList<String> buddies = new ArrayList<>();
    public int counter = 0;

    public void printMessages(final String user, String message){
        buddies.add(user);
        TextView tv = new TextView(getApplicationContext());
        tv.setText(message);
        tv.setTextColor(Color.parseColor("#315172"));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tv.setPadding(30, 30, 30, 30);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(params);
        tv.setClickable(true);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ConvoActivity.class);
                intent.putExtra("buddy", user);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            }
        });
        if((counter%2) == 0){
            tv.setBackgroundColor(Color.parseColor("#26FFFFFF"));
        }
        LinearLayout inboxLayout  = (LinearLayout) findViewById(R.id.inboxLayout);

        inboxLayout.addView(tv);
        counter++;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        networkRequest = NetworkController.getInstance(getApplicationContext());
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        navigationBar = new NavigationBarController(this, username);
        ImageButton activeIcon = (ImageButton) findViewById(R.id.messageButton);
        activeIcon.setImageResource(R.drawable.messagea);

        String url = getResources().getString(R.string.server_address) + "?rtype=getMessages&username="+username;

        JsonObjectRequest getMessagesRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject json) {
                try
                {
                    JSONArray messageList = json.getJSONArray("messageList");
                    JSONObject messageItem;
                    String message;

                    for(int i = 0; i < messageList.length(); i++)
                    {
                        messageItem = messageList.getJSONObject(i);
                        final String sUser = messageItem.getString("sendingUser");
                        final String rUser  = messageItem.getString("receivingUser");
                        String dateTime = messageItem.getString("niceDate");
                        dateTime = dateTime.toLowerCase();

                    if(sUser.equals(username) & !buddies.contains(rUser)) {
                        message = rUser + "  " + dateTime;
                        printMessages(rUser, message);
                    }
                    else if(rUser.equals(username) & !buddies.contains(sUser)) {
                        message = sUser + "  " + dateTime;
                        printMessages(sUser, message);
                    }
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
        networkRequest.addToRequestQueue(getMessagesRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inbox, menu);
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

    public void NewMessage (View v){
        Intent intent = new Intent(this, NewMessage.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed(){
    }
}
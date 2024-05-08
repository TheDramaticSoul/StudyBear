package com.studybear.cdj.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Layout;
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
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class BlockList extends FragmentActivity {
    private NetworkController networkRequest;
    public NavigationBarController navigationBar;
    public String username;
    public LinearLayout blockLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_list);

        networkRequest = NetworkController.getInstance(getApplicationContext());
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        navigationBar = new NavigationBarController(this, username);
        ImageButton activeIcon = (ImageButton) findViewById(R.id.settingsButton);
        activeIcon.setImageResource(R.drawable.settingsa);

        blockLayout  = (LinearLayout) findViewById(R.id.blockLayout);
        String url = getResources().getString(R.string.server_address) + "?rtype=getBlockList&username="+username;

        JsonObjectRequest getBlockList = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject json) {
                try
                {
                    JSONArray blockedList = json.getJSONArray("blockedList");
                    JSONObject blockedUser;

                    for(int i = 0; i < blockedList.length(); i++)
                    {
                        blockedUser = blockedList.getJSONObject(i);
                        final String blocked = blockedUser.getString("blockeduserName");
                        setTextView(blocked);
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
        networkRequest.addToRequestQueue(getBlockList);
    }

    public void addBlock(View v) {
        final EditText editBlock = (EditText) findViewById(R.id.blockUser);
        final String blockedUserName = editBlock.getText().toString().trim();

        if(blockedUserName.isEmpty()) {
            Toast.makeText(getBaseContext(), "Please enter a username", Toast.LENGTH_LONG).show();
        }
        else {
            if (blockedUserName.equals(username)) {
                Toast.makeText(getBaseContext(), "Cannot enter your own username", Toast.LENGTH_LONG).show();
            } else {
                String url = getResources().getString(R.string.server_address) + "?rtype=addBlockedUser";
                StringRequest addBlockedUser = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("error")) {
                            Toast.makeText(getBaseContext(), "Please enter valid username", Toast.LENGTH_LONG).show();
                        } else if (response.trim().equals("already")) {
                            Toast.makeText(getBaseContext(), "User already blocked", Toast.LENGTH_LONG).show();
                        }else if (response.trim().equals("success")) {
                            Toast.makeText(getBaseContext(), "User added", Toast.LENGTH_LONG).show();
                            editBlock.setText("");
                            setTextView(blockedUserName);
                        }
                        else{
                            Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getBaseContext(), "Cannot communicate with Server.", Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("username", username);
                        params.put("blockedUserName", blockedUserName);
                        return params;
                    }
                };
                networkRequest.addToRequestQueue(addBlockedUser);
            }
        }
    }

    public void setTextView (String blockedUserName){
        final TextView tv = new TextView(getApplicationContext());
        tv.setTextColor(Color.parseColor("#FFFFFF"));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tv.setPadding(5, 0, 0, 30);
        tv.setText(blockedUserName);
        blockLayout.addView(tv);
        tv.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                final String removeBlock = tv.getText().toString();
                String url = getResources().getString(R.string.server_address) + "?rtype=removeBlockedUser";
                StringRequest removeBlockedUser = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("success")) {
                            Toast.makeText(getBaseContext(), "User removed", Toast.LENGTH_LONG).show();
                            blockLayout.removeView(tv);
                        } else{
                            Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getBaseContext(), "Cannot communicate with Server.", Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("username", username);
                        params.put("blockedUserName", removeBlock);
                        return params;
                    }
                };
                networkRequest.addToRequestQueue(removeBlockedUser);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_block_list, menu);
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

    public void account(View v){
        Intent intent = new Intent(this, EditProfile.class);
        intent.putExtra("username",username);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed(){
    }
}

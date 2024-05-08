package com.studybear.cdj.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class EditProfile extends FragmentActivity {

    private NetworkController networkRequest;
    public NavigationBarController navigationBar;
    private String username;
    private String university;
    private EditText fnameView;
    private EditText lnameView;
    private EditText oldpwView;
    private EditText newpwView;
    private EditText confirmpwView;
    private Spinner spinnerView;
    private static final String TAG = "EditProfile";
    private ArrayList<String> universityList;
    private ArrayAdapter<String> universityAdapter;

    public EditProfile() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        networkRequest = NetworkController.getInstance(getApplicationContext());
        Intent getUserInfo = getIntent();
        username = getUserInfo.getStringExtra("username");
        navigationBar = new NavigationBarController(this, username);
        ImageButton activeIcon = (ImageButton) findViewById(R.id.settingsButton);
        activeIcon.setImageResource(R.drawable.settingsa);
        fnameView = (EditText) findViewById(R.id.firstname);
        lnameView = (EditText) findViewById(R.id.lastname);
        oldpwView = (EditText) findViewById(R.id.oldpassword);
        newpwView = (EditText) findViewById(R.id.password);
        confirmpwView = (EditText) findViewById(R.id.confirmpassword);

        String url = getResources().getString(R.string.server_address) + "?rtype=editAccount&username=" + username;
        JsonObjectRequest profileAttr = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject json) {

                try
                {
                    String firstName = json.getString("firstName");
                    String lastName = json.getString("lastName");
                    String firstName1 = firstName.substring(0,1).toUpperCase() + firstName.substring(1);
                    String lastName1 = lastName.substring(0,1).toUpperCase() + lastName.substring(1);
                    fnameView.setText(firstName1);
                    lnameView.setText(lastName1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(),volleyError.toString(),Toast.LENGTH_LONG).show();
            }
        });
        networkRequest.addToRequestQueue(profileAttr);

        universityList = new ArrayList<>();

        String url2 = getResources().getString(R.string.server_address) + "?rtype=getUniversity&username=" + username;
        JsonObjectRequest universityListRequest = new JsonObjectRequest(Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject json) {
                try {
                    Log.d("RESPONSE", json.toString());
                    Spinner universitySpinner = (Spinner) findViewById(R.id.registerUniversity);
                    JSONArray jsonArray = json.getJSONArray("List");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        universityList.add(jsonObject.getString("universityName"));
                    }
                    universityAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, universityList);
                    universitySpinner.setAdapter(universityAdapter);

                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), volleyError.toString(), Toast.LENGTH_LONG).show();
            }
        });
        networkRequest.addToRequestQueue(universityListRequest);

    }

    public void Update(View v){

        if(newpwView.getText().toString().equals(confirmpwView.getText().toString())) {
            Spinner universitySpinner = (Spinner) findViewById(R.id.registerUniversity);
            university = universityAdapter.getItem(universitySpinner.getSelectedItemPosition());
            String url = getResources().getString(R.string.server_address) + "?rtype=editProfile";
            StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    if (s.trim().equals("success"))
                        Toast.makeText(getBaseContext(), "Profile Updated.", Toast.LENGTH_LONG).show();
                    else if (s.trim().equals("wrongPassword"))
                        Toast.makeText(getBaseContext(), "Incorrect password", Toast.LENGTH_LONG).show();
                    Log.d(TAG, s);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getBaseContext(), "Server Error" + error.toString(), Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("fname", fnameView.getText().toString().trim());
                    params.put("lname", lnameView.getText().toString().trim());
                    params.put("university", university);
                    params.put("uname", username);
                    params.put("oldpassword", oldpwView.getText().toString());
                    params.put("newpassword", newpwView.getText().toString());

                    return params;
                }
            };
            networkRequest.addToRequestQueue(postRequest);
        }
        else
            Toast.makeText(getBaseContext(), "Password fields to not match.", Toast.LENGTH_LONG).show();
    }

    public void Back(View v){
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
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
    public void onBackPressed(){
    }

    public void blockList(View v){
        Intent intent = new Intent(this, BlockList.class);
        intent.putExtra("username",username);
        startActivity(intent);
        finish();
    }

}

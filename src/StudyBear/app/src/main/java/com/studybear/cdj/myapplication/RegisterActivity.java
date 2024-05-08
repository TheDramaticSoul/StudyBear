package com.studybear.cdj.myapplication;

import android.content.Intent;
import android.gesture.GestureStroke;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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

public class RegisterActivity extends ActionBarActivity {
    NetworkController networkRequest;
    private String fname;
    private String lname;
    private String uname;
    private String email;
    private String pword;
    private String pconfirm;
    private String university;
    private ArrayList<String> universityList;
    private ArrayAdapter<String> universityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        networkRequest = NetworkController.getInstance(getApplicationContext());
        universityList = new ArrayList<>();

        String url2 = getResources().getString(R.string.server_address) + "?rtype=getUniversityList";
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

                } catch (JSONException e) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void Register(View v) {
        TextView fnameview = (TextView) findViewById(R.id.firstname);
        TextView lnameview = (TextView) findViewById(R.id.lastname);
        TextView unameview = (TextView) findViewById(R.id.username);
        TextView emailview = (TextView) findViewById(R.id.email);
        TextView pwordview = (TextView) findViewById(R.id.password);
        TextView confirm = (TextView) findViewById(R.id.confirmpassword);
        Spinner universityView = (Spinner) findViewById(R.id.registerUniversity);

        fname = fnameview.getText().toString().trim();
        lname = lnameview.getText().toString().trim();
        uname = unameview.getText().toString().trim().toLowerCase();
        email = emailview.getText().toString().trim();
        pword = pwordview.getText().toString();
        pconfirm = confirm.getText().toString();
        university = universityAdapter.getItem(universityView.getSelectedItemPosition()).trim();

        if (fname.contains(" ") || lname.contains(" ") || uname.contains(" ") || email.contains(" "))
            Toast.makeText(getBaseContext(), "User fields cannot contain spaces.", Toast.LENGTH_LONG).show();

        else if (fname.isEmpty() || lname.isEmpty() || uname.isEmpty() || email.isEmpty() || university.isEmpty())
            Toast.makeText(getBaseContext(), "All user fields must be filled out.", Toast.LENGTH_LONG).show();

            //  else if(!email.contains("@") || !email.contains("edu") || !email.contains("."))
            //     Toast.makeText(getBaseContext(),"Invalid email format.", Toast.LENGTH_LONG).show();

        else if (!pword.equals(pconfirm)) {
            Toast.makeText(getBaseContext(), "Password fields do not match", Toast.LENGTH_LONG).show();
            pwordview.setText(null);
            confirm.setText(null);
        } else if (pword.length() < 8) {
            Toast.makeText(getBaseContext(), "Passwords must be at least 8 characters.", Toast.LENGTH_LONG).show();
            pwordview.setText(null);
            confirm.setText(null);
        } else {

            String url = getResources().getString(R.string.server_address) + "?rtype=register";
            StringRequest registerPost = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Response", response);
                            if (response.trim().equals("uname_error"))
                                Toast.makeText(getBaseContext(), "Username/Email Already Taken!", Toast.LENGTH_LONG).show();
                            else if (!(response.trim().equals("success")))
                                Toast.makeText(getBaseContext(), "Registration Error!", Toast.LENGTH_LONG).show();
                            else if (response.trim().equals("success")){
                                Toast.makeText(getBaseContext(), "Email sent to: " + email, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(RegisterActivity.this, RegisterConfirm.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                                Toast.makeText(getBaseContext(), "Unknown Error", Toast.LENGTH_LONG).show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getBaseContext(), "Server Error", Toast.LENGTH_LONG).show();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("fname", fname);
                    params.put("lname", lname);
                    params.put("uname", uname);
                    params.put("email", email);
                    params.put("pword", pword);
                    params.put("pconfirm", pconfirm);
                    params.put("university", university);
                    return params;
                }
            };
            registerPost.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            networkRequest.addToRequestQueue(registerPost);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

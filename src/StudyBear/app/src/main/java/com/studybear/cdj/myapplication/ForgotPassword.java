package com.studybear.cdj.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


public class ForgotPassword extends ActionBarActivity {
    NetworkController networkRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        networkRequest = NetworkController.getInstance(getApplicationContext());

        TextView emailView = (TextView) findViewById(R.id.fpEmail);
        final String email = emailView.getText().toString().trim();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forgot_password, menu);
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

    public void submitEmail(View v) {

        TextView fpEmail = (TextView) findViewById(R.id.fpEmail);
        final String email = fpEmail.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(getBaseContext(), "Please enter an email address", Toast.LENGTH_LONG).show();
        } else {
            String url = getResources().getString(R.string.server_address) + "?rtype=sendPasswordLink";
            StringRequest pwResetRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    if (s.trim().equals("success"))
                        Toast.makeText(getApplicationContext(), "Password reset link sent to: " + email, Toast.LENGTH_LONG).show();

                    else if (s.trim().equals("addresserror"))
                        Toast.makeText(getApplicationContext(), "Email address not found or Account is not activiated. " + email, Toast.LENGTH_LONG).show();
                    else {
                        Log.d("RESPONSE", s);
                        Toast.makeText(getApplicationContext(), "Error sending email. Try again.", Toast.LENGTH_LONG).show();
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("ERROR RESPONSE", error.toString());
                    Toast.makeText(getApplicationContext(), "Cannot communicate with the server.", Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", email);
                    return params;
                }
            };
            pwResetRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            networkRequest.addToRequestQueue(pwResetRequest);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
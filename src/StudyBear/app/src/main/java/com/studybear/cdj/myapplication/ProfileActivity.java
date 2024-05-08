package com.studybear.cdj.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

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

public class ProfileActivity extends ActionBarActivity {
    public NetworkController networkRequest;
    public NavigationBarController navigationBar;
    public TextView bio;
    public TextView classes;
    public TextView name;
    public TextView university;
    public String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        networkRequest = NetworkController.getInstance(getApplicationContext());
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        navigationBar = new NavigationBarController(this, username);
        ImageButton activeIcon = (ImageButton) findViewById(R.id.profileButton);
        activeIcon.setImageResource(R.drawable.profilea);

        String url = getResources().getString(R.string.server_address) + "?rtype=getProfile&username=" + username;

        bio = (TextView) findViewById(R.id.Biography);
        classes = (TextView) findViewById(R.id.Classes);
        name = (TextView) findViewById(R.id.Name);
        university = (TextView) findViewById(R.id.University);

        JsonObjectRequest profileAttr = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject json) {
                try
                {
                    Log.d("JSONRESPONSE", json.toString());
                    bio.setText(json.getString("biography"));
                    String firstName = json.getString("firstName");
                    String lastName = json.getString("lastName");
                    String firstName1 = firstName.substring(0,1).toUpperCase() + firstName.substring(1);
                    String lastName1 = lastName.substring(0,1).toUpperCase() + lastName.substring(1);
                    name.setText(firstName1 + " " + lastName1);
                    university.setText(json.getString("universityName"));

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
                Toast.makeText(getApplicationContext(),volleyError.toString(),Toast.LENGTH_LONG).show();
            }
        });
        networkRequest.addToRequestQueue(profileAttr);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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

    public void editBiography (View V) {
        EditText editBio = (EditText) findViewById(R.id.editBio);
        ImageButton editBioButton = (ImageButton) findViewById(R.id.editBioButton);
        ImageButton saveBioButton = (ImageButton) findViewById(R.id.saveBioButton);
        TextView Biography = (TextView) findViewById(R.id.Biography);
        String bio = Biography.getText().toString();
        editBio.setText(bio);
        editBio.setSelection(editBio.getText().length());
        editBioButton.setVisibility(View.GONE);
        saveBioButton.setVisibility(View.VISIBLE);
        ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.my_switcher);
        switcher.showNext();
        InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        imm.showSoftInput(editBio, InputMethodManager.SHOW_IMPLICIT);
    }

    public void saveBiography(View v){

        final TextView editBio = (TextView) findViewById(R.id.editBio);
        final String newBio = editBio.getText().toString().trim();

        ImageButton editBioButton = (ImageButton) findViewById(R.id.editBioButton);
        ImageButton saveBioButton = (ImageButton) findViewById(R.id.saveBioButton);
        TextView BiographyText = (TextView) findViewById(R.id.Biography);
        BiographyText.setText(newBio);
        saveBioButton.setVisibility(View.GONE);
        editBioButton.setVisibility(View.VISIBLE);
        ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.my_switcher);
        switcher.showNext();
        InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editBio.getWindowToken(), 0);

            String url = getResources().getString(R.string.server_address) + "?rtype=saveBio";
            StringRequest saveBio = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Testing to see if login passed or failed. If passed, the Server returns the string success/failed returns error
                            //Log.d("Response", response);
                            if (response.trim().equals("success"))
                                Toast.makeText(getBaseContext(), "Please enter a registered email address", Toast.LENGTH_LONG).show();
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
                    params.put("biography", newBio);
                    params.put("username", username);
                    return params;
                }
            };
            //Toast.makeText(getBaseContext(), "Sent request to server", Toast.LENGTH_LONG).show();
            networkRequest.addToRequestQueue(saveBio);
        }

    public void editClasses(View v){
        Intent intent = new Intent(this, EditClasses.class);
        intent.putExtra("username",username);
//        intent.putExtra("classes", classes);
//        intent.putExtra("university", university);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed(){
    }
}


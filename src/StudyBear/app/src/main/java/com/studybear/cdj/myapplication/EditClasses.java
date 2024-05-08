package com.studybear.cdj.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditClasses extends ActionBarActivity {
    private NetworkController networkController;
    private String username;
    private String TAG = "EditClasses";
    private ArrayAdapter<String> classAdapater;
    private ArrayList<String> universityList;
    private ArrayList<String> removeList;
    private ArrayList<String> insertList;
    private ArrayList<String> originalClassList;
    private ArrayAdapter<String> insertListAdapter;
    private ArrayList<String> classList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_classes);
        networkController = NetworkController.getInstance(getApplicationContext());

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        universityList = new ArrayList<>();
        removeList = new ArrayList<>();
        classList = new ArrayList<>();
        insertList = new ArrayList<>();
        originalClassList = new ArrayList<>();

        String url = getResources().getString(R.string.server_address) + "?rtype=getUserClasses&username=" + username;
        JsonObjectRequest classListRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject json) {
                try {
                    Log.d(TAG, json.toString());
                    JSONArray jsonArray = json.getJSONArray("classList");
                    JSONObject classItem;
                    for (int i = 0; i < jsonArray.length(); i++) {

                        classItem = jsonArray.getJSONObject(i);
                        String classItemString = classItem.getString("classId") + ", " + classItem.getString("className") + ", " + classItem.getString("professorLname") + ", " + classItem.getString("professorFname");
                        originalClassList.add(classItemString);
                        classList.add(classItemString);
                    }
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
        networkController.addToRequestQueue(classListRequest);

        insertListAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, classList);
        ListView lv = (ListView) findViewById(R.id.classListView);
        lv.setAdapter(insertListAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (originalClassList.contains(insertListAdapter.getItem(position)))
                    removeList.add(insertListAdapter.getItem(position));

                insertListAdapter.remove(insertListAdapter.getItem(position));
                insertListAdapter.notifyDataSetChanged();
                //Toast.makeText(getApplicationContext(),removeList.toString(),Toast.LENGTH_LONG).show();
            }
        });

        String url2 = getResources().getString(R.string.server_address) + "?rtype=getUniversity&username=" + username;
        JsonObjectRequest universityListRequest = new JsonObjectRequest(Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject json) {
                try {
                    Spinner universitySpinner = (Spinner) findViewById(R.id.universitySpinner);
                    universityList.clear();
                    JSONArray jsonArray = json.getJSONArray("List");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        universityList.add(jsonObject.getString("universityName"));
                    }

                    final ArrayAdapter<String> universityAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, universityList);
                    universitySpinner.setAdapter(universityAdapter);
                    universitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            try {
                                getMajor(universityAdapter.getItem(position));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
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
        networkController.addToRequestQueue(universityListRequest);
    }

    public void getMajor(final String university) throws UnsupportedEncodingException {

        String encodedParam = URLEncoder.encode(university, "UTF-8");
        String url = getResources().getString(R.string.server_address) + "?rtype=getMajor&university=" + encodedParam;

        JsonObjectRequest majorListRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject json) {
                try {
                    Log.d("MAJOR", json.toString());

                    final Spinner majorSpinner = (Spinner) findViewById(R.id.majorSpinner);
                    ArrayList<String> majorList = new ArrayList<>();

                    if (!json.isNull("majorList")) {
                        JSONArray jsonArray = json.getJSONArray("majorList");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            majorList.add(jsonObject.getString("major"));
                        }
                    } else
                        majorList.add("No Majors");
                    final ArrayAdapter<String> majorAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, majorList);
                    majorSpinner.setAdapter(majorAdapter);
                    majorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            try {
                                getClasses(majorAdapter.getItem(position), university);

                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(TAG, volleyError.toString());
                Toast.makeText(getApplicationContext(), volleyError.toString(), Toast.LENGTH_LONG).show();
            }
        });
        networkController.addToRequestQueue(majorListRequest);
    }

    public void getClasses(String major, String university) throws UnsupportedEncodingException {

        String encodedParam = URLEncoder.encode(major, "UTF-8");
        String encodedParam2 = URLEncoder.encode(university, "UTF-8");
        String url = getResources().getString(R.string.server_address) + "?rtype=getClasses&username=" + username + "&major=" + encodedParam + "&university=" + encodedParam2;
        JsonObjectRequest classListRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject json) {
                try {
                    final Spinner classSpinner = (Spinner) findViewById(R.id.classSpinner);
                    ArrayList<String> classList = new ArrayList<>();
                    if (!json.isNull("classList")) {
                        JSONArray jsonArray = json.getJSONArray("classList");
                        Log.d(TAG, json.toString());
                        JSONObject classItem;

                        for (int i = 0; i < jsonArray.length(); i++) {

                            classItem = jsonArray.getJSONObject(i);
                            String classItemString = classItem.getString("classId") + ", " + classItem.getString("className") + ", " + classItem.getString("professorLname") + ", " + classItem.getString("professorFname");
                            classList.add(classItemString);
                        }
                    } else
                        classList.add("No Classes");
                    classAdapater = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, classList);
                    classSpinner.setAdapter(classAdapater);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(TAG, volleyError.toString());
                Toast.makeText(getApplicationContext(), volleyError.toString(), Toast.LENGTH_LONG).show();
            }
        });
        networkController.addToRequestQueue(classListRequest);
    }

    public void Add(View v) {
        Spinner classSpinner = (Spinner) findViewById(R.id.classSpinner);
        ListView lv = (ListView) findViewById(R.id.classListView);
        //If the class list does contain the selected class already, then add it to the adapter to be shown in the class List View
        if (!classList.contains(classAdapater.getItem(classSpinner.getSelectedItemPosition()))) {
            insertListAdapter.add(classAdapater.getItem(classSpinner.getSelectedItemPosition()));
            insertListAdapter.notifyDataSetChanged();
        }

        //If the original class list nor the list to insert classes already has the class added, then add the class
        if (!originalClassList.contains(classAdapater.getItem(classSpinner.getSelectedItemPosition())) && !insertList.contains(classAdapater.getItem(classSpinner.getSelectedItemPosition())))
            insertList.add(classAdapater.getItem(classSpinner.getSelectedItemPosition()));

        //Set onclick listeners for the newly added classes on the class list view
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //When an item is clicked in the class list view, then we add it to the remove list if the orginal list contained it
                //otherwise, there's no point since it's not in the database anyway.
                //Then update the insertListAdapter to reflect the changes (new list)
                if (originalClassList.contains(classAdapater.getItem(position)))
                    removeList.add(insertListAdapter.getItem(position));
                insertListAdapter.remove(insertListAdapter.getItem(position));
                insertListAdapter.notifyDataSetChanged();
            }
        });
        lv.setAdapter(insertListAdapter);
    }

    public void Save(View v) {

        final JSONArray jsonRemoveList = new JSONArray();
        for (int i = 0; i < removeList.size(); i++) {
            jsonRemoveList.put(removeList.get(i));
        }

        final JSONArray jsonInsertList = new JSONArray();
        for (int i = 0; i < insertList.size(); i++) {
            jsonInsertList.put(insertList.get(i));
        }

        String url = getResources().getString(R.string.server_address) + "?rtype=saveClasses&username";
        StringRequest saveRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Toast.makeText(getBaseContext(), "Classes Updated.", Toast.LENGTH_LONG).show();
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
                params.put("username", username);
                params.put("removeList", jsonRemoveList.toString());
                params.put("insertList", jsonInsertList.toString());

                return params;
            }
        };
        networkController.addToRequestQueue(saveRequest);
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_classes, menu);
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
}

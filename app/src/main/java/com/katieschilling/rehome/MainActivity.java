package com.katieschilling.rehome;

import android.support.v7.app.ActionBarActivity;

import com.katieschilling.rehome.R;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;



public class MainActivity extends ListActivity {

    private ProgressDialog pDialog;

    // URL to get adoption applications json
    private static final String url = "http://localhost:3000/applications";

    // JSON node names
    private static final String TAG_APPLICATIONS = "applications";
    private static final String TAG_PET_NAME = "pet_name";
    private static final String TAG_OWNER_ID = "owner_id";
    private static final String TAG_REHOMING_FEE = "rehoming_fee";
    private static final String TAG_BACKGROUND_CHECK_REQUIRED = "background_check_required";
    private static final String TAG_OWNER_ACCEPTED = "owner_accepted";
    private static final String TAG_CHARITY = "charity";
    private static final String TAG_APPLICANT_ID = "applicant_id";


    // applications JSONArray
    JSONArray applications = null;

    // Hashmap for ListView
    ArrayList<HashMap<String, Object>> applicationList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // call async task to get json applications list
        new GetApplications().execute();

        applicationList = new ArrayList<HashMap<String, Object>>();
        ListView lv = getListView();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private class GetApplications extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // show waiting...
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Just a sec...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            ServiceHandler sh = new ServiceHandler();

            // make request
            String jsonString = sh.makeServiceCall(url, ServiceHandler.GET);

            // Log the response for safekeeping
            Log.d("Response from JSON request is:", "> " + jsonString);

            if (jsonString != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonString);
                    applications = jsonObj.getJSONArray(TAG_APPLICATIONS);

                    for (int i = 0; i < applications.length(); i++) {
                        JSONObject a = applications.getJSONObject(i);
                        String name = a.getString(TAG_PET_NAME);
                        Integer owner_id = a.getInt(TAG_OWNER_ID);
                        Double fee = a.getDouble(TAG_REHOMING_FEE);
                        Boolean background = a.getBoolean(TAG_BACKGROUND_CHECK_REQUIRED);
                        Boolean owner_accepted = a.getBoolean(TAG_OWNER_ACCEPTED);
                        String charity = a.getString(TAG_CHARITY);
                        Integer applicant_id = a.getInt(TAG_APPLICANT_ID);

                        //tmp hashmap for application
                        HashMap<String, Object> application = new HashMap<String, Object>();

                        //construct HashMap
                        application.put(TAG_PET_NAME, name);
                        application.put(TAG_OWNER_ID, owner_id);
                        application.put(TAG_REHOMING_FEE, fee);
                        application.put(TAG_BACKGROUND_CHECK_REQUIRED, background);
                        application.put(TAG_OWNER_ACCEPTED, owner_accepted);
                        application.put(TAG_CHARITY, charity);
                        application.put(TAG_APPLICANT_ID, applicant_id);

                        //add application to list

                        applicationList.add(application);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the endpoint");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // dismiss dialog yay
            if (pDialog.isShowing())
                pDialog.dismiss();

            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, applicationList,
                    R.layout.list_item, new String[] {TAG_PET_NAME,TAG_OWNER_ID, TAG_REHOMING_FEE, TAG_BACKGROUND_CHECK_REQUIRED, TAG_OWNER_ACCEPTED, TAG_CHARITY, TAG_APPLICANT_ID }, new int[] { R.id.name, R.id.owner_id, R.id.fee, R.id.background, R.id.owner_accepted, R.id.charity, R.id.applicant_id}
            );
            setListAdapter(adapter);
        }
    }
}

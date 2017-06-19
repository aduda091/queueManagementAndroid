package hr.unipu.duda.justintime;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hr.unipu.duda.justintime.model.Facility;

public class FacilityListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_list);
        setTitle("Ustanove");

        final ArrayList<Facility> facilities = new ArrayList<>();
        final ListView facilityListView = (ListView) findViewById(R.id.facilityListView);

        //Volley
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://justin-time.herokuapp.com/facility/read-all";

        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
               for(int i=0; i<response.length();i++) {
                   try {
                       JSONObject facilityObject = response.getJSONObject(i);
                       Facility facility = new Facility();
                       facility.setId(facilityObject.getString("id"));
                       facility.setName(facilityObject.getString("name"));
//                       facility.setAddress(facilityObject.getString("address"));
//                       facility.setTelephone(facilityObject.getString("telephone"));
                       //todo:queues

                       facilities.add(facility);
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }

               }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(FacilityListActivity.this, "Nisam uspio učitati json", Toast.LENGTH_SHORT).show();
                Log.d("onErrorResponse", "onErrorResponse: " + error.getMessage());
                AlertDialog.Builder builder = new AlertDialog.Builder(FacilityListActivity.this);
                builder.setMessage("Neuspješan dohvat podataka, molim pokušajte ponovno!").setNegativeButton("U redu", null).create().show();
            }
        });

        queue.add(jsObjRequest);
        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                facilityListView.setAdapter(new ArrayAdapter<Facility>(FacilityListActivity.this, android.R.layout.simple_list_item_1, facilities));
            }
        });

        facilityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(FacilityListActivity.this, FacilityDetailActivity.class);
                intent.putExtra("id", facilities.get(i).getId());
                startActivity(intent);
            }
        });
    }
}

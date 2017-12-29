package hr.unipu.duda.justintime.activities;

import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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
import java.util.List;

import hr.unipu.duda.justintime.R;
import hr.unipu.duda.justintime.adapters.FacilityAdapter;
import hr.unipu.duda.justintime.model.Facility;
import hr.unipu.duda.justintime.util.AppController;

public class FacilityListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<Facility> facilities;
    RequestQueue volleyQueue;
    SwipeRefreshLayout swipeContainer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_list);
        setTitle("Ustanove");

        volleyQueue = Volley.newRequestQueue(this);

        recyclerView = (RecyclerView) findViewById(R.id.facilityRecyclerView);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swipeContainer.setSize(SwipeRefreshLayout.LARGE);
        swipeContainer.setColorSchemeResources(R.color.colorPrimary);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateFacilities();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFacilities();
    }

    private void populateFacilities() {
        swipeContainer.setRefreshing(true);
        //dohvaćanje svih ustanova
        facilities = new ArrayList<>();
        String url = AppController.API_URL + "/facilities";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("populateFacilities", "onResponse: " + response.toString());
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject facilityObject = response.getJSONObject(i);
                        Facility facility = new Facility();
                        facility.setId(facilityObject.getString("_id"));
                        facility.setName(facilityObject.getString("name"));
                        facilities.add(facility);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter = new FacilityAdapter(FacilityListActivity.this, facilities);
                recyclerView.setAdapter(adapter);
                swipeContainer.setRefreshing(false);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("onErrorResponse", "onErrorResponse: " + error.getMessage());
                swipeContainer.setRefreshing(false);
                AlertDialog.Builder builder = new AlertDialog.Builder(FacilityListActivity.this);
                builder.setMessage("Neuspješan dohvat podataka, molim pokušajte ponovno!")
                        .setNegativeButton("U redu", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                populateFacilities();
                            }
                        })
                        .create().show();

            }
        });

        volleyQueue.add(request);
    }
}

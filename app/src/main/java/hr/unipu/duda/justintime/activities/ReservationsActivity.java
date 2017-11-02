package hr.unipu.duda.justintime.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import hr.unipu.duda.justintime.R;
import hr.unipu.duda.justintime.adapters.ReservationAdapter;
import hr.unipu.duda.justintime.model.Facility;
import hr.unipu.duda.justintime.model.Queue;
import hr.unipu.duda.justintime.util.UserController;

public class ReservationsActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    RequestQueue volleyQueue;
    JsonObjectRequest request;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List <Queue> reservations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations);

        reservations = new ArrayList<>();

        progressDialog = new ProgressDialog(ReservationsActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Dohvaćanje podataka u tijeku...");
        progressDialog.setCancelable(false);
        if(!progressDialog.isShowing()) progressDialog.show();


        volleyQueue = Volley.newRequestQueue(this);
        String url = "https://justin-time.herokuapp.com/queue/getQueuedUser?access_token=" + UserController.getInstance().getToken();
        request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Log.d("RESPONSE", "Response: " +response);
                try {
                    JSONObject queuedFacilities = response.getJSONObject("queuedFacilities");
                    Log.d("queuedFacilities", queuedFacilities.toString());
                    JSONArray queuedFacilitiesIds = queuedFacilities.names();
                    Log.d("queuedFacilitiesIds", queuedFacilitiesIds.toString());
                    for(int i=0;i<queuedFacilitiesIds.length();i++) {
                        JSONObject queuedFacility = queuedFacilities.getJSONObject(queuedFacilitiesIds.getString(i));
                        Log.d("queuedFacility", queuedFacility.toString());
                        Facility facility = new Facility();
                        facility.setName(queuedFacility.getString("name"));
                        facility.setId(queuedFacility.getString("id"));

                        // to radi Log.d("Facility", facility.toString());
                        // isto Log.d("queuedFacilityNames", queuedFacility.names().toString());// ["id","name","address","mail","telephone","queues"]
                        //ovako pokaže string da Log.d("queuedFacilityQueues", queuedFacility.optString("queues")); //{"5878d3f9b3646427748afe8d":{"id":"5878d3f9b3646427748afe8d","name":"Referada","priority":1}}
                        //queues nije ni array ni objekt
                        //JSONArray queues = queuedFacility.getJSONArray("queues");

                        String queueStringAll = queuedFacility.optString("queues");
                        String queueString = "[" + queueStringAll + "]";
                        JSONArray queues = new JSONArray(queueString);


                        //JSONArray queuesIds = queues.names();
                        //for(int j=0;j<queuesIds.length();j++) {
                        for(int j=0;j<queues.length();j++) {
//                            JSONObject tempQueue = queues.getJSONObject(queuesIds.getString(j));
                            JSONObject tempObject = queues.getJSONObject(j);
                            Log.d("tempObject", tempObject.toString());
                            JSONArray tempObjectIds = tempObject.names();
                            for(int k=0;k<tempObjectIds.length();k++) {
                                JSONObject tempQueue = tempObject.getJSONObject(tempObjectIds.getString(k));
                                Log.d("tempQueue", tempQueue.toString());
                                Queue queue = new Queue();
                                queue.setFacility(facility);
                                queue.setId(tempQueue.getString("id"));
                                queue.setName(tempQueue.getString("name"));
                                queue.setMyNumber(tempQueue.getInt("priority"));
                                //todo: pravi brojevi
                                queue.setCurrentNumber(0);
                                reservations.add(queue);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(progressDialog.isShowing()) progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", "onErrorResponse: " +error);
                if(progressDialog.isShowing()) progressDialog.dismiss();
            }
        });

        volleyQueue.add(request);



        /*Facility facility = new Facility();
        facility.setName("Općina");
        Queue queue = new Queue("bla","Carina");
        queue.setFacility(facility);
        queue.setCurrentNumber(5);
        queue.setMyNumber(7);
        reservations.add(queue);

        Facility facility1 = new Facility();
        facility1.setName("Konzum");
        Queue queue1 = new Queue();
        queue1.setFacility(facility1);
        queue1.setName("Brza blagajna");
        queue1.setCurrentNumber(2);
        queue1.setMyNumber(3);
        reservations.add(queue1);
        */

        volleyQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                if(progressDialog.isShowing()) progressDialog.dismiss();
                recyclerView = (RecyclerView) findViewById(R.id.reservationRecyclerView);
                recyclerView.setHasFixedSize(false);
                recyclerView.setLayoutManager(new LinearLayoutManager(ReservationsActivity.this));
                adapter = new ReservationAdapter(ReservationsActivity.this, reservations);
                recyclerView.setAdapter(adapter);
            }
        });


    }
}

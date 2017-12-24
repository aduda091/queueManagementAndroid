package hr.unipu.duda.justintime.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hr.unipu.duda.justintime.R;
import hr.unipu.duda.justintime.adapters.ReservationAdapter;
import hr.unipu.duda.justintime.model.Reservation;
import hr.unipu.duda.justintime.util.AppController;

public class ReservationsActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    RequestQueue volleyQueue;
    JsonObjectRequest request;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List <Reservation> reservations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations);

        reservations = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.reservationRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ReservationsActivity.this));

        progressDialog = new ProgressDialog(ReservationsActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Dohvaćanje podataka u tijeku...");
//        progressDialog.setCancelable(false);
        if(!progressDialog.isShowing()) progressDialog.show();


        volleyQueue = Volley.newRequestQueue(this);
        String url = AppController.API_URL + "/queue/getQueuedUser?access_token=" + AppController.getInstance().getToken();
        request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Log.d("RESPONSE", "Response: " +response);
//                try {
//                    //bitni podaci su unutar queuedFacilities JSON objekta, ostali nisu bitni ovdje
//                    JSONObject queuedFacilities = response.getJSONObject("queuedFacilities");
//                    Log.d("queuedFacilities", queuedFacilities.toString());
//                    //id-ovi redova su ključevi i nisu unaprijed poznati (ovise o korisniku)
//                    JSONArray queuedFacilitiesIds = queuedFacilities.names();
//                    Log.d("queuedFacilitiesIds", queuedFacilitiesIds.toString());
//
//                    //prolaz kroz sve ustanove u kojima korisnik čeka u redu
//                    for(int i=0;i<queuedFacilitiesIds.length();i++) {
//                        JSONObject queuedFacility = queuedFacilities.getJSONObject(queuedFacilitiesIds.getString(i));
//                        Log.d("queuedFacility", queuedFacility.toString());
//
//                        //postavljanje podataka o ustanovi
//                        Facility facility = new Facility();
//                        facility.setName(queuedFacility.getString("name"));
//                        facility.setId(queuedFacility.getString("id"));
//
//                        //queues nije ni array ni objekt? nekakva greška u parsiranju
//                        //ovako pokaže string Log.d("queuedFacilityQueues", queuedFacility.optString("queues"));
//                        // {"5878d3f9b3646427748afe8d":{"id":"5878d3f9b3646427748afe8d","name":"Referada","priority":1}}
//
//                        //prevođenje pročitanog JSON teksta u array koji se može parsirati
//                        String queueStringAll = queuedFacility.optString("queues");
//                        String queueString = "[" + queueStringAll + "]";
//                        JSONArray queues = new JSONArray(queueString);
//
//                        //prolaz kroz svaki red iznad pročitane ustanove ustanove
//                        for(int j=0;j<queues.length();j++) {
//                            JSONObject tempObject = queues.getJSONObject(j);
//                            Log.d("tempObject", tempObject.toString());
//                            JSONArray tempObjectIds = tempObject.names();
//                            for(int k=0;k<tempObjectIds.length();k++) {
//                                JSONObject tempQueue = tempObject.getJSONObject(tempObjectIds.getString(k));
//                                Log.d("tempQueue", tempQueue.toString());
//                                //postavljanje podataka o pročitanom redu
//                                Queue queue = new Queue();
//                                queue.setFacility(facility);
//                                queue.setId(tempQueue.getString("id"));
//                                queue.setName(tempQueue.getString("name"));
//                                //korisnikov broj
//                                queue.setMyNumber(tempQueue.getInt("priority"));
//                                //poseban request za dohvaćanje trenutnog broja u redu
//                                getCurrentNumber(queue);
//                            }
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//
//                }

                if(progressDialog.isShowing()) progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", "onErrorResponse: " +error);
                if(progressDialog.isShowing()) progressDialog.dismiss();
                String message = "Neuspješan dohvat podataka, molim pokušajte ponovno.";
                try {
                    if (error.networkResponse.statusCode == 401) {
                        message = "Morate se ponovno prijaviti.";
                        //todo: odjaviti korisnika i preusmjeriti ga na login
                    }
                    if (error.networkResponse.statusCode == 404) {
                        message = "Nemate rezervacija.";
                        //todo: preusmjeriti na popis ustanova
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(ReservationsActivity.this);
                builder.setMessage(message)
                        .setNegativeButton("U redu", null)
                        .create().show();
            }
        });

        //volleyQueue.add(request);

        volleyQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                if(progressDialog.isShowing()) progressDialog.dismiss();

                adapter = new ReservationAdapter(ReservationsActivity.this, reservations);
                recyclerView.setAdapter(adapter);
            }
        });


    }
}

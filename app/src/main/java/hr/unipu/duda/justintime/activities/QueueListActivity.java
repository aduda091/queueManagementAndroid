package hr.unipu.duda.justintime.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import hr.unipu.duda.justintime.R;
import hr.unipu.duda.justintime.adapters.QueueAdapter;
import hr.unipu.duda.justintime.model.Facility;
import hr.unipu.duda.justintime.model.Queue;

public class QueueListActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    Facility facility;
    RequestQueue volleyQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_list);
        facility = new Facility();
        facility.setId(getIntent().getStringExtra("id"));
        facility.setName(getIntent().getStringExtra("name"));
        setTitle(facility.getName() + " - redovi" );

        recyclerView = (RecyclerView) findViewById(R.id.queueRecyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(QueueListActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Dohvaćanje podataka u tijeku...");
        progressDialog.setCancelable(false);
        if(!progressDialog.isShowing()) progressDialog.show();

        //Volley
        volleyQueue = Volley.newRequestQueue(this);
        String url = "https://justin-time.herokuapp.com/facility/" +facility.getId();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray queues = response.getJSONArray("queues");
                    for (int i=0;i<queues.length();i++) {
                        JSONObject object = queues.getJSONObject(i);
                        Queue queue = new Queue();
                        queue.setId(object.getString("id"));
                        queue.setName(object.getString("name"));
                        queue.setFacility(facility);
                        getPriority(queue);
                        //facility.addQueue(queue);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("onErrorResponse", "onErrorResponse: " + error.getMessage());
                if(progressDialog.isShowing()) progressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(QueueListActivity.this);
                builder.setMessage("Neuspješan dohvat podataka, molim pokušajte ponovno!")
                        .setNegativeButton("U redu", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                recreate();
                            }
                        })
                        .create().show();
            }
        });

        volleyQueue.add(request);
        volleyQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                if(progressDialog.isShowing()) progressDialog.dismiss();
                adapter = new QueueAdapter(QueueListActivity.this, facility.getQueues());
                recyclerView.setAdapter(adapter);
            }
        });

    }


    private void getPriority(final Queue queue) {
        String url = "https://justin-time.herokuapp.com/queue/" + queue.getId();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    queue.setPriority(response.getInt("priority"));
                    facility.addQueue(queue);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("getPriority", "onErrorResponse: " + error.networkResponse.statusCode);
                queue.setPriority(0);
                facility.addQueue(queue);
            }
        });

        volleyQueue.add(request);
    }
}

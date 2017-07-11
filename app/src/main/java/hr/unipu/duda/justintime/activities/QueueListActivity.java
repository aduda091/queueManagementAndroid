package hr.unipu.duda.justintime.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import hr.unipu.duda.justintime.fragments.NavigationFragment;
import hr.unipu.duda.justintime.model.Facility;
import hr.unipu.duda.justintime.model.Queue;

public class QueueListActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    Facility facility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_list);

        facility = new Facility();
        facility.setId(getIntent().getStringExtra("id"));
        facility.setName(getIntent().getStringExtra("name"));
        setTitle(facility.getName() + " - redovi" );

        final ListView queueListView = (ListView) findViewById(R.id.queueListView);

        progressDialog = new ProgressDialog(QueueListActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Dohvaćanje podataka u tijeku...");
        progressDialog.setCancelable(false);
        if(!progressDialog.isShowing()) progressDialog.show();

        //Volley
        RequestQueue volleyQueue = Volley.newRequestQueue(this);
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
                        facility.addQueue(queue);
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
                queueListView.setAdapter(new ArrayAdapter<Queue>(QueueListActivity.this, android.R.layout.simple_list_item_1, facility.getQueues()));
            }
        });

    }
}

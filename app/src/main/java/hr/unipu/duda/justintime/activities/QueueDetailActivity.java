package hr.unipu.duda.justintime.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import hr.unipu.duda.justintime.R;
import hr.unipu.duda.justintime.model.Facility;
import hr.unipu.duda.justintime.model.Queue;
import hr.unipu.duda.justintime.util.UserController;

public class QueueDetailActivity extends AppCompatActivity {
    TextView facilityNameTextView, queueNameTextView, priorityTextView, myPriorityTextView;
    Button reserveButton;
    Animation animation;
    int nextNumber;
    Queue queue;
    Facility facility;
    RequestQueue volleyQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_detail);

        volleyQueue = Volley.newRequestQueue(this);

        facilityNameTextView = (TextView) findViewById(R.id.facilityNameTextView);
        queueNameTextView = (TextView) findViewById(R.id.queueNameTextView);
        priorityTextView = (TextView) findViewById(R.id.priorityTextView);
        myPriorityTextView = (TextView) findViewById(R.id.myPriorityTextView);
        reserveButton = (Button) findViewById(R.id.reserveButton);

        animation = AnimationUtils.loadAnimation(this, R.anim.zoomin);

        facility = new Facility();
        facility.setName(getIntent().getStringExtra("facilityName"));
        facility.setId(getIntent().getStringExtra("facilityId"));


        queue = new Queue();
        queue.setFacility(facility);
        queue.setId(getIntent().getStringExtra("queueId"));
        queue.setName(getIntent().getStringExtra("queueName"));
        queue.setPriority(getIntent().getIntExtra("queuePriority", 0));

        setTitle(facility.getName() + " - " + queue.getName());

        facilityNameTextView.setText(facility.getName());
        queueNameTextView.setText(queue.getName());
        priorityTextView.setText("Trenutni broj: " + queue.getPriority());
        nextNumber = queue.getPriority()+1;
        reserveButton.setText("Uzmi broj:\n" + nextNumber);

        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptEnterQueue();
                reserveButton.startAnimation(animation);
            }
        });

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                reserveButton.setVisibility(View.GONE);
                //myPriorityTextView.setText("Vaš broj je " + nextNumber);
                //myPriorityTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void attemptEnterQueue() {
        String url = "https://justin-time.herokuapp.com/queue/addUser/" + facility.getId() + "/" + queue.getId();
        url += "?access_token=" + UserController.getInstance().getToken();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("AttemptEnterQueue", "onResponse: " +response);
                Intent intent = new Intent(QueueDetailActivity.this, ReservationsActivity.class);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("AttemptEnterFail", "onErrorResponse: " + error);
                if(error.networkResponse.statusCode == 409) {
                    //todo: ne dati korisniku da pokuša uzeti broj ako je već u redu (spremiti sve redove u kojima je korisnik)
                    AlertDialog.Builder builder = new AlertDialog.Builder(QueueDetailActivity.this);
                    builder.setMessage("Već ste u ovom redu")
                            .setNegativeButton("U redu", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(QueueDetailActivity.this, ReservationsActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .create().show();
                }
            }
        });

        volleyQueue.add(request);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //inače se vrati na popis redova bez intent-a pa bude null objekt
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

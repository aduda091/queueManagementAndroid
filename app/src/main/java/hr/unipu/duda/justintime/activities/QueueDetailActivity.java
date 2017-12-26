package hr.unipu.duda.justintime.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import hr.unipu.duda.justintime.R;
import hr.unipu.duda.justintime.model.Facility;
import hr.unipu.duda.justintime.model.Queue;
import hr.unipu.duda.justintime.util.AppController;

public class QueueDetailActivity extends AppCompatActivity {
    TextView facilityNameTextView, queueNameTextView, queueCurrentNumberTextView, queueCurrentText;
    Button reserveButton;
    RelativeLayout ticketTextContainer;
    ImageView ticketImageView;
    Animation animation;
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
        queueCurrentText = findViewById(R.id.queueCurrentText);
        queueCurrentNumberTextView = (TextView) findViewById(R.id.queueCurrentNumberTextView);
        reserveButton = (Button) findViewById(R.id.reserveButton);
        ticketTextContainer = (RelativeLayout) findViewById(R.id.ticketTextContainer);
        ticketImageView = findViewById(R.id.ticketImageView);

        animation = AnimationUtils.loadAnimation(this, R.anim.zoomin);

        facility = new Facility();
        facility.setName(getIntent().getStringExtra("facilityName"));
        facility.setId(getIntent().getStringExtra("facilityId"));


        queue = new Queue();
        queue.setFacility(facility);
        queue.setId(getIntent().getStringExtra("queueId"));
        queue.setName(getIntent().getStringExtra("queueName"));
        queue.setCurrent(getIntent().getIntExtra("queuePriority", 0));
        queue.setNext(getIntent().getIntExtra("queueNext", 0));

        setTitle(facility.getName() + " - " + queue.getName());

        facilityNameTextView.setText(facility.getName());
        queueNameTextView.setText(queue.getName());

        updateText();

        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptEnterQueue();
                ticketTextContainer.startAnimation(animation);
            }
        });

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ticketTextContainer.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        updateData();
    }

    private void updateText() {
        if(queue.getCurrent() != 0) {
            queueCurrentText.setText("Trenutno se poslužuje: ");
            queueCurrentNumberTextView.setVisibility(View.VISIBLE);
            queueCurrentNumberTextView.setText(String.valueOf(queue.getCurrent()));
        } else {
            queueCurrentText.setText("Čeka se na službenika");
            queueCurrentNumberTextView.setText("");
            queueCurrentNumberTextView.setVisibility(View.GONE);
        }

        reserveButton.setText("Uzmi\nbroj:\n" + queue.getNext());
    }

    private void updateData() {
        String url = AppController.API_URL + "/queues/" +queue.getId();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    queue.setCurrent(response.getInt("current"));
                    queue.setNext(response.getInt("next"));
                    updateText();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("QueueUpdate", "onErrorResponse: " + error.getMessage());
            }
        });

        volleyQueue.add(request);
    }

    private void attemptEnterQueue() {
        String url = AppController.API_URL + "/reservations/" + queue.getId();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("AttemptEnterQueue", "onResponse: " +response);
                AppController.getInstance().downloadReservations();
                //pričekaj par sekundi da se osvježe rezervacije pa preusmjeri korisnika
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(QueueDetailActivity.this, ReservationsActivity.class);
                        startActivity(intent);
                    }
                }, 2000);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("AttemptEnterFail", "onErrorResponse: " + error);
                if(error.networkResponse.statusCode == 409) {
                    //todo: ne dati korisniku da pokuša uzeti broj ako je već u redu (spremiti sve redove u kojima je korisnik)?
                    AlertDialog.Builder builder = new AlertDialog.Builder(QueueDetailActivity.this);
                    builder.setMessage("Već ste u ovom redu")
                            .setNegativeButton("U redu", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(QueueDetailActivity.this, ReservationsActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .create().show();
                }
                if(error.networkResponse.statusCode == 401) {
                    //todo: vratiti korisnika na ovaj red nakon uspješne prijave?
                    AlertDialog.Builder builder = new AlertDialog.Builder(QueueDetailActivity.this);
                    builder.setMessage("Morate se prijaviti prije ulaska u red")
                            .setNegativeButton("U redu", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(QueueDetailActivity.this, LoginActivity.class);
                                    intent.putExtra("attemptedQueue", queue.getId());
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .create().show();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppController.getInstance().getAuthorizationHeader();
            }
        };

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

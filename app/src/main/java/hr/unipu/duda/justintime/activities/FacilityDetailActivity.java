package hr.unipu.duda.justintime.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import hr.unipu.duda.justintime.R;
import hr.unipu.duda.justintime.fragments.NavigationFragment;
import hr.unipu.duda.justintime.model.Facility;

public class FacilityDetailActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    private static final String API_KEY = "AIzaSyAPTblfWC1PKNfwegGdhPTSTDSaX1rbJl8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_detail);
        final Facility facility = new Facility();

        final TextView facilityNameTextView = (TextView) findViewById(R.id.facilityNameTextView);
        final TextView facilityAddressTextView = (TextView) findViewById(R.id.facilityAddressTextView);
        final TextView facilityTelephoneTextView = (TextView) findViewById(R.id.facilityTelephoneTextView);
        final TextView facilityMailTextView = (TextView) findViewById(R.id.facilityMailTextView);
        final WebView webView = (WebView) findViewById(R.id.facilityWebView);

        progressDialog = new ProgressDialog(FacilityDetailActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Dohvaćanje podataka u tijeku...");
        progressDialog.setCancelable(false);
        if(!progressDialog.isShowing()) progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://justin-time.herokuapp.com/facility/" +getIntent().getStringExtra("id");

        //čitanje podataka o ustanovi
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("JSON Response", response.toString());
                try {
                    facility.setId(response.getString("id"));
                    facility.setName(response.getString("name"));
                    facility.setAddress(response.getString("address"));
                    facility.setTelephone(response.getString("telephone"));
                    facility.setMail(response.getString("mail"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(FacilityDetailActivity.this, "Greška prilikom dohvaćanja podataka", Toast.LENGTH_SHORT).show();
                Log.d("JSON Error", "onErrorResponse: " + error.getMessage());
                if(progressDialog.isShowing()) progressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(FacilityDetailActivity.this);
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

        queue.add(request);

        //prikaz dohvaćenih podataka o ustanovi
        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                setTitle(facility.getName() + " - detalji");
                facilityNameTextView.setText(facility.getName());
                facilityAddressTextView.setText("Adresa: " + facility.getAddress());
                facilityTelephoneTextView.setText("Telefon: " + facility.getTelephone());
                facilityMailTextView.setText("Mail: " + facility.getMail());

                //automatsko biranje broja telefona ustanove
                facilityTelephoneTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + facility.getTelephone()));
                        startActivity(intent);
                    }
                });

                //slanje maila ustanovi
                facilityMailTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:"));
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{facility.getMail()});
                        intent.putExtra(Intent.EXTRA_SUBJECT, facility.getName() + " - upit");
                        startActivity(Intent.createChooser(intent, "Pošaljite e-mail ustanovi..."));
                    }
                });



                //karta
                final String urlAdress = facility.getAddress().replace(" ", "+") + ",+52100,+Pula";
                //Google static map api
                webView.loadUrl("https://maps.googleapis.com/maps/api/staticmap?center="+ urlAdress + "&markers="+urlAdress +"&zoom=16&size=400x400&key="+API_KEY);
                if(progressDialog.isShowing()) progressDialog.dismiss();

                //dugi klik na kartu otvara google maps aplikaciju - testirano i na emulatoru
                Toast.makeText(FacilityDetailActivity.this, "Dugi dodir na kartu otvara detaljniji prikaz", Toast.LENGTH_SHORT).show();
                webView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        webView.loadUrl("https://www.google.hr/maps/place/" + urlAdress);
                        return true;
                    }
                });
            }
        });
    }

}

package hr.unipu.duda.justintime.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import hr.unipu.duda.justintime.R;
import hr.unipu.duda.justintime.model.User;
import hr.unipu.duda.justintime.util.AppController;

public class LoginActivity extends AppCompatActivity {
    EditText etUsername;
    EditText etPassword;
    TextInputLayout inputLayoutPassword;

    RequestQueue queue;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        queue = Volley.newRequestQueue(this);

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.inputLayoutPassword);
        final TextView tvRegisterLink = (TextView) findViewById(R.id.tvRegisterLink);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(false);
        progressDialog.setMessage("Prijava u tijeku");
        progressDialog.setCancelable(false);

        if (getIntent().getExtras() != null) {
            //korisnik se upravo registrirao
            etUsername.setText(getIntent().getStringExtra("username"));
            etPassword.setText(getIntent().getStringExtra("password"));

        }

        if (AppController.getInstance().isRemembered()) {
            // TODO: ako je istekao token, popuni polja da se zatraži novi
            //korisnički podaci su već spremljeni
            /*User user = UserController.getInstance().getUser();
            etUsername.setText(user.getMail());
            etPassword.setText(user.getPassword());*/

            //preusmjeri korisnika s login ekrana ako je već prijavljen
            Intent intent = new Intent(LoginActivity.this, FacilityListActivity.class);
            startActivity(intent);
            finish();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validateFields()) return;
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();
                if (!progressDialog.isShowing()) progressDialog.show();

                String url = AppController.API_URL + "/users/login";
                Map<String, String> loginData = new HashMap<>();
                loginData.put("mail", username);
                loginData.put("password", password);
                JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(loginData), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "onResponse: " + response.toString());
                        getUserData(response);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        try {
                            Log.d("onError", "Error: " + error
                                    + "\nStatus Code " + error.networkResponse.statusCode
                                    + "\nResponse Data " + error.networkResponse.data.toString()
                                    + "\nCause " + error.getCause()
                                    + "\nmessage" + error.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (progressDialog.isShowing()) progressDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        String message = "";
                        if(error.networkResponse.statusCode == 404) {
                            message = "Nepostojeći korisnik";
                        }
                        if(error.networkResponse.statusCode == 403) {
                            message = "Pogrešna lozinka";
                        }
                        builder.setMessage(message + ", pokušajte ponovno")
                                .setNegativeButton("U redu", null)
                                .create()
                                .show();
                    }
                });
                loginRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(loginRequest);

            }
        });

        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


    }

    private boolean validateFields() {
        boolean hasErrors = false;

        if (etUsername.getText().toString().isEmpty()) {
            etUsername.requestFocus();
            etUsername.setError("E-mail ne smije biti prazan");
            hasErrors = true;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etUsername.getText().toString()).matches()) {
            etUsername.requestFocus();
            etUsername.setError("E-mail mora biti valjan");
            hasErrors = true;
        }
        if (etPassword.getText().toString().trim().isEmpty()) {
            etPassword.requestFocus();
            //etPassword.setError("Lozinka ne smije biti prazna");
            inputLayoutPassword.setError("Lozinka ne smije biti prazna");
            hasErrors = true;
        }

        return hasErrors;
    }

    private void getUserData(final JSONObject response) {

        User user = new User();
        try {
            JSONObject userObject = response.getJSONObject("user");
            user.setId(userObject.getString("id"));
            user.setFirstName(userObject.getString("firstName"));
            user.setLastName(userObject.getString("lastName"));
            user.setMail(userObject.getString("mail"));
            user.setToken(response.getString("token"));

            //spremi učitane podatke u localStorage
            AppController.getInstance().saveUser(user);

            final Intent intent;
            //provjeri koliko korisnik ima rezervacija
            if(response.getJSONArray("reservations").length() > 0) {
                //ima barem jednu, preusmjeri ga na popis rezervacija
                intent = new Intent(LoginActivity.this, ReservationsActivity.class);
            } else {
                //nema rezervacija, preusmjeri ga na popis ustanova
                intent = new Intent(LoginActivity.this, FacilityListActivity.class);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("Hvala na prijavi, " + user.getFullName() + "!")
                    .setPositiveButton("Nema na čemu", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(intent);
                            finish();
                        }
                    })
                    .create()
                    .show();
            if (progressDialog.isShowing()) progressDialog.dismiss();

        } catch (JSONException e) {
            e.printStackTrace();
            if (progressDialog.isShowing()) progressDialog.dismiss();
        }


    }


//    @Override
//    public void onBackPressed() {
//        new AlertDialog.Builder(this)
//                .setTitle("Izlaz")
//                .setMessage("Želite li izaći iz aplikacije?")
//                .setNegativeButton("Ne", null)
//                .setPositiveButton("Da", new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface arg0, int arg1) {
//                        /*moveTaskToBack(true);
//                        android.os.Process.killProcess(android.os.Process.myPid());
//                        finishAndRemoveTask();
//                        System.exit(1);*/
//                        finish();
//                    }
//                }).create().show();
//
//    }
}

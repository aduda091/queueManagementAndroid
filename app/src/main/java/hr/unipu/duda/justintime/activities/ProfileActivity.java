package hr.unipu.duda.justintime.activities;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import hr.unipu.duda.justintime.R;
import hr.unipu.duda.justintime.model.User;
import hr.unipu.duda.justintime.util.AppController;

public class ProfileActivity extends AppCompatActivity {

    EditText etName;
    EditText etLastName;
    EditText etEmail;
    Button btnSave;
    ProgressBar progressBar;

    SwitchCompat switchPush, switchBeep;

    User user;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = AppController.getInstance().getUser();
        setTitle(user.getFirstName() + " " + user.getLastName() + " - postavke profila");

        etName = (EditText) findViewById(R.id.etName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        btnSave = (Button) findViewById(R.id.btnSave);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        switchPush = (SwitchCompat) findViewById(R.id.switchPushSetting);
        switchBeep = (SwitchCompat) findViewById(R.id.switchSoundSetting);

        queue = Volley.newRequestQueue(this);

        etName.setText(user.getFirstName());
        etLastName.setText(user.getLastName());
        etEmail.setText(user.getMail());

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()) return;
                saveChanges();
            }
        });

        switchBeep.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                AppController.getInstance().updateBeepPref(b);
            }
        });

        switchPush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                AppController.getInstance().updatePushPref(b);
            }
        });


    }

    public void logout(MenuItem item) {
        AppController.getInstance().logout();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        switchBeep.setChecked(AppController.getInstance().getBeepPref());
        switchPush.setChecked(AppController.getInstance().getPushPref());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    private boolean validateFields() {

        boolean hasErrors = false;

        if (etName.getText().toString().trim().isEmpty()) {
            etName.requestFocus();
            etName.setError("Ime ne smije biti prazno");
            hasErrors = true;
        }
        if (etLastName.getText().toString().trim().isEmpty()) {
            etLastName.requestFocus();
            etLastName.setError("Prezime ne smije biti prazno");
            hasErrors = true;
        }
        if (etEmail.getText().toString().trim().isEmpty()) {
            etEmail.requestFocus();
            etEmail.setError("E-mail ne smije biti prazan");
            hasErrors = true;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
            etEmail.requestFocus();
            etEmail.setError("E-mail mora biti valjan");
            hasErrors = true;
        }
        return hasErrors;
    }

    void saveChanges() {
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        String name = etName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        final Map<String, String> params = new HashMap<>();
        params.put("firstName", name);
        params.put("lastName", lastName);
        params.put("mail", email);
//        params.put("token", user.getToken());

        Log.d("PARAMS", "saveChanges: " + params.toString());

        String url = AppController.API_URL + "/users/me";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d("ProfileResponse", "onResponse: " + response.toString());
                saveUser(response);

                progressBar.setVisibility(View.INVISIBLE);
                btnSave.setEnabled(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ProfileError", "onResponse: " + error.toString());
                progressBar.setVisibility(View.INVISIBLE);
                btnSave.setEnabled(true);
                String message = "";
                if(error.networkResponse.statusCode == 409) {
                    message = "Odabrani mail je zauzet.";
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setMessage("Došlo je do pogreške prilikom spremanja podataka! " + message)
                        .setPositiveButton("U redu", null)
                        .create().show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppController.getInstance().getAuthorizationHeader();
            }
        };

        queue.add(request);
    }

    private void saveUser(JSONObject userJsonObject) {
        User user = new User();
        try {
            user.setFirstName(userJsonObject.getString("firstName"));
            user.setLastName(userJsonObject.getString("lastName"));
            user.setMail(userJsonObject.getString("mail"));

            //spremi učitane podatke u localStorage
            AppController.getInstance().updateUser(user);

            //osvježi polja za unos i naslov
            setTitle(user.getFirstName() + " " + user.getLastName() + " - postavke profila");
            etName.setText(user.getFirstName());
            etLastName.setText(user.getLastName());
            etEmail.setText(user.getMail());

            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
            builder.setMessage("Podaci uspješno spremljeni!")
                    .setPositiveButton("U redu", null)
                    .create().show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

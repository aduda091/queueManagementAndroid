package hr.unipu.duda.justintime.activities;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import hr.unipu.duda.justintime.util.UserController;

public class ProfileActivity extends AppCompatActivity {

    EditText etName;
    EditText etLastName;
    EditText etEmail;
    Button btnSave;
    Button btnLogout;
    ProgressBar progressBar;

    User user;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = UserController.getInstance().getUser();
        setTitle(user.getFirstName() + " " + user.getLastName() + " - postavke profila");

        etName = (EditText) findViewById(R.id.etName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

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

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserController.getInstance().logout();
                finishAndRemoveTask();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateFields() {

        boolean hasErrors = false;

        if(etName.getText().toString().trim().isEmpty()) {
            etName.requestFocus();
            etName.setError("Ime ne smije biti prazno");
            hasErrors = true;
        }
        if(etLastName.getText().toString().trim().isEmpty()) {
            etLastName.requestFocus();
            etLastName.setError("Prezime ne smije biti prazno");
            hasErrors = true;
        }
        if(etEmail.getText().toString().trim().isEmpty()) {
            etEmail.requestFocus();
            etEmail.setError("E-mail ne smije biti prazan");
            hasErrors = true;
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
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
        //params.put("id", user.getId());
        params.put("firstName", name);
        params.put("lastName", lastName);
        params.put("mail", email);
        params.put("access_token", user.getToken());

        Log.d("PARAMS", "saveChanges: " + params.toString());

        String url = "https://justin-time.herokuapp.com/user/update/" + user.getId();
        //Volley i dalje ima problema sa slanjem parametara u JsonObjectRequestu
        StringRequest request = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject userJsonObject = new JSONObject(response);
                    Log.d("ProfileResponse", "onResponse: "+userJsonObject.toString());
                    saveUser(userJsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ProfileError", "onResponse: "+error.toString());
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setMessage("Došlo je do pogreške prilikom spremanja podataka!" + error.toString()) //// TODO: ukloniti detaljan ispis greške korisniku
                        .setPositiveButton("U redu", null)
                        .create().show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
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
            UserController.getInstance().updateUser(user);

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

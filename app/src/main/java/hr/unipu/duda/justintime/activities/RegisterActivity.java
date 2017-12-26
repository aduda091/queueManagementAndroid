package hr.unipu.duda.justintime.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import hr.unipu.duda.justintime.R;
import hr.unipu.duda.justintime.fragments.NavigationFragment;
import hr.unipu.duda.justintime.util.AppController;

public class RegisterActivity extends AppCompatActivity {
    EditText etName;
    EditText etLastName;
    EditText etEmail;
    EditText etPassword;
    EditText etPassword2;
    TextInputLayout inputLayoutPassword;
    TextInputLayout inputLayoutPassword2;

    Button btnRegister;
    RequestQueue queue;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //setTitle("Registracija");
        getSupportActionBar().hide();

        queue = Volley.newRequestQueue(this);

        etName = (EditText) findViewById(R.id.etName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etPassword2 = (EditText) findViewById(R.id.etPassword2);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.inputLayoutPassword);
        inputLayoutPassword2 = (TextInputLayout) findViewById(R.id.inputLayoutPassword2);

        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Registracija u tijeku");
        progressDialog.setCancelable(false);

        TextView tvLoginLink = (TextView) findViewById(R.id.tvLoginLink);
        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validateFields()) return;

                final String name = etName.getText().toString().trim();
                final String lastName = etLastName.getText().toString().trim();
                final String email = etEmail.getText().toString().trim();
                final String password = etPassword.getText().toString().trim();
                final String password2 = etPassword2.getText().toString().trim();

                String url = AppController.API_URL + "/users/register";
                final HashMap<String, String> params = new HashMap<String, String>();
                params.put("firstName", name);
                params.put("lastName", lastName);
                params.put("mail", email);
                params.put("password", password);

                if(!progressDialog.isShowing()) progressDialog.show();

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(progressDialog.isShowing()) progressDialog.dismiss();
                        Log.d("success", "onResponse: " + response);
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        builder.setMessage("Hvala na registraciji, " + name + " " + lastName + "!")
                                .setNegativeButton("Nema na čemu", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        intent.putExtra("username", email);
                                        intent.putExtra("password", password);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .create()
                                .show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("error", "onErrorResponse: " + error.networkResponse.statusCode);
                        if(progressDialog.isShowing()) progressDialog.dismiss();
                        String message = "";
                        if(error.networkResponse.statusCode == 409) {
                            message = "odabrani mail je zauzet.";
                        }
                        if(error.networkResponse.statusCode == 404) {
                            message = "pokušajte ponovno.";
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        builder.setMessage("Neuspješna registracija, " +message)
                                .setNegativeButton("U redu", null)
                                .create()
                                .show();
                    }
                });

                queue.add(request);
            }
        });


    }

    private boolean validateFields() {

        boolean hasErrors = false;

        if(etName.getText().toString().isEmpty()) {
            etName.requestFocus();
            etName.setError("Ime ne smije biti prazno");
            hasErrors = true;
        }
        if(etLastName.getText().toString().isEmpty()) {
            etLastName.requestFocus();
            etLastName.setError("Prezime ne smije biti prazno");
            hasErrors = true;
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
            etEmail.requestFocus();
            etEmail.setError("E-mail mora biti valjan");
            hasErrors = true;
        }
        if(etPassword.getText().toString().trim().isEmpty()) {
            etPassword.requestFocus();
            //etPassword.setError("Lozinka ne smije biti prazna");
            inputLayoutPassword.setError("Lozinka ne smije biti prazna");
            hasErrors = true;
        }
        if(etPassword2.getText().toString().trim().isEmpty()) {
            etPassword2.requestFocus();
            inputLayoutPassword2.setError("Lozinka ne smije biti prazna");
            hasErrors = true;
        }
        if(!etPassword.getText().toString().trim().equalsIgnoreCase(etPassword2.getText().toString().trim())) {
            etPassword.requestFocus();
            inputLayoutPassword.setError("Lozinke se moraju podudarati");
            inputLayoutPassword2.setError("Lozinke se moraju podudarati");
            hasErrors = true;
        }

        return hasErrors;
    }
}

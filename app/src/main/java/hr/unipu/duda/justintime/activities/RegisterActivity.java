package hr.unipu.duda.justintime.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import hr.unipu.duda.justintime.R;
import hr.unipu.duda.justintime.fragments.NavigationFragment;

public class RegisterActivity extends AppCompatActivity {
    EditText etName;
    EditText etLastName;
    EditText etEmail;
    EditText etPassword;
    EditText etPassword2;
    Button btnRegister;
    RequestQueue queue;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Registracija");

        queue = Volley.newRequestQueue(this);

        etName = (EditText) findViewById(R.id.etName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etPassword2 = (EditText) findViewById(R.id.etPassword2);

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
            }
        });

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = etName.getText().toString();
                final String lastName = etLastName.getText().toString();
                final String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();
                final String password2 = etPassword2.getText().toString();

                if(!password.equals(password2)) {
                    //lozinke se ne poklapaju, izlaz
                    if(progressDialog.isShowing()) progressDialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("Lozinke se moraju poklapati")
                            .setNegativeButton("U redu", null)
                            .create()
                            .show();
                    return;
                }

                if(name.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || password2.isEmpty()) {
                    //nisu popunjena sva polja
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("Morate popuniti sva polja")
                            .setNegativeButton("U redu", null)
                            .create()
                            .show();
                    return;
                }

                String url = "https://justin-time.herokuapp.com/user/create";
                final HashMap<String, String> params = new HashMap<String, String>();
                params.put("firstName", name);
                params.put("lastName", lastName);
                params.put("mail", email);
                params.put("password", password);


                if(!progressDialog.isShowing()) progressDialog.show();

                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        builder.setMessage("Neuspješna registracija, pokušajte ponovno")
                                .setNegativeButton("U redu", null)
                                .create()
                                .show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return params;
                    }
                };

                queue.add(request);
            }
        });


    }
}

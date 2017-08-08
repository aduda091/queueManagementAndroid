package hr.unipu.duda.justintime.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import hr.unipu.duda.justintime.R;
import hr.unipu.duda.justintime.model.User;
import hr.unipu.duda.justintime.util.UserController;

public class ProfileActivity extends AppCompatActivity {

    EditText etName;
    EditText etLastName;
    EditText etEmail;
    Button btnSave;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        User user = UserController.getInstance().getUser();
        setTitle(user.getFirstName() + " " + user.getLastName() + " - postavke profila");

        etName = (EditText) findViewById(R.id.etName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnLogout = (Button) findViewById(R.id.btnLogout);

        etName.setText(user.getFirstName());
        etLastName.setText(user.getLastName());
        etEmail.setText(user.getMail());


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserController.getInstance().logout();
                finishAndRemoveTask();//todo: poboljšati brisanje svih zaostalih activity-ja u history-ju zbog otežanog izlaska s back buttonom
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}

package hr.unipu.duda.justintime.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import hr.unipu.duda.justintime.R;
import hr.unipu.duda.justintime.model.Facility;
import hr.unipu.duda.justintime.model.Queue;

public class QueueDetailActivity extends AppCompatActivity {
    TextView facilityNameTextView, queueNameTextView, priorityTextView, myPriorityTextView;
    Button reserveButton;
    Animation animation;
    int nextNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_detail);
        facilityNameTextView = (TextView) findViewById(R.id.facilityNameTextView);
        queueNameTextView = (TextView) findViewById(R.id.queueNameTextView);
        priorityTextView = (TextView) findViewById(R.id.priorityTextView);
        myPriorityTextView = (TextView) findViewById(R.id.myPriorityTextView);
        reserveButton = (Button) findViewById(R.id.reserveButton);

        animation = AnimationUtils.loadAnimation(this, R.anim.zoomin);

        Facility facility = new Facility();
        facility.setName(getIntent().getStringExtra("facilityName"));
        facility.setId(getIntent().getStringExtra("facilityid"));

        Queue queue = new Queue();
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
                myPriorityTextView.setText("Vaš broj je " + nextNumber);
                myPriorityTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
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

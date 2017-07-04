package hr.unipu.duda.justintime;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import hr.unipu.duda.justintime.fragments.NavigationFragment;

public class QueueListActivity extends AppCompatActivity implements NavigationFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_list);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

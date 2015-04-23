package se.sensorship;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class RunningActivity extends Activity {

    private Intent locationServiceIntent;
    private int distance, duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);
        distance = getIntent().getIntExtra("distance", -1);
        duration = getIntent().getIntExtra("duration", -1);
        startLocationService();
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
    }


    private void startLocationService() {
        locationServiceIntent = new Intent(this, LocationService.class);
        Bundle extras = new Bundle();
        extras.putInt("distance", distance);
        extras.putInt("duration", duration);
        locationServiceIntent.putExtras(extras);
        startService(locationServiceIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_running, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void startMap(View v) {
        Intent intent = new Intent(this, LocationActivity.class);
        startActivity(intent);
    }

    public void killService(View v) {
        stopService(locationServiceIntent);
    }
}

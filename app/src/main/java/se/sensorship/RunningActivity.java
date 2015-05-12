package se.sensorship;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class RunningActivity extends Activity {

    private Intent locationServiceIntent;
    private int distance, duration;
    private Boolean audio, vibration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_vibration_help,
                (ViewGroup) findViewById(R.id.toast_layout_root));
        Intent intent = getIntent();
        distance = intent.getIntExtra("distance", -1);
        duration = intent.getIntExtra("duration", -1);
        vibration = intent.getBooleanExtra("vibration", true);
        audio = intent.getBooleanExtra("audio", true);
        startLocationService();
        getActionBar().hide();
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }


    private void startLocationService() {
        locationServiceIntent = new Intent(this, LocationService.class);
        Bundle extras = new Bundle();
        extras.putInt("distance", distance);
        extras.putInt("duration", duration);
        extras.putBoolean("audio", audio);
        extras.putBoolean("vibration", vibration);
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


    public void startMap(View v) {
        Intent intent = new Intent(this, LocationActivity.class);
        startActivity(intent);
    }

    public void killService(View v) {
        stopService(locationServiceIntent);
    }
}

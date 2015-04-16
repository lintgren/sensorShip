package se.sensorship;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends Activity {

    //TODO remove this before release
    static Intent locationServiceIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startSoundVibration(View v) {
        Intent intent = new Intent(this, SoundVibration.class);
        startActivity(intent);
    }

    public void startLocation(View v) {
        Intent intent = new Intent(this, LocationActivity.class);
        startActivity(intent);
    }


    public void startLocationService(View view) {
        locationServiceIntent = new Intent(this, LocationService.class);
        startService(locationServiceIntent);
    }

    public void stopLocatinService(View v) {
        if (locationServiceIntent == null) {
            return;
        }
        stopService(locationServiceIntent);
    }

    public void startFragment(View v) {
        Intent intent = new Intent(this, FragmentHolder.class);
        startActivity(intent);
    }
}

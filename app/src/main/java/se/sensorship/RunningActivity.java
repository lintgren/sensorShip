package se.sensorship;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


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
    }

    private void startLocationService() {
        locationServiceIntent = new Intent(this, LocationService.class);
        Bundle extras = new Bundle();
        extras.putSerializable("route",randomizeRoute());
        extras.putInt("distance",distance);
        extras.putInt("duration",duration);
        locationServiceIntent.putExtras(extras);
        startService(locationServiceIntent);
    }

    private Route randomizeRoute(){
        return new Route(new Direction[]{new Direction(55.713580, 13.211145, Direction.LEFT),
                new Direction(55.713892, 13.209557, Direction.RIGHT), new Direction(55.714694,
                13.210319, Direction.GOAL)},null);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        stopService(locationServiceIntent);
    }
}

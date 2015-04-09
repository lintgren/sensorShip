package se.sensorship;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;


public class MainActivityFuture extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_future);
        NumberPicker distancePicker = (NumberPicker) findViewById(R.id.distance_picker);
        distancePicker.setMaxValue(100);
        distancePicker.setMinValue(0);

        NumberPicker minutesPicker = (NumberPicker) findViewById(R.id.minutes_picker);
        minutesPicker.setMaxValue(59);
        minutesPicker.setMinValue(0);

        NumberPicker hoursPicker = (NumberPicker) findViewById(R.id.hours_picker);
        hoursPicker.setMaxValue(23);
        hoursPicker.setMinValue(0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity_future, menu);
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

    public void distanceButtonClicked(View v) {
        Button distanceBtn = (Button) findViewById(R.id.distance_button);
        distanceBtn.setBackgroundColor(getResources().getColor(R.color.button_material_dark));

        Button durationBtn = (Button) findViewById(R.id.duration_button);
        durationBtn.setBackgroundColor(getResources().getColor(R.color.button_material_light));


        findViewById(R.id.duration_picker_layout).setVisibility(View.INVISIBLE);
        findViewById(R.id.distance_picker_layout).setVisibility(View.VISIBLE);
    }

    public void durationButtonClicked(View v) {
        Button distanceBtn = (Button) findViewById(R.id.duration_button);
        distanceBtn.setBackgroundColor(getResources().getColor(R.color.button_material_dark));

        Button durationBtn = (Button) findViewById(R.id.distance_button);
        durationBtn.setBackgroundColor(getResources().getColor(R.color.button_material_light));


        findViewById(R.id.duration_picker_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.distance_picker_layout).setVisibility(View.INVISIBLE);
    }
}

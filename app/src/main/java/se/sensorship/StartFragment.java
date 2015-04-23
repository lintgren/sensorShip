package se.sensorship;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;


public class StartFragment extends Fragment implements View.OnClickListener {
    private Button rundomizerButton;
    private ToggleButton distanceButton, durationButton;
    private RelativeLayout durationPickerLayout, distancePickerLayout;
    private NumberPicker minutesPicker, hoursPicker, distancePicker;
    private Boolean audio, vibration;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_start, container, false);
        distancePicker = (NumberPicker) v.findViewById(R.id.distance_picker);
        distancePicker.setMaxValue(100);
        distancePicker.setMinValue(0);

        minutesPicker = (NumberPicker) v.findViewById(R.id.minutes_picker);
        minutesPicker.setMaxValue(59);
        minutesPicker.setMinValue(0);

        hoursPicker = (NumberPicker) v.findViewById(R.id.hours_picker);
        hoursPicker.setMaxValue(23);
        hoursPicker.setMinValue(0);

        distanceButton = (ToggleButton) v.findViewById(R.id.distance_button);
        durationButton = (ToggleButton) v.findViewById(R.id.duration_button);
        rundomizerButton = (Button) v.findViewById(R.id.rundomize_button);

        distanceButton.setBackgroundColor(getResources().getColor(R.color.button_material_dark));
        durationButton.setBackgroundColor(getResources().getColor(R.color.button_material_light));
        rundomizerButton.setOnClickListener(this);
        distanceButton.setOnClickListener(this);
        durationButton.setOnClickListener(this);

        durationPickerLayout = (RelativeLayout) v.findViewById(R.id.duration_picker_layout);
        distancePickerLayout = (RelativeLayout) v.findViewById(R.id.distance_picker_layout);

        distanceButtonClicked();
        return v;
    }

    private void distanceButtonClicked() {
        distanceButton.setBackgroundColor(getResources().getColor(R.color.button_material_dark));
        durationButton.setBackgroundColor(getResources().getColor(R.color.button_material_light));
        durationButton.setChecked(false);
        distanceButton.setChecked(true);
        durationPickerLayout.setVisibility(View.INVISIBLE);
        distancePickerLayout.setVisibility(View.VISIBLE);
    }

    public void durationButtonClicked() {
        durationButton.setBackgroundColor(getResources().getColor(R.color.button_material_dark));
        distanceButton.setBackgroundColor(getResources().getColor(R.color.button_material_light));
        distanceButton.setChecked(false);
        durationButton.setChecked(true);

        durationPickerLayout.setVisibility(View.VISIBLE);
        distancePickerLayout.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.distance_button):
                distanceButtonClicked();
                break;
            case (R.id.duration_button):
                durationButtonClicked();
                break;
            case (R.id.rundomize_button):
                Intent intent = new Intent(getActivity(), RunningActivity.class);
                if (durationButton.isChecked()) {
                    int duration = getTime();
                    intent.putExtra("duration", duration);
                }
                if (distanceButton.isChecked()) {
                    int distance = getDistance();
                    intent.putExtra("distance", distance);
                }
                startActivity(intent);
                break;
        }
    }

    private int getTime() {
        return minutesPicker.getValue() + hoursPicker.getValue() * 60;
    }

    private int getDistance() {
        return distancePicker.getValue();
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {
            case R.id.checkbox_audio:
                if (checked)
                    audio = true;
                else
                    audio = false;
                break;
            case R.id.checkbox_vibration:
                if (checked)
                    vibration = true;
                else
                    vibration = false;
                break;
        }
    }
}

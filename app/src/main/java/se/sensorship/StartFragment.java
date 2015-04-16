package se.sensorship;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;


public class StartFragment extends Fragment implements View.OnClickListener {
    private Button distanceButton;
    private Button durationButton;
    private RelativeLayout durationPickerLayout;
    private RelativeLayout distancePickerLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_start, container, false);
        NumberPicker distancePicker = (NumberPicker) v.findViewById(R.id.distance_picker);
        distancePicker.setMaxValue(100);
        distancePicker.setMinValue(0);

        NumberPicker minutesPicker = (NumberPicker) v.findViewById(R.id.minutes_picker);
        minutesPicker.setMaxValue(59);
        minutesPicker.setMinValue(0);

        NumberPicker hoursPicker = (NumberPicker) v.findViewById(R.id.hours_picker);
        hoursPicker.setMaxValue(23);
        hoursPicker.setMinValue(0);

        distanceButton = (Button) v.findViewById(R.id.distance_button);
        durationButton = (Button) v.findViewById(R.id.duration_button);
        distanceButton.setBackgroundColor(getResources().getColor(R.color.button_material_dark));
        durationButton.setBackgroundColor(getResources().getColor(R.color.button_material_light));
        distanceButton.setOnClickListener(this);
        durationButton.setOnClickListener(this);

        durationPickerLayout = (RelativeLayout) v.findViewById(R.id.duration_picker_layout);
        distancePickerLayout = (RelativeLayout) v.findViewById(R.id.distance_picker_layout);

        return v;
    }

    private void distanceButtonClicked() {
        distanceButton.setBackgroundColor(getResources().getColor(R.color.button_material_dark));
        durationButton.setBackgroundColor(getResources().getColor(R.color.button_material_light));

        durationPickerLayout.setVisibility(View.INVISIBLE);
        distancePickerLayout.setVisibility(View.VISIBLE);
    }

    public void durationButtonClicked() {
        durationButton.setBackgroundColor(getResources().getColor(R.color.button_material_dark));
        distanceButton.setBackgroundColor(getResources().getColor(R.color.button_material_light));

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
        }
    }
}

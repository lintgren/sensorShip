package se.sensorship.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.ToggleButton;

import java.lang.reflect.Field;

import se.sensorship.R;


public class StartFragment extends Fragment implements View.OnClickListener {
    private Button rundomizerButton;
    private RelativeLayout durationPickerLayout, distancePickerLayout;
    private NumberPicker minutesPicker, hoursPicker, distancePicker;
    private Switch distanceDurationSwitch;
    private Boolean audio = true;
    private Boolean vibration = true;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_start, container, false);
        distancePicker = (NumberPicker) v.findViewById(R.id.distance_picker);
        distancePicker.setMaxValue(100);
        distancePicker.setMinValue(0);
        setNumberPickerTextColor(distancePicker, Color.parseColor("#FFFFFF"));

        minutesPicker = (NumberPicker) v.findViewById(R.id.minutes_picker);
        minutesPicker.setMaxValue(59);
        minutesPicker.setMinValue(0);

        hoursPicker = (NumberPicker) v.findViewById(R.id.hours_picker);
        hoursPicker.setMaxValue(23);
        hoursPicker.setMinValue(0);

        setNumberPickerTextColor(distancePicker, Color.parseColor("#FFFFFF"));
        setNumberPickerTextColor(hoursPicker, Color.parseColor("#FFFFFF"));
        setNumberPickerTextColor(minutesPicker, Color.parseColor("#FFFFFF"));


        rundomizerButton = (Button) v.findViewById(R.id.rundomize_button);


        rundomizerButton.setOnClickListener(this);


        durationPickerLayout = (RelativeLayout) v.findViewById(R.id.duration_picker_layout);
        distancePickerLayout = (RelativeLayout) v.findViewById(R.id.distance_picker_layout);

        distanceDurationSwitch = (Switch) v.findViewById(R.id.distance_duration_switch);
        distanceDurationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    distancePickerLayout.setVisibility(View.INVISIBLE);
                    durationPickerLayout.setVisibility(View.VISIBLE);
                } else {
                    durationPickerLayout.setVisibility(View.INVISIBLE);
                    distancePickerLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        return v;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.rundomize_button):
                Intent intent = new Intent(getActivity(), LocationActivity.class);
                if (distanceDurationSwitch.isChecked()) {
                    int distance = getDistance();
                    intent.putExtra("distance", distance);
                } else {
                    int duration = getTime();
                    intent.putExtra("duration", duration);
                }
                intent.putExtra("vibration", vibration);
                intent.putExtra("audio", audio);
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

    //Från Stackoverflow http://stackoverflow.com/questions/22962075/change-the-text-color-of-numberpicker
    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText) child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                } catch (NoSuchFieldException e) {
                    Log.w("PickerTextColor", e);
                } catch (IllegalAccessException e) {
                    Log.w("PickerTextColor", e);
                } catch (IllegalArgumentException e) {
                    Log.w("PickerTextColor", e);
                }
            }
        }
        return false;
    }

}

package se.sensorship.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import se.sensorship.R;


public class HistoryFragment extends android.support.v4.app.Fragment implements OnItemSelectedListener {
    public static final String ARG_OBJECT = "object";

    Spinner spinner;
    ImageView map;
    String[] lap;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.fragment_history, container, false);
        spinner = (Spinner) rootView.findViewById(R.id.prev_runs);
        lap = getResources().getStringArray(R.array.prev_runs);
        map = (ImageView) rootView.findViewById(R.id.ivMap);
        spinner.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.prev_runs, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        return rootView;
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        if (parent.getItemAtPosition(pos).equals(lap[1])) {
            map.setImageResource(R.drawable.karta1);
        } else {
            map.setImageResource(R.drawable.karta);
        }
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public HistoryFragment() {
        // Required empty public constructor
    }
}

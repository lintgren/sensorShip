package se.sensorship;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Andy on 15-04-14.
 */
public class FragmentHolder extends FragmentActivity {


    private DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_framgent_holder);

        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        mDemoCollectionPagerAdapter =
                new DemoCollectionPagerAdapter(
                        getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        getActionBar().setSelectedNavigationItem(position);
                    }
                });
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        // Specify that tabs should be displayed in the action bar.

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                mViewPager.setCurrentItem(tab.getPosition());
                // show the given tab
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event
            }
        };


        // Add 2 tabs, specifying the tab's text and TabListener
        actionBar.addTab(
                actionBar.newTab()
                        .setText("History")
                        .setTabListener(tabListener));
        actionBar.addTab(
                actionBar.newTab()
                        .setText("Start")
                        .setTabListener(tabListener));
        mViewPager.setCurrentItem(1);
    }

    // Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
    public class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {
        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if (i == 0) {
                Fragment fragment = new DemoObjectFragment();
                Bundle args = new Bundle();
                // Our object is just an integer :-P
                args.putInt(DemoObjectFragment.ARG_OBJECT, i + 1);
                fragment.setArguments(args);
                return fragment;
            } else {
                Fragment fragment = new StartFragment();
                Bundle args = new Bundle();
                // Our object is just an integer :-P
                args.putInt(DemoObjectFragment.ARG_OBJECT, i + 1);
                fragment.setArguments(args);
                return fragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }

    // Instances of this class are fragments representing a single
// object in our collection.
    public static class DemoObjectFragment extends Fragment implements AdapterView.OnItemSelectedListener {
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
            if (parent.getItemAtPosition(pos).equals(lap[1])){
                map.setImageResource(R.drawable.karta1);
            }else{
                map.setImageResource(R.drawable.karta);
            }
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }

    }
}
package se.sensorship;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by Andy on 15-04-14.
 */

/**
 * Fragmenten, XML och denna klass bygger på googles exempelkod.
 * https://developer.android.com/training/implementing-navigation/lateral.html
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
                Fragment fragment = new MapFragment();
                Bundle args = new Bundle();
                // Our object is just an integer :-P
                args.putInt(MapFragment.ARG_OBJECT, i + 1);
                fragment.setArguments(args);
                return fragment;
            } else {
                Fragment fragment = new StartFragment();
                Bundle args = new Bundle();
                // Our object is just an integer :-P
                args.putInt(MapFragment.ARG_OBJECT, i + 1);
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

    public void onCheckboxClicked(View view) {
        FragmentManager fm = getSupportFragmentManager();
        StartFragment fragment = (StartFragment) fm.findFragmentById(0);
        fragment.onCheckboxClicked(view);
    }

    //   @Override
    protected void OnFragmentInteractionListener() {

    }
}
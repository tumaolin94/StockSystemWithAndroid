package com.example.tumao.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class StockActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        Intent intent =getIntent();
        //getXxxExtra方法获取Intent传递过来的数据
        String symbol=intent.getStringExtra("data");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(symbol);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
//        Bundle bundle = new Bundle();
//        bundle.putString("data",symbol);
//        CurrentView cv = new CurrentView();
//        HistoricalView hv = new HistoricalView();
//        NewsView nv = new NewsView();
//        cv.setArguments(bundle);
//        hv.setArguments(bundle);
//        nv.setArguments(bundle);
        FragmentManager fm = getSupportFragmentManager();
//        FragmentTransaction transaction  = fm.beginTransaction();
//        transaction.add(R.id.container, cv);
//        transaction.add(R.id.container, hv);
//        transaction.add(R.id.container, nv);
//        transaction.commit();
        mSectionsPagerAdapter = new SectionsPagerAdapter(fm);
        mSectionsPagerAdapter.setSymbol(symbol);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stock, menu);
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



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        String symbol = "";
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
//                    CurrentView tab1 = new CurrentView();
                    return CurrentView.newInstance(symbol);
                case 1:
//                    HistoricalView tab2  = new HistoricalView();
                    return HistoricalView.newInstance(symbol);
                case 2:
//                    NewsView tab3 = new NewsView();
                    return NewsView.newInstance(symbol);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "CURRENT";
                case 1:
                    return "HISTORICAL";
                case 2:
                    return "NEWS";
            }
            return null;
        }

        public String getSymbol(){
            return this.symbol;
        }
        public void setSymbol(String symbol){
            this.symbol = symbol;
        }
    }
}

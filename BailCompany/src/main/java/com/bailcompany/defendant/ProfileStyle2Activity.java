package com.bailcompany.defendant;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bailcompany.R;
import com.bailcompany.tools.CustomViewPager;

import java.util.ArrayList;
import java.util.List;

public class ProfileStyle2Activity extends AppCompatActivity {
    private static final String[] pageNum = {"359", "10,289", "4,317"};
    private static final String[] pageTitle = {"Photos", "Followers", "Pollowings"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile2_layout);

        Toast.makeText(getApplicationContext(),"Test",Toast.LENGTH_LONG).show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout_profile1);
        CustomViewPager viewPager = (CustomViewPager) findViewById(R.id.viewpager_profile1);
        ProfileStyle1Adapter adapter = new ProfileStyle1Adapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(getTitleTabView(pageNum[i], pageTitle[i]));
        }
    }

    private View getTitleTabView(String numString, String titleString) {
        View v = LayoutInflater.from(this).inflate(R.layout.profile1_tab_title, null);
        TextView num = (TextView) v.findViewById(R.id.titleTabCount);
        TextView title = (TextView) v.findViewById(R.id.titleTabText);
        num.setText(numString);
        title.setText(titleString);
        return v;
    }

    public class ProfileStyle1Adapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public ProfileStyle1Adapter(FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            fragments.add(new ProfileStyle2Fragment());
            fragments.add(new ProfileStyle2Fragment());
            fragments.add(new ProfileStyle2Fragment());
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int arrayPos) {
            return pageTitle[arrayPos];
        }
    }


}

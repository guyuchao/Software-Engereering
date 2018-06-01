package com.example.guyuchao.myapplication;

/**
 * Created by guyuchao on 18-4-28.
 */

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;


import com.baidu.mapapi.map.MapFragment;

import java.util.ArrayList;
import java.util.List;



public class TabActivity extends FragmentActivity {

    TabLayout tabLayout;
    ViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navilayout);
        tabLayout=findViewById(R.id.tab);
        viewpager=findViewById(R.id.viewpager);

//        tabLayout.setTabTextColors(Color.WHITE, Color.GRAY);//设置文本在选中和为选中时候的颜色
//        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);//设置选中时的指示器的颜色
//        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//可滑动，默认是FIXED


        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new Mefragment());
        fragments.add(new Mefragment());
        fragments.add(new Mefragment());

        TitleFragmentPagerAdapter adapter = new TitleFragmentPagerAdapter(getSupportFragmentManager(), fragments, new String[]{"第一栏", "第二栏", "第三栏"});
        viewpager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewpager);
    }

}
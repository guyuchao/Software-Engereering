package com.example.guyuchao.myapplication;

/**
 * Created by guyuchao on 18-4-28.
 */

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;


import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends FragmentActivity {

    TabLayout tabLayout;
    ViewPager viewpager;
    private String username;
    private String userid;


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        tabLayout=findViewById(R.id.tab);
        viewpager=findViewById(R.id.viewpager);
        username=getIntent().getStringExtra("username");
        userid=getIntent().getStringExtra("userid");

        Bundle bundle = new Bundle();
        bundle.putString("username",username);//这里的values就是我们要传的值
        bundle.putString("userid",userid);
        List<Fragment> fragments = new ArrayList<>();
        Mapfragment mapfragment = new Mapfragment();
        Mefragment mefragment=new Mefragment();
        Aboutfragment aboutfragment=new Aboutfragment();
        mapfragment.setArguments(bundle);
        mefragment.setArguments(bundle);
        fragments.add(mefragment);
        fragments.add(mapfragment);
        fragments.add(aboutfragment);

        TitleFragmentPagerAdapter adapter = new TitleFragmentPagerAdapter(getSupportFragmentManager(), fragments, new String[]{"我的", "导航", "关于"});
        viewpager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewpager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            Drawable d = null;
            switch (i) {
                case 0:
                    d = getDrawable(R.drawable.selector_home);
                    break;
                case 1:
                    d = getDrawable(R.mipmap.navi_home);
                    break;
                case 2:
                    d = getDrawable(R.drawable.selector_more);
                    break;
            }
            tab.setIcon(d);
        }
//        viewpager.setCurrentItem(2);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if(requestCode == 0 && resultCode == RESULT_OK) {
            finish();
        }
    }

}
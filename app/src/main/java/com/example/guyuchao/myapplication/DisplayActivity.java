package com.example.guyuchao.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bean.Exhibition;
import com.bean.MarkInfo;
import com.bean.Nearest;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class DisplayActivity extends FragmentActivity {

    TabLayout tabLayout;
    ViewPager viewpager;
    private ImageView Back;
    private String username;
    private String museumid;
    private String userid;
    private TextView museum_title;
    private String museumname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_main);
        tabLayout=findViewById(R.id.tab);
        viewpager=findViewById(R.id.viewpager);
        museum_title=findViewById(R.id.museum_title);
        Back=(ImageView)findViewById(R.id.btn_back);
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayActivity.this, HomeActivity.class);
                intent.putExtra("username",username);
                //intent.putExtra("from","map");
                //startActivityForResult(intent,0);
            }
        });

        username=getIntent().getStringExtra("username");
        museumid=getIntent().getStringExtra("museumid");
        userid=getIntent().getStringExtra("userid");
        museumname=getIntent().getStringExtra("name");
        museum_title.setText(museumname);

        Bundle bundle = new Bundle();
        bundle.putString("museumid",museumid);//这里的values就是我们要传的值
        bundle.putString("username",username);
        bundle.putString("userid",userid);
        BasicFragment basicfragment = new BasicFragment();
        basicfragment.setArguments(bundle);
        ExhibitFragment exhibitfragment=new ExhibitFragment();
        exhibitfragment.setArguments(bundle);
        CollectFragment collectFragment=new CollectFragment();
        collectFragment.setArguments(bundle);
//        tabLayout.setTabTextColors(Color.WHITE, Color.GRAY);//设置文本在选中和为选中时候的颜色
//        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);//设置选中时的指示器的颜色
//        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//可滑动，默认是FIXED


        List<Fragment> fragments = new ArrayList<>();
        fragments.add(basicfragment);
        fragments.add(exhibitfragment);

        fragments.add(collectFragment);

        TitleFragmentPagerAdapter adapter = new TitleFragmentPagerAdapter(getSupportFragmentManager(), fragments, new String[]{"基本信息", "展览信息", "展品信息"});
        viewpager.setAdapter(adapter);
        viewpager.setOffscreenPageLimit(3);


        tabLayout.setupWithViewPager(viewpager);
    }
    /*
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Toast.makeText(getApplicationContext(), "上传成功", Toast.LENGTH_LONG).show();
        // TODO Auto-generated method stub
        if(requestCode == 0 && resultCode == RESULT_OK) {
            finish();
        }
        if (requestCode==1 && resultCode==RESULT_OK){
            Toast.makeText(getApplicationContext(), "上传成功", Toast.LENGTH_LONG).show();

        }

    }
*/
}

package com.example.guyuchao.myapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bean.Collection;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


/**
 * A simple {@link Fragment} subclass.
 */
public class CollectFragment extends Fragment {
    private String url_for_collect_info="http://39.106.168.133:8080/api/exhibits/";
    private String username;
    private String museumid;

    private Collection collectioninfo;
    private ViewGroup linear;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_collect, container, false);

        linear=(ViewGroup) view.findViewById(R.id.collection);

        Bundle bundle=getArguments();
        username=bundle.getString("username");
        museumid=bundle.getString("museumid");

        url_for_collect_info+="426";
        OkHttpUtils.get()
                .url(url_for_collect_info)
                .build()
                .execute(new MyStringCallback());
        return view;
    }

    public class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            //handler.sendEmptyMessage(1);
            Toast.makeText( getActivity().getApplicationContext() , e.getMessage(), Toast.LENGTH_LONG).show();

        }

        @Override
        public void onResponse(String response, int id) {

            List<Collection> collections_list=jsonStringToList(response);
            if(collections_list==null||collections_list.isEmpty()){
                Toast.makeText(getContext(),"暂无展品",Toast.LENGTH_LONG);
            }
            else {
                for (Collection item : collections_list) {
                    ImageView imgview = new ImageView(getContext());
                    imgview.setImageResource(R.mipmap.yu);
                    imgview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200));
                    TextView introView = new TextView(getContext());
                    introView.setText(item.getIntroduce());
                    TextView nameView = new TextView(getContext());
                    nameView.setText(item.getName());

                    TextView labelname=new TextView(getContext());
                    labelname.setText("展品名称：");
                    TextView labelintro=new TextView(getContext());
                    labelintro.setText("展品介绍：");

                    linear.addView(imgview);
                    linear.addView(labelname);
                    linear.addView(nameView);
                    linear.addView(labelintro);
                    linear.addView(introView);
                }

            }
        }
    }
    public List<Collection> jsonStringToList(String string) {

        try {
            Gson gson = new Gson();
            List<Collection> lst = new ArrayList<>();
            JsonArray array = new JsonParser().parse(string).getAsJsonArray();
            for (final JsonElement element : array) {
                lst.add(gson.fromJson(element, Collection.class));
            }
            return lst;
        } catch (Exception e) {
            return null;
        }
    }

}

package com.example.guyuchao.myapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bean.Exhibition;
import com.bean.Nearest;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExhibitFragment extends Fragment {
    String url_for_exhibit_info="http://39.106.168.133:8080/api/exhibition/";
    String username;
    String museumid;
    Exhibition exhibitinfo;
    private TextView exhibit_title;
    private TextView exhibit_time;
    private TextView address;
    private TextView exhitbit_intro;
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_exhibit, container, false);
        exhibit_time=view.findViewById(R.id.exhibit_time);
        exhibit_title=view.findViewById(R.id.exhibit_title);
        exhitbit_intro=view.findViewById(R.id.exhibit_intro);
        address=view.findViewById(R.id.address);
        Bundle bundle=getArguments();
        username=bundle.getString("username");
        museumid=bundle.getString("museumid");
        url_for_exhibit_info+=museumid;
        OkHttpUtils.get()
                .url(url_for_exhibit_info)
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
            //Toast.makeText(getActivity().getApplicationContext(),response, Toast.LENGTH_LONG).show();
            exhibitinfo=new Gson().fromJson(response,Exhibition.class);
            exhitbit_intro.setText(exhibitinfo.getIntroduce());
            exhibit_time.setText(exhibitinfo.getTime());
            exhibit_title.setText(exhibitinfo.getTitle());
            address.setText(exhibitinfo.getAddress());
            /*Answer answer=new Gson().fromJson(response,Answer.class);
            if (Integer.parseInt(answer.getValid())==1){
                handler.sendEmptyMessage(1);
            }
            else{
                handler.sendEmptyMessage(0);
            }*/

            //Toast.makeText(getApplicationContext(),response, Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(), answer.getMsg(), Toast.LENGTH_LONG).show();
        }
    }

}

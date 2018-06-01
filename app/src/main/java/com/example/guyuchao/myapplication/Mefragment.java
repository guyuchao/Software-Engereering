package com.example.guyuchao.myapplication;

/**
 * Created by guyuchao on 18-3-27.
 */



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class Mefragment extends Fragment {
    TextView exitView;
    TextView userText;
    String username;
    String userid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.me_layout, container, false);
        exitView=view.findViewById(R.id.exit);
        userText=view.findViewById(R.id.name);
        exitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), LoginActivity.class);
                getActivity().startActivityForResult(intent,0);
            }
        });
        Bundle bundle=getArguments();
        username=bundle.getString("username");
        userid=bundle.getString("userid");
        userText.setText(username);
        return view;
    }



}
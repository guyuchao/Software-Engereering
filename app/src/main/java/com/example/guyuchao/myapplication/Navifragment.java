package com.example.guyuchao.myapplication;

/**
 * Created by guyuchao on 18-3-27.
 */



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Navifragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.me_layout, container, false);

        return view;
    }


    @Override
    public void onClick(View v) {

    }
}
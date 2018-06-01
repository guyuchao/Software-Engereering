package com.example.guyuchao.myapplication;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.bean.MarkInfo;
import com.bean.Nearest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by guyuchao on 18-3-29.
 */

public class Mapfragment extends Fragment {
    MapView mMapView = null;
    BaiduMap mBaiduMap=null;
    private LocationClient mLocationClient = null;
    private MyLocationListener listener = new MyLocationListener();
    boolean isFirstLocate=true;
    private RelativeLayout mMarkerInfoLy;
    private String username;
    private String userid;
    MarkInfo Info=null;
    private String url_for_ten_museum="http://39.106.168.133:8080/api/museum/nearest";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.mapragment, container, false);
        mMapView = (MapView)view.findViewById(R.id.bmap);
        //mMapView.showZoomControls(false);//关闭缩放
        mMarkerInfoLy = (RelativeLayout) view.findViewById(R.id.marker_info);

        mBaiduMap = mMapView.getMap();
        Bundle bundle=getArguments();
        username=bundle.getString("username");
        userid=bundle.getString("userid");

        OkHttpUtils.get()
                .url(url_for_ten_museum)
                .addParams("lng","116.46")
                .addParams("lat","39.92")
                .build()
                .execute(new MyStringCallback());

        //mLocationClient.start();//init为定位方法

        initClickListener();
        initMapClickEvent();
        return view;
    }

    private void initMapClickEvent(){
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener()
        {

            @Override
            public boolean onMapPoiClick(MapPoi arg0)
            {
                return false;
            }

            @Override
            public void onMapClick(LatLng arg0)
            {
                mMarkerInfoLy.setVisibility(View.GONE);
                mBaiduMap.hideInfoWindow();

            }
        });
    }
    private void initMyLocation(){
        // 定位初始化
        mLocationClient = new LocationClient(getContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(listener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，设置定位模式，默认高精度
        option.setCoorType("bd09ll");//bd09ll：百度经纬度坐标；
        option.setScanSpan(1000);//可选，设置发起定位请求的间隔，int类型，单位ms
        option.setOpenGps(true);//可选，设置是否使用gps，默认false
        option.setLocationNotify(true);//可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false
        option.setIgnoreKillProcess(false);//可选，定位SDK内部是一个service，并放到了独立进程。
        option.SetIgnoreCacheException(false);//可选，设置是否收集Crash信息，默认收集，即参数为false
        option.setWifiCacheTimeOut(5*60*1000);//可选，7.2版本新增能力//如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位
        option.setEnableSimulateGps(false);//可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false
        mLocationClient.setLocOption(option);//mLocationClient为第二步初始化过的LocationClient对象
    }

    @Override
    public void onResume() {
        super.onResume();
        //在Fragment执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在Fragment执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mLocationClient.stop();
    }

    //标注
    public void addOverlay(List<MarkInfo> Info_list){
        LatLng FirstLatlng=null;
        for (MarkInfo info:Info_list){
            addOverlay(info);
            if(FirstLatlng==null) {
                FirstLatlng = info.getLatlng();
            }
        }
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(FirstLatlng);
        mBaiduMap.setMapStatus(u);
    }
    public void addOverlay(MarkInfo Info) {
        Marker marker=null;
        OverlayOptions overlayoptions= new MarkerOptions()//
                .position(Info.getLatlng())// 设置marker的位置
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.maker))// 设置marker的图标
                .zIndex(9);// 設置marker的所在層級

        TextOptions textoptions=new TextOptions()
                .position(Info.getLatlng())
                .text(Info.getName())
                .bgColor(Color.WHITE)
                .fontSize(30)
                .fontColor(Color.BLACK)
                .zIndex(8);
        mBaiduMap.addOverlay(textoptions);
        marker=(Marker) mBaiduMap.addOverlay(overlayoptions);

        Bundle bundle=new Bundle();
        bundle.putSerializable("marker", Info);
        marker.setExtraInfo(bundle);
    }
    public void initClickListener() {
        BaiduMap.OnMarkerClickListener listener = new BaiduMap.OnMarkerClickListener() {
            /**
             * 地图 Marker 覆盖物点击事件监听函数
             *
             * @param marker 被点击的 marker
             */
            public boolean onMarkerClick(Marker marker) {
                Info = (MarkInfo) marker.getExtraInfo().get("marker");
                InfoWindow mInfoWindow;
                //生成一个TextView用户在地图中显示InfoWindow
                TextView location = new TextView(getActivity().getApplicationContext());
                // location.setBackgroundResource(R.drawable.textback);
                //location.setPadding(10, 10, 10, 10);
                location.setText("点击进入");
                location.setBackgroundColor(Color.GREEN);
                //将marker所在的经纬度的信息转化成屏幕上的坐标
                final LatLng ll = marker.getPosition();
                android.graphics.Point p = mBaiduMap.getProjection().toScreenLocation(ll);
                p.y -= 50;
                LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(location);
                //为弹出的InfoWindow添加点击事件
                mInfoWindow = new InfoWindow(bitmapDescriptor, llInfo,-30,
                        new InfoWindow.OnInfoWindowClickListener() {

                            @Override
                            public void onInfoWindowClick() {
                                //隐藏InfoWindow
                                //mBaiduMap.hideInfoWindow();
                                Intent intent = new Intent(getActivity(), DisplayActivity.class);
                                intent.putExtra("username",username);
                                intent.putExtra("userid",userid);
                                intent.putExtra("museumid",Info.getId());
                                intent.putExtra("name",Info.getName());
                                //
                                // Toast.makeText(getActivity().getApplicationContext(),userid,Toast.LENGTH_LONG).show();
                                getActivity().startActivityForResult(intent,0);
                            }
                        });
                //显示InfoWindow
                mBaiduMap.showInfoWindow(mInfoWindow);
                mMarkerInfoLy.setVisibility(View.VISIBLE);
                popupInfo(mMarkerInfoLy, Info);


                return true;
            }
        };
        mBaiduMap.setOnMarkerClickListener(listener);
    }

    protected void popupInfo(RelativeLayout mMarkerLy, MarkInfo info)    {
        ViewHolder viewHolder = null;
        if (mMarkerLy.getTag() == null)
        {
            viewHolder = new ViewHolder();
            viewHolder.infoImg = (ImageView) mMarkerLy
                    .findViewById(R.id.info_img);
            viewHolder.infoName = (TextView) mMarkerLy
                    .findViewById(R.id.info_name);
            viewHolder.distance = (TextView) mMarkerLy
                    .findViewById(R.id.distance);
            mMarkerLy.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) mMarkerLy.getTag();
        viewHolder.infoImg.setImageResource(info.getImgId());
        viewHolder.distance.setText(info.getDistance());
        viewHolder.infoName.setText(info.getName());
    }

    private class ViewHolder{
        ImageView infoImg;
        TextView infoName;
        TextView distance;

    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            //LatLng llA=new LatLng(location.getLatitude(),location.getLongitude());
            LatLng llA=new LatLng(150,39);

            Toast.makeText(getContext(),llA.toString(),Toast.LENGTH_LONG);

            MarkerOptions ooA = new MarkerOptions().position(llA).icon(BitmapDescriptorFactory.fromResource(R.mipmap.maker))
                    .zIndex(9).draggable(true);
            mBaiduMap.addOverlay(ooA);

            if (isFirstLocate) {
                isFirstLocate = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }

        }
    }//定位


    public class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            //handler.sendEmptyMessage(1);
            Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

        }

        @Override
        public void onResponse(String response, int id) {

            List<Nearest> nearests_list=jsonStringToList(response);
            List<MarkInfo> marklist=new ArrayList<MarkInfo>();
            for(Nearest item:nearests_list) {
                Double distance=Double.parseDouble(item.getDistance())*1000;
                String dis="距离："+distance.toString()+"公里";
                marklist.add(new MarkInfo(item.getId(),item.getName(), Double.parseDouble(item.getLng()), Double.parseDouble(item.getLat()),dis , R.mipmap.nongye));
            }
            addOverlay(marklist);
        }
    }
    public List<Nearest> jsonStringToList(String string) {

        try {
            Gson gson = new Gson();
            List<Nearest> lst = new ArrayList<>();
            JsonArray array = new JsonParser().parse(string).getAsJsonArray();
            for (final JsonElement element : array) {
                lst.add(gson.fromJson(element, Nearest.class));
            }
            return lst;
        } catch (Exception e) {
            return null;
        }
    }
}

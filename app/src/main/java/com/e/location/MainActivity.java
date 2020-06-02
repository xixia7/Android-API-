package com.e.location;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends AppCompatActivity {

    private MapView mMapView;//声明地图组件
    private BaiduMap mBaiduMap;//定义百度地图对象
    private boolean isFirstLoc;//记录是否是第一次定位
    private MyLocationConfiguration.LocationMode locationMode;//当前定位模式
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SDKInitializer.initialize(getApplicationContext());//初始化地图SDK,且必须放在 setContentView(R.layout.activity_main)前面
        setContentView(R.layout.activity_main);
        mMapView = (MapView)findViewById(R.id.bmapview); //获取地图组件，必须放在setContentView(R.layout.activity_main);后面
        mBaiduMap = mMapView.getMap();
        //实时获取经纬度
        //获取系统的LocationManager对象
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //添加权限检查
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //设置每一秒获取一次location信息
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,//GPS定位提供者
                1000, //更新数据时间为1秒
                1, //位置间隔为1米
                //监听器
                new LocationListener() { //GPS定位信息发生改变时触发，用于更新位置信息
                    @Override
                    public void onLocationChanged(Location location) {
                        locationUpdates(location); //GPS信息发生改变时，更新位置
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {
                        //位置状态发生改变时触发
                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }

                }
        );
        Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
        locationUpdates(location); //将最新的定位信息传递到locationUpdates方法中
    }

    public void locationUpdates(Location location) {
        if(location != null) {
            LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
            Log.i("Location","纬度："+location.getLatitude()+"---经度："+location.getLongitude());
            if(isFirstLoc) {
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);//更新坐标位置
//                u = MapStatusUpdateFactory.zoomTo(16f);
                mBaiduMap.animateMapStatus(u);//设置地图位置
                isFirstLoc=false;//取消第一次定位

            }
            //构造定位数据
            MyLocationData locData = new MyLocationData.Builder().accuracy(location.getAccuracy()).direction(100)//设置方向信息
            .latitude(location.getLatitude()) //设置纬度坐标
            .longitude(location.getLongitude()) //设置经度坐标
            .build();
            mBaiduMap.setMyLocationData(locData);
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
            locationMode = MyLocationConfiguration.LocationMode.NORMAL; //设置定位模式
            MyLocationConfiguration config = new MyLocationConfiguration(locationMode,true,bitmapDescriptor);
            mBaiduMap.setMyLocationConfiguration(config);//显示定位图标
        }else {
            Log.i("Location","没有获取到GPS信息");
        }
    }
    protected  void  onStart() {
        super.onStart();
        mBaiduMap.setMyLocationEnabled(true); //开启定位图层
    }
    protected  void  onStop() {
        super.onStop();
        mBaiduMap.setMyLocationEnabled(false); //停止定位图层
    }
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mMapView = null;
    }
}

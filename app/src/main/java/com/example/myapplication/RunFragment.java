package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.MyLocationStyle;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.SharedPreferences;

public class RunFragment extends Fragment implements AMapLocationListener {
    private MapView mapView;
    private AMap aMap;
    private AMapLocationClient locationClient;
    private boolean isTracking = false;
    private Button startButton;
    private TextView timeTextView, distanceTextView, speedTextView;
    private Handler handler = new Handler();
    private long startTime;
    private List<LatLng> pathPoints = new ArrayList<>();
    private PolylineOptions polylineOptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_run, container, false);
        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        // 初始化aMap对象
        if (aMap == null) {
            aMap = mapView.getMap();
        }

        // 设置定位样式
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        myLocationStyle.interval(2000); // 每2秒定位一次
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));

        aMap.setMyLocationStyle(myLocationStyle);


        // 设置地图加载监听
        aMap.setOnMapLoadedListener(() -> {
            Toast.makeText(getContext(), "地图加载成功", Toast.LENGTH_SHORT).show();

        });

        aMap.setMyLocationEnabled(true); // 启用定位图层

        startButton = view.findViewById(R.id.start_button);
        timeTextView = view.findViewById(R.id.time_text_view);
        distanceTextView = view.findViewById(R.id.distance_text_view);
        speedTextView = view.findViewById(R.id.speed_text_view); // 添加速度显示

        // 初始化PolylineOptions
        polylineOptions = new PolylineOptions().width(10).color(0xFFFF0000); // 红色线条

        startButton.setOnClickListener(v -> {
            if (!isTracking) {
                startTracking();
            } else {
                stopTracking();
            }
        });

        return view;
    }


    private void startTracking() {
        try {
            if (ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }

            // 初始化高德定位
            locationClient = new AMapLocationClient(getContext());
            AMapLocationClientOption option = new AMapLocationClientOption();
            option.setInterval(2000); // 定位间隔
            option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            locationClient.setLocationOption(option);
            locationClient.setLocationListener(this);
            locationClient.startLocation();

            isTracking = true;
            startButton.setText("结束跑步");
            startTime = System.currentTimeMillis();
            pathPoints.clear(); // 清除之前的路径点
            handler.post(updateRunnable);
            Toast.makeText(getContext(), "开始运动记录", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "初始化定位失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void stopTracking() {
        isTracking = false;
        startButton.setText("开始跑步");
        locationClient.stopLocation();
        handler.removeCallbacks(updateRunnable);

        Toast.makeText(getContext(), "停止运动记录", Toast.LENGTH_SHORT).show();
    }

    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            if (isTracking) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                timeTextView.setText("时间: " + formatTime(elapsedTime));
                double distance = calculateDistance();
                distanceTextView.setText("距离: " + String.format("%.2f", distance) + " 米");
                speedTextView.setText("速度: " + calculateSpeed(elapsedTime, distance) + " 分钟/千米"); // 显示速度
                handler.postDelayed(this, 1000);
            }
        }
    };

    private String formatTime(long milliseconds) {
        long seconds = (milliseconds / 1000) % 60;
        long minutes = (milliseconds / (1000 * 60)) % 60;
        long hours = (milliseconds / (1000 * 60 * 60)) % 24;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private double calculateDistance() {
        double totalDistance = 0.0;
        for (int i = 1; i < pathPoints.size(); i++) {
            totalDistance += calculateDistanceBetween(pathPoints.get(i - 1), pathPoints.get(i));
        }
        return totalDistance;
    }

    private double calculateDistanceBetween(LatLng start, LatLng end) {
        float[] results = new float[1];
        Location.distanceBetween(start.latitude, start.longitude, end.latitude, end.longitude, results);
        return results[0];
    }

    private String calculateSpeed(long elapsedTime, double distance) {
        if (distance == 0) return "N/A";
        double distanceInKm = distance / 1000; // 转换为千米
        double timeInHours = elapsedTime / 3600000.0; // 转换为小时
        double speed = timeInHours / distanceInKm; // 速度（小时/千米）
        return String.format("%.2f", speed * 60); // 转换为分钟/千米并保留两位小数
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        if (location != null && location.getErrorCode() == 0) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            pathPoints.add(latLng); // 添加路径点

            // 绘制路径
            polylineOptions.add(latLng);
            aMap.addPolyline(polylineOptions);

            //aMap.addMarker(new MarkerOptions().position(latLng).title("当前位置"));
            float currentZoom = aMap.getCameraPosition().zoom;
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, currentZoom));

        } else {
            Toast.makeText(getContext(), "定位失败: " + location.getErrorInfo(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        if (locationClient != null) {
            locationClient.stopLocation();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (locationClient != null) {
            locationClient.onDestroy();
        }
    }
}

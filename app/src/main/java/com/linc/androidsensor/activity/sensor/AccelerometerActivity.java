package com.linc.androidsensor.activity.sensor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.linc.androidsensor.R;
import com.linc.androidsensor.base.BaseSensorActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AccelerometerActivity extends BaseSensorActivity implements View.OnClickListener, Chronometer.OnChronometerTickListener {
    private static int flag = 0;

    private TextView mTvInfo; // 加速度TextView
    private TextView mTvTime; // 日期TextView
    private TextView mTvlati; // 经纬度TextView

    private LocationManager locationManager; //GPS定位
    private Chronometer chronometer; //秒表

    private Button btn_start, btn_stop, btn_base;
    private float mGravity = SensorManager.STANDARD_GRAVITY - 0.8f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_accelerometer);

        mTvInfo = findViewById(R.id.tv_info);
        mTvTime = findViewById(R.id.tv_time);
        mTvlati = findViewById(R.id.tv_lati);

        if (mSensor == null) {
            mTvInfo.setText("No Accelerometer senor!");
            Log.d("linc", "no this sensor.");
        }

        // 显示时间
        SimpleDateFormat formatter = new SimpleDateFormat("日期：yyyy-MM-dd\n");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        mTvTime.setText(str);

        // 秒表
        initView();

        //GPS显示经度纬度
        // 获取系统LocationManager服务
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // 从GPS获取最近的定位信息
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            // 将location里的位置信息显示在EditText中
            updateView(location);
            // 设置每2秒获取一次GPS的定位信息
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    // 当GPS定位信息发生改变时，更新位置
                    updateView(location);
                }

                @Override
                public void onProviderDisabled(String provider) {
                    updateView(null);
                }


                @Override
                public void onStatusChanged(String provider, int status,
                                            Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {

                }
            });
        }

    }

    private void updateView(Location location) {
        if (location != null && flag == 1) {
            StringBuffer sb = new StringBuffer();
            sb.append("实时GPS信息：\n  经度：");
            sb.append(location.getLongitude());
            sb.append("\n  纬度：");
            sb.append(location.getLatitude());
            sb.append("\n  高度：");
            sb.append(location.getAltitude());
            sb.append("\n  速度：");
            sb.append(location.getSpeed());
            sb.append("\n  方向：");
            sb.append(location.getBearing());
            sb.append("\n  精度：");
            sb.append(location.getAccuracy());
            mTvlati.setText(sb.toString());
        } else if (location != null && flag == 0) {
            // 如果传入的Location对象为空则清空EditText
            StringBuffer sb = new StringBuffer();
            sb.append("实时GPS信息：\n  经度：");
            sb.append("0");
            sb.append("\n  纬度：");
            sb.append("0");
            sb.append("\n  高度：");
            sb.append("0");
            sb.append("\n  速度：");
            sb.append("0");
            sb.append("\n  方向：");
            sb.append("0");
            sb.append("\n  精度：");
            sb.append("0");
            mTvlati.setText(sb.toString());
        } else {
            mTvlati.setText("没有定位");
        }
    }


    /**
     * 加速度
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.d("linc", "value size: " + sensorEvent.values.length);
        if(flag == 1){
            Sensor sensor = sensorEvent.sensor;
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float xValue = sensorEvent.values[0];// Acceleration minus Gx on the x-axis
                float yValue = sensorEvent.values[1];//Acceleration minus Gy on the y-axis
                float zValue = sensorEvent.values[2];//values[2]: Acceleration minus Gz on the z-axis
                mTvInfo.setText("\n加速度：");
                mTvInfo.append("\n  x轴： " + xValue + "\n  y轴： " + yValue + "\n  z轴： " + zValue);
                mTvInfo.append("\n  屏幕状态：");
                if (xValue > mGravity) {
                    mTvInfo.append("重力指向设备左边");
                } else if (xValue < -mGravity) {
                    mTvInfo.append("重力指向设备右边");
                } else if (yValue > mGravity) {
                    mTvInfo.append("重力指向设备下边");
                } else if (yValue < -mGravity) {
                    mTvInfo.append("重力指向设备上边");
                } else if (zValue > mGravity) {
                    mTvInfo.append("屏幕朝上");
                } else if (zValue < -mGravity) {
                    mTvInfo.append("屏幕朝下");
                }
                //mTvInfo.append("\n");
            }
        } else{
            mTvInfo.setText("\n加速度：");
            mTvInfo.append("\n  x轴： " + "0" + "\n  y轴： " + "0" + "\n  z轴： " + "0");
        }


    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected int getDefaultSensor() {
        return Sensor.TYPE_ACCELEROMETER;
    }

    // 秒表
    private void initView() {
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        btn_start = (Button) findViewById(R.id.btnStart);
//        btn_stop = (Button) findViewById(R.id.btnStop);
        btn_base = (Button) findViewById(R.id.btnReset);

        chronometer.setText("00:00:00");
        chronometer.setOnChronometerTickListener(this);

        btn_start.setOnClickListener(this);
//        btn_stop.setOnClickListener(this);
        btn_base.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        long offset = 0;
        switch (v.getId()) {
            case R.id.btnStart:
                flag = 1;
                chronometer.setBase(SystemClock.elapsedRealtime() - offset);
                chronometer.start();// 开始计时
                btn_start.setEnabled(false);
                btn_base.setEnabled(true);
//                btn_stop.setEnabled(true);
                break;
//            case R.id.btnStop:
//                chronometer.stop();// 停止计时
//                offset = SystemClock.elapsedRealtime() - chronometer.getBase();
//                btn_start.setEnabled(true);
//                btn_stop.setEnabled(false);
//                btn_base.setEnabled(true);
//                break;
            case R.id.btnReset:
                flag = 0;
                chronometer.stop();// 停止计时
                offset = SystemClock.elapsedRealtime() - chronometer.getBase();
                chronometer.setBase(SystemClock.elapsedRealtime());// 复位
                offset = 0;
                btn_base.setEnabled(false);
                btn_start.setEnabled(true);
                chronometer.setText("00:00:00");
                break;
        }
    }


    @Override
    public void onChronometerTick(Chronometer chronometer) {
        CharSequence text = chronometer.getText();
        if (text.length() == 5) {
            chronometer.setText("00:" + text);
        } else if (text.length() == 7) {
            chronometer.setText("0" + text);
        }
    }


}

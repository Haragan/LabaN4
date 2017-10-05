package com.garkin.laban4;

import android.content.Intent;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    //Создание полей класса, которые будут
    //связанны с элементам отображения.
    TextView aX, aY, aZ, mX, mY, mZ, proximity, light;

    SensorManager sensorManager;
    Sensor aSensor, mSensor, pSensor, lSensor;
    Integer brightnessValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Привязываем данные поля к элементам на activity_main.xml:
        aX = (TextView)findViewById(R.id.tvAX);
        aY = (TextView)findViewById(R.id.tvAY);
        aZ = (TextView)findViewById(R.id.tvAZ);

        mX = (TextView)findViewById(R.id.tvMX);
        mY = (TextView)findViewById(R.id.tvMY);
        mZ = (TextView)findViewById(R.id.tvMZ);

        proximity = (TextView)findViewById(R.id.tvProximity);
        light = (TextView)findViewById(R.id.tvLight);

        //sensorManager Необходим для работы с сенсорами(датчиками)
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //Из метода getDefaultSensor мы в переменную aSensor получаем Акселерометр нашего телефона
        //Теперь мы можем с ним взаимодействовать
        aSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//Акселерометр
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);//Магнитометр
        pSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);//Датчик расстояния
        lSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);//Датчик света
    }

    //Метод установливает освещенность экрана
    private void setBrightness(int brightnessValue) {
        //Если можно устанавливать, мы устанавливаем
        if (Settings.System.canWrite(getApplicationContext())) {
            Settings.System.putInt(
                    getApplicationContext().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS,
                    brightnessValue);
            this.brightnessValue = brightnessValue;
        }
        else {
            //Если нет, то запрашиваем разрешение на установку
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    //Изменяем освещенность экрана, в зависимости от показаний датчика света
    private void changeBrightness(float lightValue){
        if (400 < lightValue ) {
            setBrightness(40);
        }else if (300 < lightValue && 400 >= lightValue) {
            setBrightness(80);
        } else if (200 < lightValue && 300 >= lightValue) {
            setBrightness(150);
        }else if (100 < lightValue && 200 >= lightValue) {
            setBrightness(200);
        }else if (0 <= lightValue && 100 >= lightValue) {
            setBrightness(255);
        }
    }

    //Теперь можно переходить к обработке событий. Реализуем один из
    //методов интерфейса SensorEventListener. Этим методом будет
    //onSensorChanged.
    //Если хотя бы один из показателей сенсеров изменится вызовется этот метод
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            aX.setText(Float.toString(sensorEvent.values[0]));
            aY.setText(Float.toString(sensorEvent.values[1]));
            aZ.setText(Float.toString(sensorEvent.values[2]));
        }
        if(sensorEvent.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
            mX.setText(Float.toString(sensorEvent.values[0]));
            mY.setText(Float.toString(sensorEvent.values[1]));
            mZ.setText(Float.toString(sensorEvent.values[2]));
        }
        if(sensorEvent.sensor.getType()==Sensor.TYPE_PROXIMITY){
            proximity.setText(Float.toString(sensorEvent.values[0]));}
        if(sensorEvent.sensor.getType()==Sensor.TYPE_LIGHT){
            float lightValue = sensorEvent.values[0];
            light.setText(Float.toString(lightValue));
            changeBrightness(lightValue);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    //Если приложение входит в состояние пауза, то вызывается этот метод
    //и мы перестаем считывать показания с сенсеров
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this,aSensor);
        sensorManager.unregisterListener(this,mSensor);
        sensorManager.unregisterListener(this,pSensor);
        sensorManager.unregisterListener(this,lSensor);
    }

    //Если к приложению возвращаются, то вызывается этот метод
    //и мы подписываемя на изменения состояния сенсеров
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, pSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, lSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }
}

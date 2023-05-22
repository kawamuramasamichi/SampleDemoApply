package com.example.sampledemoapply;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sampledemoapply.data.SensorData;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final Integer START_DEFAULT = 500; // センサー値蓄積開始時間
    private static final Integer INTERVAL_DEFAULT = 500; // センサー値蓄積間隔(0.5秒)

    private static final int MATRIX_SIZE = 16;

    private static final int SENSOR_PARAMETER_X = 0; // センサー値：X軸
    private static final int SENSOR_PARAMETER_Y = 1; // センサー値：Y軸
    private static final int SENSOR_PARAMETER_Z = 2; // センサー値：Z軸

    private SensorManager manager;
    private TextView values1;
    private TextView values2;
    private TextView values3;
    // 開始時間
    private Integer start;
     //間隔
    private Integer interval;

    float[] in = new float[MATRIX_SIZE];
    float[] out = new float[MATRIX_SIZE];
    float[] I = new float[MATRIX_SIZE];

    /**
     * 各センサーデータリスト
     */
    List<SensorData> data = new ArrayList<SensorData>();
    /**
     * 加速度センサー値 X,Y,Z
     */
    float[] ksk = new float[3];
    /**
     * ジャイロセンサー値 X,Y,Z
     */
    float[] gyro = new float[3];

    float[] o = new float[3];
    /**
     * 磁力センサー値 X,Y,Z
     */
    float[] mgn = new float[3];

    //タイマー処理用
    private Timer mTimer = null;
    Handler mHandler = new Handler();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = 60 *30 * 1000;    // スタート時間     TODO:後々この値をユーザが設定可能できるようにする
        interval = 60 * 30 * 1000; // 間隔時間（30分） TODO:後々この値をユーザが設定可能できるようにする

        values1 = (TextView)findViewById(R.id.accel); // 加速度
        values2 = (TextView)findViewById(R.id.gyro); // ジャイロ
        values3 = (TextView)findViewById(R.id.inclination); // 傾き
        manager = (SensorManager)getSystemService(SENSOR_SERVICE);

        // タイマー処理
        mTimer = new Timer(true);
        // データ蓄積処理
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mHandler.post( new Runnable(){
                    public void run(){
                        // ログデータの蓄積処理
                        SensorData sensorData = new SensorData();
                        Date date = new Date();
                        sensorData.setLogDt(date);
                        sensorData.setKsk(ksk);
                        sensorData.setGyro(gyro);
                        data.add(sensorData);
                    }
                });
            }
        },START_DEFAULT,INTERVAL_DEFAULT);

        // タイマー処理
        mTimer = new Timer(true);
        // ログデータ出力(CSV)
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post( new Runnable(){
                    public void run(){
                        String header = "DATE,TIME,AX,AY,AZ,GX,GY,GZ\n";// ヘッダー部分
                        String fileName = "data.csv";
                        // 保存先
                        String path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();
                        String file = path + "/" + fileName;

                        try {
                            String str = header;
                            for(SensorData list:data){
                                float[] kasoku = list.getKsk();
                                float[] jairo = list.getGyro();
                                SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
                                SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
                                // ログデータ内容
                                str += date.format(list.getLogDt()) + ","
                                    + time.format(list.getLogDt()) + ","
                                    + kasoku[SENSOR_PARAMETER_X] + ","
                                    + kasoku[SENSOR_PARAMETER_Y] + ","
                                    + kasoku[SENSOR_PARAMETER_Z] + ","
                                    + jairo[SENSOR_PARAMETER_X] + ","
                                    + jairo[SENSOR_PARAMETER_Y] + ","
                                    + jairo[SENSOR_PARAMETER_Z] + "\n";
                                /*
                                str += convertSensorValue(kasoku[SENSOR_PARAMETER_X]) + ","
                                    + convertSensorValue(kasoku[SENSOR_PARAMETER_Y]) + ","
                                    + convertSensorValue(kasoku[SENSOR_PARAMETER_Z]) + ","
                                    + convertSensorValue(jairo[SENSOR_PARAMETER_X]) + ","
                                    + convertSensorValue(jairo[SENSOR_PARAMETER_Y]) + ","
                                    + convertSensorValue(jairo[SENSOR_PARAMETER_Z]) + "\n";
                                */
                            }
                            FileOutputStream outputStream = new FileOutputStream(file);

                            // ログデータ出力
                            outputStream.write(str.getBytes());
                            outputStream.close();
                        } catch(IOException e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        },start,interval);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Listenerの登録解除
        manager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Listenerの登録
        List<Sensor> kskSensors = manager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        List<Sensor> gyrSensors = manager.getSensorList(Sensor.TYPE_GYROSCOPE);
        List<Sensor> mgnSensors = manager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
        // 加速度が取得できている場合
        if(kskSensors.size() > 0) {
            Sensor s = kskSensors.get(0);
            manager.registerListener(this, s, SensorManager.SENSOR_DELAY_FASTEST); // 遅延無し
        }
        // ジャイロセンサー値が取得できている場合
        if(gyrSensors.size() > 0) {
            Sensor s = gyrSensors.get(0);
            manager.registerListener(this, s, SensorManager.SENSOR_DELAY_FASTEST); // 遅延無し
        }
        // 磁力センサー値が取得できている場合
        if(mgnSensors.size() > 0) {
            Sensor s = mgnSensors.get(0);
            manager.registerListener(this, s, SensorManager.SENSOR_DELAY_FASTEST); // 遅延無し
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onSensorChanged(SensorEvent event) {
        // イベント内容：加速度
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // 書き込み用
            ksk = event.values;
            // 画面表示
            String str = "加速度センサー値:"
            + "\nX軸:" + ksk[SENSOR_PARAMETER_X]
            + "\nY軸:" + ksk[SENSOR_PARAMETER_Y]
            + "\nZ軸:" + ksk[SENSOR_PARAMETER_Z];
            values1.setText(str);
        }
        // イベント内容：ジャイロ
        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // 書き込み用
            gyro = event.values;
            // 画面表示
            String str = "ジャイロセンサー値:"
                + "\nX軸中心:" + gyro[SENSOR_PARAMETER_X]
                + "\nY軸中心:" + gyro[SENSOR_PARAMETER_Y]
                + "\nZ軸中心:" + gyro[SENSOR_PARAMETER_Z];
            values2.setText(str);
        }
        // イベント内容：磁力
        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            // 書き込み用
            mgn = event.values;
            // 画面表示
            String str = "磁力:"
                    + "\nX軸:" + mgn[SENSOR_PARAMETER_X]
                    + "\nY軸:" + mgn[SENSOR_PARAMETER_Y]
                    + "\nZ軸:" + mgn[SENSOR_PARAMETER_Z];
            values3.setText(str);
        }
        // 傾きの計算 一旦ストップ
/*********************************************************************************************************************************************************************
        if(ksk != null && mgn != null){
            SensorManager.getRotationMatrix(in, I, ksk, mgn);
            SensorManager.remapCoordinateSystem(in, SensorManager.AXIS_X, SensorManager.AXIS_Z, out);
            SensorManager.getOrientation(out, o);

            // 画面表示
            String str = "傾き:"
                    + "\nroll:" + String.valueOf((o[2] * 180 / 3.1415 ))
                    + "\npitch:" + String.valueOf((o[1] * 180 / 3.1415 ))
                    + "\nyaw:" + String.valueOf((o[0] * 180 / 3.1415 ));
            values3.setText(str);
        }
************************************************************************************************************************************************************************/
    }

    /**
     * 数値変換　センサー値を小数点第二位以下に切り上げ
     * @param sensorData センサー値
     * @return ret センサー値(少数点第二まで)
     */
    public BigDecimal convertSensorValue(float sensorData){
        BigDecimal ret;
        BigDecimal bd = new BigDecimal(sensorData);
        ret = bd.setScale(2,RoundingMode.HALF_UP);// 切り上げ
        return ret;
    }
}
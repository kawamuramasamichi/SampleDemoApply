package com.example.sampledemoapply;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sampledemoapply.data.SensorData;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final Integer START_DEFAULT = 500; // センサー値蓄積開始時間
    private static final Integer INTERVAL_DEFAULT = 500; // センサー値蓄積間隔(0.5秒)
    private static final int SENSOR_PARAMETER_X = 0; // センサー値：X軸
    private static final int SENSOR_PARAMETER_Y = 1; // センサー値：Y軸
    private static final int SENSOR_PARAMETER_Z = 2; // センサー値：Z軸

    private SensorManager manager;
    private TextView accelText;
    private TextView gyroText;
    private TextView magneText;
    private Integer csvStart; // 開始時間
    private Integer csvInterval; //間隔
    private Integer comStart; // 開始時間
    private Integer comInterval; //間隔

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
    /**
     * 磁力センサー値 X,Y,Z
     */
    float[] mgn = new float[3];

    private boolean accSensorFlg;
    private boolean gyroSensorFlg;
    private boolean magSensorFlg;

    Handler mHandler = new Handler();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // CSVファイル
/*        csvStart = 30 * 60 * 1000;    // スタート時間     TODO:後々この値をユーザが設定可能予定
        csvInterval = 30 * 60 * 1000; // 間隔時間（30分） TODO:後々この値をユーザが設定可能予定

        // LTE通信
        comStart = 4 * 60 * 60 * 1000;    // スタート時間     TODO:後々この値をユーザが設定可能予定
        comInterval = 4 * 60 * 60 * 1000; // 間隔時間（4時間） TODO:後々この値をユーザが設定可能予定
*/
        csvStart = 15 * 1000;    // スタート時間
        csvInterval = 15 * 1000; // 間隔時間（30秒）
        // LTE通信
        comStart = 60 * 1000;    // スタート時間
        comInterval = 60 * 1000; // 間隔時間（60秒）

        accelText = findViewById(R.id.accel); // 加速度
        gyroText = findViewById(R.id.gyro); // ジャイロ
        magneText = findViewById(R.id.magne); // 地磁気
        manager = (SensorManager)getSystemService(SENSOR_SERVICE);

        // タイマー処理
        // タイマー処理用
        // データ蓄積
        Timer dataTimer = new Timer(true);
        // データ蓄積処理
        dataTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mHandler.post( new Runnable(){
                    public void run(){
                        // バッテリー残量取得
/*                        BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
                        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                        LedActivity ledAction = new LedActivity();
                        // バッテリー残量チェック
                        if (batLevel < 50) {
                            // LED:黄色
                            ledAction.Led1_GreenOn();
                            ledAction.Led1_RedOn();
                        } else if (batLevel <= 20) {
                            // LED:赤色
                            ledAction.Led1_GreenOff();
                            ledAction.Led1_RedOn();
                        } else {
                            // LED:緑色
                            ledAction.Led1_RedOff();
                            ledAction.Led1_GreenOn();
                        }
*/
                        // ログデータの蓄積処理
                        SensorData sensorData = new SensorData();
                        Date date = new Date();
                        sensorData.setLogDt(date);
                        sensorData.setKsk(ksk[0], ksk[1], ksk[2]);
                        sensorData.setGyro(gyro[0], gyro[1], gyro[2]);
                        sensorData.setMgn(mgn[0], mgn[1], mgn[2]);
                        data.add(sensorData);
                    }
                });
            }
        },START_DEFAULT,INTERVAL_DEFAULT);

        // タイマー処理
        // CSV出力
        Timer csvTimer = new Timer(true);
        // ログデータ出力(CSV)
        csvTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post( new Runnable(){
                    public void run(){
                        String id = "9999999999"; // 利用者ID
                        Date now = new Date(); // 現在日時
                        SimpleDateFormat fileDate = new SimpleDateFormat("yyyyMMdd", Locale.JAPANESE);
                        SimpleDateFormat fileTime = new SimpleDateFormat("HHmmss", Locale.JAPANESE);
                        SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd", Locale.JAPANESE);
                        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", Locale.JAPANESE);

                        String header = "DATE,TIME,AX,AY,AZ,GX,GY,GZ,MX,MY,MZ\n";// ヘッダー部分
                        String fileName = id + "-" + fileDate.format(now)
                                             + "_" + fileTime.format(now) + "_log.csv";
                        // 保存先
                        String path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();
                        String file = path + "/" + fileName;

                        try {
                            StringBuilder str = new StringBuilder(header);
                            for(SensorData list:data){
                                BigDecimal[] kasoku = list.getKsk();
                                BigDecimal[] jairo = list.getGyro();
                                BigDecimal[] magne = list.getMgn();
                                // ログデータ内容
                                str.append(date.format(list.getLogDt()));
                                str.append(",").append(time.format(list.getLogDt()));
                                str.append(",").append(kasoku[SENSOR_PARAMETER_X]);
                                str.append(",").append(kasoku[SENSOR_PARAMETER_Y]);
                                str.append(",").append(kasoku[SENSOR_PARAMETER_Z]);
                                str.append(",").append(jairo[SENSOR_PARAMETER_X]);
                                str.append(",").append(jairo[SENSOR_PARAMETER_Y]);
                                str.append(",").append(jairo[SENSOR_PARAMETER_Z]);
                                str.append(",").append(magne[SENSOR_PARAMETER_X]);
                                str.append(",").append(magne[SENSOR_PARAMETER_Y]);
                                str.append(",").append(magne[SENSOR_PARAMETER_Z]);
                                str.append("\n");
                            }
                            FileOutputStream outputStream = new FileOutputStream(file);
                            // ログデータ出力
                            outputStream.write(str.toString().getBytes());
                            outputStream.close();
                        } catch(IOException e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        },csvStart,csvInterval);

        // タイマー処理
        // 通信
        Timer comTimer = new Timer(true);
        // LTE通信を使用してDBに登録
        comTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post( new Runnable(){
                    public void run(){
                        // DB登録
                        DbInsert dbInsert = new DbInsert();
                        dbInsert.execute();
                    }
                });
            }
        }, comStart, comInterval);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Listenerの登録解除
        manager.unregisterListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //センサーマネージャのリスナ登録破棄
        if (accSensorFlg || gyroSensorFlg || magSensorFlg) {
            manager.unregisterListener(this);
            accSensorFlg = false;
            gyroSensorFlg = false;
            magSensorFlg = false;
        }
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
            manager.registerListener(this, s, SensorManager.SENSOR_DELAY_UI); // 遅延無し
            accSensorFlg = true;
        }
        // ジャイロセンサー値が取得できている場合
        if(gyrSensors.size() > 0) {
            Sensor s = gyrSensors.get(0);
            manager.registerListener(this, s, SensorManager.SENSOR_DELAY_UI); // 遅延無し
            gyroSensorFlg = true;
        }
        // 磁力センサー値が取得できている場合
        if(mgnSensors.size() > 0) {
            Sensor s = mgnSensors.get(0);
            manager.registerListener(this, s, SensorManager.SENSOR_DELAY_UI); // 遅延無し
            magSensorFlg = true;
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
            accelText.setText(str);
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
            gyroText.setText(str);
        }
        // イベント内容：地磁気
        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            // 書き込み用
            mgn = event.values;
            // 画面表示
            String str = "地磁気センサー値:"
                    + "\nX軸:" + mgn[SENSOR_PARAMETER_X]
                    + "\nY軸:" + mgn[SENSOR_PARAMETER_Y]
                    + "\nZ軸:" + mgn[SENSOR_PARAMETER_Z];
            magneText.setText(str);
        }
    }
}
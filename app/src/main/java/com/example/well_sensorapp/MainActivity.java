package com.example.well_sensorapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.well_sensorapp.data.SensorData;
import com.example.well_sensorapp.data.SensorDataM;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final Integer START_DEFAULT = 1000; // センサー値蓄積開始時間
    private static final Integer INTERVAL_DEFAULT = 1000; // センサー値蓄積間隔(1秒)
    private static final int SENSOR_PARAMETER_X = 0; // センサー値：X軸
    private static final int SENSOR_PARAMETER_Y = 1; // センサー値：Y軸
    private static final int SENSOR_PARAMETER_Z = 2; // センサー値：Z軸
    private static final String API_URL = "https://pcw57.furonto.work/api/data";           // APIのURL
    // TODO: 10.0.2.2:8000をサーバのドメインに変更
//    private static final String API_URL = "http://10.0.2.2:8000/api/data";           // APIのURL
    private static final String API_METHOD = "POST";    // HttpMethod

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
     * API通信で使用する各センサーデータリスト
     */
    List<SensorDataM> dataM = new ArrayList<SensorDataM>();
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

    private CallApiTask callApiTask;

    // API
    private static class CallApiTask {
        ExecutorService executorService;
        List<SensorDataM> dataM;

        //　コンストラクタ
        public CallApiTask(List<SensorDataM> datas) {
            super();
            this.dataM = datas;

            executorService = Executors.newSingleThreadExecutor();
        }

        private class TaskRun implements Runnable {

            @Override
            public void run() {
                Log.d("TaskRun", "run()");
                String json = new Gson().toJson(dataM);
                try {
                    execSaveData(json);
                } catch (IOException e) {
                    Log.w("execSaveData", e.toString());
                    throw new RuntimeException(e);
                }
            }
        }

        void execute() {
            onPreExecute();
            executorService.submit(new TaskRun());
        }

        void onPreExecute() {
            Log.d("TaskRun", "onPreExecute()");
        }

        void onPostExecute(double number) {
            Log.d("TaskRun", "onPostExecute()");
        }

        // データを保存するAPI
        private void execSaveData(String jsonData) throws IOException {
            Log.d("execSaveData", "start");
            URL url = new URL(API_URL);  // TODO: APIのURL
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            // HttpURLConnectionの各種設定
            connection.setRequestMethod(API_METHOD);                                               // HTTPメソッドの設定
            connection.setDoInput(true);                                                           // リクエストボディへの書き込み許可
            connection.setDoOutput(true);                                                          // レスポンスボディの取得許可
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");      // リクエスト形式をJSONに指定

            // 接続の確立
            connection.connect();

            // リクエストとボディに情報を書き込み
            // HttpURLConnectionからOutputStream取得し、JSON文字列を書き込む
            PrintStream pStream = new PrintStream(connection.getOutputStream());
            pStream.print(jsonData);
            pStream.close();

            // レスポンス受け取り
            if (connection.getResponseCode() != 200) {
                // TODO: エラー処理
                Log.w("execSaveData", "Error");
            }

            // HttpURLConnectionからInputStreamを取得し、読み出す
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

            StringBuilder builder = new StringBuilder();
            String line;

            while((line = reader.readLine()) != null) {
                builder.append(line);
            }

            // InputStreamReaderを閉じる
            reader.close();

            // データを初期化
            dataM.clear();

            Log.d("execSaveData", "end");
        }
    }

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
        boolean isReadOnly = this.isExternalStorageReadOnly();
        Log.d("ExternalStorage isReadOnly", String.valueOf(isReadOnly));
        boolean isAvailable = this.isExternalStorageAvailable();
        Log.d("ExternalStorage isAvailable", String.valueOf(isAvailable));

        csvStart = 15 * 1000;    // スタート時間
        csvInterval = 15 * 1000; // 間隔時間（30秒）
        // LTE通信
        comStart = 60 * 100;    // スタート時間
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

                        // 日付の表示形式設定
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                        String dateStr = dateFormat.format(date).toString();

                        // 時間の表示形式設定
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                        String timeStr = timeFormat.format(date).toString();
                        
                        
                        // APIで利用するデータの蓄積処理
                        SensorDataM sensorDataM = new SensorDataM();
                        sensorDataM.setUserId(999999999);    // TODO:　ユーザのIDを入れること
                        sensorDataM.setLogDt(date);
                        sensorDataM.setDataDt(dateStr);
                        sensorDataM.setDataTime(timeStr);
                        sensorDataM.setKsk(ksk[0], ksk[1], ksk[2]);
                        sensorDataM.setGyro(gyro[0], gyro[1], gyro[2]);
                        sensorDataM.setMgn(mgn[0], mgn[1], mgn[2]);
                        dataM.add(sensorDataM);
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
                        Log.d("CSV", "CSVデータ出力開始");
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
//                        String path = "/storage/sdcard0/Download/covia_tmp";
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
                            Log.d("CSV", "CSVデータ出力終了");
                        } catch(IOException e){
                            e.printStackTrace();
                            Log.d("CSV", "CSVデータ出力失敗");
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
                         try{
                             Class.forName("com.mysql.jdbc.Driver");
                             Connection conn= DriverManager.getConnection("jdbc:mysql://133.242.152.41:3306/test_db","test_user_01","fsvrV@1+");
                             Statement stmt = conn.createStatement();
                             String sql = null;
                             for(SensorData list:data){
                                 sql = "insert into SensorDataM (user_id,sensor_data_dt,sensor_data_time,"
                                     + "sensor_data_accel_x,sensor_data_accel_y,sensor_data_accel_z,"
                                     + "sensor_data_gyro_x,sensor_data_gyro_y,sensor_data_gyro_z,"
                                     + "sensor_data_magne_x,sensor_data_magne_y,sensor_data_magne_z)"
                                     + "values ('"
                                     + "999999999," + list.getLogDt() + "," + list.getLogDt() + ","
                                     + list.getKsk()[0] + "," + list.getKsk()[1] + "," + list.getKsk()[2] + ","
                                     + list.getGyro()[0] + "," + list.getGyro()[1] + "," + list.getGyro()[2] + ","
                                     + list.getMgn()[0] + "," + list.getMgn()[1] + "," + list.getMgn()[2]
                                     + "')";
                             }
                             stmt.executeUpdate(sql);
                             stmt.close();
                             conn.close();
                         }catch(Exception e){

                         }
                     }
                 });
             }
         }, comStart, comInterval);

        // タイマー処理
        // API通信
        Timer apiTimer = new Timer(true);
        // LTE通信を使用してDBに登録
        apiTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                callApiTask = new CallApiTask(dataM);
                callApiTask.execute();
            }
        }, comStart, comInterval);

//        sdcard();
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
            manager.registerListener(this, s, SensorManager.SENSOR_DELAY_UI); // 遅延あり
            accSensorFlg = true;
        }
        // ジャイロセンサー値が取得できている場合
        if(gyrSensors.size() > 0) {
            Sensor s = gyrSensors.get(0);
            manager.registerListener(this, s, SensorManager.SENSOR_DELAY_UI); // 遅延あり
            gyroSensorFlg = true;
        }
        // 磁力センサー値が取得できている場合
        if(mgnSensors.size() > 0) {
            Sensor s = mgnSensors.get(0);
            manager.registerListener(this, s, SensorManager.SENSOR_DELAY_UI); // 遅延あり
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

    // 外部ストレージが読み取り専用かどうか
    private boolean isExternalStorageReadOnly() {
        String storageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(storageState)) {
            return true;
        } else {
            return false;
        }
    }
    // 外部ストレージが利用可能かどうか
    private boolean isExternalStorageAvailable() {
        String storageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(storageState)) {
            return true;
        } else {
            return false;
        }
    }

    private void sdcard() {
        StorageManager storageManager = (StorageManager) getSystemService(STORAGE_SERVICE);
        List<StorageVolume> storageVolumeList = storageManager.getStorageVolumes();
        StorageVolume storageVolume = storageVolumeList.get(1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            String path = storageVolume.getDirectory().getPath();
            Log.d("SDCard", path);
        }
    }
}
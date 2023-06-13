package com.example.sampledemoapply;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;
import android.os.Looper;

public class DbInsert {
    private class AsyncRunnable implements Runnable {
        private String result;
        Handler handler = new Handler(Looper.getMainLooper());
        @Override
        public void run() {
            onPreExecute();
            result = doInBackground();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onPostExecute(result);
                }
            });
        }
    }

    public void execute() {
        ExecutorService executorService  = Executors.newSingleThreadExecutor();
        executorService.submit(new AsyncRunnable());
    }

    void onPreExecute() {

    }

    String doInBackground() {
        // DB接続と書き込み
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn= DriverManager.getConnection("jdbc:mysql://133.242.152.41:3306/test_db","test_user_01","fsvrV@1+");
            Statement stmt = conn.createStatement();

            String sql = null;
//            for(SensorData list:data){
                sql = "insert into SensorDataM (user_id,sensor_data_dt,sensor_data_time,"
                    + "sensor_data_accel_x,sensor_data_accel_y,sensor_data_accel_z,"
                    + "sensor_data_gyro_x,sensor_data_gyro_y,sensor_data_gyro_z,"
                    + "sensor_data_magne_x,sensor_data_magne_y,sensor_data_magne_z)"
                    + "values ('"
                    + "999999999," + "2023/06/08,10:20:30,"
                    + "0.221,0.221,0.221,0.221,0.221,0.221,0.221,0.221,0.221"
                    + "')";
//            }

            stmt.executeUpdate(sql);
            stmt.close();
            conn.close();
        }catch(Exception e){

        }
        return null;
    }

    void onPostExecute(String result) {

    }
}
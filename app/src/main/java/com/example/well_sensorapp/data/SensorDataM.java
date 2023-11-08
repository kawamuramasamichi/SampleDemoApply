package com.example.well_sensorapp.data;

import java.math.BigDecimal;
import java.util.Date;

public class SensorDataM {

    /**
     * ユーザID
     */
    private int user_id;

    /**
     * データの日付
     */
    private String sensor_data_dt;

    /**
     * データの時間
     */
    private String sensor_data_time;

    /**
     * ログ日付
     */
    private Date logDt;
    /**
     * 加速度センサー値 X,Y,Z軸
     */
    private BigDecimal sensor_data_accel_x = new BigDecimal(0.0);
    private BigDecimal sensor_data_accel_y = new BigDecimal(0.0);
    private BigDecimal sensor_data_accel_z = new BigDecimal(0.0);
    /**
     * ジャイロセンサー値 X,Y,Z軸
     */
    private BigDecimal sensor_data_gyro_x = new BigDecimal(0.0);
    private BigDecimal sensor_data_gyro_y = new BigDecimal(0.0);
    private BigDecimal sensor_data_gyro_z = new BigDecimal(0.0);
    /**
     * 地磁気センサー値 X,Y,Z軸
     */
    private BigDecimal sensor_data_magne_x = new BigDecimal(0.0);
    private BigDecimal sensor_data_magne_y = new BigDecimal(0.0);
    private BigDecimal sensor_data_magne_z = new BigDecimal(0.0);

    public void setUserId(int user_id) { this.user_id = user_id; }
    public void setDataDt(String data_dt) { this.sensor_data_dt = data_dt; }
    public void setDataTime(String data_time) { this.sensor_data_time = data_time; }
    public void setLogDt(Date logDt){ this.logDt = logDt; }
    public void setKsk(float kskX, float kskY, float kskZ) {
        this.sensor_data_accel_x = convertBigDecimal(kskX);
        this.sensor_data_accel_y = convertBigDecimal(kskY);
        this.sensor_data_accel_z = convertBigDecimal(kskZ);
    }
    public void setGyro(float gyroX, float gyroY, float gyroZ) {
        this.sensor_data_gyro_x = convertBigDecimal(gyroX);
        this.sensor_data_gyro_y = convertBigDecimal(gyroY);
        this.sensor_data_gyro_z = convertBigDecimal(gyroZ);
    }
    public void setMgn(float mgnX, float mgnY, float mgnZ) {
        this.sensor_data_magne_x = convertBigDecimal(mgnX);
        this.sensor_data_magne_y = convertBigDecimal(mgnY);
        this.sensor_data_magne_z = convertBigDecimal(mgnZ);
    }

    public Date getLogDt() { return logDt; }
//    public BigDecimal[] getKsk() { return ksk; }
//    public BigDecimal[] getGyro() { return gyro; }
//    public BigDecimal[] getMgn() { return mgn; }

    private BigDecimal convertBigDecimal(float data) {
        BigDecimal ret = new BigDecimal(String.valueOf(data));
        return ret.setScale(3, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public String toString() {
        return "SensorDataM{" +
                "user_id='" + user_id + '\'' +
                ", sensor_data_dt=" + sensor_data_dt +'\'' +
                ", sensor_data_time=" + sensor_data_time +'\'' +
                ", sensor_data_accel_x=" + sensor_data_accel_x +'\'' +
                ", sensor_data_accel_y=" + sensor_data_accel_y +'\'' +
                ", sensor_data_accel_z=" + sensor_data_accel_z +'\'' +
                ", sensor_data_gyro_x=" + sensor_data_gyro_x +'\'' +
                ", sensor_data_gyro_y=" + sensor_data_gyro_y +'\'' +
                ", sensor_data_gyro_z=" + sensor_data_gyro_z +'\'' +
                ", sensor_data_magne_x=" + sensor_data_magne_x +'\'' +
                ", sensor_data_magne_y=" + sensor_data_magne_y +'\'' +
                ", sensor_data_magne_z=" + sensor_data_magne_z +'\'' +
                '}';
    }


}

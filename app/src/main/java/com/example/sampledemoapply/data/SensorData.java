package com.example.sampledemoapply.data;

import java.util.Date;

public class SensorData {

    /**
     * ログ日付
     */
    private Date logDt;
    /**
     * 加速度センサー値 X,Y,Z軸
     */
    private float[] ksk = new float[3];
    /**
     * ジャイロセンサー値 X,Y,Z軸
     */
    private float[] gyro = new float[3];
    /**
     * 地磁気センサー値 X,Y,Z軸
     */
    private float[] mgn = new float[3];

    public void setLogDt(Date logDt){ this.logDt = logDt; }
    public void setGyro(float[] gyro) { this.gyro = gyro; }
    public void setKsk(float[] ksk) { this.ksk = ksk; }
    public void setMgn(float[] mgn) { this.mgn = mgn; }

    public Date getLogDt() { return logDt; }
    public float[] getGyro() { return gyro; }
    public float[] getKsk() { return ksk; }
    public float[] getMgn() { return mgn; }
}

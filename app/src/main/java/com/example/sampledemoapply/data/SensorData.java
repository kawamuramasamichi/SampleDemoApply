package com.example.sampledemoapply.data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class SensorData {

    /**
     * ログ日付
     */
    private Date logDt;
    /**
     * 加速度センサー値 X,Y,Z軸
     */
    private BigDecimal[] ksk = new BigDecimal[3];
    /**
     * ジャイロセンサー値 X,Y,Z軸
     */
    private BigDecimal[] gyro = new BigDecimal[3];
    /**
     * 地磁気センサー値 X,Y,Z軸
     */
    private BigDecimal[] mgn = new BigDecimal[3];

    public void setLogDt(Date logDt){ this.logDt = logDt; }
    public void setKsk(float kskX, float kskY, float kskZ) {
        this.ksk[0] = convertBigDecimal(kskX);
        this.ksk[1] = convertBigDecimal(kskY);
        this.ksk[2] = convertBigDecimal(kskZ);
    }
    public void setGyro(float gyroX, float gyroY, float gyroZ) {
        this.gyro[0] = convertBigDecimal(gyroX);
        this.gyro[1] = convertBigDecimal(gyroY);
        this.gyro[2] = convertBigDecimal(gyroZ);
    }
    public void setMgn(float mgnX, float mgnY, float mgnZ) {
        this.mgn[0] = convertBigDecimal(mgnX);
        this.mgn[1] = convertBigDecimal(mgnY);
        this.mgn[2] = convertBigDecimal(mgnZ);
    }

    public Date getLogDt() { return logDt; }
    public BigDecimal[] getKsk() { return ksk; }
    public BigDecimal[] getGyro() { return gyro; }
    public BigDecimal[] getMgn() { return mgn; }

    private BigDecimal convertBigDecimal(float data) {
        BigDecimal ret = new BigDecimal(String.valueOf(data));
        return ret.setScale(3, BigDecimal.ROUND_HALF_UP);
    }
}

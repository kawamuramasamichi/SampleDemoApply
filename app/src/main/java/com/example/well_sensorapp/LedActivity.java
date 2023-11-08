package com.example.well_sensorapp;

import android.app.Activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LedActivity extends Activity {

    // 第一LET(赤) 点灯
    public void Led1_RedOn() {
        try {
            File file=new File("/sys/class/leds/led1-red/brightness");
            FileOutputStream fos=new FileOutputStream(file);
            fos.write(String.valueOf("255").getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 第一LED(赤)　消灯
    public void Led1_RedOff() {
        try {
            File file=new File("/sys/class/leds/led1-red/brightness");
            FileOutputStream fos=new FileOutputStream(file);
            fos.write(String.valueOf("0").getBytes());
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 第一LED(緑)　点灯
    public void Led1_GreenOn() {
        try {
            File file=new File("/sys/class/leds/led1-green/brightness");
            FileOutputStream fos=new FileOutputStream(file);
            fos.write(String.valueOf("255").getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 第一LED(緑)　消灯
    public void Led1_GreenOff() {
        try {
            File file=new File("/sys/class/leds/led1-green/brightness");
            FileOutputStream fos=new FileOutputStream(file);
            fos.write(String.valueOf("0").getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 第二LED(赤)　点灯
    public void Led2_RedOn() {
        try {
            File file=new File("/sys/class/leds/led2-red/brightness");
            FileOutputStream fos=new FileOutputStream(file);
            fos.write(String.valueOf("255").getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 第二LED(赤)　消灯
    public void Led2_RedOff() {
        try {
            File file=new File("/sys/class/leds/led2-red/brightness");
            FileOutputStream fos=new FileOutputStream(file);
            fos.write(String.valueOf("0").getBytes());
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 第二LED(緑)　発光
    public void Led2_GreenOn() {
        try {
            File file=new File("/sys/class/leds/led2-green/brightness");
            FileOutputStream fos=new FileOutputStream(file);
            fos.write(String.valueOf("255").getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 第二LED(緑)　消灯
    public void Led2_GreenOff() {
        try {
            File file=new File("/sys/class/leds/led2-green/brightness");
            FileOutputStream fos=new FileOutputStream(file);
            fos.write(String.valueOf("0").getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
package com.example.sampledemoapply;

import android.app.Activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LedActivity extends Activity {

    // Function: Set  "LED 1"  red diode light on
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

    // Function: Set  "LED 1"  red diode light off
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

    // Function: Set  "LED 1"  green diode light on
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

    // Function: Set  "LED 1"  green diode light off
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

    // Function: Set  "LED 2"  red diode light on
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

    // Function: Set  "LED 2"  red diode light off
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

    // Function: Set  "LED 2"  green diode light on
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

    // Function: Set  "LED 2"  green diode light off
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
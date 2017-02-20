package com.hqs.hqs_common_qprogress_android;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.hqs.common.helper.qprogress.QProgress;

public class MainActivity extends AppCompatActivity {

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void show1(View view){

        QProgress.Builder builder = new QProgress.Builder(this);
        builder.setCancelable(true)
                .setWheelColor(Color.BLUE)
                .setDismissOnTouch(true)
                .setDismissOnTouch(false);

        builder.create().show();

    }

    public void show2(View view){


        new Thread(new Runnable() {
            @Override
            public void run() {

                final QProgress qProgress = new QProgress.Builder(MainActivity.this)
                        .setCancelable(false)
                        .create();

                int p = 0;
                while (p <= 100){
                    final  int pp = p;

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            qProgress.show(pp, "正在下载");
                        }
                    });

                    p++;
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
                        qProgress.dismiss();
                    }
                });

            }
        }).start();

    }


}

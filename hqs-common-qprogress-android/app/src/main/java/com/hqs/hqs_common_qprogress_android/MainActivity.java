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

        QProgress.setCancelable(true);
        QProgress.setWheelColor(Color.BLUE);
        QProgress.setDismissOnTouch(true);
        QProgress.setDismissOnTouch(false);

        QProgress.show(this);

    }

    public void show2(View view){


        new Thread(new Runnable() {
            @Override
            public void run() {



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

                            QProgress.showProgress(MainActivity.this, pp, "正在下载");
                            QProgress.setCancelable(false);
                        }
                    });

                    p++;
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
                        QProgress.dismiss();
                    }
                });

            }
        }).start();

    }


}

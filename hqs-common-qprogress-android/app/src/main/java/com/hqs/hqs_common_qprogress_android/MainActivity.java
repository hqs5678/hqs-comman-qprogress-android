package com.hqs.hqs_common_qprogress_android;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.hqs.common.helper.qprogress.QProgress;
import com.hqs.common.utils.Log;

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
                        .setCancelable(true)
                        .create();
                qProgress.setOnProgressListener(new QProgress.OnProgressListener() {
                    @Override
                    public void onProgressShow() {
                        Log.print("onProgressShow");
                    }

                    @Override
                    public void onProgressCancel() {
                        Log.print("onProgressCancel");
                    }

                    @Override
                    public void onProgressDestroy() {
                        Log.print("onProgressDestroy");
                    }
                });
                int p = 0;
                while (p <= 10000){
                    final  int pp = p;

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            qProgress.show(pp/100, "正在下载");
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

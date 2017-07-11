package com.hqs.hqs_common_qprogress_android;

import android.content.res.Configuration;
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

    private QProgress progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void show1(View view){

        QProgress.Builder builder = new QProgress.Builder(this);
        builder.setCancelable(true)
                .setWheelColor(Color.BLUE)
                .setDismissOnTouch(true);

        progress = builder.create().show();

    }

    boolean isLoading = false;

    public void show2(View view){

        isLoading = true;

        new Thread(new Runnable() {
            @Override
            public void run() {

                progress = new QProgress.Builder(MainActivity.this)
                        .setCancelable(true)
                        .setDismissOnTouch(false)
                        .setOnProgressListener(new QProgress.OnProgressListener() {
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
                        })
                        .create();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progress.show();
                    }
                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int p = 0;
                while (p <= 1000 && isLoading){
                    final  int pp = p;

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            // 设置进度
                            progress.show(pp/10, "正在下载");
                        }
                    });

                    p++;
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                    }
                });

            }
        }).start();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (progress != null){
            progress.onConfigurationChanged(newConfig);
        }
    }


    @Override
    public void onBackPressed() {
        if (progress != null && progress.onBackPressed()){
            isLoading = false;
            return;
        }
        super.onBackPressed();
    }
}

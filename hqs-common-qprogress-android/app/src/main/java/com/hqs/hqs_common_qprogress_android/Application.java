package com.hqs.hqs_common_qprogress_android;


import com.squareup.leakcanary.LeakCanary;

/**
 * Created by apple on 16/9/27.
 */

public class Application extends android.app.Application {

    private static Application instance;

    public static Application getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;


        // 调试Android 内存泄露  在发布时移除!!!!
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        else{
            LeakCanary.install(this);
        }
    }


}

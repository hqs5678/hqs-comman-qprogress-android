package com.hqs.common.helper.qprogress;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.hqs.common.utils.StatusBarUtil;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.lang.ref.WeakReference;

/**
 * Created by super on 2017/2/8.
 */

public final class QProgress {

    private static QProgressParam progressParam;
    private static WeakReference<QProgressActivity> progressActivityReference;

    private static boolean isWorking = false;

    public static void show(Activity activity){
        if (progressActivityReference == null || progressActivityReference.get() == null) {
            Intent intent = new Intent(activity, QProgressActivity.class);

            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0);
        }
    }

    public static void showProgress(Activity activity, int progress, String preText) {
        if (progressActivityReference == null || progressActivityReference.get() == null) {
            if (isWorking){
                return;
            }
            isWorking = true;
            Intent intent = new Intent(activity, QProgressActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString("preText", preText);
            bundle.putInt("progress", progress);
            intent.putExtras(bundle);

            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0);
        }
        else{
            QProgressActivity progressActivity = progressActivityReference.get();
            progressActivity.updateProgress(progress, preText);
        }
    }

    public static void dismiss(){
        if (progressActivityReference != null){
            QProgressActivity activity = progressActivityReference.get();
            if (activity != null) {
                activity.onFinish();
            }
        }
    }

    public static void setWheelColor(int wheelColor) {
        if (progressParam == null){
            progressParam = new QProgressParam();
        }
        progressParam.wheelColor = wheelColor;
    }

    public static void setCancelable(boolean cancelable) {
        if (progressParam == null){
            progressParam = new QProgressParam();
        }
        progressParam.cancelable = cancelable;
    }

    public static void setDismissOnTouch(boolean dismissOnTouch) {
        if (progressParam == null){
            progressParam = new QProgressParam();
        }
        progressParam.dismissOnTouch = dismissOnTouch;
    }

    public static void setWheelBackgroundColor(int wheelBackgroundColor) {
        if (progressParam == null){
            progressParam = new QProgressParam();
        }
        progressParam.wheelBackgroundColor = wheelBackgroundColor;
    }

    public static void setProgressBarBackgroundColor(int progressBarBackgroundColor) {
        if (progressParam == null){
            progressParam = new QProgressParam();
        }
        progressParam.progressBarBackgroundColor = progressBarBackgroundColor;
    }

    public static void setProgressBarTintColor(int progressBarTintColor) {
        if (progressParam == null){
            progressParam = new QProgressParam();
        }
        progressParam.progressBarTintColor = progressBarTintColor;
    }

    public static class QProgressActivity extends Activity{

        private RelativeLayout contentView;
        private CardView bgView;
        private ProgressWheel progressWheel;
        private int progress;
        private String preText;
        private CircleProgress circleProgress;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            StatusBarUtil.transparencyBar(this);

            progressActivityReference = new WeakReference<QProgressActivity>(this);
            if (progressParam == null){
                progressParam = new QProgressParam();
            }
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                progress = bundle.getInt("progress");
                preText = bundle.getString("preText");

                contentView = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.q_progress_circle_layout, null);

                this.circleProgress = (CircleProgress) contentView.findViewById(R.id.q_circle_progress);
                circleProgress.setUnfinishedColor(progressParam.progressBarBackgroundColor);
                circleProgress.setFinishedColor(progressParam.progressBarTintColor);
                circleProgress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                this.circleProgress.setProgress(progress);
                circleProgress.setPrefixText(preText);
            }
            else {
                contentView = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.q_progress_layout, null);

                progressWheel = (ProgressWheel) contentView.findViewById(R.id.q_progress_wheel);
                progressWheel.setBarColor(progressParam.wheelColor);
                progressWheel.spin();

                bgView = (CardView) contentView.findViewById(R.id.q_progress_bg_view);
                bgView.setCardBackgroundColor(progressParam.wheelBackgroundColor);
                bgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }


            setContentView(contentView);
            contentView.setEnabled(false);

            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (progressParam.dismissOnTouch && progressParam.cancelable){
                        onFinish();
                    }
                }
            });

            Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            animation.setDuration(100);
            animation.setFillAfter(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    contentView.setEnabled(true);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            contentView.setAnimation(animation);

        }

        private void onFinish(){

            contentView.setEnabled(false);
            Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
            animation.setDuration(50);
            animation.setFillAfter(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    finish();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            contentView.clearAnimation();
            contentView.setAnimation(animation);
        }

        private void updateProgress(int progress, String preText){
            this.progress = progress;
            this.preText = preText;

            circleProgress.setProgress(progress);
            circleProgress.setPrefixText(preText);
        }


        @Override
        public void finish() {
            super.finish();
            overridePendingTransition(0, 0);
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();

            progressParam = null;
            progressActivityReference.clear();
            progressActivityReference = null;
            isWorking = false;
        }

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && progressParam.cancelable){
                if (contentView.isEnabled()){
                    contentView.setEnabled(false);
                    onFinish();
                }
            }
            return true;
        }


    }

    private static class QProgressParam{
        private int wheelColor = Color.BLUE;
        private int wheelBackgroundColor = Color.rgb(240, 240, 240);
        private int progressBarBackgroundColor = Color.rgb(240, 240, 240);
        private int progressBarTintColor = Color.rgb(54, 185, 248);
        private boolean cancelable = true;
        private boolean dismissOnTouch = false;
    }
}

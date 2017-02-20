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
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.hqs.common.utils.StatusBarUtil;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.io.Serializable;
import java.lang.ref.WeakReference;

/**
 * Created by super on 2017/2/8.
 */

public final class QProgress {

    private QProgressParam param;
    private WeakReference<Activity> activityReference;

    private boolean isWorking = false;
    private static OnProgressDismissCallBack onProgressDismissCallBack;

    private QProgress(Activity activity, QProgressParam param){
        this.param = param;
        this.activityReference = new WeakReference<Activity>(activity);
    }

    public void show(){
        if (QProgressActivity.progressActivityReference == null || QProgressActivity.progressActivityReference.get() == null) {
            Activity activity = this.activityReference.get();
            if (activity != null){
                Intent intent = new Intent(activity, QProgressActivity.class);

                intent.putExtra("progressParam", param);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
            }
        }
    }

    public void show(int progress, String preText) {
        if (QProgressActivity.progressActivityReference == null || QProgressActivity.progressActivityReference.get() == null) {
            if (isWorking){
                return;
            }
            Activity activity = this.activityReference.get();
            if (activity != null){
                isWorking = true;
                Intent intent = new Intent(activity, QProgressActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("preText", preText);
                bundle.putInt("progress", progress);
                bundle.putSerializable("progressParam", param);
                intent.putExtras(bundle);

                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
            }
        }
        else{
            QProgressActivity progressActivity = QProgressActivity.progressActivityReference.get();
            progressActivity.updateProgress(progress, preText);
        }
    }

    public void setOnProgressDismissCallBack(OnProgressDismissCallBack onProgressDismissCallBack) {
        QProgress.onProgressDismissCallBack = onProgressDismissCallBack;
    }

    public void dismiss(){
        if (QProgressActivity.progressActivityReference != null){
            QProgressActivity activity = QProgressActivity.progressActivityReference.get();
            if (activity != null) {
                activity.onFinish();
            }
            this.param = null;
            if (this.activityReference != null) {
                this.activityReference.clear();
                this.activityReference = null;
            }
        }
    }

    public static class Builder{

        private QProgressParam progressParam;
        private Activity activity;
        public Builder(Activity activity){
            this.activity = activity;
            this.progressParam = new QProgressParam();
        }

        public Builder setWheelColor(int wheelColor) {
            progressParam.wheelColor = wheelColor;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            progressParam.cancelable = cancelable;
            return this;
        }

        public Builder setDismissOnTouch(boolean dismissOnTouch) {
            progressParam.dismissOnTouch = dismissOnTouch;
            return this;
        }

        public Builder setWheelBackgroundColor(int wheelBackgroundColor) {
            progressParam.wheelBackgroundColor = wheelBackgroundColor;
            return this;
        }

        public Builder setProgressBarBackgroundColor(int progressBarBackgroundColor) {
            progressParam.progressBarBackgroundColor = progressBarBackgroundColor;
            return this;
        }

        public Builder setProgressBarTintColor(int progressBarTintColor) {
            progressParam.progressBarTintColor = progressBarTintColor;
            return this;
        }

        public QProgress create(){

            QProgress qProgress = new QProgress(this.activity, progressParam);
            this.activity = null;
            this.progressParam = null;
            return qProgress;
        }
    }

    public static class QProgressActivity extends Activity{

        private QProgressParam progressParam;
        private RelativeLayout contentView;
        private CardView bgView;
        private ProgressWheel progressWheel;
        private int progress;
        private String preText;
        private CircleProgress circleProgress;

        private static WeakReference<QProgressActivity> progressActivityReference;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            StatusBarUtil.transparencyBar(this);

            progressActivityReference = new WeakReference<QProgressActivity>(this);

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {

                progressParam = (QProgressParam) bundle.getSerializable("progressParam");
                if (progressParam == null) {
                    progressParam = new QProgressParam();
                }
                progress = bundle.getInt("progress");
                preText = bundle.getString("preText");

                if (preText != null) {
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
                } else {
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
            }
            else{
                Toast.makeText(this, "Progress Wrong!!!", Toast.LENGTH_SHORT).show();
                return;
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
            if (progressActivityReference != null){
                progressActivityReference.clear();
                progressActivityReference = null;
            }
            if (onProgressDismissCallBack != null) {
                onProgressDismissCallBack.onProgressDismiss();
                onProgressDismissCallBack = null;
            }
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

    public interface OnProgressDismissCallBack {
        void onProgressDismiss();
    }

    private static class QProgressParam implements Serializable{
        private int wheelColor = Color.BLUE;
        private int wheelBackgroundColor = Color.rgb(240, 240, 240);
        private int progressBarBackgroundColor = Color.rgb(240, 240, 240);
        private int progressBarTintColor = Color.rgb(54, 185, 248);
        private boolean cancelable = true;
        private boolean dismissOnTouch = false;
    }
}

package com.hqs.common.helper.qprogress;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.hqs.common.utils.ScreenUtils;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.io.Serializable;
import java.lang.ref.WeakReference;

/**
 * Created by super on 2017/2/8.
 */

public final class QProgress {

    private QProgressParam param;
    private WeakReference<Activity> activityReference;
    private boolean isShowing = false;
    private QProgressComponent progressComponent;


    private QProgress(Activity activity, QProgressParam param){
        this.param = param;
        this.activityReference = new WeakReference<>(activity);
    }

    public QProgress show(){
        if (progressComponent == null || !isShowing) {
            Activity activity = this.activityReference.get();
            progressComponent = new QProgressComponent(activity, param);
            isShowing = true;
        }
        return this;
    }

    public QProgress show(int progress, String preText) {
        if (progress < 0){
            Activity activity = this.activityReference.get();
            Toast.makeText(activity, "progress should be greater than zero", Toast.LENGTH_LONG).show();
            return this;
        }
        if (progressComponent == null){
            Activity activity = this.activityReference.get();
            progressComponent = new QProgressComponent(activity, param);
        }
        progressComponent.updateProgress(progress, preText);
        isShowing = true;

        return this;
    }

    public void dismiss(){
        if (progressComponent != null){
            progressComponent.preFinish();
            progressComponent = null;
            param = null;
            activityReference = null;
            isShowing = false;
        }
    }

    // 添加返回按钮点击事件
    // 需要在调用者的activity中调用
    public boolean onBackPressed() {
        if (param != null && param.cancelable && progressComponent.contentView.isEnabled()){
            progressComponent.contentView.setEnabled(false);
            progressComponent.preFinish();
            return true;
        }
        return false;
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

        public Builder setOnProgressListener(OnProgressListener onProgressListener) {
            progressParam.onProgressListener = onProgressListener;
            return this;
        }

        public QProgress create(){

            QProgress qProgress = new QProgress(this.activity, progressParam);
            this.activity = null;
            this.progressParam = null;
            return qProgress;
        }
    }

    public class QProgressComponent{

        private ViewGroup parent;
        private QProgressParam progressParam;
        private RootView contentView;
        private CardView bgView;
        private ProgressWheel progressWheel;
        private int progress = -1;
        private String preText;
        private CircleProgress circleProgress;
        private Context context;
        private OnProgressListener onProgressListener;

        public QProgressComponent(Activity activity, QProgressParam progressParam) {
            this.context = activity;
            this.onProgressListener = progressParam.onProgressListener;
            this.progressParam = progressParam;
            this.parent = getRootView(activity);

            if (parent == null){
                Toast.makeText(activity, "fatal error occurred", Toast.LENGTH_SHORT).show();
                return;
            }

            init();
            enterAnim();
        }

        // 获取activity的root view
        private ViewGroup getRootView(Activity context) {
            return (ViewGroup) ((ViewGroup)context.findViewById(android.R.id.content)).getChildAt(0);
        }

        private void init(){

            if (progress >= 0) {
                contentView = (RootView) LayoutInflater.from(context).inflate(R.layout.q_progress_circle_layout, null);

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
                contentView = (RootView) LayoutInflater.from(context).inflate(R.layout.q_progress_layout, null);

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
            contentView.setEnabled(false);
            contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            float h = ScreenUtils.screenH(context);
            int parentH = parent.getHeight();
            contentView.setY((parentH - h) * 0.5f);

            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (progressParam.dismissOnTouch && progressParam.cancelable){
                        preFinish();
                    }
                }
            });

            contentView.setOnDetachedFromWindow(new Runnable() {
                @Override
                public void run() {
                    destroy();
                }
            });

            parent.addView(contentView);
        }

        private void enterAnim(){
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
            animation.setDuration(100);
            animation.setFillAfter(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    contentView.setEnabled(true);
                    if (onProgressListener != null) {
                        onProgressListener.onProgressShow();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            contentView.setAnimation(animation);
        }


        private void preFinish(){

            if (onProgressListener != null) {
                onProgressListener.onProgressCancel();
            }

            contentView.setEnabled(false);
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
            animation.setDuration(50);
            animation.setFillAfter(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    destroy();
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

            if (circleProgress == null){
                parent.removeView(contentView);
                init();
            }
            circleProgress.setProgress(progress);
            circleProgress.setPrefixText(preText);
        }

        private void destroy() {
            if (onProgressListener != null) {
                onProgressListener.onProgressDestroy();
            }
        }
    }

    public interface OnProgressListener {
        void onProgressShow();
        void onProgressCancel();
        void onProgressDestroy();
    }

    private static class QProgressParam implements Serializable{
        private int wheelColor = Color.BLUE;
        private int wheelBackgroundColor = Color.rgb(240, 240, 240);
        private int progressBarBackgroundColor = Color.rgb(240, 240, 240);
        private int progressBarTintColor = Color.rgb(54, 185, 248);
        private boolean cancelable = true;
        private boolean dismissOnTouch = false;
        private OnProgressListener onProgressListener;
    }
}

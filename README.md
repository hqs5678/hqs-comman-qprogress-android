# hqs-comman-qprogress
封装进度条, 支持横屏模式, 支持横竖屏切换.

#### 运行效果
![运行效果图](https://github.com/hqs5678/hqs-comman-qprogress-android/blob/master/2017-07-11%2017_26_07.gif)



### 添加到项目

#### gradle

```

dependencies {
    compile 'com.hqs.common.helper.qprogress:qprogress:1.0.9'
}

```

### 使用说明

##### 快速使用, 创建并显示进度条
```
QProgress.Builder builder = new QProgress.Builder(this)
                .setCancelable(true)
                .setWheelColor(Color.BLUE)
                .setDismissOnTouch(true);

progress = builder.create().show();

```

##### 创建并设置监听

```

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
    .create()
    .show();

```


#### 设置横竖屏切换支持
> 支持横竖屏切换需要在做如下操作
##### 1. 为activity 设置监听手机屏幕横竖翻转的权限
> 在AndroidManifest.xml文件中添加如下代码, 以MainActivity为例, 为MainActivity 添加 configChanges属性


```
<uses-permission android:name="android.permission.CHANGE_CONFIGURATION"/>

...
    <activity android:name=".MainActivity" android:configChanges="orientation|screenSize">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />

            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>

...

```
##### 2. 在activity中重写onConfigurationChanged 函数, 例如:
```
@Override
public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);

    // 添加如下代码
    if (progress != null){
        progress.onConfigurationChanged(newConfig);
    }

    // do your own things
    // ...
}
```

#### 设置返回按钮事件
> 这个步骤不能省, 如果忘记本步骤, 会造成点击返回按钮时整个activity都返回或调起用户自定义的其他事件, 具体内容可以参考如下代码

```

@Override
public void onBackPressed() {
    if (progress != null && progress.onBackPressed()){
        // do your own things
        // ...
    }
    else{
        super.onBackPressed();
    }
}

```


##### 自定义进度条样式(Build中的方法)

1. setWheelColor  设置进度条圆圈颜色
1. setWheelBackgroundColor 设置背景颜色
1. setProgressBarBackgroundColor  设置圆形进度条背景颜色
1. setProgressBarTintColor 设置已完成部分的颜色
1. setCancelable  设置是否能够取消本次(点击返回按钮)
1. setDismissOnTouch  设置点击空白的地方消失
1. setOnProgressListener  设置监听进度条

##### 其他方式(res中文件)
> 覆盖资源文件中的属性的值修改样式, res文件主要内容如下:

```

color.xml

<!--进度条背景颜色-->
<color name="q_progressBackgroundColor">@android:color/darker_gray</color>

<!--圆形进度条字体颜色-->
<color name="q_progressTextColor">#402e2e</color>


dimens.xml

<!--圆形进度条背景大小-->
<dimen name="q_circleProgressBgWH">110dp</dimen>
<!--圆形进度条的圆角大小  即 q_circleProgressBgWH * 0.5 -->
<dimen name="q_circleProgressBgWHHalf">55dp</dimen>

<!--进度条圆角大小-->
<dimen name="q_progressCornerRadius">10dp</dimen>

<!--圆形进度条标题字体的大小-->
<dimen name="q_progressTextSize">14sp</dimen>

```

##### 具体使用请使用查看Demo

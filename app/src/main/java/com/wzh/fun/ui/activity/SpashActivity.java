package com.wzh.fun.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.wzh.fun.R;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SpashActivity extends BaseActivity {


    private static final int ANIMATION_DURATION = 2000;
    private static final float SCALE_END = 1.13F;

//    private static final int[] SPLASH_ARRAY = {
//            R.drawable.splash1
//    };


    ImageView mIvSplash;


    TextView mSplashAppName;

    TextView Version;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spashl);

        mSplashAppName = (TextView) findViewById(R.id.splash_app_name);
        Version = (TextView) findViewById(R.id.Version);
        mIvSplash = (ImageView) findViewById(R.id.iv_splash);
        Version.setText("V"+getVersion());
        animateImage();
    }

    private void animateImage() {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(mIvSplash, "scaleX", 0.5f, SCALE_END);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(mIvSplash, "scaleY", 0.5f, SCALE_END);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(ANIMATION_DURATION);
        set.play(animatorX).with(animatorY);
        set.start();

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                MainActivity.start(SpashActivity.this);
                SpashActivity.this.finish();
            }
        });
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager =SpashActivity.this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(SpashActivity.this.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}

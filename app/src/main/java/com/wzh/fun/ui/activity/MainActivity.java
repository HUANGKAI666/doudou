package com.wzh.fun.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.wzh.fun.R;
import com.wzh.fun.entity.WeatherEntity;
import com.wzh.fun.event.LoginSuccessdEvent;
import com.wzh.fun.http.ApiResponseWraperNoList;
import com.wzh.fun.ui.fragment.NewImageJokeFragment;
import com.wzh.fun.ui.fragment.NewTextJokeFragment;
import com.wzh.fun.ui.fragment.circleFragment.CircleFragment;
import com.wzh.fun.ui.fragment.circleFragment.FollowFragment;
import com.wzh.fun.view.CircleImageView;
import com.wzh.fun.view.ShowDialog;
import com.wzh.fun.view.tab.BarEntity;
import com.wzh.fun.view.tab.BottomTabBar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;

import static android.R.attr.path;

/**
 * Created by zhihao.wen on 2016/11/4.
 */

public class MainActivity extends BaseActivity implements BottomTabBar.OnSelectListener, View.OnClickListener{
    private BottomTabBar tb ;
    private List<BarEntity> bars ;
    private NewTextJokeFragment textJokeFragment ;
    private   NewImageJokeFragment  newimageJokeFragment ;
    private CircleFragment circleFragment ;
    private FragmentManager manager ;
    private Subscription sb ;
    private Observer<ApiResponseWraperNoList<WeatherEntity>> obXW ;
    private ShowDialog dialog ;
    private View loginOut ;
    private TextView nameView;
    private CircleImageView avatar ;
    private TextView cacheSizeTv ;
    private String cacheSize ;
    private TextView versionCode ;
    private LinearLayout mean;
    private TextView fans,focus,findUser;
    private LinearLayout mUser;
    private long exitTime = 0;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_tab_main);
        initView();
        inintMean();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUser();
    }
    private void initView() {
        manager = getSupportFragmentManager();
        tb = (BottomTabBar) findViewById(R.id.tb);
        mUser = (LinearLayout) findViewById(R.id.User);
        tb.setManager(manager);
        tb.setOnSelectListener(this);
        bars = new ArrayList<>();
        bars.add(new BarEntity("笑话",R.drawable.ic_textjoke_select,R.drawable.ic_textjoke_unselect));
        bars.add(new BarEntity("趣图",R.drawable.ic_imagejoke_select,R.drawable.ic_imagejoke_unselect));
        bars.add(new BarEntity("朋友圈",R.drawable.ic_dt_select,R.drawable.ic_dt_unselect));
        tb.setBars(bars);
    }

    @Override
    public void onSelect(int position) {
        switch (position){
            case 0:
                if (textJokeFragment==null){
                    textJokeFragment = new NewTextJokeFragment();
                }
                tb.switchContent(textJokeFragment);
                break;
            case 1:
                if (newimageJokeFragment==null){
                    newimageJokeFragment = new NewImageJokeFragment();
                }
                tb.switchContent(newimageJokeFragment);
                break;
            case 2:
                if (circleFragment==null){
                    circleFragment = new CircleFragment();
                }
                tb.switchContent(circleFragment);
                break;
            default:
                break;
        }
    }
    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, MainActivity.class));
    }


    private void inintMean() {
        focus = (TextView) findViewById(R.id.focus);
        focus.setOnClickListener(this);
        fans = (TextView) findViewById(R.id.fans);
        fans.setOnClickListener(this);
        findUser = (TextView) findViewById(R.id.find);
        findUser.setOnClickListener(this);
        dialog = new ShowDialog();
        loginOut =  findViewById(R.id.login_out) ;
        loginOut.setOnClickListener(this);
        mean = (LinearLayout) findViewById(R.id.mean);
        versionCode = (TextView) findViewById(R.id.versionCode);
        versionCode.setText("V "+getVersion());


        findViewById(R.id.suggest).setOnClickListener(this);
        nameView = (TextView) findViewById(R.id.nameView);
        avatar = (CircleImageView) findViewById(R.id.avatar);
        avatar.setOnClickListener(this);
        initData();

    }

    private void initData() {

//        obXW = new Observer<ApiResponseWraperNoList<WeatherEntity>>() {
//            @Override
//            public void onCompleted() {
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                showErrorView("数据加载失败,点击重试",R.drawable.ic_error);
//            }
//
//            @Override
//            public void onNext(ApiResponseWraperNoList<WeatherEntity> xwEntity) {
//                showContentView();
//                Log.e("main","==="+xwEntity.getResult().toString());
//            }
//        };
//        request(true);
    }
//
//
//    public void request(boolean isRefresh) {
//        if (isRefresh){
//            showLoadingPage("正在加载中...",R.drawable.ic_loading);
//        }
//        RequestParam param = new RequestParam();
//        param.put("key", Constant.WKEY);
//        param.put("cityname","北京");
//        sb = NetWorkUtil.getWeatherApi()
//                .getWeather(param)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(obXW);
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.login_out:
                dialog.show(MainActivity.this, "", "是否确认注销？", new ShowDialog.OnBottomClickListener() {
                    @Override
                    public void positive() {
                        mUser.setVisibility(View.GONE);
                        loginOut.setVisibility(View.GONE);
                        nameView.setText("请登录");
                        avatar.setImageResource(R.drawable.default_head);
                        AVUser.getCurrentUser().logOut();
                        EventBus.getDefault().post(new LoginSuccessdEvent());
                    }
                    @Override
                    public void negtive() {

                    }
                });
                break;

            case  R.id.avatar:
                if (AVUser.getCurrentUser()==null){
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }else {
                    com.wzh.fun.ui.fragment.circleFragment.PersonFragment.go(MainActivity.this, AVUser.getCurrentUser());
                }
                break;

            case  R.id.suggest:
                startActivity(new Intent(this, SuggestActivity.class));
                break;

            case  R.id.find:
                startActivity(new Intent(MainActivity.this,FindUserActivity.class));
                break;

            case  R.id.fans:
                Intent intent = new Intent(MainActivity.this,FollowActivity.class) ;
            intent.putExtra(FollowFragment.TYPE,FollowFragment.TYPE_FOLLOWER);
            startActivity(intent);
                break;
            case  R.id.focus:
                Intent intent2 = new Intent(MainActivity.this,FollowActivity.class) ;
            intent2.putExtra(FollowFragment.TYPE,FollowFragment.TYPE_FOLLOWING);
            startActivity(intent2);
                break;

            default:
                break;
        }
    }
    public void checkUser(){
        AVUser user = AVUser.getCurrentUser() ;
        if(user==null){
            mUser.setVisibility(View.GONE);
            loginOut.setVisibility(View.GONE);
            nameView.setText("请登录");
            avatar.setImageResource(R.drawable.default_head);
            return;
        }
//            同步更新
            user.fetchInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject avObject, AVException e) {
                    AVUser u = (AVUser) avObject;
                    if (u == null) {
                        avatar.setImageResource(R.drawable.default_head);
                        nameView.setText("请登录");
                        mUser.setVisibility(View.GONE);
                        loginOut.setVisibility(View.GONE);
                    } else {
                        nameView.setText(u.getUsername());
                        loginOut.setVisibility(View.VISIBLE);
                        mUser.setVisibility(View.VISIBLE);
                        if (u.getAVFile("avatar") !=null) {
                            String path = u.getAVFile("avatar").getUrl();
                            if (!TextUtils.isEmpty(path)) {
                                Glide.with(MainActivity.this).load(path).placeholder(R.drawable.default_head).into(avatar);
                            } else {
                                avatar.setImageResource(R.drawable.default_head);
                            }

                        } else {
                            avatar.setImageResource(R.drawable.default_head);
                        }

                    }

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
            PackageManager manager =MainActivity.this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(MainActivity.this.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

}

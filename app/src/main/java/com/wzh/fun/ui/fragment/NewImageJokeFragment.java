package com.wzh.fun.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.bumptech.glide.Glide;
import com.wzh.fun.R;
import com.wzh.fun.ui.activity.MainActivity;
import com.wzh.fun.ui.adapter.ImageJokeAdapter;
import com.wzh.fun.ui.activity.ImageLookActivity;
import com.wzh.fun.entity.ImageJokeEntity;
import com.wzh.fun.http.ApiResponseWraperNoData;
import com.wzh.fun.http.NetWorkUtil;
import com.wzh.fun.http.RequestParam;
import com.wzh.fun.utils.Constant;
import com.wzh.fun.view.CircleImageView;
import com.wzh.fun.view.listview.CustomPtrFrameLayout;
import com.wzh.fun.view.listview.LoadMoreListView;
import com.wzh.fun.view.listview.PtrDefaultHandler;
import com.wzh.fun.view.listview.PtrFrameLayout;

import java.util.ArrayList;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.R.attr.action;
import static com.wzh.fun.R.id.avatar;
import static com.wzh.fun.R.id.nameView;


public class NewImageJokeFragment extends BaseFragment implements BaseFragment.OnReloadDataListener{
    private CustomPtrFrameLayout customPtrFrameLayout ;
    private LoadMoreListView listView ;
    private Subscription sb ;
    private ImageJokeAdapter adapter ;
    private ArrayList<ImageJokeEntity> imageJokeEntities ;
    private Observer<ApiResponseWraperNoData<ImageJokeEntity>> obXW ;
    private int currentPage = 1 ;
    private CircleImageView headView;
    private int pagetSize =10 ;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
         setContentView(R.layout.fragment_new_image_joke);
        setTitle("趣图");
//        setHeadView();
         initView();
         initData();
//        setHeadImage();
         return getContentView() ;
    }

    private void setHeadImage() {
        headView = (CircleImageView) findViewById(R.id.toolbar_left_head);
        AVUser user = AVUser.getCurrentUser() ;
        if(user==null){
            headView.setImageResource(R.drawable.default_head);
            return;
        }
        user.fetchInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                AVUser u = (AVUser) avObject;
                Log.d("headimage", "done1: ");
                if (u == null) {
                    Log.d("headimage", "done2: ");
                    headView.setImageResource(R.drawable.default_head);
                } else {
                    Log.d("headimage", "done3: ");
                    if (u.getAVFile("avatar") !=null) {
                        String path = u.getAVFile("avatar").getUrl();
                        if (!TextUtils.isEmpty(path)) {
                            Log.d("headimage", "done4: ");
                            Glide.with(NewImageJokeFragment.this).load(path).placeholder(R.drawable.default_head).into(headView);
                            Log.d("headimage", ""+path);
                        } else {
                            headView.setImageResource(R.drawable.default_head);
                        }

                    } else {
                        headView.setImageResource(R.drawable.default_head);
                    }

                }

            }
        });

    }

    private void initData() {
        obXW = new Observer<ApiResponseWraperNoData<ImageJokeEntity>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                showErrorView("加载失败,点击重试",R.drawable.error_place);
            }

            @Override
            public void onNext(ApiResponseWraperNoData<ImageJokeEntity> xwEntity) {
                if (currentPage==1){
                    imageJokeEntities.clear();
                }
                imageJokeEntities.addAll(xwEntity.getResult());
                adapter.notifyDataSetChanged();
                customPtrFrameLayout.refreshComplete();
                listView.getMoreComplete();
                showContentView();
                if (xwEntity.getResult().size()<pagetSize){
                    listView.setNoMore();
                }
                Log.e("main","==="+xwEntity.getResult().toString());
            }
        };
        request(true);
    }


    private void initView() {

        listView = (LoadMoreListView) findViewById(R.id.list);
        customPtrFrameLayout = (CustomPtrFrameLayout) findViewById(R.id.contentView);
        customPtrFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                currentPage=1 ;
                request(false);
            }
        });
        imageJokeEntities = new ArrayList<>();
        listView.setOnGetMoreListener(new LoadMoreListView.OnGetMoreListener() {
            @Override
            public void onGetMore() {
                currentPage++;
                request(false);
            }
        });
        adapter = new ImageJokeAdapter(getActivity(),imageJokeEntities);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ImageLookActivity.class);
                intent.putExtra("url",adapter.getItem(i).getUrl());
                startActivity(intent);
            }
        });
        listView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        if (event.getX()>200){
                            listView.getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        else {
                            listView.getParent().requestDisallowInterceptTouchEvent(false);
                        }

                }
                return false;
            }
        });
        setOnReloadDataListener(this);
    }

    @Override
    public void request(boolean isRefresh) {
        if (isRefresh){
            showLoadingPage("正在加载中...",R.drawable.ic_loading);
        }
        RequestParam param = new RequestParam();
        param.put("key", Constant.KEY);
        param.put("pagesize",pagetSize);
        param.put("page",currentPage);
        param.put("type","pic");
        sb = NetWorkUtil.getImageJokeApi()
                .getImageJoke(param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(obXW);
    }
}

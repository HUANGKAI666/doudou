package com.wzh.fun.ui.fragment.circleFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVStatus;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.wzh.fun.R;
import com.wzh.fun.utils.CircleServerUtils;
import com.wzh.fun.ui.activity.ImageLookActivity;
import com.wzh.fun.ui.activity.StatusSendActivity;
import com.wzh.fun.ui.adapter.StatusListAdapter;
import com.wzh.fun.entity.Status;
import com.wzh.fun.event.LoginSuccessdEvent;
import com.wzh.fun.ui.fragment.BaseFragment;
import com.wzh.fun.view.CircleImageView;
import com.wzh.fun.view.Menu;
import com.wzh.fun.view.listview.CustomPtrFrameLayout;
import com.wzh.fun.view.listview.LoadMoreListView;
import com.wzh.fun.view.listview.PtrDefaultHandler;
import com.wzh.fun.view.listview.PtrFrameLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

import static android.app.Activity.RESULT_OK;
import static com.wzh.fun.R.id.avatar;
import static com.wzh.fun.R.id.nameView;


public class CircleFragment extends BaseFragment implements View.OnClickListener {
    private StatusListAdapter adapter;
    private LoadMoreListView list;
    private CustomPtrFrameLayout customPtrFrameLayout;
    private Observable observable;
    private Subscriber subscriber;
    private long maxId = 0;
    private int limit = 15;
    int skip = 0;
    private static final int SEND_REQUEST = 2;
    private Menu menu;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.fragment_main_circle);
        EventBus.getDefault().register(this);
        setTitle("朋友圈");
//        setHeadView();
        initSend();
        initView();

        return getContentView();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void initSend() {
        if (AVUser.getCurrentUser()!=null){
        setRightImage(R.drawable.send, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), StatusSendActivity.class);
                startActivityForResult(intent, SEND_REQUEST);
            }
        });
        }
    }

    private void initView() {
        if (AVUser.getCurrentUser()==null){
            showLoginView();
            return;
        }


//            AVUser.getCurrentUser().fetchInBackground(new GetCallback<AVObject>() {
//                @Override
//                public void done(AVObject avObject, AVException e) {
//                    AVUser u = (AVUser) avObject;
//                    if (u == null) {
//                        showLoginView();
//                    }
//                    else {
//                        request(true);
//                    }
//
//                }
//            });
            adapter = new StatusListAdapter(getActivity());
            list = (LoadMoreListView) findViewById(R.id.listview);
            customPtrFrameLayout = (CustomPtrFrameLayout) findViewById(R.id.contentView);
            customPtrFrameLayout.setPtrHandler(new PtrDefaultHandler() {
                @Override
                public void onRefreshBegin(PtrFrameLayout frame) {
                    skip = 0;
                    list.setHasMore();
                    request(false);
                }
            });
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        Intent intent = new Intent(getActivity(), ImageLookActivity.class);
                        intent.putExtra("url", adapter.getDatas().get(i).getInnerStatus().getImageUrl());
                        startActivity(intent);


                }
            });

            list.setOnGetMoreListener(new LoadMoreListView.OnGetMoreListener() {
                @Override
                public void onGetMore() {
                    skip += limit;
                    request(false);
                }
            });
            list.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            if (event.getX() > 200) {
                                list.getParent().requestDisallowInterceptTouchEvent(true);
                            } else {
                                list.getParent().requestDisallowInterceptTouchEvent(false);
                            }

                    }

                    return false;
                }
            });


        request(true);
    }
    @Subscribe
    public  void onEventMainThread(LoginSuccessdEvent event){
        initView();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//取消注册
    }
    private void request(final boolean isRefresh) {
        if (isRefresh) {
            showLoadingPage("正在加载中...", R.drawable.ic_loading);
        }
        subscriber = new Subscriber<List<Status>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                list.setNoMore();
            }

            @Override
            public void onNext(List<Status> data) {
                list.getMoreComplete();
                customPtrFrameLayout.refreshComplete();
                showContentView();
                if (data == null || data.size() < 1) {
                    list.setNoMore();
                    return;
                }else {
                    if (skip==0){
                        adapter.clear();
                    }
                    adapter.addAll(data);
                    adapter.notifyDataSetChanged();
                }

            }
        };
        Log.e("xxxxxxxxx", "maxid: "+maxId+"---skip="+skip);
        try {
            maxId = getMaxId(skip, adapter.getDatas());
            if (maxId == 0) {
                list.setNoMore();
                list.getMoreComplete();
            } else {
                CircleServerUtils.getStatusDatas(maxId, limit, subscriber);
            }
        } catch (AVException e) {
            e.printStackTrace();
        }
    }

    public static long getMaxId(int skip, List<Status> currentDatas) {
        long maxId;
        if (skip == 0) {
            maxId = Long.MAX_VALUE;
        } else {
            AVStatus lastStatus = currentDatas.get(currentDatas.size()-1).getInnerStatus();
            maxId = lastStatus.getMessageId()-1;
        }
        return maxId;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SEND_REQUEST) {
               request(false);
            }
        }
    }
    @Override
    public void onClick(View v) {

    }
}

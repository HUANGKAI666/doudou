package com.wzh.fun.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVStatus;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.ShareContent;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.ShareBoardConfig;
import com.wzh.fun.App;
import com.wzh.fun.R;
import com.wzh.fun.utils.CircleServerUtils;
import com.wzh.fun.ui.activity.ImageLookActivity;
import com.wzh.fun.entity.Status;
import com.wzh.fun.ui.fragment.circleFragment.PersonFragment;
import com.wzh.fun.view.CircleImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class StatusListAdapter extends BaseListAdapter<Status> {

  public StatusListAdapter(Context ctx) {
    super(ctx);
  }

  @Override
  public View getView(int position, View conView, ViewGroup parent) {
    ViewHolder viewHolder = null ;
    if (conView == null) {
        viewHolder = new ViewHolder();
        conView = inflater.inflate(R.layout.status_item, null, false);
        viewHolder.nameView = (TextView) conView.findViewById(R.id.nameView);
        viewHolder.textView = (TextView) conView.findViewById(R.id.statusText);
        viewHolder.avatarView = (CircleImageView) conView.findViewById(R.id.avatarView);
        viewHolder.imageView = (ImageView) conView.findViewById( R.id.statusImage);
        viewHolder.likeView = (ImageView) conView.findViewById(R.id.likeView);
        viewHolder.likeCountView = (TextView) conView.findViewById(R.id.likeCount);
        viewHolder.likeLayout = conView.findViewById( R.id.likeLayout);
        viewHolder.timeView = (TextView) conView.findViewById( R.id.timeView);
        viewHolder.share= conView.findViewById(R.id.share);
        conView.setTag(viewHolder);
    }else {
      viewHolder = (ViewHolder) conView.getTag();
    }

    final Status status = datas.get(position);
    final AVStatus innerStatus = status.getInnerStatus();

    AVUser source = innerStatus.getSource();
    AVFile file = source.getAVFile("avatar");
    if (file != null) {
      Glide.with(ctx).load(file.getUrl()).placeholder(R.drawable.default_head).into(viewHolder.avatarView);
      notifyDataSetChanged();
      Log.d("getImageUrl",file.getUrl());
    }else {
      viewHolder.avatarView.setImageResource(R.drawable.default_head);
    }
    viewHolder.nameView.setText(source.getUsername());

    viewHolder.avatarView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        PersonFragment.go(ctx, innerStatus.getSource());
      }
    });

    if (TextUtils.isEmpty(innerStatus.getMessage())) {
      viewHolder.textView.setVisibility(View.GONE);
    } else {
      viewHolder.textView.setText(innerStatus.getMessage());
      viewHolder.textView.setVisibility(View.VISIBLE);}
    if (!TextUtils.isEmpty(innerStatus.getImageUrl())) {
      viewHolder.imageView.setVisibility(View.VISIBLE);
      Glide.with(ctx).load(innerStatus.getImageUrl()).into(viewHolder.imageView);

    } else {
      viewHolder.imageView.setVisibility(View.GONE);
    }
    final AVObject detail = status.getDetail();
    final List<String> likes;
    if (detail.get(App.LIKES) != null) {
      likes = (List<String>) detail.get(App.LIKES);
    } else {
      likes = new ArrayList<String>();
    }

    int n = likes.size();
    if (n > 0) {
      viewHolder.likeCountView.setText(n + "");
    } else {
      viewHolder.likeCountView.setText("");
    }

    final AVUser user = AVUser.getCurrentUser();
    final String userId = user.getObjectId();
    final boolean contains = likes.contains(userId);
    if (contains) {
      viewHolder.likeView.setImageResource(R.drawable.status_ic_player_liked);
    } else {
      viewHolder.likeView.setImageResource(R.drawable.ic_player_like);
    }
    viewHolder.likeLayout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        SaveCallback saveCallback = new SaveCallback() {
          @Override
          public void done(AVException e) {
            if (CircleServerUtils.filterException(ctx, e)) {
              notifyDataSetChanged();
            }
          }
        };
        if (contains) {
          likes.remove(userId);
          CircleServerUtils.saveStatusLikes(detail, likes, saveCallback);
        } else {
          likes.add(userId);
          CircleServerUtils.saveStatusLikes(detail, likes, saveCallback);
        }
      }
    });

    viewHolder.timeView.setText(millisecs2DateString(innerStatus.getCreatedAt().getTime()));
    viewHolder.share.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
         share(status);
      }
    });
    return conView;
  }

  private void share(Status status) {
    ShareContent sc = new ShareContent();
    sc.mText = status.getInnerStatus().getMessage();
    sc.mTargetUrl = "http://www.qiushibaike.com/";
    sc.mTitle = "逗你笑的分享" ;

    ShareBoardConfig  sbc = new ShareBoardConfig();
    sbc.setTitleVisibility(false);

    new ShareAction((Activity)ctx).setShareContent(sc).withMedia(new UMImage(ctx,status.getInnerStatus().getImageUrl()))
            .setDisplayList(SHARE_MEDIA.SINA,SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE)
            .setCallback(umShareListener).open(sbc);
  }
  private UMShareListener umShareListener = new UMShareListener() {
      @Override
      public void onResult(SHARE_MEDIA platform) {
        Log.d("plat","platform"+platform);
        Toast.makeText(ctx, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();

      }

      @Override
      public void onError(SHARE_MEDIA platform, Throwable t) {
        Toast.makeText(ctx,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
        if(t!=null){
          Log.d("throw","throw:"+t.getMessage());
        }
      }

      @Override
      public void onCancel(SHARE_MEDIA platform) {
        Toast.makeText(ctx,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
      }
  };


  public class ViewHolder{
    TextView nameView ;
    TextView textView ;
    CircleImageView avatarView;
    ImageView imageView ;
    ImageView likeView ;
    TextView likeCountView ;
    View likeLayout,share ;
    TextView timeView;

  }

  public static String getDate(Date date) {
    SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
    return format.format(date);
  }

  public static String millisecs2DateString(long timestamp) {
    return getDate(new Date(timestamp));
  }

}

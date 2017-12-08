package com.wzh.fun.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.bumptech.glide.Glide;
import com.wzh.fun.R;
import com.wzh.fun.view.CircleImageView;


public class FollowAdapter extends BaseListAdapter<AVUser> {
    public FollowAdapter(Context ctx) {
        super(ctx);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder ;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_follow, null, false);
            viewHolder.avatar = (CircleImageView) convertView.findViewById(R.id.avatarView);
            viewHolder.name = (TextView) convertView.findViewById(R.id.nameView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final AVUser user = datas.get(position);
        AVFile file = user.getAVFile("avatar");
        if (file != null) {
            Glide.with(ctx).load(file.getUrl()).placeholder(R.drawable.default_head).into(viewHolder.avatar);
        }else {
            Glide.with(ctx).load(R.drawable.default_head).into(viewHolder.avatar);
        }
        viewHolder.name.setText(user.getUsername());
        return convertView;
    }
    public class ViewHolder{
        CircleImageView avatar ;
        TextView name ;
    }
}

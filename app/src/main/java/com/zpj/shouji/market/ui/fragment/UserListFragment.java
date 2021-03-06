package com.zpj.shouji.market.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.model.UserInfo;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.fragment.profile.ProfileFragment;
import com.zpj.shouji.market.utils.BeanUtils;

import java.util.List;

public class UserListFragment extends NextUrlFragment<UserInfo> {

    public static UserListFragment newInstance(String defaultUrl) {
        Bundle args = new Bundle();
        args.putString(Keys.DEFAULT_URL, defaultUrl);
        UserListFragment fragment = new UserListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_user;
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, UserInfo data) {
        Log.d("UserListFragment", "userInfo=" + data);
        ProfileFragment.start(data.getId(), true);
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<UserInfo> list, int position, List<Object> payloads) {
        final UserInfo appItem = list.get(position);
        holder.setText(R.id.tv_title, appItem.getNickName());
        holder.setText(R.id.tv_info, "在线：" + appItem.isOnline());
        Glide.with(context).load(appItem.getAvatarUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.getImageView(R.id.iv_icon));
    }

    @Override
    public UserInfo createData(Element element) {
        return BeanUtils.createBean(element, UserInfo.class);
//        return UserInfo.from(element);
    }

}

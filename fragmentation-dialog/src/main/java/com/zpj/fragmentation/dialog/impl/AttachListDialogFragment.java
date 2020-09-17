package com.zpj.fragmentation.dialog.impl;

import android.content.res.ColorStateList;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zpj.fragmentation.dialog.animator.PopupAnimator;
import com.zpj.fragmentation.dialog.base.AttachDialogFragment;
import com.zpj.fragmentation.dialog.R;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.widget.tinted.TintedImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AttachListDialogFragment<T> extends AttachDialogFragment {

    protected RecyclerView recyclerView;
    protected int bindLayoutId;
    protected int bindItemLayoutId;
    protected int tintColor = -1;
    protected int textColor;

    private final List<T> items = new ArrayList<>();
    private final List<Integer> iconIds = new ArrayList<>();

    @Override
    protected int getContentLayoutId() {
        return R.layout._dialog_layout_attach_impl_list;
    }

    @Override
    protected PopupAnimator getShadowAnimator(FrameLayout flContainer) {
        return null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (attachView == null && touchPoint == null) {
            touchPoint = new PointF(0, 0);
        }
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        textColor = context.getResources().getColor(R.color._dialog_text_major_color);

        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setupDivider();

        EasyRecyclerView<T> easyRecyclerView = new EasyRecyclerView<>(recyclerView);
        easyRecyclerView.setData(items)
                .setItemRes(bindItemLayoutId == 0 ? R.layout._dialog_item_text : bindItemLayoutId)
                .onBindViewHolder((holder, list, position, payloads) -> {
                    TextView tvText = holder.getView(R.id.tv_text);
                    tvText.setText(list.get(position).toString());
                    tvText.setTextColor(textColor);

                    TintedImageView ivImage = holder.getView(R.id.iv_image);
                    if (iconIds.size() > position) {
                        ivImage.setVisibility(View.VISIBLE);
                        ivImage.setImageResource(iconIds.get(position));
//                        ivImage.setImageDrawable(context.getResources().getDrawable(iconIds.get(position)));
                        if (tintColor != -1) {
                            ivImage.setTint(ColorStateList.valueOf(tintColor));
                        }
                    } else {
                        ivImage.setVisibility(View.GONE);
                    }
                    holder.getView(R.id._dialog_view_divider).setVisibility(View.GONE);
                })
                .onItemClick((holder, view1, data) -> {
                    dismiss();
                    if (selectListener != null) {
                        selectListener.onSelect(holder.getAdapterPosition(), data);
                    }

                })
                .build();
    }

    /**
     * 传入自定义的布局，对布局中的id有要求
     *
     * @param layoutId 要求layoutId中必须有一个id为recyclerView的RecyclerView，如果你需要显示标题，则必须有一个id为tv_title的TextView
     * @return
     */
    public AttachListDialogFragment<T> bindLayout(int layoutId) {
        this.bindLayoutId = layoutId;
        return this;
    }

    /**
     * 传入自定义的 item布局
     *
     * @param itemLayoutId 条目的布局id，要求布局中必须有id为iv_image的ImageView，和id为tv_text的TextView
     * @return
     */
    public AttachListDialogFragment<T> bindItemLayout(int itemLayoutId) {
        this.bindItemLayoutId = itemLayoutId;
        return this;
    }

//    public void show(View atView) {
//        popupInfo.atView = atView;
//        show();
//    }
//
//    public void show(float x, float y) {
//        popupInfo.touchPoint = new PointF(x, y);
//        show();
//    }

    public AttachListDialogFragment<T> setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    public AttachListDialogFragment<T> setIconTintColor(int tintColor) {
        this.tintColor = tintColor;
        return this;
    }

    public AttachListDialogFragment<T> setItems(List<T> items) {
        this.items.clear();
        this.items.addAll(items);
        return this;
    }

    public AttachListDialogFragment<T> addItems(List<T> items) {
        this.items.addAll(items);
        return this;
    }

    public AttachListDialogFragment<T> addItems(T...items) {
        this.items.addAll(Arrays.asList(items));
        return this;
    }

    public AttachListDialogFragment<T> addItemsIf(boolean flag, T...items) {
        if (flag) {
            this.items.addAll(Arrays.asList(items));
        }
        return this;
    }

    public AttachListDialogFragment<T> addItem(T item) {
        this.items.add(item);
        return this;
    }

    public AttachListDialogFragment<T> addItemIf(boolean flag, T item) {
        if (flag) {
            this.items.add(item);
        }
        return this;
    }

    public AttachListDialogFragment<T> setIconIds(List<Integer> iconIds) {
        this.iconIds.clear();
        this.iconIds.addAll(iconIds);
        return this;
    }

    public AttachListDialogFragment<T> addIconId(int iconId) {
        this.iconIds.add(iconId);
        return this;
    }

    public AttachListDialogFragment<T> addIconIds(Integer...ids) {
        this.iconIds.addAll(Arrays.asList(ids));
        return this;
    }

    public AttachListDialogFragment<T> setOffsetXAndY(int offsetX, int offsetY) {
        this.defaultOffsetX += offsetX;
        this.defaultOffsetY += offsetY;
        return this;
    }

    private OnSelectListener<T> selectListener;

    public AttachListDialogFragment<T> setOnSelectListener(OnSelectListener<T> selectListener) {
        this.selectListener = selectListener;
        return this;
    }

    public interface OnSelectListener<T> {
        void onSelect(int position, T text);
    }

}
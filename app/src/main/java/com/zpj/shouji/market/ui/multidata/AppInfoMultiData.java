package com.zpj.shouji.market.ui.multidata;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.zpj.http.core.HttpObserver;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.api.PreloadApi;
import com.zpj.shouji.market.glide.GlideRequestOptions;
import com.zpj.shouji.market.glide.ImageViewDrawableTarget;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.widget.DownloadButton;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.ObservableEmitter;

public abstract class AppInfoMultiData extends BaseHeaderMultiData<AppInfo> {

    public AppInfoMultiData(String title) {
        super(title);
    }

    @Override
    public int getChildColumnCount(int viewType) {
        return getMaxColumnCount();
    }

    @Override
    public int getMaxColumnCount() {
        return 4;
    }

    @Override
    public int getChildLayoutId(int viewType) {
        return R.layout.item_app_grid;
    }

    @Override
    public int getChildViewType(int position) {
        return R.layout.item_app_grid;
    }

    @Override
    public boolean hasChildViewType(int viewType) {
        return viewType == R.layout.item_app_grid;
    }

    @Override
    public boolean loadData() {
        if (getKey() != null) {
            HttpApi.getXml(getKey().getUrl())
                    .flatMap((HttpObserver.OnFlatMapListener<Document, List<AppInfo>>) (document, emitter) -> {
                        Elements elements = document.select("item");
                        List<AppInfo> list = new ArrayList<>();
                        for (Element element : elements) {
                            AppInfo info = AppInfo.parse(element);
                            if (info == null) {
                                continue;
                            }
                            list.add(info);
                        }
                        emitter.onNext(list);
                    })
                    .onSuccess(this::onGetDoc)
                    .onError(new IHttp.OnErrorListener() {
                        @Override
                        public void onError(Throwable throwable) {
                            showError();
                        }
                    })
                    .subscribe();
        }
        return false;
    }

    @Override
    public void onBindChild(EasyViewHolder holder, List<AppInfo> list, int position, List<Object> payloads) {
        AppInfo info = list.get(position);
        holder.setText(R.id.item_title, info.getAppTitle());
        holder.setText(R.id.item_info, info.getAppSize());
        ImageView ivIcon = holder.getImageView(R.id.item_icon);
        Glide.with(holder.getContext())
                .load(info.getAppIcon())
                .apply(GlideRequestOptions.getDefaultIconOption())
                .into(ivIcon);
        DownloadButton downloadButton = holder.getView(R.id.tv_download);
        downloadButton.bindApp(info);
        holder.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDetailFragment.start(info);
            }
        });
    }

    public abstract PreloadApi getKey();

    protected void onGetDoc(List<AppInfo> list) {
//        Elements elements = document.select("item");
//        for (Element element : elements) {
//            AppInfo info = AppInfo.parse(element);
//            if (info == null) {
//                continue;
//            }
//            list.add(info);
//        }
        this.list.addAll(list);
//        int count = adapter.getItemCount();
//        adapter.notifyItemRangeInserted(count - getCount(), getCount());
        showContent();
    }

}
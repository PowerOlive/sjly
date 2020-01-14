package com.zpj.shouji.market.ui.fragment.manager;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.wuhenzhizao.titlebar.widget.CommonTitleBar;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.adapter.PageAdapter;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.ui.widget.DotPagerIndicator;
import com.zpj.shouji.market.ui.widget.ScaleTransitionPagerTitleView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.SwipeBackLayout;

public class AppManagerFragment extends BaseFragment {

    private static final String[] TAB_TITLES = {"下载管理", "更新", "已安装", "安装包"};

    private List<BaseFragment> fragments = new ArrayList<>();

    private UpdateFragment updateFragment = new UpdateFragment();
    private InstalledFragment installedFragment = new InstalledFragment();
    private PackageFragment packageFragment = new PackageFragment();

//    private QTabLayout tabLayout;
    private CommonTitleBar titleBar;

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_app_manager;
    }

    @Override
    protected String getToolbarTitle() {
        return "应用管理";
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        titleBar = view.findViewById(R.id.title_bar);

        fragments.add(new DownloadFragment());
        fragments.add(updateFragment);
        fragments.add(installedFragment);
        fragments.add(packageFragment);

        PageAdapter adapter = new PageAdapter(getChildFragmentManager(), fragments, TAB_TITLES);
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        titleBar.getRightImageButton().setImageResource(R.drawable.ic_settings_white_24dp);
                        break;
                    case 1:
                        titleBar.getRightImageButton().setImageResource(R.drawable.ic_search_white_24dp);
                        break;
                    case 2:
                        titleBar.getRightImageButton().setImageResource(R.drawable.ic_search_white_24dp);
                        break;
                    case 3:
                        titleBar.getRightImageButton().setImageResource(R.drawable.ic_search_white_24dp);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        MagicIndicator magicIndicator = view.findViewById(R.id.magic_indicator);
        CommonNavigator navigator = new CommonNavigator(getContext());
        navigator.setAdjustMode(true);
        navigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return TAB_TITLES.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                ScaleTransitionPagerTitleView titleView = new ScaleTransitionPagerTitleView(context);
                titleView.setNormalColor(Color.WHITE);
                titleView.setSelectedColor(Color.WHITE);
                titleView.setTextSize(14);
                titleView.setText(TAB_TITLES[index]);
                titleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewPager.setCurrentItem(index);
                    }
                });
                return titleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return new DotPagerIndicator(context);
            }
        });
        magicIndicator.setNavigator(navigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getSwipeBackLayout().setEdgeOrientation(SwipeBackLayout.EDGE_RIGHT);
    }
}

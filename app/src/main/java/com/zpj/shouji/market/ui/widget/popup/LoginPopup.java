package com.zpj.shouji.market.ui.widget.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.felix.atoast.library.AToast;
import com.lxj.xpopup.core.CenterPopupView;
import com.lxj.xpopup.core.PopupInfo;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.widget.AutoSizeViewPager;
import com.zpj.shouji.market.ui.widget.ScaleTransitionPagerTitleView;
import com.zpj.shouji.market.ui.widget.SignInLayout;
import com.zpj.shouji.market.ui.widget.SignUpLayout;
import com.zpj.shouji.market.utils.HttpApi;
import com.zpj.shouji.market.utils.UserManager;
import com.zpj.utils.KeyboardUtil;
import com.zpj.utils.ScreenUtils;
import com.zpj.widget.ZViewPager;
import com.zpj.widget.toolbar.ZToolbar;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;

import java.util.ArrayList;
import java.util.List;

public class LoginPopup extends CenterPopupView
        implements UserManager.OnLoginListener {

    private static final String[] TAB_TITLES = {"登录", "注册"};
    private AutoSizeViewPager viewPager;
    private int currentPosition = 0;


    public static LoginPopup with(Context context) {
        return new LoginPopup(context);
    }

    public LoginPopup(@NonNull Context context) {
        super(context);
        popupInfo = new PopupInfo();
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_popup_login;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        UserManager.getInstance().addOnLoginListener(this);
        List<View> list = new ArrayList<>();
        SignUpLayout signUpLayout = new SignUpLayout(getContext());
        SignInLayout signInLayout = new SignInLayout(getContext());
        list.add(signUpLayout);
        list.add(signInLayout);

        ZToolbar toolbar = findViewById(R.id.tool_bar);
        toolbar.getRightImageButton().setOnClickListener(v -> dismiss());
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new LoginPagerAdapter(list));
        // 注意：AutoSizeViewPager#setOffscreenPageLimit一定要设置为view的数量，否则将无法获取一些view的高度
        viewPager.setOffscreenPageLimit(list.size());
        MagicIndicator magicIndicator = (MagicIndicator) toolbar.getCenterCustomView();
        CommonNavigator navigator = new CommonNavigator(getContext());
        navigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return TAB_TITLES.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                ScaleTransitionPagerTitleView titleView = new ScaleTransitionPagerTitleView(context);
                titleView.setNormalColor(getResources().getColor(R.color.color_text_major));
                titleView.setSelectedColor(getResources().getColor(R.color.colorPrimary));
                titleView.setTextSize(14);
                titleView.setText(TAB_TITLES[index]);
                titleView.setOnClickListener(view1 -> viewPager.setCurrentItem(index));
                return titleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineHeight(ScreenUtils.dp2px(context, 4f));
                indicator.setLineWidth(ScreenUtils.dp2px(context, 12f));
                indicator.setRoundRadius(ScreenUtils.dp2px(context, 4f));
                indicator.setColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimary));
                return indicator;
            }
        });
        magicIndicator.setNavigator(navigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
        viewPager.setCurrentItem(currentPosition);
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        UserManager.getInstance().removeOnLoginListener(this);
    }

    public LoginPopup setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
        return this;
    }

    @Override
    public void onLoginSuccess() {
        dismiss();
    }

    @Override
    public void onLoginFailed(String errInfo) {

    }

    private static class LoginPagerAdapter extends PagerAdapter {

        private final List<View> list;

        LoginPagerAdapter(List<View> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            container.addView(list.get(position));
            return list.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(list.get(position));
        }
    }

}

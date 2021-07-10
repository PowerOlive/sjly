package com.zpj.shouji.market.ui.fragment.profile;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.zpj.blur.ZBlurry;
import com.zpj.fragmentation.anim.DefaultVerticalAnimator;
import com.zpj.fragmentation.dialog.ZDialog;
import com.zpj.rxbus.RxBus;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.imagepicker.ImagePicker;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.MemberInfo;
import com.zpj.shouji.market.model.MessageInfo;
import com.zpj.shouji.market.ui.fragment.FeedbackFragment;
import com.zpj.shouji.market.ui.fragment.WebFragment;
import com.zpj.shouji.market.ui.fragment.backup.CloudBackupFragment;
import com.zpj.shouji.market.ui.fragment.base.SkinFragment;
import com.zpj.shouji.market.ui.fragment.dialog.NicknameModifiedDialogFragment;
import com.zpj.shouji.market.ui.fragment.login.LoginFragment;
import com.zpj.shouji.market.ui.fragment.manager.DownloadManagerFragment;
import com.zpj.shouji.market.ui.fragment.manager.InstalledManagerFragment;
import com.zpj.shouji.market.ui.fragment.manager.PackageManagerFragment;
import com.zpj.shouji.market.ui.fragment.manager.UpdateManagerFragment;
import com.zpj.shouji.market.ui.fragment.setting.AboutSettingFragment;
import com.zpj.shouji.market.ui.fragment.setting.SettingFragment;
import com.zpj.shouji.market.ui.widget.PullZoomView;
import com.zpj.shouji.market.ui.widget.ToolBoxCard;
import com.zpj.shouji.market.utils.EventBus;
import com.zpj.shouji.market.utils.PictureUtil;
import com.zpj.skin.SkinEngine;
import com.zpj.toast.ZToast;
import com.zpj.utils.ClickHelper;
import com.zpj.utils.ColorUtils;
import com.zpj.widget.setting.SwitchSettingItem;

public class MyFragment extends SkinFragment
        implements View.OnClickListener {

    private ImageView ivWallpaper;
    private TextView tvName;
    private TextView tvSignature;
    private ImageView ivAvatar;
    private TextView tvCheckIn;
    private TextView tvEditInfo;
    private TextView tvLevel;
    private TextView tvFollower;
    private TextView tvFans;

    private ToolBoxCard toolBoxCard;

    private TextView tvDownloadManaer;
    private TextView tvAppManager;
    private TextView tvPackageManager;
    private TextView tvUpdateManager;

    private TextView tvCloudBackup;
    private TextView tvFeedback;
    //    private SwitchSettingItem tvNightMode;
    private TextView tvSetting;
    private TextView tvCommonSetting;
    private TextView tvDownloadSetting;
    private TextView tvInstallSetting;
    private TextView tvAbout;
    private View tvSignOut;

    private View shadowView;
    private ImageView ivAppName;

    private boolean isLightStyle = true;

//    private LoginPopup loginPopup;

    private float alpha = 0f;
    private ZBlurry blurred;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.onSkinChangeEvent(this, s -> {
            initStatusBar();
            if (toolBoxCard != null) {
                toolBoxCard.initBackground();
            }
        });
        EventBus.onSignOutEvent(this, s -> {
            toolBoxCard.onSignOut();
            tvCheckIn.setVisibility(View.GONE);
            tvSignOut.setVisibility(View.GONE);
            tvName.setText("点击头像登录");
            tvLevel.setText("Lv.0");
            tvSignature.setText("手机乐园，发现应用的乐趣");
            tvFollower.setText("关注 0");
            tvFans.setText("粉丝 0");
            tvEditInfo.setText(R.string.text_not_log_in);
            ivAvatar.setImageResource(R.drawable.ic_profile);
            ivWallpaper.setImageResource(R.drawable.bg_member_default);
            ZToast.success("注销成功");
        });
        EventBus.onSignInEvent(this, new RxBus.PairConsumer<Boolean, String>() {
            @Override
            public void onAccept(Boolean isSuccess, String errorMsg) throws Exception {
                if (isSuccess) {
                    toolBoxCard.onLogin();
                    MemberInfo info = UserManager.getInstance().getMemberInfo();
                    tvCheckIn.setVisibility(View.VISIBLE);
                    if (!info.isCanSigned()) {
                        tvCheckIn.setBackgroundResource(R.drawable.bg_button_round_pink);
                        tvCheckIn.setText("已签到");
                    }
                    tvEditInfo.setText(R.string.text_edit);
                    tvName.setText(info.getMemberNickName());
                    tvLevel.setText("Lv." + info.getMemberLevel());
                    if (TextUtils.isEmpty(info.getMemberSignature())) {
                        tvSignature.setText(info.getMemberScoreInfo());
                    } else {
                        tvSignature.setText(info.getMemberSignature());
                    }
                    tvFollower.setText("关注 " + info.getFollowerCount());
                    tvFans.setText("粉丝 " + info.getFansCount());
                    PictureUtil.loadAvatar(ivAvatar);
                    PictureUtil.loadBackground(ivWallpaper);
                    tvSignOut.setVisibility(View.VISIBLE);
                } else {
                    tvCheckIn.setVisibility(View.GONE);
                    tvEditInfo.setText(R.string.text_not_log_in);
                }
            }
        });

        EventBus.onImageUploadEvent(this, new RxBus.SingleConsumer<Boolean>() {
            @Override
            public void onAccept(Boolean isAvatar) throws Exception {
                if (isAvatar) {
                    PictureUtil.loadAvatar(ivAvatar);
                } else {
                    PictureUtil.loadBackground(ivWallpaper);
                }
            }
        });

        EventBus.registerObserver(this, MessageInfo.class, info -> {
            toolBoxCard.onUpdateMessage(info);
        });
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        PullZoomView pullZoomView = view.findViewById(R.id.view_pull_zoom);
        pullZoomView.setIsZoomEnable(true);
        pullZoomView.setIsParallax(false);
        pullZoomView.setSensitive(2.5f);
        pullZoomView.setZoomTime(500);
        pullZoomView.setOnScrollListener(new PullZoomView.OnScrollListener() {
            @Override
            public void onScroll(int offsetX, int offsetY, int oldOffsetX, int oldOffsetY) {
                super.onScroll(offsetX, offsetY, oldOffsetX, oldOffsetY);
                if (oldOffsetY <= toolbar.getHeight()) {
                    if (Math.abs(offsetY - oldOffsetY) < 2) {
                        return;
                    }
                    alpha = 1f * offsetY / toolbar.getHeight();
                    alpha = Math.min(alpha, 1f);

                    initStatusBar();
                }
            }
        });
        pullZoomView.setOnPullZoomListener(new PullZoomView.OnPullZoomListener() {
            @Override
            public void onPullZoom(int originHeight, int currentHeight) {
                super.onPullZoom(originHeight, currentHeight);
                Log.d("onPullZoom", "onPullZoom originHeight=" + originHeight + " currentHeight=" + currentHeight);
            }
        });

//        blurred = ZBlurry.with(findViewById(R.id.fl_container))
////                .fitIntoViewXY(false)
////                .antiAlias(true)
////                .foregroundColor(Color.parseColor("#20000000"))
//                .scale(0.1f)
//                .radius(8)
////                .maxFps(40)
//                .blur(toolbar, new ZBlurry.Callback() {
//                    @Override
//                    public void down(Bitmap bitmap) {
//                        Drawable drawable = new BitmapDrawable(bitmap);
//                        drawable.setAlpha((int) (alpha * 255));
//                        toolbar.setBackground(drawable, true);
//                    }
//                });
//        blurred.pauseBlur();

        shadowView = view.findViewById(R.id.shadow_view);

        ivWallpaper = view.findViewById(R.id.iv_wallpaper);
        tvName = view.findViewById(R.id.tv_name);
        tvSignature = view.findViewById(R.id.tv_signature);
        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvCheckIn = view.findViewById(R.id.tv_check_in);
        tvEditInfo = view.findViewById(R.id.tv_edit_info);
        tvLevel = view.findViewById(R.id.tv_level);
        tvFollower = view.findViewById(R.id.tv_follower);
        tvFans = view.findViewById(R.id.tv_fans);
        toolBoxCard = view.findViewById(R.id.my_tools_card);

        tvDownloadManaer = findViewById(R.id.tv_download_manager);
        tvAppManager = findViewById(R.id.tv_app_manager);
        tvPackageManager = findViewById(R.id.tv_package_manager);
        tvUpdateManager = findViewById(R.id.tv_update_manager);

        tvCloudBackup = view.findViewById(R.id.tv_cloud_backup);
        tvFeedback = view.findViewById(R.id.tv_feedback);
        SwitchSettingItem tvNightMode = view.findViewById(R.id.tv_night_mode);
        tvNightMode.setChecked(AppConfig.isNightMode());
        tvSetting = view.findViewById(R.id.tv_setting);
        tvCommonSetting = view.findViewById(R.id.tv_common_setting);
        tvDownloadSetting = view.findViewById(R.id.tv_download_setting);
        tvInstallSetting = view.findViewById(R.id.tv_install_setting);
        tvAbout = view.findViewById(R.id.tv_about);
        tvSignOut = view.findViewById(R.id.tv_sign_out);


        tvName.setOnClickListener(this);
        ivAvatar.setOnClickListener(this);
        tvCheckIn.setOnClickListener(this);
        tvEditInfo.setOnClickListener(this);
        tvCloudBackup.setOnClickListener(this);
        tvFeedback.setOnClickListener(this);
//        tvNightMode.setOnClickListener(this);
        tvSetting.setOnClickListener(this);
        tvCommonSetting.setOnClickListener(this);
        tvDownloadSetting.setOnClickListener(this);
        tvInstallSetting.setOnClickListener(this);
        tvAbout.setOnClickListener(this);
        tvSignOut.setOnClickListener(this);

        tvDownloadManaer.setOnClickListener(this);
        tvUpdateManager.setOnClickListener(this);
        tvAppManager.setOnClickListener(this);
        tvPackageManager.setOnClickListener(this);


        hideSoftInput();

        ClickHelper.with(ivAvatar)
                .setOnLongClickListener((v, x, y) -> {
                    if (!UserManager.getInstance().isLogin()) {
                        return false;
                    }
                    ZDialog.attach()
                            .addItems("更换我的头像", "保存头像")
                            .setOnSelectListener((fragment, position, title) -> {
                                fragment.dismiss();
                                switch (position) {
                                    case 0:
                                        ImagePicker.startCrop(true);
                                        break;
                                    case 1:
                                        PictureUtil.saveImage(context, UserManager.getInstance().getMemberInfo().getMemberAvatar());
                                        break;
                                }
                            })
                            .setTouchPoint(x, y)
                            .show(context);
                    return true;
                });

        ClickHelper.with(view.findViewById(R.id.iv_wallpaper))
                .setOnLongClickListener((v, x, y) -> {
                    if (!UserManager.getInstance().isLogin()) {
                        return false;
                    }
                    String bgUrl = UserManager.getInstance().getMemberInfo().getMemberBackGround();
                    boolean canDelete = !TextUtils.isEmpty(bgUrl) && !bgUrl.contains("default_user_bg");
                    ZDialog.attach()
                            .addItems("更换主页背景", "保存背景")
                            .addItemIf(canDelete, "删除背景")
                            .setOnSelectListener((fragment, position, title) -> {
                                fragment.dismiss();
                                switch (position) {
                                    case 0:
                                        ImagePicker.startCrop(false);
                                        break;
                                    case 1:
                                        PictureUtil.saveImage(context, bgUrl);
                                        break;
                                    case 2:
                                        HttpApi.deleteBackgroundApi()
                                                .onSuccess(data -> {
                                                    Log.d("deleteBackgroundApi", "data=" + data);
                                                    String info = data.selectFirst("info").text();
                                                    if ("success".equals(data.selectFirst("result").text())) {
                                                        ZToast.success(info);
                                                        UserManager.getInstance().getMemberInfo().setMemberBackGround("");
                                                        UserManager.getInstance().saveUserInfo();
                                                        PictureUtil.saveDefaultBackground(() -> ivWallpaper.setImageResource(R.drawable.bg_member_default));
                                                    } else {
                                                        ZToast.error(info);
                                                    }
                                                })
                                                .onError(throwable -> {
                                                    throwable.printStackTrace();
                                                    ZToast.error(throwable.getMessage());
                                                })
                                                .subscribe();
                                        break;
                                }
                            })
                            .setTouchPoint(x, y)
                            .show(context);
                    return true;
                });

//        UserManager.getInstance().addOnSignInListener(this);
//        onSignInEvent(new SignInEvent(UserManager.getInstance().isLogin()));
//        SignInEvent.post(UserManager.getInstance().isLogin());
        EventBus.sendIsSignIn();
    }

    @Override
    public void toolbarLeftCustomView(@NonNull View view) {
        super.toolbarLeftCustomView(view);
        ivAppName = (ImageView) view;
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (blurred != null) {
                    blurred.startBlur();
                }
            }
        }, 360);
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        if (blurred != null) {
            blurred.pauseBlur();
        }
    }

    @Override
    protected void initStatusBar() {
        int color = ColorUtils.alphaColor(SkinEngine.getColor(context, R.attr.backgroundColor), alpha * 0.95f);
        toolbar.setBackgroundColor(color);
        toolbar.setLightStyle(alpha <= 0.5);
        if (AppConfig.isNightMode()) {
            toolbar.setLightStyle(true);
            ivAppName.setColorFilter(Color.WHITE);
            shadowView.setVisibility(alpha > 0.5f ? View.VISIBLE : View.GONE);
            lightStatusBar();
        } else if (alpha > 0.5) {
            isLightStyle = false;
            toolbar.setLightStyle(false);
            darkStatusBar();
            shadowView.setVisibility(View.VISIBLE);
            ivAppName.setColorFilter(Color.BLACK);
        } else {
            isLightStyle = true;
            toolbar.setLightStyle(true);
            lightStatusBar();
            shadowView.setVisibility(View.GONE);
            ivAppName.setColorFilter(Color.WHITE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.post(UserManager.getInstance().getMessageInfo());
    }

    @Override
    public void toolbarRightImageButton(@NonNull ImageButton imageButton) {
        imageButton.setOnClickListener(v -> {
//            SearchFragment.start();
            ZDialog.attach()
                    .addItems("打开我的主页", "打开主页网页", "分享主页")
                    .addItem(UserManager.getInstance().isLogin() ? "注销" : "登录")
                    .setOnSelectListener((fragment, position, title) -> {
                        switch (position) {
                            case 0:
                                if (UserManager.getInstance().isLogin()) {
//                                    fragment.dismissWithStart(ProfileFragment.newInstance(UserManager.getInstance().getUserId()));
                                    ProfileFragment.start(UserManager.getInstance().getUserId(), false);
                                } else {
                                    LoginFragment.start();
//                                    fragment.dismissWithStart(LoginFragment.newInstance(false));
                                }
                                break;
                            case 1:
//                                WebFragment.shareHomepage(UserManager.getInstance().getUserId());
                                WebFragment.shareHomepage(UserManager.getInstance().getUserId());
                                break;
                            case 2:
                                ZToast.normal("TODO 分享主页");
//                                fragment.dismiss();
                                break;
                            case 3:
                                if (UserManager.getInstance().isLogin()) {
                                    UserManager.getInstance().signOut(context);
                                } else {
                                    LoginFragment.start();
//                                    fragment.dismissWithStart(LoginFragment.newInstance(false));
//                                    LoginFragment.start(false);
                                }
                                break;
                        }
                        fragment.dismiss();
                    })
                    .setAttachView(v)
                    .show(context);
        });
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        toolBoxCard.initBackground();
    }

    @Override
    public void onClick(View v) {
        if (v == tvCheckIn) {
            MemberInfo memberInfo = UserManager.getInstance().getMemberInfo();
            if (memberInfo.isCanSigned()) {
                HttpApi.getXml("/app/xml_signed.jsp")
                        .bindToLife(this)
                        .onSuccess(data -> {
                            Log.d("tvCheckIn", "data=" + data);
                            String info = data.selectFirst("info").text();
                            if ("success".equals(data.selectFirst("result").text())) {
                                ZToast.success(info);
                                memberInfo.setCanSigned(false);
                                UserManager.getInstance().saveUserInfo();
                                tvCheckIn.setBackgroundResource(R.drawable.bg_button_round_pink);
                                tvCheckIn.setText("已签到");
                            } else {
                                ZToast.error(info + "，登录可能已失效");
                            }
                        })
                        .onError(throwable -> {
                            throwable.printStackTrace();
                            ZToast.error(throwable.getMessage());
                        })
                        .subscribe();
            } else {
                ZToast.warning("你已签到过了");
            }
        } else if (v == tvEditInfo) {
            if (UserManager.getInstance().isLogin()) {
                start(new MyInfoFragment());
            } else {
                start(LoginFragment.newInstance(false));
            }
        } else if (v == ivAvatar) {
            if (UserManager.getInstance().isLogin()) {
//                UploadUtils.upload(_mActivity, true);
                ProfileFragment.start(UserManager.getInstance().getUserId(), false);

            } else {
                LoginFragment.start(false);
            }
        } else if (v == tvName) {
            if (UserManager.getInstance().isLogin()) {
//                NicknameModifiedPopup.with(context).show();
                new NicknameModifiedDialogFragment().show(context);
            } else {
                LoginFragment.start(false);
            }
        } else if (v == tvCloudBackup) {
            if (UserManager.getInstance().isLogin()) {
                CloudBackupFragment.start();
            } else {
                LoginFragment.start(false);
            }
        } else if (v == tvFeedback) {
            if (UserManager.getInstance().isLogin()) {
                FeedbackFragment.start();
            } else {
                LoginFragment.start(false);
            }
        } else if (v == tvSetting) {
            SettingFragment.start(context);
//        } else if (v == tvCommonSetting) {
//            CommonSettingFragment.start();
//        } else if (v == tvDownloadSetting) {
//            DownloadSettingFragment.start();
//        } else if (v == tvInstallSetting) {
//            InstallSettingFragment.start();
        } else if (v == tvAbout) {
            AboutSettingFragment.start(context);
        } else if (v == tvSignOut) {
            UserManager.getInstance().signOut(context);
        } else if (v == tvDownloadManaer) {
            DownloadManagerFragment.start(true);
        } else if (v == tvAppManager) {
            InstalledManagerFragment.start(true);
        } else if (v == tvPackageManager) {
            PackageManagerFragment.start(true);
        } else if (v == tvUpdateManager) {
            UpdateManagerFragment.start(true);
        }
    }

    public void showLoginPopup(int page) {
        _mActivity.setFragmentAnimator(new DefaultVerticalAnimator());
        LoginFragment.start();
//        if (loginPopup == null) {
//            loginPopup = LoginPopup.with(context);
//            loginPopup.setPopupCallback(new SimpleCallback() {
//                @Override
//                public void onDismiss() {
//                    loginPopup = null;
//                }
//
////                @Override
////                public void onShow() {
////                    loginPopup.clearFocus();
////                }
////
//                @Override
//                public void onHide() {
//                    loginPopup.clearFocus();
//                }
//            });
//        }
//        loginPopup.setCurrentPosition(page);
//        loginPopup.show();
    }

}

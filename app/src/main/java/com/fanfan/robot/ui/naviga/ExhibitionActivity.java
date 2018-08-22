package com.fanfan.robot.ui.naviga;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.fanfan.robot.app.common.Constants;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.robot.db.manager.NavigationDBManager;
import com.fanfan.robot.model.NavigationBean;
import com.fanfan.novel.utils.DialogUtils;
import com.fanfan.robot.R;
import com.fanfan.robot.ui.setting.act.naviga.AddNavigationActivity;
import com.fanfan.robot.view.RangeClickImageView;
import com.seabreeze.log.Print;

import butterknife.BindView;

/**
 * Created by android on 2018/1/12.
 */

public class ExhibitionActivity extends BarBaseActivity implements RangeClickImageView.OnRangeClickListener,
        RangeClickImageView.OnResourceReadyListener, RangeClickImageView.OnRangeLongClickListener {

    @BindView(R.id.iv_navigation)
    RangeClickImageView ivNavigation;

    public static final String FILE_NAME = "file_name";

    public static void newInstance(Activity context, String name) {
        Intent intent = new Intent(context, ExhibitionActivity.class);
        intent.putExtra(FILE_NAME, name);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private String fileName;

    private String[] localNavigation;

    private NavigationDBManager mNavigationDBManager;

    private NavigationBean bean;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_navigation_exhibition;
    }

    @Override
    protected void initView() {
        super.initView();

        fileName = getIntent().getStringExtra(FILE_NAME) + ".png";

        ivNavigation.setFileName(fileName, Constants.displayWidth, Constants.displayHeight - toolbar.getHeight());
        ivNavigation.setOnResourceReadyListener(this);
        ivNavigation.setOnRangeClickListener(this);
        ivNavigation.setOnRangeLongClickListener(this);
    }

    @Override
    protected void initData() {
        mNavigationDBManager = new NavigationDBManager();
    }

    @Override
    public void onClickImage(View view, Object tag, int x, int y) {
        String title = (String) tag;
        bean = mNavigationDBManager.queryTitle(title);
        if (bean == null) {
            bean = new NavigationBean();
            bean.setTitle(title);
            bean.setPosX(x);
            bean.setPosY(y);
            AddNavigationActivity.newInstance(this, title, AddNavigationActivity.ADD_NAVIGATION_REQUESTCODE);
        } else {
            AddNavigationActivity.newInstance(this, bean.getId(), AddNavigationActivity.ADD_NAVIGATION_REQUESTCODE);
        }
    }

    @Override
    public void onLongClickImage(View view, final Object tag, int x, int y) {
        final NavigationBean navigationBean = mNavigationDBManager.queryTitle((String) tag);
        if (navigationBean != null) {
            DialogUtils.showBasicNoTitleDialog(this, "确定要删除" + tag + "所有设置吗", "删除",
                    "取消", new DialogUtils.OnNiftyDialogListener() {
                        @Override
                        public void onClickLeft() {

                            boolean isDel = mNavigationDBManager.delete(navigationBean);
                            if (isDel) {
                                ivNavigation.setDelete(navigationBean.getTitle());
                            }
                        }

                        @Override
                        public void onClickRight() {

                        }
                    });
        }
    }

    @Override
    public boolean onResourceReady(int realWidth, int realHeight, int ivWidth, int ivHeight) {

        localNavigation = getResources().getStringArray(R.array.local_navigation);
        Print.e("ivWidth " + ivWidth);
        Print.e("ivHeight " + ivHeight);
        //设置点击事件
        ivNavigation.setClickRange(localNavigation[0], 847.0f, 325.0f);
        ivNavigation.setClickRange(localNavigation[1], 327.0f, 587.0f);
        ivNavigation.setClickRange(localNavigation[2], 990.0f, 810.0f);
        ivNavigation.setClickRange(localNavigation[3], 508.0f, 1708.0f);
        for (String str : localNavigation) {
            NavigationBean bean = mNavigationDBManager.queryTitle(str);
            if (bean != null) {
                if (bean.getPosX() == 0 || bean.getPosY() == 0) {
                    mNavigationDBManager.delete(bean);
                } else {
                    ivNavigation.setDelete(bean.getTitle());
                }
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddNavigationActivity.ADD_NAVIGATION_REQUESTCODE) {
            if (resultCode == RESULT_OK) {
                String title = data.getStringExtra(AddNavigationActivity.RESULT_CODE);
                NavigationBean resultBean = mNavigationDBManager.queryTitle(title);
                if (resultBean == null) {
                    Print.e("error");
                    return;
                }
                resultBean.setPosX(bean.getPosX());
                resultBean.setPosY(bean.getPosY());
                resultBean.setImgUrl(fileName);
                mNavigationDBManager.update(resultBean);
                ivNavigation.setFinish(bean.getTitle());
            }
        }
    }

}

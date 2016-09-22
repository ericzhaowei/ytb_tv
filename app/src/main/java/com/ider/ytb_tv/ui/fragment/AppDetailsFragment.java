package com.ider.ytb_tv.ui.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.DetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;

import com.ider.ytb_tv.R;
import com.ider.ytb_tv.data.Application;
import com.ider.ytb_tv.db.DBManager;
import com.ider.ytb_tv.ui.activity.AppDetailsActivity;
import com.ider.ytb_tv.ui.activity.DetailsActivity;
import com.ider.ytb_tv.ui.activity.MainActivity;
import com.ider.ytb_tv.ui.presenter.DetailsDescriptionPresenter;
import com.ider.ytb_tv.utils.ProcessManager;

import java.util.ArrayList;

/**
 * Created by ider-eric on 2016/9/8.
 */
public class AppDetailsFragment extends DetailsFragment {

    private static final int ACTION_LAUNCH = 1;
    private static final int ACTION_CLEAR_DATA = 2;
    private static final int ACTION_UNINSTALL = 3;
    private static final int ACTION_FAVORITE = 4;

    private Application mApplication;
    private BackgroundManager mBackgroundManager;
    private ClassPresenterSelector mPresenterSelector;
    private ArrayObjectAdapter mAdapter;
    private DetailsDescriptionPresenter mDetailsDescriptionPresenter;
    private DBManager dbManager;
    private boolean isFav;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        dbManager = DBManager.getInstance(getActivity().getApplicationContext());

        String pkgname = getActivity().getIntent().getStringExtra("pkgname");
        String activity = getActivity().getIntent().getStringExtra("activity");
        isFav = getActivity().getIntent().getBooleanExtra(AppDetailsActivity.COLLECTED, false);
        mApplication = Application.make(getActivity(), pkgname, activity);

        if (mApplication == null) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        } else {
            setupAdapter();
            setupDetailOverviewRow();
            setupDetailsOverviewRowPresenter();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public void setupAdapter() {
        mPresenterSelector = new ClassPresenterSelector();
        mAdapter = new ArrayObjectAdapter(mPresenterSelector);
        setAdapter(mAdapter);
    }

    public void setupDetailOverviewRow() {
        DetailsOverviewRow detailsOverviewRow = new DetailsOverviewRow(mApplication);
        detailsOverviewRow.setImageDrawable(mApplication.getIcon());
        detailsOverviewRow.addAction(new Action(ACTION_LAUNCH, getResources().getString(R.string.m_app_action_launch)));
        detailsOverviewRow.addAction(new Action(ACTION_CLEAR_DATA, getResources().getString(R.string.m_app_action_clear)));
        detailsOverviewRow.addAction(new Action(ACTION_UNINSTALL, getResources().getString(R.string.m_app_action_uninstall)));
        detailsOverviewRow.addAction(new Action(ACTION_FAVORITE,
                isFav ? getResources().getString(R.string.remove_favorite):getResources().getString(R.string.m_app_action_favorite)));
        mAdapter.add(detailsOverviewRow);
    }

    public void setupDetailsOverviewRowPresenter() {
        mDetailsDescriptionPresenter = new DetailsDescriptionPresenter();
        DetailsOverviewRowPresenter detailsOverviewRowPresenter = new DetailsOverviewRowPresenter(mDetailsDescriptionPresenter);
        detailsOverviewRowPresenter.setBackgroundColor(getResources().getColor(R.color.selected_background));
        detailsOverviewRowPresenter.setStyleLarge(false);
        detailsOverviewRowPresenter.setSharedElementEnterTransition(getActivity(), AppDetailsActivity.SHARED_ELEMENT_NAME);

        detailsOverviewRowPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            @Override
            public void onActionClicked(Action action) {
                if(action.getId() == ACTION_LAUNCH) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(mApplication.getPkgName(), mApplication.getActivity()));
                    startActivity(intent);

                } else if(action.getId() == ACTION_CLEAR_DATA) {
                    ProcessManager.clearDataForPackage(getActivity(), mApplication.getPkgName());
                    ProcessManager.forceStop(getActivity(), mApplication.getPkgName());

                } else if(action.getId() == ACTION_UNINSTALL) {
                    uninstallApp(mApplication.getPkgName());

                } else if(action.getId() == ACTION_FAVORITE) {
                    if(isFav) {
                        dbManager.deleteApplication(mApplication);
                        sendFavBroadCast(AppDetailsActivity.ACTION_FAVORITE_APP_REMOVED);
                        action.setLabel1(getResources().getString(R.string.m_app_action_favorite));
                        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
                        isFav = false;
                    } else {
                        dbManager.insertApplication(mApplication);
                        sendFavBroadCast(AppDetailsActivity.ACTION_FAVORITE_APP_ADDED);
                        action.setLabel1(getResources().getString(R.string.remove_favorite));
                        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
                        isFav = true;
                    }

                }
            }
        });

        mPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsOverviewRowPresenter);
    }

    public void sendFavBroadCast(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(AppDetailsActivity.APPLICATION_PACKAGE, mApplication.getPkgName());
        intent.putExtra(AppDetailsActivity.APPLICATION_ACTIVITY, mApplication.getActivity());
        getActivity().sendBroadcast(intent);
    }

    public void uninstallApp(String packnaem) {
        Uri uri = Uri.parse("package:" + packnaem);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        startActivity(intent);

    }



}

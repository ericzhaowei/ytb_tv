package com.ider.ytb_tv.ui.fragment;

import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;

/**
 * Created by ider-eric on 2016/9/14.
 */
public class TestFragment extends BrowseFragment {
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHeadersState(HEADERS_ENABLED);
    }
}

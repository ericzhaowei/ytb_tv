package com.ider.ytb_tv.data;

/**
 * Created by ider-eric on 2016/8/15.
 */
public class LoadState {
    public final static int STATE_LOADING = 0;
    public final static int STATE_RETRY = 1;
    public final static int STATE_NO_RESUTL = 2;
    public final static int STATE_NETWORK_NOTAVAILABLE = 3;

    public int mState;
    public LoadState(int state) {
        this.mState = state;
    }

}

package com.mj.gpsclient.Activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mj.gpsclient.R;
import com.mj.gpsclient.global.MyApplication;

/**
 * Created by majin on 15/5/28.
 */
public class TrackFenceFragment extends Fragment {
    private MyApplication application;
    private Activity mActivity = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = this.getActivity();
        application = (MyApplication) mActivity.getApplication();
        View v =inflater.inflate(R.layout.fragment_track_fence, container, false);
        //initViews(v);
        return v;
    }
}

package com.mj.gpsclient.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mj.gpsclient.R;
import com.mj.gpsclient.global.MyApplication;

/**
 * Created by majin on 15/5/26.
 */
public class DevicesAlarmFragment extends Fragment {

    private MyApplication application;
    private Activity mActivity = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = this.getActivity();
        application = (MyApplication) mActivity.getApplication();
        View v =inflater.inflate(R.layout.fragment_devices_alarm, container, false);
        //initViews(v);
        return v;
    }
}

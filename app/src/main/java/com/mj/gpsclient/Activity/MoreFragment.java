package com.mj.gpsclient.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mj.gpsclient.R;
import com.mj.gpsclient.global.MyApplication;

/**
 * Created by majin on 15/5/26.
 */
public class MoreFragment extends Fragment implements View.OnClickListener{

    private MyApplication application;
    private Activity mActivity = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = this.getActivity();
        application = (MyApplication) mActivity.getApplication();
        View v =inflater.inflate(R.layout.fragment_more, container, false);
        initViews(v);
        initDatas();

        return v;
    }

    private void initViews(View view){
        TextView TextLogout= (TextView) view.findViewById(R.id.txt_logout);
        TextLogout.setOnClickListener(this);
        RelativeLayout TextResetPwd= (RelativeLayout) view.findViewById(R.id.layout_resetPwd);
        TextResetPwd.setOnClickListener(this);

    }

    private void initDatas(){


    }


    @Override
    public void onClick(View v) {
        Intent intent =null;
        switch (v.getId()){
            case R.id.txt_logout:
                application.clearLoginParams();
                intent = new Intent(mActivity,LoginActivity.class);
                startActivity(intent);
                mActivity.finish();
                break;
            case R.id.layout_resetPwd:
                intent= new Intent(mActivity,ResetPwdActivity.class);
                startActivity(intent);
                break;

        }
    }
}

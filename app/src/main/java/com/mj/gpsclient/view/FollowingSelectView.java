package com.mj.gpsclient.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mj.gpsclient.R;
import com.mj.gpsclient.Activity.PubUtil;

public class FollowingSelectView extends RelativeLayout {

	private TextView following_sure;
	private LinearLayout ll_follow_view;
	private ImageView iv_follow_view;
	private FollowingCallBack mCallBack;
	private boolean selectStatus = false;

	public FollowingSelectView(Context context) {
		super(context);
		init();
	}

	public FollowingSelectView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		View.inflate(getContext(), R.layout.following_view, this);

		following_sure = (TextView) findViewById(R.id.following_sure);
		ll_follow_view = (LinearLayout) findViewById(R.id.ll_follow_view);
		iv_follow_view = (ImageView) findViewById(R.id.iv_follow_view);
		following_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCallBack != null) {
					mCallBack.sureSelect();
				}
			}
		});

		ll_follow_view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCallBack != null) {
					mCallBack.selectAll();
					}
				

			}
		});
	}

	public void SetCallBack(FollowingCallBack callBack) {
		this.mCallBack = callBack;
	}

	public interface FollowingCallBack {
		void selectAll();

		void sureSelect();
	}
}

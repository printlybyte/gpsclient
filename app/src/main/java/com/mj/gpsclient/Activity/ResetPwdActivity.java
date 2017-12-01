package com.mj.gpsclient.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.ab.fragment.AbProgressDialogFragment;
import com.ab.global.AbActivityManager;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.soap.AbSoapUtil;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.ab.view.titlebar.AbTitleBar;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mj.gpsclient.R;
import com.mj.gpsclient.global.Constant;
import com.mj.gpsclient.global.MyApplication;
import com.mj.gpsclient.global.XMLHelper;

//import org.json.JSONException;
//import org.json.JSONObject;

public class ResetPwdActivity extends AbActivity implements
		View.OnClickListener {
	private Activity ResetPwdActivity = null;
	private EditText mEdOldPwd;
	private EditText mEdNewPwdOne;
	private EditText mEdNewPwdTwo;
	private Button mBtRest;
	private AbSoapUtil mAbSoapUtil;
	private AbHttpUtil http;
	private AbTitleBar mAbTitleBar = null;
	private MyApplication application;
	private RelativeLayout loadingLayout;
	private RelativeLayout loginLayout;
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int result = msg.what;
			switch (result) {
			case 0: // 重新登录
				application.clearLoginParams();
				Intent intent = new Intent(ResetPwdActivity.this,
						LoginActivity.class);
				startActivity(intent);
				ResetPwdActivity.this.finish();
				break;
			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("修改密码");
		mAbTitleBar.setBackgroundColor(getResources().getColor(R.color.aaa));
		mAbTitleBar.setLogo(R.drawable.back_n);
		// mAbTitleBar.setTitleBarBackground(R.drawable.tab_top_bg);
		mAbTitleBar.getLogoView().setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						ResetPwdActivity.this.finish();
					}
				});
		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
		setAbContentView(R.layout.activity_resetpwd);
		AbActivityManager.getInstance().addActivity(this);
		application = (MyApplication) abApplication;
		initView();

	}

	private void initView() {
		mEdOldPwd = (EditText) findViewById(R.id.editText_old_pwd);
		mEdNewPwdOne = (EditText) findViewById(R.id.editText_pwd_one);
		mEdNewPwdTwo = (EditText) findViewById(R.id.editText_pwd_two);
		mBtRest = (Button) findViewById(R.id.btn_reset);
		mBtRest.setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_reset:
			goResetPwd();
			break;
		default:
			break;
		}
	}

	private void goResetPwd() {
		String oldPwd = mEdOldPwd.getText().toString().trim();
		String newPwdOne = mEdNewPwdOne.getText().toString().trim();
		String newPwdTwd = mEdNewPwdTwo.getText().toString().trim();
		if (TextUtils.isEmpty(oldPwd)) {
			Toast.makeText(this, "请输入旧密码", Toast.LENGTH_SHORT).show();
			return;
		} else if (TextUtils.isEmpty(newPwdOne)) {
			Toast.makeText(this, "请输入新密码", Toast.LENGTH_SHORT).show();
			return;
		} else if (TextUtils.isEmpty(newPwdTwd)) {
			Toast.makeText(this, "请确认新密码", Toast.LENGTH_SHORT).show();
			return;
		} else if (!newPwdOne.equals(newPwdTwd)) {
			Toast.makeText(this, "两次新密码输入不一致，请重新输入", Toast.LENGTH_SHORT).show();
			return;
		} else if (oldPwd.equals(newPwdOne)) {
			Toast.makeText(this, "新旧密码完全一样，请重新输入！", Toast.LENGTH_SHORT).show();
			return;
		}
		String pwd = application.mUser.getPassword();
		if (!oldPwd.equals(pwd)) {
			Toast.makeText(this, "原密码输入有误，请重新输入！", Toast.LENGTH_SHORT).show();
			return;
		}
		resetPwd2(oldPwd, newPwdOne);

	}

	private void resetPwd2(final String oldPwd, final String newPwd) {
		final AbProgressDialogFragment dialogFragment = AbDialogUtil
				.showProgressDialog(ResetPwdActivity.this, 0, "设置中...");
		dialogFragment.setCancelable(true);
		String url = Constant.URL + "/WebService/GLService.asmx/ChangePassword";
		http = AbHttpUtil.getInstance(this);
		http.setTimeout(1000);
		AbRequestParams params = new AbRequestParams();
		params.put("name", application.mUser.getUserName());
		params.put("Oldpassword", oldPwd);
		params.put("newPassword", newPwd);
		http.post(url, params, new AbStringHttpResponseListener() {
			@Override
			public void onSuccess(int statusCode, String content) {
				dialogFragment.dismiss();
				XMLHelper.getResult("string", content,
						new XMLHelper.CallBack() {
							@Override
							public void getResult(String result) {
								JSONObject object = JSON.parseObject(result);
								String r = object.getString("Result");
								if (r.equals("ok")) {
									AbToastUtil.showToast(
											ResetPwdActivity.this,
											"密码修改成功！请重新登陆！");
									application.clearLoginParams();
									Intent intent = null;
									intent = new Intent(ResetPwdActivity.this,
											LoginActivity.class);
									startActivity(intent);
								} else {
									AbToastUtil.showToast(
											ResetPwdActivity.this, r);
								}
							}
						});
			}

			@Override
			public void onStart() {

			}

			@Override
			public void onFinish() {

			}

			@Override
			public void onFailure(int statusCode, String content,
					Throwable error) {

			}
		});
	}

}

package com.mj.gpsclient.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.ab.fragment.AbProgressDialogFragment;
import com.ab.fragment.AbSampleDialogFragment;
import com.ab.global.AbActivityManager;
import com.ab.soap.AbSoapUtil;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.ab.view.titlebar.AbTitleBar;
import com.ab.view.wheel.AbNumericWheelAdapter;
import com.ab.view.wheel.AbWheelUtil;
import com.ab.view.wheel.AbWheelView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.mj.gpsclient.R;
import com.mj.gpsclient.global.Constant;
import com.mj.gpsclient.global.MyApplication;
import com.mj.gpsclient.global.XMLHelper;
import com.mj.gpsclient.model.User;
import com.mj.gpsclient.reg.AESTool;
import com.mj.gpsclient.reg.DCPubic;
import com.mj.gpsclient.reg.Global;
import com.mj.gpsclient.reg.log;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Timer;

//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;

public class LoginActivity extends AbActivity implements View.OnClickListener {
	AbProgressDialogFragment dialogFragment;
	private EditText mEdName;
	private EditText mEdPass;
	private CheckBox mCb;
	private Button mBtLogin;
	private AbSoapUtil mAbSoapUtil;
	private AbTitleBar mAbTitleBar = null;
	private MyApplication application;
	private CheckBox checkBox;
	private RelativeLayout loadingLayout;
	private RelativeLayout loginLayout;
	private boolean isExit = false;
	// public Handler mHandler = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	// int result = msg.what;
	// switch (result) {
	// case 0: //搜索点击
	// login();
	// break;
	// default:
	// break;
	// }
	// }
	//
	// };
	public static String strreg;
	private Dialog dialog_Reg;
	String input;
	ProgressDialog p;
	private Timer mTimer;
	String code = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		BugPublicUtils.checkToUploadBugInfos(this, "/SCPS_crash/", "http://zc.xun365.net/WebService/SoftWare.asmx/SetBugInfo", "201801121045", "SCPS");

		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setAbContentView(R.layout.activity_login);
		AbActivityManager.getInstance().addActivity(this);
		regist();
		application = (MyApplication) abApplication;
		initView();
		if (null != application.mUser) {
			mEdName.setText(application.mUser.getUserName());
			mEdPass.setText(application.mUser.getPassword());
		}

		if (application.mUser != null
				&& !TextUtils.isEmpty(application.mUser.getUserName())
				&& !TextUtils.isEmpty(application.mUser.getPassword())) {
			// loginLayout.setVisibility(View.GONE);
			// loadingLayout.setVisibility(View.VISIBLE);
			// mHandler.sendEmptyMessageDelayed(0,100);
		} else {
			// loadingLayout.setVisibility(View.GONE);
			// loginLayout.setVisibility(View.VISIBLE);
		}

	}

	private void regist() {

		try {
			strreg = AESTool.decrypt("lyx123456ybf", DCPubic.getDataFromSp(
					LoginActivity.this, "REG", "OBJREG", ""));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (strreg == null || TextUtils.isEmpty(strreg)) {
			showRegDialog();
		}
	}

	private void showRegDialog() {
		// TODO Auto-generated method stub
		View v = LayoutInflater.from(this).inflate(R.layout.reg_dialog, null);
		dialog_Reg = new Dialog(this, R.style.DialogStyle);
		dialog_Reg.setCanceledOnTouchOutside(false);
		dialog_Reg.show();
		dialog_Reg.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				dialog_Reg.dismiss();
				finish();
			}
		});
		Window window = dialog_Reg.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		window.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		lp.width = dip2px(this, 290); // 宽度
		lp.height = dip2px(this, 200); // 高度
		lp.alpha = 0.7f; // 透明度
		window.setAttributes(lp);
		window.setContentView(v);
		final TextView reg = (TextView) v.findViewById(R.id.editTextReg);
		ImageButton ib = (ImageButton) v.findViewById(R.id.imageButtonReg);
		ImageButton.OnClickListener listener = new ImageButton.OnClickListener() {

			public void onClick(View v) {

				input = reg.getText().toString().trim();
				if (input == null || TextUtils.isEmpty(input)) {
					Toast.makeText(LoginActivity.this, "注册码错误，请重新输入",
							Toast.LENGTH_SHORT).show();
					return;
				}

				// 网络验证中
				p = ProgressDialog.show(LoginActivity.this, "请稍候",
						"注册码验证中请不要进行其他操作", true);

				new Thread() {
					public void run() {

						// Message m = new Message();
						// m.what = 0;

						TelephonyManager telephonyManager = (TelephonyManager) LoginActivity.this
								.getSystemService(Context.TELEPHONY_SERVICE);
						String IMEI = telephonyManager.getDeviceId();
						log.recordLog(IMEI, true);

						String sURL = "http://218.246.35.74:5050/PC/Default.aspx?Number="
								+ input + "&Onlycode=" + IMEI;

						java.net.URL l_url = null;
						try {
							l_url = new java.net.URL(sURL);
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							// Toast.makeText(context,
							// "网络验证异常",Toast.LENGTH_SHORT).show();

							p.dismiss();

							LoginActivity.this.mMessageHandler
									.sendEmptyMessage(3);
						}
						java.net.HttpURLConnection l_connection = null;
						try {
							l_connection = (java.net.HttpURLConnection) l_url
									.openConnection();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							// Toast.makeText(context, "网络验证（建立网络）异常",
							// Toast.LENGTH_SHORT).show();

							p.dismiss();

							LoginActivity.this.mMessageHandler
									.sendEmptyMessage(4);
						}
						try {
							l_connection.connect();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							// Toast.makeText(context, "网络验证（连接网络）异常",
							// Toast.LENGTH_SHORT).show();

							p.dismiss();

							LoginActivity.this.mMessageHandler
									.sendEmptyMessage(5);

						}
						InputStream l_urlStream = null;
						try {
							l_urlStream = l_connection.getInputStream();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							p.dismiss();

							LoginActivity.this.mMessageHandler
									.sendEmptyMessage(6);
							return;

						}

						java.io.BufferedReader l_reader = new java.io.BufferedReader(
								new java.io.InputStreamReader(l_urlStream));
						String sCurrentLine = "";
						code = "";
						try {
							while ((sCurrentLine = l_reader.readLine()) != null) {
								code += sCurrentLine;
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							// Toast.makeText(context, "网络验证（解析数据）异常",
							// Toast.LENGTH_SHORT).show();

							p.dismiss();

							LoginActivity.this.mMessageHandler
									.sendEmptyMessage(7);
						}

						p.dismiss();

						LoginActivity.this.mMessageHandler.sendEmptyMessage(0);

					}
				}.start();

			}
		};

		ib.setOnClickListener(listener);
	}

	public int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	boolean notsendmsg = false;
	Handler mMessageHandler = new Handler() {
		// @Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:// time out
				Toast.makeText(LoginActivity.this, "网络连接超时，注册码验证失败",
						Toast.LENGTH_SHORT).show();

				if (mTimer != null) {
					mTimer.cancel();
				}

				if (p != null) {
					p.dismiss();
				}

				break;
			/* 当取得识别为 离开运行线程时所取得的短信 */
			case 0:

				if (mTimer != null) {
					mTimer.cancel();
				}

				if (code == null || TextUtils.isEmpty(code)) {
					Toast.makeText(LoginActivity.this, "注册码验证失败，请重试",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (code.equalsIgnoreCase("1")) {
					Toast.makeText(LoginActivity.this, "注册码验证成功",
							Toast.LENGTH_SHORT).show();

					SharedPreferences startauto = LoginActivity.this
							.getSharedPreferences("REG", 0);
					SharedPreferences.Editor editor = startauto.edit();
					// editor.putString("OBJREG", input);
					try {
						editor.putString("OBJREG",
								AESTool.encrypt("lyx123456ybf", input));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					editor.commit();
					LoginActivity.strreg = input;
					Global.REGSTR = input;
					dialog_Reg.dismiss();
					// onConfirm.OK();

				} else if (code.equalsIgnoreCase("11")) {

					Toast.makeText(LoginActivity.this, "注册码超过有效使用次数",
							Toast.LENGTH_SHORT).show();

				} else if (code.equalsIgnoreCase("12")) {

					Toast.makeText(LoginActivity.this, "注册码已过期",
							Toast.LENGTH_SHORT).show();

				} else if (code.equalsIgnoreCase("13")) {

					Toast.makeText(LoginActivity.this, "注册码超过有效使用次数或已过期",
							Toast.LENGTH_SHORT).show();

				} else if (code.equalsIgnoreCase("14")) {

					Toast.makeText(LoginActivity.this, "此注册码未授权在此机器使用",
							Toast.LENGTH_SHORT).show();

				} else if (code.equalsIgnoreCase("15")) {

					Toast.makeText(LoginActivity.this, "注册码已被禁用",
							Toast.LENGTH_SHORT).show();

				} else if (code.equalsIgnoreCase("16")) {

					Toast.makeText(LoginActivity.this, "注册码不存在",
							Toast.LENGTH_SHORT).show();

				} else if (code.equalsIgnoreCase("17")) {

					Toast.makeText(LoginActivity.this, "注册中发生未知异常,注册失败",
							Toast.LENGTH_SHORT).show();

				} else {

					Toast.makeText(LoginActivity.this, "注册码错误，请重新输入",
							Toast.LENGTH_SHORT).show();

				}

				break;
			case 3:
				Toast.makeText(LoginActivity.this, "网络验证异常", Toast.LENGTH_SHORT)
						.show();
				break;
			case 4:
				Toast.makeText(LoginActivity.this, "网络验证（建立连接网络）异常",
						Toast.LENGTH_SHORT).show();
				break;
			case 5:
				Toast.makeText(LoginActivity.this, "网络验证（连接网络）异常",
						Toast.LENGTH_SHORT).show();
				break;
			case 6:
				Toast.makeText(LoginActivity.this, "网络验证（获取数据）异常",
						Toast.LENGTH_SHORT).show();
				break;
			case 7:
				Toast.makeText(LoginActivity.this, "网络验证（解析数据）异常",
						Toast.LENGTH_SHORT).show();
				break;
			case 12:
				String name = (String) msg.obj;
				SharedPreferences sp = getSharedPreferences("userName",
						MODE_PRIVATE);
				Editor et = sp.edit();
				et.putString("username", name);
				et.commit();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		};
	};

	private void initView() {
		mEdName = (EditText) findViewById(R.id.editText_user_name);
		mEdPass = (EditText) findViewById(R.id.editText_user_pass);
		mCb = (CheckBox) findViewById(R.id.user_sava_pass);
		mBtLogin = (Button) findViewById(R.id.btn_login);
		checkBox = (CheckBox) findViewById(R.id.user_sava_pass);
		checkBox.setChecked(true);
		loadingLayout = (RelativeLayout) findViewById(R.id.login_bg);
		loginLayout = (RelativeLayout) findViewById(R.id.login_content);
		mBtLogin.setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_login:
			if (isNetworkAvailable(LoginActivity.this)) {
				login();
			} else {
				Toast.makeText(LoginActivity.this, "登录失败，请先检查网络连接",
						Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}

	private void login() {
		String name = mEdName.getText().toString().trim();

		String pass = mEdPass.getText().toString();
		if (TextUtils.isEmpty(name)) {
			Toast.makeText(this, "请输入用户名", 0).show();
			return;
		} else if (TextUtils.isEmpty(pass)) {
			Toast.makeText(this, "请输入密码", 0).show();
			return;
		} else {
			loginWithHehe(name, pass);
		}

	}

	private void loginWithHehe(final String name, final String pass) {
		final AbProgressDialogFragment dialogFragment = AbDialogUtil
				.showProgressDialog(LoginActivity.this, 0, "登录中...");
		dialogFragment.setCancelable(true);
		String url = Constant.URL + "/WebService/GLService.asmx/Login";
		FinalHttp http = new FinalHttp();
		AjaxParams params = new AjaxParams();
		params.put("name", name);
		params.put("password", pass);
		http.post(url, params, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String s) {
				super.onSuccess(s);
				// dialogFragment.dismiss();
				XMLHelper.getResult("string", s, new XMLHelper.CallBack() {
					@Override
					public void getResult(String result) {
						JSONObject object = JSON.parseObject(result);
						String r = object.getString("Result");
						String model = object.getString("Model");
						if (r.equals("ok")) {
							Message msg = new Message();
							msg.what = 12;
							msg.obj = name;
							mMessageHandler.sendMessage(msg);
							User user = new User();
							user.setUserName(name);
							user.setPassword(pass);
							application.userPasswordRemember = checkBox
									.isChecked();
							application.updateLoginParams(user);
							Intent intent = new Intent(LoginActivity.this,
									NewMainActivity.class);
							intent.putExtra("model", model.toString());
							startActivity(intent);
							finish();
						} else {
							AbToastUtil.showToast(LoginActivity.this,
									"用户名或密码错误！");
						}
					}
				});
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				dialogFragment.dismiss();
				AbToastUtil.showToast(LoginActivity.this, "服务器无响应");
			}
		});
	}

	// private void loginWithName(final String name, final String pass){
	//
	// String urlString= Constant.URL+"/WebService/GLService.asmx/Login";
	// String nameSpace="http://tempuri.org/";
	// String methodName = "Login";
	// AbSoapParams params = new AbSoapParams();
	// params.put("name",name);
	// params.put("password",pass);
	// mAbSoapUtil= AbSoapUtil.getInstance(this);
	// mAbSoapUtil.setTimeout(10000);
	// mAbSoapUtil.call(urlString,nameSpace,methodName,params, new
	// AbSoapListener() {
	//
	// //获取数据成功会调用这里
	// @Override
	// public void onSuccess(int statusCode, SoapObject object) {
	//
	// String LoginResult = object.getPrimitivePropertyAsString("LoginResult");
	// // Log.d("majin", "statusCode=" + statusCode + "SoapObject=" +
	// LoginResult);
	// AbDialogUtil.removeDialog(LoginActivity.this);
	// JSONObject jobj=null;
	// try {
	// jobj=new JSONObject(LoginResult);
	// } catch (JSONException e) {
	// AbToastUtil.showToast(LoginActivity.this, "系统返回异常！");
	// e.printStackTrace();
	// }
	// String result =jobj.optString("Result");
	// if(result.equals("ok")){
	// AbToastUtil.showToast(LoginActivity.this, "登录成功！");
	// JSONArray jmode = jobj.optJSONArray("Model");
	// User user =new User();
	// user.setUserName(name);
	// user.setPassword(pass);
	// // if(checkBox.isChecked()){
	// // application.updateLoginParams(user);
	// // }
	// application.userPasswordRemember =checkBox.isChecked();
	// application.updateLoginParams(user);
	// Intent intent = new Intent(LoginActivity.this, MainActivity.class);
	// intent.putExtra("model",jmode.toString());
	// startActivity(intent);
	// finish();
	// }else {
	// AbToastUtil.showToast(LoginActivity.this, "用户名或密码错误！");
	// }
	//
	// }
	//
	// // 失败，调用
	// @Override
	// public void onFailure(int statusCode, String content,
	// Throwable error) {
	//
	// }
	//
	// // 失败，调用
	// @Override
	// public void onFailure(int statusCode, SoapFault fault) {
	//
	// }
	//
	// // 开始执行前
	// @Override
	// public void onStart() {
	//
	// }
	//
	// // 完成后调用，失败，成功
	// @Override
	// public void onFinish() {
	// Log.d("majin","onFinish");
	// };
	//
	// });
	//
	// }

	private void testWheel() {

		View mTimeView2 = null;
		TextView timeTextView2 = new TextView(application);
		mTimeView2 = mInflater.inflate(R.layout.choose_three, null);
		initWheelTime(mTimeView2, timeTextView2);

		final AbWheelView mWheelView1 = (AbWheelView) mTimeView2
				.findViewById(R.id.wheelView0);
		mWheelView1.setAdapter(new AbNumericWheelAdapter(2000, 2020));
		// 可循环滚动
		mWheelView1.setCyclic(true);
		// 添加文字
		mWheelView1.setLabel("      年");
		// 初始化时显示的数据
		mWheelView1.setCurrentItem(15);
		mWheelView1.setValueTextSize(35);
		mWheelView1.setLabelTextSize(35);
		mWheelView1.setLabelTextColor(0x80000000);
		mWheelView1.setCenterSelectDrawable(this.getResources().getDrawable(
				R.drawable.wheel_select));

		AbSampleDialogFragment abs = AbDialogUtil.showDialog(mTimeView2,
				Gravity.BOTTOM);
		abs.setOnDismissListener(new DialogInterface.OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialogInterface) {
				int index = mWheelView1.getCurrentItem();
				String val = mWheelView1.getAdapter().getItem(index);
				Log.d("majin", "val =" + val + "---");
			}
		});

	}

	public void initWheelTime(View mTimeView, TextView mText) {
		final AbWheelView mWheelViewMD = (AbWheelView) mTimeView
				.findViewById(R.id.wheelView1);
		final AbWheelView mWheelViewMM = (AbWheelView) mTimeView
				.findViewById(R.id.wheelView2);
		final AbWheelView mWheelViewHH = (AbWheelView) mTimeView
				.findViewById(R.id.wheelView3);
		Button okBtn = (Button) mTimeView.findViewById(R.id.okBtn);
		Button cancelBtn = (Button) mTimeView.findViewById(R.id.cancelBtn);
		mWheelViewMD.setCenterSelectDrawable(this.getResources().getDrawable(
				R.drawable.wheel_select));
		mWheelViewMM.setCenterSelectDrawable(this.getResources().getDrawable(
				R.drawable.wheel_select));
		mWheelViewHH.setCenterSelectDrawable(this.getResources().getDrawable(
				R.drawable.wheel_select));
		AbWheelUtil.initWheelTimePicker( mText, mWheelViewMD,
				mWheelViewMM, mWheelViewHH, okBtn, cancelBtn, 2013, 1, 1, 10,
				0, true);
	}

	// 判断当前网络是否可用
	public boolean isNetworkAvailable(Activity activity) {
		Context context = activity.getApplicationContext();
		// 获取手机所有连接管理对象（包括对wifi,net等连接的管理）
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			return false;
		} else {
			// 获取NetworkInfo对象
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

			if (networkInfo != null && networkInfo.length > 0) {
				for (int i = 0; i < networkInfo.length; i++) {
					// 判断当前网络状态是否为连接状态
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 描述：返回.
	 */
	@Override
	public void onBackPressed() {
		if (isExit == false) {
			isExit = true;
			AbToastUtil.showToast(LoginActivity.this, "再按一次退出程序");
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					isExit = false;
				}

			}, 2000);
		} else {
			super.onBackPressed();
		}
	}
}

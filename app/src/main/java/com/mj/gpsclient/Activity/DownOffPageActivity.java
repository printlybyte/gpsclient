package com.mj.gpsclient.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.global.AbActivityManager;
import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.mj.gpsclient.R;
import com.mj.gpsclient.R.color;
import com.mj.gpsclient.adapter.CityExpandableAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.mj.gpsclient.R.id.citydelete_tv;

public class DownOffPageActivity extends Activity implements OnClickListener,
        MKOfflineMapListener {

    private ImageView download_back;
    private TextView citylist_top_tv1;
    private TextView citylist_top_tv2;
    private ListView localmaplist;
    private LinearLayout citylist_ll;
    private RelativeLayout localmap_layout;
    private MKOfflineMap mOffline = null;
    private ExpandableListView cityExpandable_lv;
    private ArrayList<MKOLUpdateElement> localMapList =new ArrayList<MKOLUpdateElement>();
    private LocalMapAdapter lAdapter = null;
    private LinearLayout description_ll;
    private LinearLayout delete_all_ll;
    private LinearLayout download_all_ll;
    private LinearLayout pause_all_ll;
    // private boolean isdownloadpressed = true;

    private ArrayList<MKOLSearchRecord> group;
    private ArrayList<List<MKOLSearchRecord>> child;
    private CityExpandableAdapter cityExpandAdapter;
    private ImageView download_all_iv;
    private ImageView pause_all_iv;
    private TextView download_all_tv;
    private TextView pause_all_tv;
    //初始化网络状态广播
    private ConnectionChangeReceiver myReceiver;
    private String Tag = "DownOffPageActivity";

    private List<Integer> finishedCityIds = new ArrayList<Integer>();//已下载完成离线地图的城市


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.downloadoffpage);
        AbActivityManager.getInstance().addActivity(this);
        mOffline = new MKOfflineMap();
        mOffline.init(this);
        findView();
        initView();
        setOnclick();
        registNetWorkConnectionReceiver();


    }

    /**
     * 注册网络状态变化的广播
     */
    private void registNetWorkConnectionReceiver() {
        //网络状态
        IntentFilter filterz = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        myReceiver = new ConnectionChangeReceiver();
        this.registerReceiver(myReceiver, filterz);
    }

    @Override
    protected void onResume() {
        updateView();
        cityExpandAdapter.notifyDataSetChanged();
        initBottomButtonBg();
        super.onResume();
    }

    /**
     * 更改底部按钮的bg
     */
    private void initBottomButtonBg() {
        localMapList = mOffline.getAllUpdateInfo();
        if (localMapList==null) {
          localMapList =new ArrayList<MKOLUpdateElement>();
        }
        if (localMapList.size() > 0) {
            ArrayList<MKOLUpdateElement> pauseDates = new ArrayList<MKOLUpdateElement>();
            ArrayList<MKOLUpdateElement> downloadDates = new ArrayList<MKOLUpdateElement>();
            for (MKOLUpdateElement bean : localMapList) {
                if (bean.status == 1 || bean.status == 2) {
                    pauseDates.add(bean);
                } else if (bean.status == 3||bean.status == 8) {
                    downloadDates.add(bean);
                }

            }
            if (pauseDates.size() > 0) {
                pause_all_ll.setBackgroundResource(R.drawable.pause_all_press);
                pause_all_iv.setBackgroundResource(R.drawable.pause_hight);
                pause_all_tv.setTextColor(getResources().getColor(color.white));
            } else {
                pause_all_ll.setBackgroundResource(R.drawable.pause_all_normal);
                pause_all_iv.setBackgroundResource(R.drawable.pause_grey);
                pause_all_tv.setTextColor(getResources().getColor(color.grey));
            }
            if (downloadDates.size() > 0) {
                download_all_iv
                        .setBackgroundResource(R.drawable.download_hight);
                download_all_tv.setTextColor(getResources().getColor(
                        color.white));
                download_all_ll
                        .setBackgroundResource(R.drawable.download_all_bg);
            } else {
                download_all_iv.setBackgroundResource(R.drawable.download_grey);
                download_all_tv.setTextColor(getResources()
                        .getColor(color.grey));
                download_all_ll
                        .setBackgroundResource(R.drawable.download_all_bg_grey);
            }
        }

    }

    private void initView() {

        group = new ArrayList<MKOLSearchRecord>();
        child = new ArrayList<List<MKOLSearchRecord>>();
        // 获取已下过的离线地图信息
        localMapList = mOffline.getAllUpdateInfo();
        if (localMapList == null) {
            localMapList = new ArrayList<MKOLUpdateElement>();
        }
        // 获取所有支持离线地图的城市
        final ArrayList<MKOLSearchRecord> records2 = mOffline
                .getOfflineCityList();
        group = records2;
        for (MKOLSearchRecord bean : records2) {

            if (bean.childCities != null) {
                ArrayList<MKOLSearchRecord> list = new ArrayList<MKOLSearchRecord>();
                list.add(bean);
                for (MKOLSearchRecord bean_ : bean.childCities) {
                    list.add(bean_);
                }
                child.add(list);
            } else {
                ArrayList<MKOLSearchRecord> list = new ArrayList<MKOLSearchRecord>();
                child.add(list);
            }

        }
        InitCityExpandableListView();

        initLocalMapListView();
        if (localMapList.isEmpty()) {
            description_ll.setVisibility(View.VISIBLE);
            citylist_ll.setVisibility(View.GONE);
            localmap_layout.setVisibility(View.GONE);
        } else {
            localmap_layout.setVisibility(View.VISIBLE);
            citylist_ll.setVisibility(View.GONE);
            description_ll.setVisibility(View.GONE);
        }
    }
    /**
     * 初始化CityExpandableListView的item点击事件
     */
    private void InitCityExpandableListView() {
        cityExpandAdapter = new CityExpandableAdapter(this, group, child,
                mOffline);
        cityExpandable_lv.setAdapter(cityExpandAdapter);
        cityExpandable_lv.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView arg0, View view,
                                        int arg2, long arg3) {
                MKOLSearchRecord bean = group.get(arg2);
                if (bean.childCities == null) {
                    if (getDownloadingCitys() > 25) {
                        Log.i(Tag, "==-----" + getDownloadingCitys());
                        Toast.makeText(DownOffPageActivity.this, "超过最大同时下载数", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    if (!GetArrays().contains(1)) {
                        mOffline.start(1);
                    }
                    int cityid = bean.cityID;
                    if (GetArrays().contains(cityid)) {
                        Toast.makeText(DownOffPageActivity.this, "已添加到下载列表", 0)
                                .show();
                        ViewToDownLoad();
                    } else {
                        mOffline.start(cityid);
                        Toast.makeText(DownOffPageActivity.this, "已添加到下载任务", 0)
                                .show();
                    }

                    updateView();
                    cityExpandAdapter.notifyDataSetChanged();
                    return true;
                } else {
                    return false;// false 代表group展开
                }

            }
        });
        cityExpandable_lv.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView arg0, View view,
                                        int groupPosition, int childPosition, long arg4) {
                MKOLSearchRecord bean = child.get(groupPosition).get(
                        childPosition);
                int cityid = bean.cityID;
                if (!GetArrays().contains(1)) {
                    mOffline.start(1);
                }
                if (GetArrays().contains(cityid)) {

                    Toast.makeText(DownOffPageActivity.this, "已添加到下载列表", 0)
                            .show();
                    ViewToDownLoad();

                } else {
                    if (bean.cityType == 1) {
                        if (getDownloadingCitys() + bean.childCities.size() > 25) {
                            Log.i(Tag, "==-----" + getDownloadingCitys());
                            Toast.makeText(DownOffPageActivity.this, "超过最大同时下载数", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    } else {
                        if (getDownloadingCitys() > 25) {
                            Log.i(Tag, "==-----" + getDownloadingCitys());
                            Toast.makeText(DownOffPageActivity.this, "超过最大同时下载数", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    }
                    mOffline.start(cityid);
                    if (childPosition == 0) {
                        ViewToDownLoad();
                    }
                    Toast.makeText(DownOffPageActivity.this, "已添加到下载任务", 0)
                            .show();
                }
                updateView();
                cityExpandAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    /**
     * 初始化下载城市列表的listView
     */
    private void initLocalMapListView() {
        ListView localMapListView = (ListView) findViewById(R.id.localmaplist);
        lAdapter = new LocalMapAdapter();
        localMapListView.setAdapter(lAdapter);
        localMapListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                                    long arg3) {
                MKOLUpdateElement bean = localMapList.get(arg2);
                final int cityid = bean.cityID;
                TextView tv = (TextView) view.findViewById(R.id.citydelete_tv);

                if (bean.status == 1) {// 正在下载
                    mOffline.pause(cityid);
                    tv.setText("已暂停");

                } else if (bean.status == 2) {// 等待下载
                    mOffline.pause(cityid);
                    tv.setText("已暂停");
                } else if (bean.status == 3) {// 已暂停
                    mOffline.start(cityid);
                    tv.setText("暂停下载");
                }
                updateView();
                initBottomButtonBg();
            }
        });
        localMapListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                MKOLUpdateElement bean = localMapList.get(position);
                final int cityid = bean.cityID;
                showBackDialog(cityid);
                return false;
            }
        });
    }
    public ArrayList<Integer> GetArrays() {
        ArrayList<Integer> arrays = new ArrayList<Integer>();
        localMapList = mOffline.getAllUpdateInfo();
        if (localMapList==null) {
            localMapList =new ArrayList<MKOLUpdateElement>();
        }
        if (localMapList.size() != 0) {
            for (MKOLUpdateElement bean_ : localMapList) {
                arrays.add(bean_.cityID);
            }
        }
        return arrays;
    }

    private void showBackDialog(final int cityid) {
        View v = LayoutInflater.from(this).inflate(R.layout.back_dialog, null);
        final Dialog dialog_c = new Dialog(this, R.style.DialogStyle);
        dialog_c.setCanceledOnTouchOutside(true);
        dialog_c.show();
        Window window = dialog_c.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        lp.width = dip2px(this, 300); // 宽度
        lp.height = dip2px(this, 230); // 高度
        // lp.alpha = 0.7f; // 透明度
        window.setAttributes(lp);
        window.setContentView(v);
        TextView confirm_tv = (TextView) v.findViewById(R.id.confirm_tv);
        TextView cancel_tv = (TextView) v.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog_c.dismiss();
            }
        });
        confirm_tv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (cityid == -1) {
                    localMapList = mOffline.getAllUpdateInfo();
                    if (localMapList == null) {
                        localMapList = new ArrayList<MKOLUpdateElement>();
                    }
                    if (!localMapList.isEmpty()) {
                        for (MKOLUpdateElement bean : localMapList) {
                            mOffline.remove(bean.cityID);
                        }
                    }
                } else {
                    mOffline.remove(cityid);
                }
                dialog_c.dismiss();
                if (localMapList.isEmpty()) {
                    description_ll.setVisibility(View.VISIBLE);
                    citylist_ll.setVisibility(View.GONE);
                    localmap_layout.setVisibility(View.GONE);
                }
                updateView();
                initBottomButtonBg();

            }
        });

    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onPause() {
        pauseAllUnDownLoadCity();

        super.onPause();
    }

    /**
     * 将所有未下载完成的城市暂停
     */
    private void pauseAllUnDownLoadCity() {
        ArrayList<MKOLUpdateElement> localMapList = mOffline.getAllUpdateInfo();
        if (localMapList != null && !localMapList.isEmpty()) {
            for (MKOLUpdateElement bean : localMapList) {
                if (bean.status == 1 || bean.status == 2) {
                    mOffline.pause(bean.cityID);

                }
            }
        }
        updateView();
        initBottomButtonBg();
    }

    @Override
    protected void onDestroy() {
        /**
         * 退出时，销毁离线地图模块
         */
        mOffline.destroy();
        if (finishedCityIds!=null) {
            finishedCityIds.clear();
        }
        unregisterReceiver(myReceiver);
        super.onDestroy();
    }

    private void setOnclick() {
        download_back.setOnClickListener(this);
        citylist_top_tv1.setOnClickListener(this);
        citylist_top_tv2.setOnClickListener(this);

        delete_all_ll.setOnClickListener(this);
        download_all_ll.setOnClickListener(this);
        pause_all_ll.setOnClickListener(this);
    }

    /**
     * 开始下载所有的未下载完成的城市
     */
    private void startDownLoadAllCitys() {
        localMapList = mOffline.getAllUpdateInfo();
        if (localMapList==null) {
            localMapList =new ArrayList<MKOLUpdateElement>();
        }
        if (!localMapList.isEmpty()) {
            for (MKOLUpdateElement bean : localMapList) {
                if (bean.status == 3) {
                    mOffline.start(bean.cityID);
//					threadPoolUtil.downloadCity(bean.cityID);
                }else if (bean.status == 8) {
                    mOffline.remove(bean.cityID);
                    mOffline.start(bean.cityID);
                }
            }
            lAdapter.notifyDataSetChanged();
        }
        initBottomButtonBg();
    }

    /**
     * 获取正在下载或者等待下载的城市数量
     *
     * @return
     */
    private Integer getDownloadingCitys() {
        int downloadingSize = 0;
        localMapList = mOffline.getAllUpdateInfo();
        if (localMapList == null) {
            return downloadingSize;
        }

        if (!localMapList.isEmpty()) {
            for (MKOLUpdateElement bean : localMapList) {
                if (bean.status == 1 || bean.status == 2) {
                    downloadingSize++;
                }
            }
        }
        return downloadingSize;
    }

    public String formatDataSize(int size) {
        String ret = "";
        if (size < (1024 * 1024)) {
            ret = String.format("%dK", size / 1024);
        } else {
            ret = String.format("%.1fM", size / (1024 * 1024.0));
        }
        return ret;
    }

    /**
     * 更新下载管理列表
     */
    public void updateView() {
        localMapList = mOffline.getAllUpdateInfo();
        if (localMapList == null) {
            localMapList = new ArrayList<MKOLUpdateElement>();
        }
        lAdapter.notifyDataSetChanged();
    }

    private void findView() {

        download_all_tv = (TextView) findViewById(R.id.download_all_tv);
        pause_all_tv = (TextView) findViewById(R.id.pause_all_tv);

        download_all_iv = (ImageView) findViewById(R.id.download_all_iv);
        pause_all_iv = (ImageView) findViewById(R.id.pause_all_iv);
        delete_all_ll = (LinearLayout) findViewById(R.id.delete_all_ll);
        download_all_ll = (LinearLayout) findViewById(R.id.download_all_ll);
        pause_all_ll = (LinearLayout) findViewById(R.id.pause_all_ll);

        download_back = (ImageView) findViewById(R.id.download_back);
        citylist_top_tv1 = (TextView) findViewById(R.id.citylist_top_tv1);
        citylist_top_tv2 = (TextView) findViewById(R.id.citylist_top_tv2);
        localmaplist = (ListView) findViewById(R.id.localmaplist);
        citylist_ll = (LinearLayout) findViewById(R.id.citylist_ll);
        localmap_layout = (RelativeLayout) findViewById(R.id.localmap_layout);
        description_ll = (LinearLayout) findViewById(R.id.description_ll);
        cityExpandable_lv = (ExpandableListView) findViewById(R.id.cityExpandable_lv);
        cityExpandable_lv.setGroupIndicator(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download_back:
                finish();
                break;
            case R.id.citylist_top_tv1:// 城市列表
                citylist_ll.setVisibility(View.VISIBLE);
                localmap_layout.setVisibility(View.GONE);
                description_ll.setVisibility(View.GONE);
                // isdownloadpressed = false;
                cityExpandAdapter.notifyDataSetChanged();
                citylist_top_tv1.setBackgroundResource(R.drawable.citylist_press);
                citylist_top_tv2.setBackgroundResource(R.drawable.download_normal);
                citylist_top_tv1.setTextColor(getResources().getColor(
                        color.text_new_tag));
                citylist_top_tv2.setTextColor(getResources().getColor(color.blue));


                break;
            case R.id.citylist_top_tv2:// 下载管理
                // isdownloadpressed = true;
                ViewToDownLoad();
                break;
            case R.id.download_all_ll:// 全部下载点击事件
                startDownLoadAllCitys();
                break;
            case R.id.delete_all_ll:// 全部删除点击事件
                showBackDialog(-1);
                break;
            case R.id.pause_all_ll:// 全部暂停点击事件
                pauseAllUnDownLoadCity();
                break;
            default:
                break;
        }

    }

    /**
     * 切换到下载管理界面
     */

    private void ViewToDownLoad() {
        citylist_top_tv1.setBackgroundResource(R.drawable.citylist_normal);
        citylist_top_tv2.setBackgroundResource(R.drawable.download_press);
        citylist_top_tv1.setTextColor(getResources().getColor(color.blue));
        citylist_top_tv2.setTextColor(getResources().getColor(
                color.text_new_tag));
        updateView();
        if (localMapList.isEmpty()) {
            description_ll.setVisibility(View.VISIBLE);
            citylist_ll.setVisibility(View.GONE);
            localmap_layout.setVisibility(View.GONE);
        } else {
            localmap_layout.setVisibility(View.VISIBLE);
            citylist_ll.setVisibility(View.GONE);
            description_ll.setVisibility(View.GONE);
        }
        initBottomButtonBg();

    }

    /**
     * 离线地图管理列表适配器
     */
    public class LocalMapAdapter extends BaseAdapter {
        public LocalMapAdapter() {
            if (localMapList!=null) {
                for (MKOLUpdateElement mkolUpdateElement : localMapList) {
                    if (mkolUpdateElement.status==4||mkolUpdateElement.status==10) {
                        if (!finishedCityIds.contains(mkolUpdateElement.cityID)) {
                            finishedCityIds.add(mkolUpdateElement.cityID);
                        }
                    }
                }

            }

        }

        @Override
        public int getCount() {
            return localMapList == null ? 0 : localMapList.size();
        }

        @Override
        public Object getItem(int index) {
            return localMapList.get(index);
        }

        @Override
        public long getItemId(int index) {
            return index;
        }

        @Override
        public View getView(int index, View view, ViewGroup arg2) {
            ViewHolder holder = null;
            if (view == null) {
                view = LayoutInflater.from(DownOffPageActivity.this).inflate(
                        R.layout.download_item, null);
                holder = new ViewHolder();
                holder.cityname_tv = (TextView) view
                        .findViewById(R.id.cityname_tv);
                holder.citysize_tv = (TextView) view
                        .findViewById(R.id.citysize_tv);
                holder.cityprogress_tv = (TextView) view
                        .findViewById(R.id.cityprogress_tv);
                holder.citydelete_tv = (TextView) view
                        .findViewById(R.id.citydelete_tv);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            MKOLUpdateElement e = localMapList.get(index);

            initViewItem(holder, e);
            return view;
        }

        public void initViewItem(ViewHolder holder, final MKOLUpdateElement e) {

            holder.cityprogress_tv.setText(e.ratio + "%");
            holder.cityname_tv.setText(e.cityName);
            holder.citysize_tv.setText(formatDataSize(e.size));
            if (e.status == 1) {// 正在下载
                holder.citydelete_tv.setText("暂停下载");
            } else if (e.status == 2) {// 等待下载
                holder.citydelete_tv.setText("等待下载");
            } else if (e.status == 3||e.status == 8) {// 已暂停
                holder.citydelete_tv.setText("已暂停");
            } else if (e.status == 4 || e.status == 10) {// 下载完成
                holder.citydelete_tv.setText("已完成");
                holder.cityprogress_tv.setText("");
                if (!finishedCityIds.contains(e.cityID)) {
                    finishedCityIds.add(e.cityID);
                    initBottomButtonBg();
                }


            }
        }

        class ViewHolder {

            private TextView cityname_tv;
            private TextView citysize_tv;
            private TextView cityprogress_tv;
            private TextView citydelete_tv;

            public ViewHolder() {

            }


        }

    }

    @Override
    public void onGetOfflineMapState(int type, int state) {

        switch (type) {
            case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
                MKOLUpdateElement update = mOffline.getUpdateInfo(state);
                // 处理下载进度更新提示
                if (update != null) {
                    updateView();
                    cityExpandAdapter.notifyDataSetChanged();
                }
            }
            break;
            case MKOfflineMap.TYPE_NEW_OFFLINE:
                // 有新离线地图安装
                Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));
                break;
            case MKOfflineMap.TYPE_VER_UPDATE:
                // 版本更新提示
                // MKOLUpdateElement e = mOffline.getUpdateInfo(state);

                break;
            default:
                break;
        }

    }

    /**
     * 监听网络状态广播
     */
    public class ConnectionChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (!wifiNetInfo.isConnected()) {//网络断开时，将所有任务暂停
                pauseAllUnDownLoadCity();
            } else {//网络恢复后，开始所有未完成任务
                startDownLoadAllCitys();
            }
        }
    }

}

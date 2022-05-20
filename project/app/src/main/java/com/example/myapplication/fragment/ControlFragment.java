package com.example.myapplication.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.provider.Settings;
import androidx.fragment.app.Fragment;
import android.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.hmspicker.HmsPickerBuilder;
import com.codetroopers.betterpickers.hmspicker.HmsPickerDialogFragment;
import com.kyleduo.switchbutton.SwitchButton;

import com.example.myapplication.R;
import com.example.myapplication.base.IOnFocusListenable;
import com.example.myapplication.receiver.CountDownFinishReceiver;
import com.example.myapplication.receiver.NotifyUserReceiver;
import com.example.myapplication.receiver.UpdateTimerReceiver;
import com.example.myapplication.service.CountDownService;
import com.example.myapplication.service.MonitorAppsService;
import com.example.myapplication.view.NotifyLayout;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ControlFragment extends BaseFragment implements IOnFocusListenable {
    public static Fragment me;
    private View mView;
    private LinearLayout linelayoutCountTime;

    private TextView tvHour;
    private TextView tvMinute;
    private TextView tvSecond;
    private TextView tvTotalTime;
    private Button btnStartCount;

    private NotifyLayout notifyLayout;
    private WindowManager.LayoutParams notifyParams;


    private UpdateTimerReceiver updateTimerReceiver;
    private NotifyUserReceiver notifyUserReceiver;
    private CountDownFinishReceiver countFinishReceiver;
    private MonitorAppsService monitorAppsService;
    private WindowManager mWindowManager;
    private boolean isPrepared;
    /**
     * 首次点击屏幕的时间，用来判断双击使用
     */
    private long startTime;
    private boolean isClickStart = false;

    public boolean isZeroTime() {
        String time = tvHour.getText().toString()
                + tvMinute.getText().toString() + tvSecond.getText().toString();
        if (time.equals("000000")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isSetedCountTime = false;

    public void setSetedCountTime(boolean set) {
        isSetedCountTime = set;
    }

    /**
     *
     * @return 若已设置完时间返回True，否则返回False
     */
    public boolean getIsSetedCountTime() {
        return isSetedCountTime;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        me=this;
        mView = inflater.inflate(R.layout.fragment_control, container, false);
        initViews();
        register();
        loadLocalData();
        return mView;
    }

    private void register() {
        registerCountDownReceiver();
        registerCountFinishReceiver();
        registerNotifyUserReceiver();
    }

    /**
     * 注册监听倒计时更新的广播
     */
    private void registerCountDownReceiver() {
        updateTimerReceiver = new UpdateTimerReceiver();
        IntentFilter filter = new IntentFilter(this.getString(R.string.countDownAction));
        getActivity().registerReceiver(updateTimerReceiver, filter);
    }

    /**
     * 注册监听倒计时更新的广播
     */
    private void registerMonitorApppsReceiver() {
        monitorAppsService = new MonitorAppsService();
        IntentFilter filter = new IntentFilter(this.getString(R.string.countDownAction));
        getActivity().registerReceiver(updateTimerReceiver, filter);
    }

    /**
     * 注册倒计时完成广播
     */
    private void registerCountFinishReceiver() {
        countFinishReceiver = new CountDownFinishReceiver();
        IntentFilter filter = new IntentFilter(this.getString(R.string.countFinishAction));
        getActivity().registerReceiver(countFinishReceiver, filter);
    }

    /**
     * 注册监听用户打开的APP信息广播
     */
    private void registerNotifyUserReceiver() {
        notifyUserReceiver = new NotifyUserReceiver();
        IntentFilter filter = new IntentFilter(this.getString(R.string.notify_user_action));
        getActivity().registerReceiver(notifyUserReceiver, filter);
    }


    private void initViews() {

        SwitchButton mSwitchButton = (SwitchButton) mView.findViewById(R.id.switchButton);
        mSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    if (!MonitorAppsService.isAccessibilitySettingsOn(getActivity()))
                        showNoPermission();
                    //判断任务
                    SharedPreferences sp = getContext().getSharedPreferences("suc",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    int f3 = sp.getInt("f3",3);
                    if (f3==1){
                        editor.putInt("f3",2);
                        editor.commit();
                }

            }
        });

        tvHour = (TextView) mView.findViewById(R.id.tv_hour);
        tvMinute = (TextView) mView.findViewById(R.id.tv_minute);
        tvSecond = (TextView) mView.findViewById(R.id.tv_second);
        linelayoutCountTime= (LinearLayout) mView.findViewById(R.id.linlayout_count_time);
        linelayoutCountTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HmsPickerBuilder hpb = new HmsPickerBuilder()
                        .setFragmentManager(getActivity().getSupportFragmentManager())
                        .setStyleResId(R.style.BetterPickersDialogFragment)
                        .addHmsPickerDialogHandler(new HmsPickerDialogFragment.HmsPickerDialogHandlerV2() {
                            @Override
                            public void onDialogHmsSet(int reference, boolean isNegative, int hours, int minutes, int seconds) {
                                tvHour.setText(hours+"");
                                tvMinute.setText(minutes+"");
                                tvSecond.setText(seconds+"");
                                Toast.makeText(getActivity(),hours+":"+minutes+":"+seconds,Toast.LENGTH_SHORT).show();
                            }
                        });
                hpb.show();
            }
        });

        /**
         *开启专注模式
         */
        btnStartCount = (Button) mView.findViewById(R.id.btn_start_count);
        btnStartCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isZeroTime() && !isSetedCountTime) {
                    stratMonitorService();
                    countServiceEnable();
                }
            }

        });
    }

    /**
     * 启动后台监测正在运行的程序服务
     */
    public void stratMonitorService() {
        if (MonitorAppsService.isAccessibilitySettingsOn(getActivity())) {
            MonitorAppsService.getInstance();
        } else {
            showNoPermission();
        }
    }

    private AlertDialog.Builder noPermissionDialog = null;

    /**
     * 提示并引导用户开启辅助权限
     */
    private void showNoPermission() {
        if (noPermissionDialog == null) {
            noPermissionDialog = new AlertDialog.Builder(getActivity())
                    .setTitle(this.getString(R.string.no_permission))
                    .setMessage(this.getString(R.string.no_permission_advice))
                    .setPositiveButton(this.getString(R.string.go_open), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(this.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
        }
        noPermissionDialog.show();
    }

    /**
     * 先判断是否开启辅助权限，若已开启，则直接启动倒计时服务，若还没开启，等待开启完后再开启倒计时服务
     */
    private void countServiceEnable() {
        if (MonitorAppsService.isAccessibilitySettingsOn(getActivity())) {
            startCountService();
        } else {
            setClickStart(true);
        }
    }
    /**
     * 启动倒计时后台服务
     */
    public void startCountService() {
        Log.d("dddd","ddddd");
        Intent intent = new Intent(getActivity(), CountDownService.class);
        intent.putExtra(this.getString(R.string.countHour), Long.parseLong(tvHour.getText().toString()));
        intent.putExtra(this.getString(R.string.countMinute), Long.parseLong(tvMinute.getText().toString()));
        intent.putExtra(this.getString(R.string.countSecond), Long.parseLong(tvSecond.getText().toString()));
        //启动服务
        getActivity().startService(intent);

    }





    /**
     * 取得系统窗体
     */
    private void retrieveSystemWindowManager() {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) getActivity().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        }
    }

    public void setClickStart(boolean clickStart) {
        isClickStart = clickStart;
    }

    public boolean isClickStart() {
        return isClickStart;
    }


    private  int top;
    @Override
    public void onWindowFocusChanged(int top) {
        this.top=top;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            getActivity().moveTaskToBack(true);
            return true;
        }
        return false;
    }

    /**
     * 设置TextView 小时的值
     *
     * @param text
     */
    public void setTvHour(String text) {
        if (tvHour != null) {
            tvHour.setText(text);
        }
    }

    /**
     * 设置TextView 分钟的值
     *
     * @param text
     */
    public void setTvMinute(String text) {
        if (tvMinute != null) {
            tvMinute.setText(text);
        }
    }

    /**
     * 设置TextView 秒的值
     *
     * @param text
     */

    public void setTvSecond(String text) {
        if (tvSecond != null) {
            tvSecond.setText(text);
        }
    }


    /**
     * 设置了累计时间
     */
    public void setTvTotalTime(String text) {
        if (text != null) {
            tvTotalTime.setText(text);
        }
    }

    /**
     * 取回本地SharedPreferences累计时间的数据
     */
    private void retrieveTotalTimeData() {
        SharedPreferences preferences = getActivity().getSharedPreferences("myPref2", MODE_PRIVATE);  //当前程序才能读取
        int day = preferences.getInt("day", 0);
        int hour = preferences.getInt("hour", 0);
        int minute = preferences.getInt("minute", 0);
        int second = preferences.getInt("second", 0);
        String totalTime ="累计学习了" +day + "天" + hour + "小时" + minute + "分钟" + second + "秒";
        tvTotalTime = (TextView) mView.findViewById(R.id.tv_total_time);
        tvTotalTime.setText(totalTime);
    }

    /**
     * 加载本地数据，如取出SharedPreferences、SQLite等数据
     */
    private void loadLocalData() {
        retrieveTotalTimeData();
    }

    /**
     * 关闭其他应用，实际上并没有关闭应用，只是模拟点击Home键，将屏幕退回到主界面，以此模拟实现关闭效果
     *
     * @param packageName 要关闭的应用的包名
     */
    public void closeApp(String packageName) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        (getActivity().getApplicationContext()).startActivity(intent);
        Log.d(TAG, "关闭应用:" + packageName);
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        getActivity().getApplicationContext().unregisterReceiver(countFinishReceiver);
//        getActivity().getApplicationContext().unregisterReceiver(notifyUserReceiver);
//        getActivity().getApplicationContext().unregisterReceiver(updateTimerReceiver);
//    }

    @Override
    protected void lazyLoad()
    {
        if(!isPrepared || !isVisible) {
            return;
        }
        //填充各控件
    }

}
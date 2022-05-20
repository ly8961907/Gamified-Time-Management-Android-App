package com.example.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;


import com.example.myapplication.activity.BaseFragmentActivity;
import com.example.myapplication.base.IListener;
import com.example.myapplication.base.ListenerManager;
import com.example.myapplication.base.UITools;
import com.example.myapplication.fragment.ControlFragment;
import com.example.myapplication.fragment.MoreFragment;
import com.example.myapplication.fragment.PlanFragment;
import com.example.myapplication.fragment.StatisticFragment;
import com.example.myapplication.fragment.TodayFragment;
import com.example.myapplication.fragment.task.DB;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends BaseFragmentActivity implements View.OnClickListener,IListener{


    private FragmentManager fragmentManager;
    public ViewPager viewPager_content;
    private TextView txt_bottom_plan;
    private TextView txt_bottom_control;
    private TextView txt_bottom_statistic;
    private TextView txt_bottom_more;
    private TextView txt_bottom_today;
    private ImageView title_more, title_change;
    Handler mHandler;

    private AnimatorSet mAnimatorSetLeft, mAnimatorSetRight;
    private ObjectAnimator mItemsliding;
    private ObjectAnimator mItemsAlpha;


    private final int TAB_PLAN = 0;
    private final int TAB_TODAY = 1;
    private final int TAB_CONTROL = 2;
    private final int TAB_STATISTICS = 3;
    private final int TAB_MORE = 4;
    private int IsTab;

    private PlanFragment planFragment;
    private ControlFragment controlFragment;
    private StatisticFragment statisticFragment;
    private MoreFragment moreFragment;
    private TodayFragment todayFragment;

    private FragmentAdapter adapter;

    private HorizontalScrollView horizontalScrollView;
    private LinearLayout container;
    private Integer mImgIds[] = new Integer[]{R.drawable.paster1, R.drawable.paster2, R.drawable.paster3, R.drawable.paster4,
            R.drawable.paster5, R.drawable.paster6};
    private ArrayList<Integer> data;
    private TextView noPaster;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListenerManager.getInstance().registerListtener(this);

        initID();//初始化绑定组件id
        initView();//初始化视图
        loadPaster();
    }

    private void loadPaster() {
        DB db = new DB(MainActivity.this, DB.DB_NAME, null, 1);
        data = db.get();
        if (data.size()==0){
            noPaster.setVisibility(View.VISIBLE);
        }else {
            Log.e("s",data.size()+"");
            noPaster.setVisibility(View.GONE);
            container.removeAllViews();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.setMargins(20, 0, 20, 0);
            for (int i=0;i<data.size();i++){
                ImageView imageView = new ImageView(this);
                imageView.setImageResource(mImgIds[data.get(i)]);
                imageView.setLayoutParams(layoutParams);
                container.addView(imageView);
                container.invalidate();
            }
        }

    }

    //初始化控件加载
    public void initID() {
        TextView title_back = (TextView) findViewById(R.id.title_back);//隐藏后退键
        title_back.setVisibility(View.GONE);
        noPaster = findViewById(R.id.noPasterView);
        data = new ArrayList<>();

        /**
         * 绑定控件
         */
        viewPager_content = (ViewPager) findViewById(R.id.viewPager_content);
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        container = (LinearLayout) findViewById(R.id.horizontalScrollViewItemContainer);
        txt_bottom_plan = (TextView) findViewById(R.id.txt_bottom_plan);
        txt_bottom_control = (TextView) findViewById(R.id.txt_bottom_control);
        txt_bottom_statistic = (TextView) findViewById(R.id.txt_bottom_statistic);
        txt_bottom_more = (TextView) findViewById(R.id.txt_bottom_more);
        txt_bottom_today = (TextView) findViewById(R.id.txt_bottom_today);

        Collections.addAll(data, mImgIds);
        /**
         * 为贴纸框添加动画拖动效果
         */
        UITools.elasticPadding(horizontalScrollView, 300); // 可选 为左右回弹效果实现
        mAnimatorSetLeft = new AnimatorSet();
        mAnimatorSetRight = new AnimatorSet();
        mItemsliding = ObjectAnimator.ofFloat(container,"translationX",0,-300);
        mItemsAlpha = ObjectAnimator.ofFloat(container,"alpha",1,1);
        mAnimatorSetLeft.setDuration(0);
        mAnimatorSetLeft.play(mItemsliding).with(mItemsAlpha);
        mAnimatorSetLeft.start();
        mAnimatorSetLeft.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mItemsliding = ObjectAnimator.ofFloat(container,"translationX",-300,0);
                mItemsAlpha = ObjectAnimator.ofFloat(container,"alpha",1,1);
                mAnimatorSetRight.setStartDelay(500);
                mAnimatorSetRight.setDuration(500);
                mAnimatorSetRight.play(mItemsliding).with(mItemsAlpha);
                mAnimatorSetRight.start();
            }
        });





        title_more = (ImageView) findViewById(R.id.title_more);
        title_change = (ImageView) findViewById(R.id.title_change);

        /*title_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CreatepActivity.class));
            }
        });*/
        txt_bottom_plan.setOnClickListener(this);
        txt_bottom_control.setOnClickListener(this);
        txt_bottom_statistic.setOnClickListener(this);
        txt_bottom_more.setOnClickListener(this);
        txt_bottom_today.setOnClickListener(this);

        title_more.setOnClickListener(this);
        title_change.setOnClickListener(this);

        /**
         * Fragment之间的切换
         */
        viewPager_content.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.i("main_viewpager", "position--" + position);
                switch (position) {
                    case TAB_PLAN://点击首页模块执行
                        IsTab = 1;
                        jumpPlanFragment();
                        break;
                    case TAB_TODAY://点击首页模块执行
                        IsTab = 2;
                        jumpTodayFragment();
                        break;
                    case TAB_CONTROL://点击巡店模块执行
                        IsTab = 3;
                        jumpControlFragment();
                        break;
                    case TAB_STATISTICS://点击拜访模块执行
                        IsTab = 4;
                        jumpStatisticFragment();
                        break;
                    case TAB_MORE://点击培训模块执行
                        IsTab = 5;
                        jumpMoreFragment();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    //初始化视图,默认显示首界面
    public void initView() {

        setSelected(txt_bottom_plan);//强调按钮
        setTitleName("任务计划");//改标题

        planFragment = new PlanFragment();
        statisticFragment = new StatisticFragment();
        controlFragment = new ControlFragment();
        moreFragment = new MoreFragment();
        todayFragment = new TodayFragment();


        adapter = new FragmentAdapter(getSupportFragmentManager());


        Bundle args = new Bundle();
        args.putString("date",planFragment.getDate());
        todayFragment.setArguments(args);
        statisticFragment.setArguments(args);

        /**
         * 为ViewPager添加Fragment适配器
         */
        viewPager_content.setAdapter(adapter);
        viewPager_content.setOffscreenPageLimit(1);



    }

    // @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.txt_bottom_plan://点击任务计划模块执行
                IsTab = 1;
                jumpPlanFragment();
                break;
            case R.id.txt_bottom_today://点击手机控制模块执行
                IsTab = 2;
                jumpTodayFragment();
                break;
            case R.id.txt_bottom_control://点击手机控制模块执行
                IsTab = 3;
                jumpControlFragment();
                break;
            case R.id.txt_bottom_statistic://点击培训模块执行
                IsTab = 4;
                jumpStatisticFragment();
                break;
            case R.id.txt_bottom_more://点击个人中心模块执行
                IsTab = 5;
                jumpMoreFragment();
                break;
            case R.id.title_more:
                break;
            default:
                break;
        }
    }




    //当选中的时候变色,改变底部文字颜色
    public void setSelected(TextView textView) {

        txt_bottom_plan.setSelected(false);
        txt_bottom_control.setSelected(false);
        txt_bottom_statistic.setSelected(false);
        txt_bottom_more.setSelected(false);
        txt_bottom_today.setSelected(false);
        textView.setSelected(true);

    }

    /*
     * 模块Fragment适配器
     */
    public class FragmentAdapter extends FragmentPagerAdapter {
        private final int TAB_COUNT = 5;

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int id) {
            switch (id) {
                case TAB_PLAN:
                    return planFragment;
                case TAB_TODAY:
                    return todayFragment;
                case TAB_CONTROL:
                    return controlFragment;
                case TAB_STATISTICS:
                    return statisticFragment;
                case TAB_MORE:
                    return moreFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //监听返回键，如果当前界面不是首界面，或没切换过界面，切到首界面
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (IsTab != 1) {
                IsTab = 1;
                jumpPlanFragment();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Rect rect = new Rect();
        // /取得整个视图部分,注意，如果你要设置标题样式，这个必须出现在标题样式之后，否则会出错
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int top = rect.top;

    }



    /**
     * 显示主界面TodayFragemnt
     */
    private void jumpPlanFragment() {
        title_more.setVisibility(View.GONE);
        title_change.setVisibility(View.GONE);
        setSelected(txt_bottom_plan);
        viewPager_content.setCurrentItem(TAB_PLAN, false);
        setTitleName("任务");
    }

    /**
     * 显示ControlFragment
     */
    public void jumpControlFragment() {
        title_more.setVisibility(View.GONE);
        title_change.setVisibility(View.GONE);
        setSelected(txt_bottom_control);

        Bundle args = new Bundle();
        args.putString("date",planFragment.getDate());
        statisticFragment.setArguments(args);

        viewPager_content.setCurrentItem(TAB_CONTROL, false);
        setTitleName("手机控制");
    }

    /**
     * 显示StatisticFragment,提供给新建拜访完成后调用
     */
    public void jumpStatisticFragment() {
        title_more.setVisibility(View.GONE);
        title_change.setVisibility(View.GONE);
        setSelected(txt_bottom_statistic);

        Bundle args = new Bundle();
        args.putString("date",planFragment.getDate());
        statisticFragment.setArguments(args);

        viewPager_content.setCurrentItem(TAB_STATISTICS, false);
        setTitleName("统计");

    }

    /**
     * 显示MoreFragment
     */
    public void jumpMoreFragment() {
        title_more.setVisibility(View.GONE);
        title_change.setVisibility(View.GONE);
        setSelected(txt_bottom_more);

        Bundle args = new Bundle();
        args.putString("date",planFragment.getDate());
        statisticFragment.setArguments(args);

        viewPager_content.setCurrentItem(TAB_MORE, false);
        setTitleName("更多");
    }

    /**
     * 显示TodayFragment
     */
    public void jumpTodayFragment() {
        title_more.setVisibility(View.GONE);
        title_change.setVisibility(View.GONE);
        setSelected(txt_bottom_today);


        Bundle args = new Bundle();
        args.putString("date",planFragment.getDate());
        todayFragment.setArguments(args);
//        todayFragment.date = PlanFragment.date;
//
//        String s= PlanFragment.date;
//        Message msg = new Message();
//        msg.what = 1;
//        msg.obj = s;
//        mHandler.sendMessage(msg);
        viewPager_content.setCurrentItem(TAB_TODAY, false);
        setTitleName("今日");

    }

    @Override
    public void notifyAllActivity(int flag, String str, String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadPaster();
            }
        });
    }


}
package com.example.myapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.base.SetItemView;
import com.example.myapplication.activity.QuestActivity;

public class MoreFragment extends BaseFragment {
    private View mView;
    private SetItemView mQuestItem;
    private SetItemView mAboutItem;
    private SetItemView mExitItem;
    private SetItemView mFeedbackItem;
    private boolean isPrepared;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //加载布局
        mView = inflater.inflate(R.layout.fragment_more, container, false);
        isPrepared = true;
        lazyLoad();
        initView();

        return mView;
    }

    /**
     * 初始化控件信息
     */
    private void initView() {
        mExitItem=(SetItemView) mView.findViewById(R.id.rl_exit);
        mAboutItem = (SetItemView) mView.findViewById(R.id.rl_about);
        mFeedbackItem = (SetItemView) mView.findViewById(R.id.rl_feedback);
        mQuestItem = (SetItemView) mView.findViewById(R.id.rl_quest);

        mQuestItem.setmOnSetItemClick(new SetItemView.OnSetItemClick() {
            @Override
            public void click() {
                Toast.makeText(mActivity, "点击了任务", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(),QuestActivity.class);
                startActivity(intent);
            }
        });

        mFeedbackItem.setmOnSetItemClick(new SetItemView.OnSetItemClick() {
            @Override
            public void click() {
                Toast.makeText(mActivity, "点击了意见反馈", Toast.LENGTH_SHORT).show();
            }
        });
        mAboutItem.setmOnSetItemClick(new SetItemView.OnSetItemClick() {
            @Override
            public void click() {
                Toast.makeText(mActivity, "点击了关于", Toast.LENGTH_SHORT).show();
            }
        });
        mExitItem.setmOnSetItemClick(new SetItemView.OnSetItemClick() {
            @Override
            public void click() {
                getActivity().finish();
                Toast.makeText(mActivity, "点击了退出", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void lazyLoad()
    {
        if(!isPrepared || !isVisible) {
            return;
        }
        //填充各控件
    }

}
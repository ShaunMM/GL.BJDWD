package com.bjdwd.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bjdwd.R;
import com.bjdwd.activitys.HomeActivity;
import com.bjdwd.activitys.LoginActivity;
import com.bjdwd.activitys.MainActivity;
import com.bjdwd.activitys.NoteActivity;
import com.bjdwd.interfaces.FragmentBackListener;

/**
 * Created by dell on 2017/3/27.
 * 系统工具
 */
public class SystemToolsFragment extends Fragment implements View.OnClickListener, FragmentBackListener {
    private View view;
    private Intent intentsystem;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeActivity) {
            ((HomeActivity) context).setBackListener(this);
            ((HomeActivity) context).setInterception(true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_systemtools, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.system_tools1).setOnClickListener(this);//日历1
        view.findViewById(R.id.system_tools2).setOnClickListener(this);//计算器1
        view.findViewById(R.id.system_tools3).setOnClickListener(this);//记事本1
        view.findViewById(R.id.system_tools4).setOnClickListener(this);//拍照1
        view.findViewById(R.id.system_tools5).setOnClickListener(this);//录音1
        view.findViewById(R.id.system_tools6).setOnClickListener(this);//录像1
        view.findViewById(R.id.system_tools7).setOnClickListener(this);//退出系统1

    }

    @Override
    public void onClick(View view) {
        intentsystem = new Intent();
        switch (view.getId()) {
            case R.id.system_tools1:
                //日历
                intentsystem.setComponent(new ComponentName("com.android.calendar", "com.android.calendar.LaunchActivity"));
                intentsystem.setFlags(Intent.EXTRA_DOCK_STATE_DESK);
                startActivity(intentsystem);
                break;
            case R.id.system_tools2:
                //计算器
                intentsystem.setClassName("com.android.calculator2", "com.android.calculator2.Calculator");
                intentsystem.setFlags(Intent.EXTRA_DOCK_STATE_DESK);
                startActivity(intentsystem);
                break;
            case R.id.system_tools3:
                //记事本
                intentsystem = new Intent(getActivity(), NoteActivity.class);
                startActivity(intentsystem);
                break;
            case R.id.system_tools4:
                //拍照
                intentsystem = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivity(intentsystem);
                break;
            case R.id.system_tools5:
                //录音
                intentsystem = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                startActivity(intentsystem);
                break;
            case R.id.system_tools6:
                //录像
                intentsystem.setAction("android.media.action.VIDEO_CAPTURE");
                intentsystem.addCategory("android.intent.category.DEFAULT");
                startActivity(intentsystem);
                break;
            case R.id.system_tools7:
                //退出系统
                intentsystem = new Intent(getActivity(), LoginActivity.class);
                getActivity().finish();
                startActivity(intentsystem);
                break;
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getActivity() instanceof HomeActivity) {
            ((HomeActivity) getActivity()).setBackListener(null);
            ((HomeActivity) getActivity()).setInterception(false);
        }
    }

    @Override
    public void onbackForward() {
        Intent intent = new Intent();
        intent.setClass(getContext(), MainActivity.class);
        startActivity(intent);
        ((HomeActivity) getActivity()).finish();

    }
}
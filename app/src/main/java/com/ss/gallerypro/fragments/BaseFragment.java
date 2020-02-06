package com.ss.gallerypro.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ss.gallerypro.setting.callback.ThemeChangeObserver;
import com.ss.gallerypro.theme.ColorTheme;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment implements ThemeChangeObserver {
    protected Unbinder unbinder;
    //protected Activity mAttachedActivity;
    protected ColorTheme mColorTheme;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        //mAttachedActivity = getActivity();
        super.onCreate(savedInstanceState);
        mColorTheme = new ColorTheme(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        // bind view using butter knife
        if (view != null) {
            unbinder = ButterKnife.bind(this, view);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(unbinder != null) {
            unbinder.unbind();
        }
    }

    protected abstract int getLayoutId();

    @Override
    public void requestUpdateTheme() {

    }
}


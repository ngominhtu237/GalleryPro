package com.tunm.gallerypro.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tunm.gallerypro.DrawerLocker;
import com.tunm.gallerypro.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutUsFragment extends Fragment {


    public AboutUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        ((DrawerLocker) getActivity()).setDrawerEnabled(false);
        return inflater.inflate(R.layout.fragment_about_us, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

package com.ss.gallerypro.fragments.viewer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ss.gallerypro.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimeViewFragment extends Fragment {


    public TimeViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time_view, container, false);
    }

}

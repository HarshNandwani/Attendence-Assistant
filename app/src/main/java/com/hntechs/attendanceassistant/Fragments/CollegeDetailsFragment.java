package com.hntechs.attendanceassistant.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hntechs.attendanceassistant.R;


public class CollegeDetailsFragment extends Fragment {

    private View view;

    public CollegeDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view= inflater.inflate(R.layout.fragment_college_details, container, false);

        return view;
    }

}

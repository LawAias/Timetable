package com.gd.timetable.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gd.timetable.R;


/**
 * 关于页面
 *
 */
public class AboutFragment extends Fragment {

//	private static final String TAG = AboutFragment.class.getSimpleName();

	private View rootView;

	private static AboutFragment instance;

	public static AboutFragment getInstance() {
		if (instance == null) instance = new AboutFragment();
		return instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_about, container, false);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

}

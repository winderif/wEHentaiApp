package com.example.ehentaiapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.example.ehentaiapp.Filter;

public abstract class BaseFragment extends Fragment {
	protected Filter filter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		filter = new Filter();
		
//		setHasOptionsMenu(true);
	}
}

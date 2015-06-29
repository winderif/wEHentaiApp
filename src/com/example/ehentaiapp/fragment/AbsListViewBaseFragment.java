package com.example.ehentaiapp.fragment;

import android.app.ProgressDialog;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.Toast;

public abstract class AbsListViewBaseFragment extends BaseFragment {

	protected AbsListView listView;
	protected ProgressDialog mLoadingDialog;
	protected int mark;
	
	@Override
	public void onResume() {
		super.onResume();
		applyScrollListener();
	}
	
	@Override
	public void onPause() {
		dismissProgressDialog();
		super.onPause();
	}
	
	@Override
    public void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }
	
	protected void showProgressDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new ProgressDialog(getActivity());
            mLoadingDialog.setTitle("Please wait");
			mLoadingDialog.setMessage("Loading...");
            mLoadingDialog.setCancelable(false);
        }
        mLoadingDialog.show();
    }
	
	protected void dismissProgressDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }
	
//	protected abstract void refreshView();
	
	protected abstract void applyScrollListener();
	
	protected void showToast(String msg) {
    	Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
	
	protected void setActionBarTitle() {
		getActivity().getActionBar().setTitle("-/-");
	}
	
	protected void setActionBarTitle(int index, int total) {
		getActivity().getActionBar().setTitle("Page:" + index + "/" + total);
	}
}

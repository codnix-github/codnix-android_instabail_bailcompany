package com.bailcompany.custom;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

import com.bailcompany.R;

// TODO: Auto-generated Javadoc
/**
 * The Class CustomFragment.
 */
public class CustomFragment extends Fragment implements OnClickListener {

	/**
	 * Set the touch and click listener for a View.
	 * 
	 * @param v
	 *            the view
	 * @return the same view
	 */

	ProgressDialog pDialog;

	public View setTouchNClick(View v) {

		v.setOnClickListener(this);
		v.setOnTouchListener(CustomActivity.TOUCH);
		return v;
	}

	public View setClick(View v) {
		v.setOnClickListener(this);
		return v;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {

	}

	/**
	 * Show progress dialog if not showing already
	 * 
	 * @param message
	 *            Message to display with progress dialog
	 */
	public void showProgressDialog(String message) {
		if (pDialog == null || !pDialog.isShowing())
			pDialog = ProgressDialog.show(getActivity(), "",
					message == "" ? getString(R.string.txt_progress) : message);
	}

	/**
	 * Dismiss progress dialog if showing
	 */
	public void dismissProgressDialog() {
		if (pDialog != null && pDialog.isShowing())
			pDialog.dismiss();
	}

}

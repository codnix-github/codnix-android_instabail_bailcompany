package com.bailcompany;

import java.io.File;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.utils.Commons;
import com.bailcompany.utils.Const;
import com.bailcompany.utils.ImageSelector;
import com.bailcompany.utils.ImageUtils;
import com.bailcompany.utils.Log;
import com.bailcompany.utils.StaticData;
import com.bailcompany.utils.ImageSelector.RemoveListener;

public class EditProfile extends CustomActivity {

	private Spinner selectInsurance;
	private ImageView changeProfile;
	private static File file;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_profile);
		setActionBar();
		setTouchNClick(R.id.btn_save_edit);
		setTouchNClick(R.id.btn_cancel_edit);
		changeProfile = (ImageView) findViewById(R.id.img_profile);
		setTouchNClick(R.id.img_profile);
		selectInsurance = (Spinner) findViewById(R.id.insure_sel_edit_profile);
		selectInsurance.setAdapter(getAdapter(R.array.insurence_num));
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v.getId() == R.id.btn_save_edit
				|| v.getId() == R.id.btn_cancel_edit)
			finish();
		if (v.getId() == R.id.img_profile) {
			file = new File(Const.TEMP_PHOTO + "/"+Const.getUniqueIdforImage()
					+ ".png");
			ImageSelector.openChooser(THIS, file, null);
		}
	}

	void hideKeyboard() {
		View view = THIS.getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) THIS
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		hideKeyboard();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			File f;
			if (requestCode == ImageSelector.IMAGE_CAPTURE) {
				f = file;
			} else {
				f = new File(ImageSelector.getImagePath(THIS, data));
			}
			String path = f.getAbsolutePath();
			Log.e("Path = " + path);
			if (!Commons.isEmpty(path)) {
				changeProfile.setImageResource(0);
				changeProfile.setImageBitmap(compressFile(path));
			}
		}

	}

	private Bitmap compressFile(String path) {
		File f = new File(path);
		Bitmap bm = ImageUtils.getOrientationFixedImage(f,
				StaticData.getDIP(110), StaticData.getDIP(110),
				ImageUtils.SCALE_FIT_CENTER);
		bm = ImageUtils.getCircularBitmap(bm);
		return bm;
	}

	private void setActionBar() {
		final ActionBar actionBar = getActionBar();
		actionBar.setTitle(getString(R.string.edit_profile));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home)
			finish();
		return super.onOptionsItemSelected(item);
	}
}

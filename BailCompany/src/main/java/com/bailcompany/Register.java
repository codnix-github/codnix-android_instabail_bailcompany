package com.bailcompany;

import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.widget.Toast;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.ui.RegistrationPartOne;
import com.bailcompany.utils.Methods;
import com.bailcompany.utils.Utility;

public class Register extends CustomActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_container);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, new RegistrationPartOne())
				.commit();
	}

	private void setActionBar() {
		final ActionBar actionBar = getActionBar();
		actionBar.setTitle(getString(R.string.register));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home)
			finish();
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		for (Fragment f : getSupportFragmentManager().getFragments())
			f.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		boolean back = getSupportFragmentManager().popBackStackImmediate();
		if (!back)
			super.onBackPressed();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
			String[] permissions, int[] grantResults) {

		if (grantResults.length > 0
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

			boolean result = Utility.checkPermission(this);
			// if (result) {
			// RegistrationPartOne fragment = (RegistrationPartOne)
			// getSupportFragmentManager()
			// .findFragmentById(R.id.register);
			// fragment.selectImage();
			//
			// }
		} else {
			Methods.showToast(this, "Permission Denny", Toast.LENGTH_SHORT);
		}

	}

}
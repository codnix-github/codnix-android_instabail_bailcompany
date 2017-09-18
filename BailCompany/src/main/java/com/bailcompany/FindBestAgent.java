package com.bailcompany;

import java.util.ArrayList;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.bailcompany.custom.CustomActivity;
import com.bailcompany.model.AgentModel;
import com.bailcompany.ui.AgentList;
import com.bailcompany.ui.ShowAllOnMap;
import com.bailcompany.web.WebAccess;

public class FindBestAgent extends CustomActivity {

	ArrayList<AgentModel> agentsList;
	public static String agentRequestId = "";
	public static String locLatt, locLng;
	public static boolean isRequestedAgent;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.findbest_agent_container);

		agentsList = (ArrayList<AgentModel>) getIntent().getSerializableExtra(
				"agents");
		agentRequestId = WebAccess.agentRequestId;

		Intent intent = getIntent();
		locLatt = intent.getStringExtra("locLatt");
		locLng = intent.getStringExtra("locLng");
		Log.e("@@@@@@@@@", "" + locLatt + "=" + locLng);
		setupContainer();
		setActionBar();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
	}

	private void setActionBar() {
		final ActionBar actionBar = getActionBar();
		actionBar.setTitle(getString(R.string.find_bst));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
	}

	private void setupContainer() {
		getSupportFragmentManager().addOnBackStackChangedListener(
				new OnBackStackChangedListener() {

					@Override
					public void onBackStackChanged() {
						setActionBarTitle();
						if (isRequestedAgent)
							finish();
					}
				});
		launchFragment(0);
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

	private void setActionBarTitle() {
		if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
			getActionBar().setTitle(R.string.find_bst);
			return;
		}
		String title = getSupportFragmentManager().getBackStackEntryAt(
				getSupportFragmentManager().getBackStackEntryCount() - 1)
				.getName();
		getActionBar().setTitle(
				title == null ? getString(R.string.app_name) : title);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (menu != null)
			menu.clear();
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.e("AgentList", "Option: " + item.getItemId());
		if (item.getItemId() == R.id.menu_map)
			launchFragment(1);
		if (item.getItemId() == android.R.id.home)
			onKeyDown(KeyEvent.KEYCODE_BACK, null);
		return super.onOptionsItemSelected(item);
	}

	private void launchFragment(int which) {
		Fragment f = null;
		String title = null;
		Bundle arg = new Bundle();
		arg.putBoolean("isHired", false);
		arg.putSerializable("agents", agentsList);
		arg.putString("locLatt1", locLatt);
		arg.putString("locLng1", locLng);
		switch (which) {
		case 0:
			f = new AgentList();
			title = getString(R.string.hired_ag);
			f.setArguments(arg);
			break;
		case 1:
			f = new ShowAllOnMap();
			title = getString(R.string.find_bst);
			f.setArguments(arg);
		}

		Log.d("Title", title);

		if (f != null) {
			while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
				getSupportFragmentManager().popBackStackImmediate();
			}
			if (which == 0)
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.content_frame, f).commit();
			else
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.content_frame, f).addToBackStack(title)
						.commit();

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
				getSupportFragmentManager().popBackStackImmediate();
				// setupContainer();
			} else {
				WebAccess.fromFindMe = true;
				finish();
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}

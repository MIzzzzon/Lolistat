package info.papdt.lolistat.ui.base;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toolbar;

import info.papdt.lolistat.R;
import info.papdt.lolistat.ui.app.AboutFragment;
import info.papdt.lolistat.ui.app.LicenseFragment;
import info.papdt.lolistat.ui.app.PerActivityFragment;
import info.papdt.lolistat.ui.app.PerAppFragment;
import info.papdt.lolistat.ui.app.SettingsFragment;
import static info.papdt.lolistat.ui.utils.UiUtility.*;

public class GlobalActivity extends Activity
{
	static interface OnReturnCallback {
		void onReturn();
	}
	
	private Toolbar mToolbar;
	private String mExtraPass;
	private OnReturnCallback mOnReturn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base);
		
		mToolbar = $(this, R.id.toolbar);
		mToolbar.setElevation(15.0f);
		
		setActionBar(mToolbar);
		
		Intent i = getIntent();
		
		Class<? extends Fragment> clazz = null;
		String extra = i.getStringExtra("fragment");
		mExtraPass = i.getStringExtra("pass");
		
		switch (extra != null ? extra : "") {
			case "perapp":
				clazz = PerAppFragment.class;
				break;
			case "peract":
				clazz = PerActivityFragment.class;
				break;
			case "settings":
				clazz = SettingsFragment.class;
				break;
			case "about":
				clazz = AboutFragment.class;
				break;
			case "license":
				clazz = LicenseFragment.class;
				break;
			default:
				clazz = PerAppFragment.class;
		}
		
		try {
			getFragmentManager().beginTransaction().replace(R.id.container, clazz.newInstance()).commit();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			if (mOnReturn != null)
				mOnReturn.onReturn();
			else
				finish();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	
	Toolbar getToolbar() {
		return mToolbar;
	}
	
	String getExtraPass() {
		return mExtraPass;
	}
	
	void setOnReturnCallback(OnReturnCallback callback) {
		mOnReturn = callback;
	}
}

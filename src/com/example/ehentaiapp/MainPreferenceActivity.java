package com.example.ehentaiapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainPreferenceActivity extends PreferenceActivity {
	
	private SharedPreferences prefs;
	private int prefCacheDirValue = 0;
	
	public static final String PREF_KEY_CACHE_DIR = "cachedir";
	public static final String[] PREF_CACHE_DIR = {
		"/storage/sdcard0/ehentaiapp/download",
		"/storage/sdcard1/ehentaiapp/download"
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefCacheDirValue = Integer.parseInt(prefs.getString(PREF_KEY_CACHE_DIR, "0"));		
		
		ListPreference listPref = (ListPreference)findPreference(PREF_KEY_CACHE_DIR);
		listPref.setSummary(PREF_CACHE_DIR[prefCacheDirValue]);
		listPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference pref, Object newValue) {				
				pref.setSummary(PREF_CACHE_DIR[Integer.parseInt(newValue.toString())]);
				return true;
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
    	finish();     	
		super.onBackPressed();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home_page, menu);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case android.R.id.home:
				setResult(RESULT_OK);
            	finish();  
				return true;
			default:
				return super.onOptionsItemSelected(item); 
		}
	}
}

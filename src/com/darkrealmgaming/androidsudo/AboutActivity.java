/*
 * Sudo for Android Installer
 * com.darkrealmgaming.androidsudo.MainActivity
 * 
 * Copyright (c) 2014 Dark Realm Gaming
 * Licensed under The MIT License (http://github.com/ColtonDRG/AndroidSudo/blob/master/LICENSE)
 */



package com.darkrealmgaming.androidsudo;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class AboutActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		
		
	}
	
	
	
	public void github(View view) {
		Uri uri = Uri.parse("http://github.com/ColtonDRG/AndroidSudo");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
	
	
	public void ossLicenses(View view) {
		Intent intent = new Intent(AboutActivity.this, OssLicensesActivity.class);
		startActivity(intent);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		// int id = item.getItemId();
		// if (id == R.id.action_settings) {
			// return true;
		// }
		return super.onOptionsItemSelected(item);
	}
}

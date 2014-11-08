/*
 * Sudo for Android Installer
 * com.darkrealmgaming.androidsudo.OssLicensesActivity
 * 
 * Copyright (c) 2014 Dark Realm Gaming
 * Licensed under The MIT License (http://github.com/ColtonDRG/AndroidSudo/blob/master/LICENSE)
 */



package com.darkrealmgaming.androidsudo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class OssLicensesActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_oss_licenses);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.oss_licenses, menu);
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
	
	int ponycounter = 0;
	
	public void pony(MenuItem item) {
		ponycounter = ponycounter + 1;
		if(ponycounter == 5) {
			Uri uri = Uri.parse("http://drgo.ga/rocky");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
	}
	
}

/*
 * Sudo for Android Installer
 * com.darkrealmgaming.androidsudo.MainActivity
 * 
 * Copyright (c) 2014 Dark Realm Gaming
 * Licensed under The MIT License (http://github.com/ColtonDRG/AndroidSudo/blob/master/LICENSE)
 */



package com.darkrealmgaming.androidsudo;

import java.io.File;

import com.darkrealmgaming.androidapi.AssetManager;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import eu.chainfire.libsuperuser.Shell;

public class MainActivity extends ActionBarActivity {
	
	private boolean retryInstall = true;

	public static final String LOGTAG = "AndroidSudoInstaller";
	
    @Override
    protected void onCreate(Bundle androidSudo) {
        super.onCreate(androidSudo);
        setContentView(R.layout.activity_main);
    	
        if (androidSudo == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        
    }
    
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
    	refresh(null);
    	
    	// final Button install = (Button) findViewById(R.id.installButton);
    	// install.setOnClickListener(new View.OnClickListener() {
            // public void onClick(View v) {
            	// install();
            // }
        // });
    	
    	
    	// final Button remove = (Button) findViewById(R.id.removeButton);
    	// remove.setOnClickListener(new View.OnClickListener() {
            // public void onClick(View v) {
            	// remove();
            // }
        // });
    	
    	
    	// final Button about = (Button) findViewById(R.id.aboutButton);
    	// about.setOnClickListener(new View.OnClickListener() {
    		// public void onClick(View v) {
    			// Intent intent = new Intent(MainActivity.this, AboutActivity.class);
    			// startActivity(intent);
    		// }
    	// });
    	
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;

    }
    
    
    
    public void refresh(View view){
    	MainActivity.this.runOnUiThread(new Runnable() {
		    @Override
		    public void run() {
		    	Log.i(LOGTAG, "Checking if Sudo is installed...");
		    	final TextView isInstalledText = (TextView) findViewById(R.id.isInstalled);
		    	File sudoFile = new File("/system/xbin/sudo");
		    	if (sudoFile.exists()) {
		    	Log.i(LOGTAG, "Sudo IS installed! Reporting...");
		    	isInstalledText.setTextColor(Color.GREEN);
		    	isInstalledText.setText(R.string.installed);
		    	} else {
		    	Log.i(LOGTAG, "Sudo IS NOT installed! Reporting...");
		    	isInstalledText.setTextColor(Color.RED);
		    	isInstalledText.setText(R.string.not_installed);
		    	}
		    }
    	});
    }
    
    public void install(View view){
    	setRetryInstall(true);
    	Log.i(LOGTAG, "Install button pressed! Starting install...");
    	new Thread() {
    		public void run() {
    			Log.i(LOGTAG, "Getting Root Access...");
    			if(Shell.SU.available()) {
    				Log.i(LOGTAG, "Root Access Successful! Starting install...");
    				Log.i(LOGTAG, "Using DRGAPI-AssetManager to extract files...");
    				AssetManager.ExtractToStorage(MainActivity.this, "sudoscript.txt", "sudo-temp");
    				Log.i(LOGTAG, "Mounting /system as read-write...");
    				Shell.SU.run("mount -o remount,rw /system");
    				Log.i(LOGTAG, "Copying files...");
            		Shell.SU.run("cp /sdcard/sudo-temp /system/xbin/sudo");
            		Log.i(LOGTAG, "Setting permissions...");
            		Shell.SU.run("chmod 755 /system/xbin/sudo");
            		Log.i(LOGTAG, "Cleaning up temporary data...");
            		Shell.SH.run("rm -rf /sdcard/sudo-temp");
            		Log.i(LOGTAG, "Finishing up...");
            		Shell.SU.run("mount -o remount /system");
            		Log.i(LOGTAG, "Install complete! Rechecking if Sudo is installed...");
            		refresh(null);
            	}
            	else {
            		rootfail();
            	}
    		}
    	}.start();

    }
    
    public void recoveryInstall(View view){
    	// This is an early preparation for the future Recovery Install feature.
		Log.i(LOGTAG, "Recovery Install button pressed. Showing recovery install information...");
		Builder recoveryInfo = new AlertDialog.Builder(MainActivity.this);
        recoveryInfo.setMessage(R.string.rootfail_message);
        recoveryInfo.setTitle(R.string.rootfail_title);
        recoveryInfo.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int which) { 
        		// Installation code goes here.
        	}
        });
        recoveryInfo.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int which) {}
        });
        recoveryInfo.show();
    }
    
    public void remove(View view){
    	setRetryInstall(false);
    	Log.i(LOGTAG, "Remove button pressed! Starting cleanup...");
    	new Thread() {
    		public void run() {
    			Log.i(LOGTAG, "Getting Root Access...");
    			if(Shell.SU.available()) {
    				Log.i(LOGTAG, "Root Access Successful! Starting cleanup...");
    				Log.i(LOGTAG, "Mounting /system as read-write...");
            		Shell.SU.run("mount -o remount,rw /system");
            		Log.i(LOGTAG, "Removing installed files...");
            		Shell.SU.run("rm -f /system/xbin/sudo");
            		Log.i(LOGTAG, "Finishing up...");
            		Shell.SU.run("mount -o remount /system");
            		Log.i(LOGTAG, "Cleanup complete! Rechecking if Sudo is installed...");
            		refresh(null);
            	} else {
            		rootfail();
            	}
    		}
    	}.start();

    }
    
    public void rootfail(){
		MainActivity.this.runOnUiThread(new Runnable() {
		    @Override
		    public void run() {
		    	Log.e(LOGTAG, "Unable to get root access! Sudo cannot be installed/removed!");
        		Log.i(LOGTAG, "Showing error information...");
		    	Builder suFailed = new AlertDialog.Builder(MainActivity.this);
        		suFailed.setMessage(R.string.rootfail_message);
        		suFailed.setTitle(R.string.rootfail_title);
        		suFailed.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int which) { 
        				if(isRetryInstall()){
        					install(null);
        				}
        				else{
        					remove(null);
        				}
        			}
        		});
        		suFailed.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int which) {}
        		});
        		suFailed.show();
		    }
		});
    }
    
    public void about(MenuItem item){
		Intent intent = new Intent(MainActivity.this, AboutActivity.class);
		startActivity(intent);
    }

	public boolean isRetryInstall() {
		return retryInstall;
	}

	public boolean setRetryInstall(boolean retryInstall) {
		this.retryInstall = retryInstall;
		return retryInstall;
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
            
            
        }
        
                
    }
    
}

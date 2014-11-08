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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import eu.chainfire.libsuperuser.Shell;

public class MainActivity extends FragmentActivity {
	
	// Tells the root failure class what class to run if the Retry button is pressed
	private int retryMode;

	// The tag that is used for this application's logcat entries
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
    	// Check the current install status
    	refresh();
    	
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    // Class for checking the current install status
    public void refresh(){
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
    
    // Normal installation class
    public void install(View view){
    	retryMode = 0;
    	Log.i(LOGTAG, "Install button pressed! Starting install...");
    	new Thread() {
    		public void run() {
    			Log.i(LOGTAG, "Getting Root Access...");
    			if(Shell.SU.available()) {
    				Log.i(LOGTAG, "Root Access Successful! Starting install...");
    				Log.i(LOGTAG, "Using DRGAPI-AssetManager to extract files...");
    				AssetManager.ExtractToAppCache(MainActivity.this, "sudoscript.txt", "sudo-temp");
    				Log.i(LOGTAG, "Mounting /system as read-write...");
    				Shell.SU.run("mount -o remount,rw /system");
    				Log.i(LOGTAG, "Copying files...");
            		Shell.SU.run("cp " + MainActivity.this.getCacheDir() + "/sudo-temp /system/xbin/sudo");
            		Log.i(LOGTAG, "Setting permissions...");
            		Shell.SU.run("chmod 755 /system/xbin/sudo");
            		Log.i(LOGTAG, "Finishing up...");
            		Shell.SU.run("mount -o remount /system");
            		Log.i(LOGTAG, "Install complete! Rechecking if Sudo is installed...");
            		refresh();
            	} else {
            		rootfail();
            	}
    		}
    	}.start();

    }
    
    // Recovery installation class
    public void recoveryInstall(View view){
    	retryMode = 2;
		Log.i(LOGTAG, "Recovery Install button pressed. Showing recovery install information...");
		Builder recoveryInfo = new AlertDialog.Builder(MainActivity.this);
        recoveryInfo.setMessage(R.string.recovery_install_message);
        recoveryInfo.setTitle(R.string.recovery_install_title);
        recoveryInfo.setPositiveButton(R.string.install_text, new DialogInterface.OnClickListener() {
        	// Recovery Install
        	public void onClick(DialogInterface dialog, int which) {
        		Log.i(LOGTAG, "Recovery Install confirmed. Starting recovery install process...");
        		new Thread() {
        			public void run() {
        				Log.i(LOGTAG, "Getting Root Access...");
        				if(Shell.SU.available()) {
        					Log.i(LOGTAG, "Root Access Successful! Starting install...");
        					Log.i(LOGTAG, "Using DRGAPI-AssetManager to extract files...");
        					AssetManager.ExtractToAppCache(MainActivity.this, "sudoscript.txt", "sudo-temp");
        	        		AssetManager.ExtractToAppCache(MainActivity.this, "recoveryinstall.zip", "sudo-temp.zip");
        	        		Log.i(LOGTAG, "Using DRGAPI-AssetManager to load OpenRecoveryScript...");
        	        		AssetManager.OpenRecoveryScript(MainActivity.this, "recoveryinstall.txt");
        	        		Log.i(LOGTAG, "Waiting a few seconds...");
        	        		try {
        	        			Thread.sleep(3000);
        	        		} catch(InterruptedException ex) {
        	        			Thread.currentThread().interrupt();
        	        			Log.e(LOGTAG, "An unknown error occurred!");
        	        		}
        	        		Log.i(LOGTAG, "Attempting reboot to recovery...");
        	        		Shell.SU.run("reboot recovery");
        	        		Log.w(LOGTAG, "Reboot may have failed. Trying alternate method...");
        	        		Log.i(LOGTAG, "Using DRGAPI-AssetManager to extract files...");
        	        		AssetManager.ExtractToAppCache(MainActivity.this, "reboot", "reboot");
        	        		Log.i(LOGTAG, "Setting Permissions...");
        	        		Shell.SH.run("chmod 755 " + MainActivity.this.getCacheDir() + "/reboot");
        	        		Log.i(LOGTAG, "Rebooting to recovery...");
        	        		Shell.SU.run(MainActivity.this.getCacheDir() + "/reboot recovery");
        	        		Log.w(LOGTAG, "Reboot may have failed!");
        				} else {
        					rootfail();
        				}
        			}
        		}.start();
        	}
        });
        recoveryInfo.setNegativeButton(R.string.remove_text, new DialogInterface.OnClickListener() {
        	// Recovery Remove
        	public void onClick(DialogInterface dialog, int which) {
        		Log.i(LOGTAG, "Recovery Remove confirmed. Starting recovery removal process...");
        		new Thread() {
        			public void run() {
        				Log.i(LOGTAG, "Getting Root Access...");
        				if(Shell.SU.available()) {
        					Log.i(LOGTAG, "Root Access Successful! Starting removal...");
        					Log.i(LOGTAG, "Using DRGAPI-AssetManager to extract files...");
        	        		AssetManager.ExtractToAppCache(MainActivity.this, "recoveryremove.zip", "sudo-temp.zip");
        	        		Log.i(LOGTAG, "Using DRGAPI-AssetManager to load OpenRecoveryScript...");
        	        		AssetManager.OpenRecoveryScript(MainActivity.this, "recoveryinstall.txt");
        	        		Log.i(LOGTAG, "Waiting a few seconds...");
        	        		try {
        	        			Thread.sleep(3000);
        	        		} catch(InterruptedException ex) {
        	        			Thread.currentThread().interrupt();
        	        			Log.e(LOGTAG, "An unknown error occurred!");
        	        		}
        	        		Log.i(LOGTAG, "Attempting reboot to recovery...");
        	        		Shell.SU.run("reboot recovery");
        	        		Log.w(LOGTAG, "Reboot may have failed. Trying alternate method...");
        	        		Log.i(LOGTAG, "Using DRGAPI-AssetManager to extract files...");
        	        		AssetManager.ExtractToAppCache(MainActivity.this, "reboot", "reboot");
        	        		Log.i(LOGTAG, "Setting Permissions...");
        	        		Shell.SH.run("chmod 755 " + MainActivity.this.getCacheDir() + "/reboot");
        	        		Log.i(LOGTAG, "Rebooting to recovery...");
        	        		Shell.SU.run(MainActivity.this.getCacheDir() + "/reboot recovery");
        	        		Log.w(LOGTAG, "Reboot may have failed!");
        				} else {
        					rootfail();
        				}
        			}
        		}.start();
        	}
        });
        recoveryInfo.show();
    }
    
    // Normal removal class
    public void remove(View view){
    	retryMode = 1;
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
            		refresh();
            	} else {
            		rootfail();
            	}
    		}
    	}.start();

    }
    
    // Root failure message class
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
        				if(retryMode == 0) {
        					install(null);
        				} else if(retryMode == 1) {
        					remove(null);
        				} else if(retryMode == 2) {
        					recoveryInstall(null);
        				} else {
        					Log.e(LOGTAG, "An error has occurred!");
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

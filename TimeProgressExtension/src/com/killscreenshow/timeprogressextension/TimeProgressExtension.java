package com.killscreenshow.timeprogressextension;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;
import com.killscreenshow.timeprogressextension.R;

public class TimeProgressExtension extends DashClockExtension {
	
	private static final String TAG = "TimeProgressExtension";
	
	public static final String PREF_START = "pref_start";
	public static final String PREF_END = "pref_end"; 
	public static final String PREF_TITLE = "pref_title";
	public static final String PREF_WEEKDAY = "pref_weekday";

	@Override
	protected void onUpdateData(int arg0) {
		// Get preference value.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String start = sp.getString(PREF_START, getString(R.string.pref_start));
        String end = sp.getString(PREF_END, getString(R.string.pref_end));
        String title = sp.getString(PREF_TITLE, getString(R.string.pref_title));
        
        boolean weekday = sp.getBoolean(PREF_WEEKDAY, true);
        
        String createdTitle = createTitle(start, end, weekday);
        String createdBody = createBody(createdTitle);
        
        
     // Publish the extension data update.
        if(createdTitle == ""){
        	publishUpdate(new ExtensionData()
        	.visible(false));
        }
        
        else {
        	publishUpdate(new ExtensionData()
            .visible(true)
            .icon(R.drawable.ic_tp_icon)
            .status(createdTitle + " " + title)
            .expandedTitle(createdTitle + " " + title)
            .expandedBody(createdBody)
            //.contentDescription("Completely different text for accessibility if needed.")
            .clickIntent(new Intent(this, TimePreference.class)));
        }
        
	}
	
	private String createTitle(String s, String e, boolean weekday){
		
		//Prepare time objects
		Time start = new Time();
		Time end = new Time();
		Time now = new Time();
		
		now.setToNow();
		
		String[] s2 = s.split(":");
		String[] e2 = e.split(":");
		
		start.set(0, Integer.parseInt(s2[1]), Integer.parseInt(s2[0]), now.monthDay, now.month, now.year);
		end.set(0, Integer.parseInt(e2[1]), Integer.parseInt(e2[0]), now.monthDay, now.month, now.year);
		
		//Check time
		if(now.after(end)) return ("");
		if(now.before(start)) return ("");
		if((now.weekDay == 0 || now.weekDay == 6) && weekday) return "";
		
		//Calculate %
		long startMil = start.toMillis(false);
		long endMil = end.toMillis(false);
		long nowMil = now.toMillis(false);
		
		Log.d("startMil", startMil + "");
		Log.d("endMil", endMil + "");
		
		
		double percentage = ((double)(nowMil - startMil) / (double)(endMil - startMil));
		Log.d("percentage", percentage + "");
		
		int percInt = (int) (percentage * 100);
		
		return (percInt + "%"); 
	}

	private String createBody(String percent){
		String parsePercent = percent.split("%")[0];
		if (parsePercent == "") return "";
		
		String bar = "[";
		
		int per = Integer.parseInt(parsePercent);
		int total = 69;
		int ratio = findRatio(per, total);
		
		for(int i = 0; i < total; i++){
			if (i < ratio){
				bar = bar + "|";
			} else {
				bar = bar + " ";
			}
		}
		
		bar = bar + "]";
		return bar;
	}
	
	private int findRatio(int top, int total){
		float temp = (top * total)/100;
		return (int)temp;
	}
}

package at.fhj.app.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import at.fhj.app.R;
import at.fhj.app.util.Configuration;
import at.fhj.app.util.GroupFilter;

/**
 * Make settings editable by the user.
 * 
 * Why can't every activity be as straightforward as this one?!
 * 
 * @author Markus Deutsch <Markus.Deutsch.ITM09@fh-joanneum.at>
 */
public class SettingsActivity extends PreferenceActivity implements OnPreferenceClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
	
	private Preference groups, course, year;
	private SharedPreferences p;
			
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.settings);
		
		groups = findPreference("hiddengroups");
		course = findPreference(Configuration.PREFERENCE_COURSE);
		year = findPreference(Configuration.PREFERENCE_YEAR);
		
        groups.setOnPreferenceClickListener(this);
        
        p = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        p.registerOnSharedPreferenceChangeListener(this);
	}

	public boolean onPreferenceClick(Preference preference) {
		Intent intent = new Intent(this, GroupManagerActivity.class);
		startActivity(intent);
		return true;
	}
	
	private void updateDescriptions(){
		
		course.setSummary(p.getString(Configuration.PREFERENCE_COURSE, "N/A"));
		year.setSummary(p.getString(Configuration.PREFERENCE_YEAR, "N/A"));
		
		// Filters
		GroupFilter azm = new GroupFilter(getApplicationContext());
		ArrayList<String> z = azm.getGroups();
		
		if(z == null || z.size() < 1){
			groups.setSummary(getString(R.string.lblNone));
		} else if (z.size() == 1) {
			groups.setSummary(z.get(0));
		} else if (z.size() == 2) {
			groups.setSummary(z.get(0) + " "+getString(R.string.lblAnd)+" " + z.get(1));
		} else if (z.size() == 3) {
			groups.setSummary(z.get(0) + ", " + z.get(1) + " "+getString(R.string.lblAnd)+" " + z.get(2));
		} else {
			groups.setSummary(z.get(0) + ", " + z.get(1) + " "+getString(R.string.lblAnd)+" " + (z.size() - 2) + " "+getString(R.string.lblOthers));
		}
	}

	public void onResume(){
		super.onResume();
		updateDescriptions();
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        p.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        updateDescriptions();
    }
}

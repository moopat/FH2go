package at.fhj.app.util;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * This class provides information about hidden groups, 
 * and is responsible for persisting changes to this list.
 * 
 * @author Markus Deutsch <Markus.Deutsch.ITM09@fh-joanneum.at>
 *
 */

public class GroupFilter {

	private SharedPreferences p;
	private HashMap<String, String> groups;
	private final String KEY = "hiddengroups";
	
	public GroupFilter(Context c){
		
		this.p = PreferenceManager.getDefaultSharedPreferences(c);
		this.groups = new HashMap<String, String>();
		
		updateDataset();
		
	}
	
	public void updateDataset(){
		
		String z = p.getString(KEY, "");
		String[] za = z.split(",");
		
		groups.clear();
		
		for (String string : za) {
			string = string.trim();		
			if(string == null || string.equals("")){
				continue;
			}	
			groups.put(string, string);
		}
		
	}

	public ArrayList<String> getGroups() {
		ArrayList<String> result = new ArrayList<String>();
		
		for(String key : groups.values()){
			result.add(key);
		}
		
		return result;
	}
	
	public void removeGroup(String group){
		groups.remove(group);
		commit();
	}
	
	public void addGroup(String group){
		if(group == null || group.equals("")){
			return;
		}
		groups.put(group, group);
		commit();
	}
	
	private void commit(){
		StringBuilder sb = new StringBuilder();
		
		for(String key : groups.values()){
			sb.append(key);
			sb.append(",");
		}
		
		SharedPreferences.Editor e = p.edit();
		e.putString(KEY, sb.toString());
		e.commit();
	}
	
	public boolean isHidden(String group){
		return groups.get(group) == null || groups.get(group).equals("") ? false : true;
	}
	
}

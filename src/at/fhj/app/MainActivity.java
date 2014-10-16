package at.fhj.app;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import at.fhj.app.activity.*;
import at.fhj.app.model.NavigationItem;
import at.fhj.app.retriever.FHPIRetriever;
import at.fhj.app.util.ConnectionInformation;

/**
 * Main Activity of FH2go
 * 
 * This activity shows the home screen with all navigation.
 * It's the software's entry point.
 * It has over 250 lines of code, which is by some considered a bad smell. But I like the smell.
 * 
 * @author Markus Deutsch <Markus.Deutsch.ITM09@fh-joanneum.at>
 */
public class MainActivity extends Activity implements OnItemClickListener {
    public ProgressDialog prog;
    private SharedPreferences prefs;
    
    private GridView grid;
    private ArrayList<NavigationItem> items;
    
    private final int ACTION_SCHEDULE = 0;
    private final int ACTION_MARKS = 1;
    private final int ACTION_EXAMS = 2;
    private final int ACTION_NEWS = 3;
    private final int ACTION_ABOUT = 4;
    private final int ACTION_SETTINGS = 5;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        items = new ArrayList<NavigationItem>();
        loadNavigationItems();
        
        grid = (GridView) findViewById(R.id.grid);
        grid.setAdapter(new NavigationAdapter(this));
        grid.setOnItemClickListener(this);
        
        /*
         * Initialize shared preferences and register a changeListener to 
         * address complaints by inexperienced Android users who didn't know
         * whether properties were saved or not.
         */
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }
	
	/**
	 * Fills the ArrayList with NavigationItems.
	 * 
	 * The IDs given to NavigationItems should not be changed over revisions, even if order changes, 
	 * because they are used to reference the functionality of the button.
	 * 
	 */
	private void loadNavigationItems(){
		items.add(new NavigationItem(ACTION_SCHEDULE, R.drawable.btn_ele_schedule, R.string.lbl_schedule));
		items.add(new NavigationItem(ACTION_MARKS, R.drawable.btn_ele_marks2, R.string.lbl_marks));
		items.add(new NavigationItem(ACTION_EXAMS, R.drawable.btn_ele_exams, R.string.lbl_exams));
		
		items.add(new NavigationItem(ACTION_NEWS, R.drawable.btn_ele_news, R.string.lbl_news));
		items.add(new NavigationItem(ACTION_ABOUT, R.drawable.btn_ele_help, R.string.lbl_help));
		items.add(new NavigationItem(ACTION_SETTINGS, R.drawable.btn_ele_settings, R.string.lbl_settings));
	}
	
	/**
	 * Find out whether year and course are set.
	 * 
	 * @return true if year and course are set
	 */
	private boolean validateCourseData(){
		return (prefs.getString("course", "").equals("") || prefs.getString("year", "").equals("")) ? false : true;
	}
	
	/**
	 * Catch button clicks.
	 */
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(items.get(arg2).getId() == ACTION_SETTINGS){
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
		}
		
		if(items.get(arg2).getId() == ACTION_ABOUT){
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
		}
		
		if(items.get(arg2).getId() == ACTION_MARKS){
			startActivity(new Intent(this, MarksActivity.class));
		}
		
		if(items.get(arg2).getId() == ACTION_EXAMS){
			startActivity(new Intent(this, ExamsActivity.class));
		}
		
		if(items.get(arg2).getId() == ACTION_NEWS){
			prog = ProgressDialog.show(this, "", getString(R.string.prog_wait), false);
			NewsRetriever r = new NewsRetriever(this);
			r.execute("Hallo");
		}
		
		/**
		 * If the course and year have been set, load schedule, if not, redirect to the
		 * selector screen.
		 */
		if(items.get(arg2).getId() == ACTION_SCHEDULE){
			if(!validateCourseData()){
				Intent intent = new Intent(this, ScheduleChooserActivity.class);
				startActivity(intent);
			} else {
				String course = prefs.getString("course", "");
				String year = prefs.getString("year", "");
				
				Bundle bundle = new Bundle();
				bundle.putString("course", course);
				bundle.putString("year", year);
				Intent intent = new Intent(this, ScheduleActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, 0);
			}
			
		}
		
	}

	/**
	 * Thread to retrieve news and then redirect to NewsActivity
	 * 
	 * @author Markus Deutsch <Markus.Deutsch.ITM09@fh-joanneum.at>
	 *
	 */
	private class NewsRetriever extends AsyncTask<String, String, String>{
		Activity a;
		String status;
		
		public NewsRetriever(Activity a){
			this.a = a;
		}

		@Override
		protected String doInBackground(String... params) {
			FHPIRetriever fr = new FHPIRetriever();
			fr.prepareRequest("getNews.php");
			String result = fr.retrieve();
			return result;
		}
		
		protected void onPostExecute(String result) {
			status = ConnectionInformation.getConnectionStatus(result);
			prog.dismiss();
			if(status.equals("OK")){
				Bundle bundle = new Bundle();
				bundle.putString("values", result);
				Intent intent = new Intent(a, NewsActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, 0);
			} else {
				// If we had trouble, say the word!
				Toast.makeText(getApplicationContext(), getString(ConnectionInformation.getConnectionStatusString(status)), Toast.LENGTH_LONG).show();
			}
			
	     }
		
	}
	
	/**
	 * NavigationAdapter for Grid Menu
	 * 
	 * @author Markus Deutsch <Markus.Deutsch.ITM09@fh-joanneum.at>
	 */
	public class NavigationAdapter extends BaseAdapter{
		Context context;
	      
		public NavigationAdapter(Context context) {
			this.context = context;
		}
	    
		/**
		 * Return total count of navigation items.
		 */
		public int getCount(){
			return items.size();
		}

		public View getView(int position, View convertView, ViewGroup parent){
			View navigationItem = convertView;
	         
			if(convertView == null){	            
	            //Inflate the layout
	            LayoutInflater li = getLayoutInflater();
	            navigationItem = li.inflate(R.layout.item_navigation, null);
	            
	            // Map layout items to objects
	            TextView text = (TextView) navigationItem.findViewById(R.id.NavigationItemText);
	            ImageView image = (ImageView) navigationItem.findViewById(R.id.NavigationItemImage);

	            // Fill Grid Elements with life!
	            text.setText(getResources().getString(items.get(position).getTitleResource()));           
	            image.setImageResource(items.get(position).getImageResource());
	         }
	         
	         return navigationItem;
	      }

	      public Object getItem(int arg0) {
	         // TODO Auto-generated method stub
	         return null;
	      }

	      public long getItemId(int arg0) {
	         // TODO Auto-generated method stub
	         return 0;
	      }

	   }
	
}
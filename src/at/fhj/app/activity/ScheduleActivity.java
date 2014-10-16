package at.fhj.app.activity;

import java.util.ArrayList;
import java.util.Date;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import at.fhj.app.MainActivity;
import at.fhj.app.R;
import at.fhj.app.model.Event;
import at.fhj.app.model.EventDAO;
import at.fhj.app.util.Configuration;
import at.fhj.app.util.GroupFilter;
import at.fhj.app.util.ScheduleHelper;

/**
 * This Activity displays the schedule of one specific day.
 * 
 * @author Markus Deutsch <Markus.Deutsch.ITM09@fh-joanneum.at>
 *
 */
public class ScheduleActivity extends Activity implements OnClickListener, OnDateSetListener, OnItemClickListener {
	private ArrayList<Event> events;
	private TextView activityMainTitle, activityTitle, btn_next, btn_prev, updated;
	private ProgressDialog prog;
	private ImageView btn_update, btn_chdate, btn_chclass;
	
	private String course;
	private String year;
	
	private ScheduleHelper sh;
	private ScheduleHelperTask sht;
	
	// Date items needed for the datepicker
	private int mDay;
	private int mMonth;
	private int mYear;
	static final int DATE_DIALOG_ID = 0;
		
	// Describes the day which's exams we want to display.
	private long date;
	private String date_formatted;
	
	private ListView listview;
	private TextView empty;
	
	// Schedule Dialog
	private TextView scheduleDetail;
	private ImageView scheduleImageView;
	private AlertDialog.Builder builder;
	private AlertDialog alertDialog;
	private LayoutInflater inflater;
	private View layout;

    protected void onCreate(Bundle savedInstanceState){    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        
        Configuration.initFormatter();
        
        // Retrieve bundle and set year and course we want to see
        Bundle bundle = this.getIntent().getExtras();
		year = bundle.getString("year");
		course = bundle.getString("course");
		
		/**
		 * If a date has already been handed over, use it.
		 * Otherwise: use today's date.
		 */
		try {
			date = bundle.getLong("date");
		} catch (Exception e) {
			date = new Date().getTime();
		}
		if(date == 0){
			date = new Date().getTime();
		}
		
		/**
		 * Perform actions defining the date
		 * date_formatted stores the date in the format "20.12.2012"
		 */
		Date temp = new Date();
		temp.setTime(date);
		date_formatted = Configuration.SIMPLE_DATE_DAY.format(temp);
		
		mDay = temp.getDate();
		mMonth = temp.getMonth();
		mYear = temp.getYear() + 1900;
		
		/**
		 * ScheduleHelper manages online and offline schedule!
		 * It rocks!
		 */
		sh = new ScheduleHelper(course, year, this.getApplicationContext());
		
		/**
		 * Map items from the layout to properties
		 */
		activityTitle = (TextView) findViewById(R.id.activityTitle);
		activityMainTitle = (TextView) findViewById(R.id.activityMainTitle);
		btn_prev = (TextView) findViewById(R.id.prev);
		btn_next = (TextView) findViewById(R.id.next);
		btn_update = (ImageView) findViewById(R.id.btn_update);
		btn_chdate = (ImageView) findViewById(R.id.btn_chdate);
		btn_chclass = (ImageView) findViewById(R.id.btn_chclass);
		updated = (TextView) findViewById(R.id.updated);
		listview = (ListView) findViewById(R.id.listview);
		empty = (TextView) findViewById(R.id.empty);
				
		/**
		 * Register listeners for buttons (datepicker, back & forth)
		 */
		btn_chclass.setOnClickListener(this);
		btn_chdate.setOnClickListener(this);
		btn_prev.setOnClickListener(this);
		btn_next.setOnClickListener(this);
		btn_update.setOnClickListener(this);
		listview.setOnItemClickListener(this);
		registerForContextMenu(listview);
		
		/**
		 * Set (both) activity title(s).
		 */
		activityTitle.setText(date_formatted);
		activityMainTitle.setText(course + year.substring(2));

		/**
		 * If the ScheduleHelper was not initialized, start a thread to retrieve schedule items.
		 * Otherwise, just get the freakin' schedule items we are all waiting for!
		 */
		if(!sh.isInitialized()){
			//Log.i("ScheduleActivity", "The size of the ScheduleHelper is " + sh.getSize() + ". Synchronization triggered.");
			sht = new ScheduleHelperTask();
			/**
			 * @TODO Get rid of all the annoying "Hello" and "Blabla" strings in AsyncTask's execute methods!
			 */
			sht.execute("blabla");
			prog = ProgressDialog.show(this, "", getString(R.string.prog_wait), false);
		} else {
			events = sh.getSchedule(date);
			//Log.i("ScheduleActivity", "The size of the ScheduleHelper is " + sh.getSize() + ". Populating list.");
			populateList();
		}
		
		/**
		 * Prepare the detailed Schedule Dialog
		 */
		inflater = (LayoutInflater) this.getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		layout = inflater.inflate(R.layout.schedule_dialog, (ViewGroup) findViewById(R.id.layout_root));
		scheduleDetail = (TextView) layout.findViewById(R.id.text);
		scheduleImageView = (ImageView) layout.findViewById(R.id.image);
		scheduleImageView.setImageResource(R.drawable.btn_ele_schedule);
		
		builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		alertDialog = builder.create();
    }
    
    private void updateLastUpdate(){
    	EventDAO.init(getApplicationContext());
		long lastUpdate = EventDAO.getLastUpdate(course, year);
		
		if(lastUpdate > 0){
			updated.setText(this.getResources().getText(R.string.lbl_lastupdate)+" "+Configuration.SIMPLE_DATETIME.format(new Date(lastUpdate*1000)));
		} else {
			updated.setText(this.getResources().getText(R.string.lbl_lastupdate)+" "+this.getResources().getText(R.string.lbl_unknown));
		}
    }
    
    /**
     * Populate ListView with this day's schedule items.
     */
    private void populateList(){
    	
    	updateLastUpdate();
    	
		if(events != null && events.size() > 0){
			listview.setVisibility(View.VISIBLE);
			empty.setVisibility(View.GONE);
			
			// Initialize event colors, so each type has its own color.
			Configuration.initEventColors();
			
	        listview.setAdapter(new ScheduleAdapter());
		} else {
			empty.setVisibility(View.VISIBLE);
			listview.setVisibility(View.GONE);
		}
			
    }
    
    /**
     * Go to next day.
     */
	public void goToNext(){
		Bundle bundle = new Bundle();
		bundle.putString("course", course);
		bundle.putString("year", year);
		bundle.putLong("date", date + 86400000);
		Intent intent = new Intent(this.getApplicationContext(), ScheduleActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}
	
	/**
     * Go to schedule chooser.
     */
	public void goToChooser(){
		Bundle bundle = new Bundle();
		bundle.putString("year", year);
		bundle.putString("course", course);
    	Intent intent = new Intent(this.getApplicationContext(), ScheduleChooserActivity.class);
		intent.putExtras(bundle);
    	startActivity(intent);
	}
	
	/**
	 * Navigate to previous day.
	 */
	public void goToPrev(){
		Bundle bundle = new Bundle();
		bundle.putString("course", course);
		bundle.putString("year", year);
		bundle.putLong("date", date - 86400000);
		Intent intent = new Intent(this.getApplicationContext(), ScheduleActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}
	
	/**
	 * Return to home screen
	 * 
	 * This is necessary because the return key should not get the key back to the schedule.
	 */
	private void goToHome(){
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}
	
	public void updateDataset(boolean fromWebservice){
		if(fromWebservice){
			sht = new ScheduleHelperTask();
			sht.execute("refresh");
			prog = ProgressDialog.show(this, "", getString(R.string.prog_wait), false);
		} else {
			if(sh.isInitialized()){
				events = sh.getSchedule(date);
				populateList();
			}
		}
	}

	/**
	 * Map back/forth buttons to actions.
	 */
	public void onClick(View v) {
		if(btn_prev.isPressed()){
			goToPrev();
		} else if (btn_next.isPressed()) {
			goToNext();
		} else if (btn_update.isPressed()){
			updateDataset(true);
		} else if (btn_chclass.isPressed()){
			goToChooser();
		} else if (btn_chdate.isPressed()){
			showDialog(DATE_DIALOG_ID);
		}
		
	}
	
	/**
	 * Handle hardware back key.
	 * 
	 * If the device's hardware back key is pressed, go to MainActivity
	 * so the user doesn't have to click through all the days he has 
	 * flicked through all over again.
	 */
	@Override
	public void onBackPressed() {
		goToHome();
	}
	
	/**
	 * Create an options menu.
	 * 
	 * Create options menu to let user quickly jump to home, update schedule or choose another class.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_schedule, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
		    case R.id.menu_home:
		    	goToHome();
		        return true;
		    default:
		        return super.onOptionsItemSelected(item);
		    }
	}
	
	/**
	 * Create DatePickerDialog.
	 * 
	 * ScheduleActivity is registered as dateChangeListener.
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
	    switch (id) {
	    case DATE_DIALOG_ID:
	    	return new DatePickerDialog(this, this, mYear, mMonth, mDay);
	    }
	    return null;
	}
	
	/**
	 * Start ScheduleActivity with new date.
	 * 
	 * Newly picked Date is parsed to millisecond display and new
	 * activity is started.
	 */
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		Date picked = new Date();
		picked.setDate(dayOfMonth);
		picked.setMonth(monthOfYear);
		picked.setYear(year - 1900);
		
		Bundle bundle = new Bundle();
		bundle.putString("course", course);
		bundle.putString("year", this.year);
		bundle.putLong("date", picked.getTime());
		Intent intent = new Intent(this.getApplicationContext(), ScheduleActivity.class);
		intent.putExtras(bundle);
		startActivityForResult(intent, 0);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.context_event, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case R.id.menu_hide:
	        	try {
	        		GroupFilter f = new GroupFilter(getApplicationContext());
	        		f.addGroup(events.get(info.position).getType());
	        		updateDataset(false);
	        	} catch (Exception e) {
	        		// Something went wrong
	        	}
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	
	/**
	 * Thread to perform schedule synchronization in.
	 * @author Markus Deutsch <Markus.Deutsch.ITM09@fh-joanneum.at>
	 */
	class ScheduleHelperTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			if(params[0].equals("refresh")){
				//Log.i("ScheduleActivity", "Synchronization forced by user.");
				sh.synchronize(true);
			}
			//Log.i("ScheduleActivity", "The size of the ScheduleHelper is " + sh.getSize() + ". Synchronization triggered.");
			events = sh.getSchedule(date);
			return "null";
		}
		
		protected void onPostExecute(String result) {
			populateList();
			prog.dismiss();
		}
		
	}
	
	private class ScheduleAdapter extends BaseAdapter {
    	private Event current;

		public int getCount() {
			return events.size();
		}

		public Object getItem(int position) {
			return events.get(position);
		}

		public long getItemId(int position) {
			return position;
		}
		
		public int getItemViewType(int position) {
			return 0;
		}
		
		public int getViewTypeCount() {
			return 1;
		}
		
		public boolean isEnabled(int position) {
			return true;
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			
			/**
			 * Set a type flag.
			 */
			if(convertView == null) {
				final LayoutInflater inflater = LayoutInflater.from(ScheduleActivity.this);
				final int layout = R.layout.item_schedule;
				convertView = inflater.inflate(layout, parent, false);
			}

			current = (Event) getItem(position);
			TextView col1 = ((TextView) convertView.findViewById(R.id.item1));
			TextView col2 = ((TextView) convertView.findViewById(R.id.item2));
			View col3 = ((View) convertView.findViewById(R.id.item3));
			
			Integer bgcolor = Configuration.EVENT_COLORS.get(current.getType().toUpperCase());
	    	if(bgcolor == null){bgcolor = R.color.white;}
	    		    				
			col1.setText(Configuration.SIMPLE_DATE_TIME.format(current.getStart())+" - "+Configuration.SIMPLE_DATE_TIME.format(current.getEnd()));
			col2.setText(current.getSubject() + " ("+ current.getType()+ ")");
			col3.setBackgroundResource(bgcolor);

			return convertView;
		}
    	
    }

	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		StringBuilder sb = new StringBuilder();
    	sb.append(Configuration.SIMPLE_DATE_DAY.format(events.get(position).getStart()));
    	sb.append("\n");
    	sb.append(Configuration.SIMPLE_DATE_TIME.format(events.get(position).getStart())+" - "+Configuration.SIMPLE_DATE_TIME.format(events.get(position).getEnd())+"\n");
    	sb.append(events.get(position).getSubject()+" ("+events.get(position).getType()+")\n");
    	sb.append(getString(R.string.lbl_location) + ": " + events.get(position).getLocation()+"\n");
    	sb.append(getString(R.string.lbl_lecturer) + ": " + events.get(position).getLecturer());
    	    	
    	scheduleDetail.setText(sb.toString());
		alertDialog.setTitle(getString(R.string.lbl_schedule_dialog));
		alertDialog.show();
		
	}

}

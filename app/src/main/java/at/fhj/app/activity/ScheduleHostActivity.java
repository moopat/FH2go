package at.fhj.app.activity;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import at.fhj.app.R;
import at.fhj.app.fragment.ScheduleFragment;
import at.fhj.app.model.EventDAO;
import at.fhj.app.util.Configuration;
import at.fhj.app.util.ScheduleProvider;

import java.util.Date;

/**
 * This activity host a ViewPager which contains all ScheduleFragments.
 * 
 * @author Markus Deutsch <Markus.Deutsch.ITM09@fh-joanneum.at>
 *
 */
public class ScheduleHostActivity extends FragmentActivity implements OnClickListener, OnDateSetListener {
	private TextView activityMainTitle, updated;
	private ProgressDialog prog;
	private ImageView btn_update, btn_chdate, btn_chclass;

    private ViewPager vpHost;
    private SchedulePagerAdapter adapter;
    private int lastPagerPosition;
	
	private String course;
	private String year;
	
	private ScheduleProvider sp;

	// Date items needed for the datepicker
	private int mDay;
	private int mMonth;
	private int mYear;
	static final int DATE_DIALOG_ID = 0;

    protected void onCreate(Bundle savedInstanceState){    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_host);
        
        Configuration.initFormatter();

		// Init the offset day with today.
        Date temp = new Date();

        // Offset for the datepicker
		mDay = temp.getDate();
		mMonth = temp.getMonth();
		mYear = temp.getYear() + 1900;

		/**
		 * Map items from the layout to properties
		 */
		activityMainTitle = (TextView) findViewById(R.id.activityMainTitle);
		btn_update = (ImageView) findViewById(R.id.btn_update);
		btn_chdate = (ImageView) findViewById(R.id.btn_chdate);
		btn_chclass = (ImageView) findViewById(R.id.btn_chclass);
		updated = (TextView) findViewById(R.id.updated);
        vpHost = (ViewPager) findViewById(R.id.vpHost);
				
		/**
		 * Register listeners for buttons (datepicker, back & forth)
		 */
		btn_chclass.setOnClickListener(this);
		btn_chdate.setOnClickListener(this);
		btn_update.setOnClickListener(this);

        sp = ScheduleProvider.getInstance(this);

        // Get course data from the preferences.
        updateCourse();

        // Redirect if no selection was made.
        if(year.equals("") || course.equals("")){
            Intent intent = new Intent(this, ScheduleChooserActivity.class);
            startActivityForResult(intent, ScheduleChooserActivity.REQUEST_CODE_SCHEDULE);
            return;
        }

		initSchedule();
    }

    /**
     * Download lectures if necessary, show schedule otherwise.
     */
    private void initSchedule(){
        if(sp.getCount(course, year) < 1){
            updateSchedule();
        } else {
            preparePager();
        }
    }

    /**
     * Fetch course data from the preferences and update the heading accordingly.
     */
    private void updateCourse(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        year = prefs.getString(Configuration.PREFERENCE_YEAR, "");
        course = prefs.getString(Configuration.PREFERENCE_COURSE, "");
        if(year.equals("") || course.equals("")) return;
        activityMainTitle.setText(course + year.substring(2));
    }

    /**
     * Set the adapter and jump to the appropriate day.
     */
    private void preparePager(){
        adapter = new SchedulePagerAdapter(getSupportFragmentManager());
        updateLastUpdate();
        vpHost.setAdapter(adapter);
        vpHost.setCurrentItem(lastPagerPosition);
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
     * Go to schedule chooser.
     */
	public void goToChooser(){
    	Intent intent = new Intent(this, ScheduleChooserActivity.class);
    	startActivityForResult(intent, ScheduleChooserActivity.REQUEST_CODE_SCHEDULE);
	}

	public void updateSchedule(){
        prog = ProgressDialog.show(this, "", getString(R.string.prog_wait), false);
        lastPagerPosition = vpHost.getCurrentItem();
		sp.update(course, year, new ScheduleProvider.ScheduleActionListener() {
            @Override
            public void onUpdated() {
                preparePager();
                prog.dismiss();
            }
        });
	}

	/**
	 * Map back/forth buttons to actions.
	 */
	public void onClick(View v) {
		if (btn_update.isPressed()){
			updateSchedule();
		} else if (btn_chclass.isPressed()){
			goToChooser();
		} else if (btn_chdate.isPressed()){
			showDialog(DATE_DIALOG_ID);
		}
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

        int newpos = (int) ((picked.getTime() - new Date().getTime()) / (24 * 60 * 60 * 1000));

        newpos = newpos >= 100 ? 99 : newpos;
        newpos = newpos < 0 ? 0 : newpos;

        if(vpHost != null) vpHost.setCurrentItem(newpos, true);
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ScheduleChooserActivity.REQUEST_CODE_SCHEDULE && resultCode == RESULT_OK){
            lastPagerPosition = vpHost.getCurrentItem();
            updateCourse();
            initSchedule();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public class SchedulePagerAdapter extends FragmentStatePagerAdapter {

        private final long today;

        public SchedulePagerAdapter(FragmentManager fm) {
            super(fm);
            today = new Date().getTime();
        }

        @Override
        public Fragment getItem(int i) {
            return ScheduleFragment.newInstance(getFormattedDate(i), getDate(i));
        }

        @Override
        public int getCount() {
            // Max of 100 days ahead.
            return 100;
        }

        private String getFormattedDate(int i){
            return Configuration.SIMPLE_DATE_DAY.format(new Date(getDate(i)));
        }

        private long getDate(int i){
            return today + (long) i * 24 * 60 * 60 * 1000;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getFormattedDate(position);
        }
    }

}

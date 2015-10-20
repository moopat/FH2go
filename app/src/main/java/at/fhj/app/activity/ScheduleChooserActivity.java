package at.fhj.app.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import at.fhj.app.R;
import at.fhj.app.adapter.DegreeProgrammeAdapter;
import at.fhj.app.util.Configuration;

/**
 * This class lets the user choose which class' schedule he wants to see.
 * 
 * This is displayed whenever the user wants to change the class in ScheduleActivity
 * or whenever he calls ScheduleActivity from MainActivity without having set his
 * preferences for class and year.
 * 
 * @author Markus Deutsch <Markus.Deutsch.ITM09@fh-joanneum.at>
 */
public class ScheduleChooserActivity extends Activity implements OnClickListener,
		OnItemSelectedListener {

    public static final int REQUEST_CODE_SCHEDULE = 100;

	private Button button;
	private Spinner yearSpinner;
    private SharedPreferences prefs;
	
	private String defaultCourse;
	private String defaultYear;
	
	private TextView selectedCourse;
	private AutoCompleteTextView actCourse;

	private DegreeProgrammeAdapter degreeProgrammeAdapter;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chschedule);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		/**
		 * Retrieve extras if available
		 */
        defaultCourse = prefs.getString(Configuration.PREFERENCE_COURSE, "");
        defaultYear = prefs.getString(Configuration.PREFERENCE_YEAR, "");
		
		/**
		 * This TextView is the textual representation of the currently selected course
		 */
		selectedCourse = (TextView) findViewById(R.id.selected_course);

		/**
		 * Course Selection (Autocomplete)
		 */
		degreeProgrammeAdapter = new DegreeProgrammeAdapter(this);
		actCourse = (AutoCompleteTextView) findViewById(R.id.actCourse);
		actCourse.setAdapter(degreeProgrammeAdapter);
		actCourse.setText(defaultCourse);
		actCourse.setOnItemSelectedListener(this);
		actCourse.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				updateSelectedCourseTextView();
			}
		});

	    /**
	     * Spinner for year.
	     * @TODO Make years dynamic.
	     */
	    yearSpinner = (Spinner) findViewById(R.id.year_spinner);
	    ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this, R.array.years, android.R.layout.simple_spinner_item);
	    yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    yearSpinner.setAdapter(yearAdapter);
	    yearSpinner.setSelection(this.getYearPosition(defaultYear));
	    
	    updateSelectedCourseTextView();
	    
	    /**
	     * Map buttons and register a nice ocl on it.
	     */
	    button = (Button) findViewById(R.id.button);
	    button.setOnClickListener(this);
	}

	/**
	 * Do the magic when somebody clicks the button.
	 * 
	 * Bundle course and year and send them to ScheduleActivity.ArrayAdapter<CharSequence> 
	 * We don't put a date so "today" will be assumed.
	 */
	public void onClick(View v) {
		String course = actCourse.getText().toString();
		String year;
		
		// DAF Workaround
		if(course.equalsIgnoreCase("DAF")){
			year = getResources().getStringArray(R.array.daf)[yearSpinner.getSelectedItemPosition()];
		} else {
			year = getResources().getStringArray(R.array.years)[yearSpinner.getSelectedItemPosition()];
		}

        prefs.edit().putString(Configuration.PREFERENCE_COURSE, course).putString(Configuration.PREFERENCE_YEAR, year).commit();

        setResult(RESULT_OK);
		finish();
	}
	
	/**
	 * Get the position of a year in the array.
	 * 
	 * Returns the position of the year in the
	 * static xml array file.
	 */
	public int getYearPosition(String year){
		String[] years = this.getResources().getStringArray(R.array.years);
		
		for(int i = 0; i < years.length; i++){
			if(years[i].equals(year)){
				return i;
			}
		}
		
		return 0;
	}
	
	/**
	 * Update Selected Course TextView with title of selected course.
	 * Update year selection for special courses like DAF.
	 */
	public void updateSelectedCourseTextView(){

		String course = actCourse.getText().toString();

		/**
		 * Display full name of course
		 */
		selectedCourse.setText(degreeProgrammeAdapter.getCourseNameForAbbreviation(course));
		
		/**
		 * Create year adapter depending on selected course
		 */
		ArrayAdapter<CharSequence> yearAdapter;
		int previousYear = yearSpinner.getSelectedItemPosition();
		int yearAdapterResource = R.array.years;
		
		if(course.equalsIgnoreCase("DAF")){
		    yearAdapterResource = R.array.daf;
		}
		
		yearAdapter = ArrayAdapter.createFromResource(this, yearAdapterResource, android.R.layout.simple_spinner_item);
	    yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    yearSpinner.setAdapter(yearAdapter);

	    try {
	    	String[] array = getResources().getStringArray(yearAdapterResource);
	    	if(previousYear < array.length){
	    		yearSpinner.setSelection(previousYear);
	    	}
	    } catch (Exception e) {
	    	//s2.setSelection(0);
	    }
	    
	}

	/**
	 * Update TextView if an item gets selected.
	 */
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		updateSelectedCourseTextView();
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

}

package at.fhj.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import at.fhj.app.MainActivity;
import at.fhj.app.R;
import at.fhj.app.model.Exam;
import at.fhj.app.retriever.FHPIRetriever;
import at.fhj.app.util.Configuration;
import at.fhj.app.util.ConnectionInformation;

/**
 * This activity is used to display the details of an exam.
 * @author Markus Deutsch <Markus.Deutsch.ITM09@fh-joanneum.at>
 */
public class ExamDetailActivity extends Activity implements OnClickListener {
	private TextView examTitle, examDate, examDeadline, examType, examMode, examNote, examStatus;
	private Exam exam;
	private Button button;
	private ProgressDialog prog;
	private SharedPreferences prefs;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exam);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		Configuration.initFormatter();
		
		// Map properties
		examTitle = (TextView) findViewById(R.id.examTitle);
		examDate = (TextView) findViewById(R.id.date);
		examDeadline = (TextView) findViewById(R.id.deadline);
		examType = (TextView) findViewById(R.id.type);
		examMode = (TextView) findViewById(R.id.mode);
		examNote = (TextView) findViewById(R.id.note);
		examStatus = (TextView) findViewById(R.id.status);
		
		// Set listeners for button
		button = (Button) findViewById(R.id.btn_signon);
		button.setOnClickListener(this);
		
		try {
			/**
			 * Process and display the Exam class passed to this activity.
			 */
			Bundle bundle = this.getIntent().getExtras();
			exam = (Exam) bundle.getSerializable("exam");
			examTitle.setText(exam.getCourse());
			examDate.setText(Configuration.SIMPLE_DATE.format(exam.getExamDate()));
			examDeadline.setText(Configuration.SIMPLE_DATETIME.format(exam.getExamRegistrationEnd()));
			examType.setText(exam.getType());
			examMode.setText(exam.getMode());
			examNote.setText(exam.getNote());
			examStatus.setText(exam.getStatusReadable());
		} catch (Exception e){
			e.printStackTrace();
			examTitle.setText(getString(R.string.msg_noexams));
		}
		
		updateButton();
	}
	
	/**
	 * Depending on current status, sign up or off on button press.
	 */
	public void onClick(View arg0) {
		if(button.isPressed()){
			prog = ProgressDialog.show(this, "", getString(R.string.prog_wait), false);
			if(exam.getStatus().equals("registered")){
				ExamModifier r = new ExamModifier(this);
				r.execute(ExamModifier.SIGNOFF);
			} else {
                ExamModifier r = new ExamModifier(this);
				r.execute(ExamModifier.SIGNUP);
			}
			
		}
	}
	
	/**
	 * Update button action to reflect current status.
	 * 
	 * Disable button if status doesn't allow status change, switch it to 
	 * "Sign off" if signed up, switch to "Sign up" if otherwise.
	 * 
	 * @TODO Disable change of exam status if deadline has been reached.
	 */
	public void updateButton(){
		if(exam.getStatus().equals("registered")){
			// If you are registered, you are able to sign off
			button.setText(getText(R.string.lbl_signoff));
			button.setBackgroundResource(R.drawable.selector_button_red);
			button.setClickable(true);
		} else if(exam.isLocked()){
			// If the exam is locked you cannot sign up or off
			button.setText(getText(R.string.lbl_nochangepermitted));
			button.setBackgroundResource(R.drawable.selector_button_gray);
			button.setClickable(false);
		} else {
			// If you are not signed up you might want to do so.
			button.setText(getText(R.string.lbl_signup));
			button.setBackgroundResource(R.drawable.selector_button_green);
			button.setClickable(true);
		}
	}
	
	/**
	 * Thread to sign up for exam.
	 * @author Markus Deutsch <Markus.Deutsch.ITM09@fh-joanneum.at>
	 */
	private class ExamModifier extends AsyncTask<String, String, String>{
		Activity a;
		String status;
        String action;

        public static final String SIGNUP = "signup";
        public static final String SIGNOFF = "signoff";
		
		public ExamModifier(Activity a){
			this.a = a;
		}

		@Override
		protected String doInBackground(String... params) {
            action = params[0];
			FHPIRetriever fr = new FHPIRetriever();
			String[] arguments = {"u", "p", "e", "a"};
			String[] values = {prefs.getString("username", "0"), prefs.getString("password", "0"), exam.getId().toString(), action};
			fr.prepareRequest("signup.php", arguments, values);
			
			return fr.retrieve();
		}
		
		protected void onPostExecute(String result) {
			status = ConnectionInformation.getConnectionStatus(result);
			prog.dismiss();
			if(status.equals("OK")){
                Toast.makeText(getApplicationContext(), getString(action.equals(SIGNOFF) ? R.string.msg_signedoff : R.string.msg_signedup), Toast.LENGTH_LONG).show();
				setResult(RESULT_OK);
                finish();
			} else {
				Toast.makeText(getApplicationContext(), getString(ConnectionInformation.getConnectionStatusString(status)), Toast.LENGTH_LONG).show();
			}
	     }
	}

}
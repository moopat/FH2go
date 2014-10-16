package at.fhj.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import at.fhj.app.R;
import at.fhj.app.util.ConnectionInformation;

/**
 * This class helps the user to log in.
 * 
 * The class shows input fields for username and password, as well as the possibility
 * to remember credentials. After entering the activity finishes and returns credentials
 * to the calling activity.
 * 
 * @author Markus Deutsch <markus.deutsch@edu.fh-joanneum.at>
 */
public class LoginActivity extends Activity implements OnClickListener {
	private Button button;
	private EditText username, password;
    private TextView error;
    private SharedPreferences prefs;

    /**
     * This request code is used when no or incomplete user data
     * is stored in the preferences and the user needs to log in.
     */
    public static final int REQUEST_LOGIN = 1;

    /**
     * This request code is used when authentication failed because
     * of wrong credentials.
     */
    public static final int REQUEST_REAUTH = 2;

    public static final String STATUS_OK = "OK";
    public static final String STATUS_ERROR = "ERROR";
    public static final String STATUS_FAILED = "INVALID_DATA";
    public static final String STATUS_INCOMPLETE = "INCOMPLETE_DATA";
    public static final String STATUS_LOCKED = "LOCKED";

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        error = (TextView) findViewById(R.id.error);
		username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
	    button = (Button) findViewById(R.id.button);
	    button.setOnClickListener(this);

        password.setImeActionLabel(getString(R.string.lbl_login), EditorInfo.IME_ACTION_DONE);
        password.setImeOptions(EditorInfo.IME_ACTION_DONE);
        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    button.requestFocus();
                    onClick(null);
                    return true;
                }
                return false;
            }
        });
	}

    public void onResume(){
        super.onResume();
        username.setText(prefs.getString("username", ""));
        password.setText(prefs.getString("password", ""));

        if(getIntent().getStringExtra("status") != null && !getIntent().getStringExtra("status").equals("")){
            error.setText(ConnectionInformation.getConnectionStatusString(getIntent().getStringExtra("status")));
            error.setVisibility(View.VISIBLE);
        }
    }

	public void onClick(View v) {
        String u = username.getText().toString();
        String p = password.getText().toString();

		if(u != null && !u.equals("") && p != null && !p.equals("")){
            error.setVisibility(View.GONE);
            prefs.edit().putString("username", u).putString("password", p).commit();
            setResult(RESULT_OK);
            finish();
        } else {
            error.setText(R.string.login_incomplete);
            error.setVisibility(View.VISIBLE);
        }
	}

}

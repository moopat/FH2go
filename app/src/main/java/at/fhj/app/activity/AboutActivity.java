package at.fhj.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import at.fhj.app.R;

/**
 * AboutActivity displays some information about the application.
 * 
 * @author Markus Deutsch <Markus.Deutsch.ITM09@fh-joanneum.at>
 *
 */
public class AboutActivity extends Activity implements OnClickListener {
	private Button help, facebook, mail, faq;
	private TextView developers, tutor, credits;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		// Map layout items to properties
		help = (Button) findViewById(R.id.help);
		mail = (Button) findViewById(R.id.mail);
		faq = (Button) findViewById(R.id.faq);
		facebook = (Button) findViewById(R.id.facebook);
		
		developers = (TextView) findViewById(R.id.developers);
		tutor = (TextView) findViewById(R.id.tutor);
		credits = (TextView) findViewById(R.id.credits);
		
		// Set listener for help button
		help.setOnClickListener(this);
		mail.setOnClickListener(this);
		facebook.setOnClickListener(this);
		faq.setOnClickListener(this);
		
		developers.setText("Markus Deutsch");
		tutor.setText("DI Johannes Feiner");
		credits.setText("DanRabbit (Icons)\nThomas Radeke (Stundenplan-Service)");
	}

	public void onClick(View v) {
		// Open browser and display help page.
		if(help.isPressed()){
			String url = "http://www.moop.at/fhj/help.php";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
		}
		
		// Open browser and display help page.
				if(faq.isPressed()){
					String url = "http://www.moop.at/fhj/help.php#faq";
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					startActivity(i);
				}
		
		// Send an email.
		if(mail.isPressed()){
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("text/html");
        	i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"support@moop.at"});
        	i.putExtra(android.content.Intent.EXTRA_SUBJECT, "FH2go Feedback");
        	try {
        		startActivity(i);
        	} catch (Exception e){
        		Toast.makeText(getApplicationContext(), getString(R.string.msg_nomailintent), Toast.LENGTH_LONG).show();
        	}
		}
		
		// Open browser and display facebook page.
		if(facebook.isPressed()){
			String url = "http://www.facebook.com/pages/FH2go/337845092906426";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
		}
		
	}

}

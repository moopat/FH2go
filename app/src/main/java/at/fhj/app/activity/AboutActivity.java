package at.fhj.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import at.fhj.app.R;

/**
 * AboutActivity displays some information about the application.
 *
 * @author Markus Deutsch <Markus.Deutsch.ITM09@fh-joanneum.at>
 */
public class AboutActivity extends Activity {
    private TextView developers, tutor, credits;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        developers = (TextView) findViewById(R.id.developers);
        tutor = (TextView) findViewById(R.id.tutor);
        credits = (TextView) findViewById(R.id.credits);

        developers.setText("Markus Deutsch, MSc (2012-2017)");
        tutor.setText("DI Johannes Feiner");
        credits.setText("DanRabbit (Icons)\nThomas Radeke (Stundenplan-Service)");
    }

}

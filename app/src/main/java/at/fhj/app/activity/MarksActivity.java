package at.fhj.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ListView;
import android.widget.Toast;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.StringReader;
import java.util.ArrayList;

import at.fhj.app.R;
import at.fhj.app.adapter.MarksAdapter;
import at.fhj.app.model.Mark;
import at.fhj.app.model.MarkContentHandler;
import at.fhj.app.retriever.FHPIRetriever;
import at.fhj.app.retriever.RequestFinishedListener;
import at.fhj.app.util.ConnectionInformation;

/**
 * Display a list of all marks
 *
 * @author Markus Deutsch <markus.deutsch@edu.fh-joanneum.at>
 */
public class MarksActivity extends Activity implements RequestFinishedListener {
    private ListView listview;
    private ArrayList<Mark> marks;
    private ArrayList<Object> listitems;
    public ProgressDialog prog;
    private SharedPreferences prefs;

    protected void onCreate(Bundle savedInstanceState) {
        // Set property for XML parser
        System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marks);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        startRoutine();
    }

    /**
     * Based on the availability of login data the webservice request
     * is triggered or the login screen is shown.
     */
    private void startRoutine() {
        if (hasLoginData()) {
            fetchMarks();
        } else {
            Intent login = new Intent(this, LoginActivity.class);
            startActivityForResult(login, LoginActivity.REQUEST_LOGIN);
        }
    }

    /**
     * Checks if the user has provided a username and password.
     *
     * @return boolean whether the user has provided credentials
     */
    public boolean hasLoginData() {
        return !(prefs.getString("username", "").equals("") || prefs.getString("password", "").equals(""));
    }

    /**
     * Download the list of marks.
     * <p>
     * After the download onSuccess or onFail are called.
     */
    private void fetchMarks() {
        prog = ProgressDialog.show(this, "", getString(R.string.prog_wait), false);
        prog.setCancelable(true);
        prog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                finish();
            }
        });
        MarksRetriever r = new MarksRetriever(this);
        r.execute("Hallo");
    }

    @Override
    public void onSuccess(ArrayList resultList) {
        listview = (ListView) findViewById(R.id.listview);
        listview.setAdapter(new MarksAdapter(listitems, getApplicationContext()));
    }

    @Override
    public void onFail(String status) {
        if (status.equals("ERROR")) {
            Toast.makeText(getApplicationContext(), ConnectionInformation.getConnectionStatusString(status), Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        Intent login = new Intent(this, LoginActivity.class);
        login.putExtra("status", status);
        startActivityForResult(login, LoginActivity.REQUEST_REAUTH);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LoginActivity.REQUEST_LOGIN && resultCode == RESULT_OK) {
            startRoutine();
        }

        if (requestCode == LoginActivity.REQUEST_REAUTH && resultCode == RESULT_OK) {
            startRoutine();
        }

        /**
         * If the user cancelled the login screen then we most likely cannot proceed.
         */
        if (resultCode == RESULT_CANCELED) {
            finish();
        }

    }

    /**
     * Thread to retrieve marks from server and redirect to MarksActivity
     *
     * @author Markus Deutsch <Markus.Deutsch.ITM09@fh-joanneum.at>
     */
    private class MarksRetriever extends AsyncTask<String, String, Boolean> {
        RequestFinishedListener rfl;
        String status;

        public MarksRetriever(RequestFinishedListener rfl) {
            this.rfl = rfl;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            FHPIRetriever fr = new FHPIRetriever();
            String[] arguments = {"u", "p"};
            String[] values = {prefs.getString("username", "0"), prefs.getString("password", "0")};
            fr.prepareRequest("getmarks.php", arguments, values);

            ArrayList<Mark> marks = new ArrayList<Mark>();

            String vals = fr.retrieve();
            status = ConnectionInformation.getConnectionStatus(vals);

            /**
             * Parse marks.
             */
            try {
                XMLReader xmlReader = XMLReaderFactory.createXMLReader();
                MarkContentHandler mch = new MarkContentHandler();
                xmlReader.setContentHandler(mch);
                xmlReader.parse(new InputSource(new StringReader(vals)));
                marks = mch.marks;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            /*
             * The marks have to be loaded into a new array.
             * The array consists of Strings (term names) and Marks objects.
             * Right now, this is the easiest ways to draw separators for terms.
             */
            listitems = new ArrayList<Object>();
            if (marks != null && marks.size() > 0) {
                for (int i = 0; i < marks.size(); i++) {
                    if (i == 0 || !marks.get(i).getTerm().equals(marks.get(i - 1).getTerm())) {
                        listitems.add(marks.get(i).getTerm());
                    }
                    listitems.add(marks.get(i));
                }
            }

            return true;

        }

        protected void onPostExecute(Boolean boo) {
            prog.dismiss();
            if (status.equals("OK")) {
                rfl.onSuccess(null);
            } else {
                rfl.onFail(status);
            }

        }

    }

}

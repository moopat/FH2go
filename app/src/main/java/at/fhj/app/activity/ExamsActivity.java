package at.fhj.app.activity;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import at.fhj.app.retriever.FHPIRetriever;
import at.fhj.app.retriever.RequestFinishedListener;
import at.fhj.app.util.ConnectionInformation;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import at.fhj.app.R;
import at.fhj.app.model.Exam;
import at.fhj.app.model.ExamContentHandler;
import at.fhj.app.util.Configuration;

/**
 * This activity is used to display a list of all upcoming exams.
 * @author Markus Deutsch <Markus.Deutsch.ITM09@fh-joanneum.at>
 */
public class ExamsActivity extends ListActivity implements RequestFinishedListener {
	private ArrayList<Exam> exams;
    public ProgressDialog prog;
    private SharedPreferences prefs;

    public static final int REQUEST_EXAMS = 3;

    protected void onCreate(Bundle savedInstanceState){
    	// Set the property so XML can be parsed.
    	System.setProperty("org.xml.sax.driver","org.xmlpull.v1.sax2.Driver");
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        
        Configuration.initFormatter();
        Log.e("ExamsActivity", "onCreate");
        startRoutine();

    }

    private void startRoutine(){
        if(hasLoginData()){
            fetchExams();
        } else {
            Intent login = new Intent(this, LoginActivity.class);
            startActivityForResult(login, LoginActivity.REQUEST_LOGIN);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == LoginActivity.REQUEST_LOGIN && resultCode == RESULT_OK){
            startRoutine();
        }

        if(requestCode == LoginActivity.REQUEST_REAUTH && resultCode == RESULT_OK){
            startRoutine();
        }

        if(requestCode == REQUEST_EXAMS){
            startRoutine();
            return;
        }

        if(resultCode == RESULT_CANCELED){
            finish();
        }

    }

    public boolean hasLoginData(){
        return !(prefs.getString("username", "").equals("") || prefs.getString("password", "").equals(""));
    }

    public void fetchExams(){
        prog = ProgressDialog.show(this, "", getString(R.string.prog_wait), false);
        prog.setCancelable(true);
        prog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                finish();
            }
        });
        ExamsRetriever r = new ExamsRetriever(this);
        r.execute("Hallo");
    }
    
    protected void onListItemClick(ListView l, View v, int position, long id){
    	Bundle bundle = new Bundle();
		bundle.putSerializable("exam", exams.get(position));
		Intent intent = new Intent(ExamsActivity.this, ExamDetailActivity.class);
		intent.putExtras(bundle);
		startActivityForResult(intent, REQUEST_EXAMS);
    }

    @Override
    public void onSuccess(ArrayList resultList) {
        exams = resultList;
        if(exams == null && exams.size() > 0){
            //Log.i("ExamsActivity", "No exams available.");
        } else {
            //Log.i("ExamsActivity", "Iterating over exams list with size "+exams.size()+".");
            List<HashMap<String, Object>> content = new ArrayList<HashMap<String, Object>>();
            String[] from = new String[] {"col_1", "col_2", "status"};
            int[] to = new int[] { R.id.item1, R.id.item2, R.id.status};
            HashMap<String, Object> map = new HashMap<String, Object>();

            // Map single exams to list items which are then added to ListView
            for(int i=0; i<exams.size(); i++){
                map = new HashMap<String, Object>();
                map.put("col_1", exams.get(i).getCourse());
                map.put("col_2", Configuration.SIMPLE_DATE.format(exams.get(i).getExamDate()));

                /**
                 * Display star, depending on status of exam.
                 * @TODO Implement isSignedUp() method for class Exam, to manage status centrally.
                 */
                if(exams.get(i).getStatus().equals("registered") || exams.get(i).getStatus().equals("registeredByOffice")){
                    map.put("status", R.drawable.star);
                } else {
                    map.put("status", R.drawable.star_bw);
                }
                //Log.i("ExamsActivity", "Iterated over item " + i + " with course " + exams.get(i).getCourse()+".");
                content.add(map);
            }

            SimpleAdapter adapter = new SimpleAdapter(this, content, R.layout.item_exam, from, to);
            setListAdapter(adapter);
        }
    }

    @Override
    public void onFail(String status) {
        if(status.equals("ERROR")){
            Toast.makeText(getApplicationContext(), ConnectionInformation.getConnectionStatusString(status), Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        Intent login = new Intent(this, LoginActivity.class);
        login.putExtra("status", status);
        startActivityForResult(login, LoginActivity.REQUEST_REAUTH);
    }

    /**
     * Thread to retrieve exams from server and redirect to ExamsActivity
     *
     * @author Markus Deutsch <Markus.Deutsch.ITM09@fh-joanneum.at>
     *
     */
    private class ExamsRetriever extends AsyncTask<String, String, ArrayList<Exam>> {
        String status;
        at.fhj.app.retriever.RequestFinishedListener rfl;

        public ExamsRetriever(at.fhj.app.retriever.RequestFinishedListener rfl){
            this.rfl = rfl;
        }

        @Override
        protected ArrayList<Exam> doInBackground(String... params) {
            FHPIRetriever fr = new FHPIRetriever();
            String[] arguments = {"u", "p"};
            String[] values = {prefs.getString("username", "0"), prefs.getString("password", "0")};
            fr.prepareRequest("getexams.php", arguments, values);

            String result = fr.retrieve();
            status = ConnectionInformation.getConnectionStatus(result);
            ArrayList<Exam> exams = new ArrayList<Exam>();

            try {
                XMLReader xmlReader = XMLReaderFactory.createXMLReader();
                ExamContentHandler ech = new ExamContentHandler();
                xmlReader.setContentHandler(ech);
                xmlReader.parse(new InputSource(new StringReader(result)));
                exams = ech.exams;
            } catch(Exception e) {
                //Log.i("ExamsActivity", "Parsing error.");
                e.printStackTrace();
            }

            return exams;
        }

        protected void onPostExecute(ArrayList<Exam> exams) {

            prog.dismiss();
            if(status.equals("OK")){
                rfl.onSuccess(exams);
            } else {
                rfl.onFail(status);
            }

        }

    }
	
}

package at.fhj.app.activity;

import java.io.StringReader;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.*;
import at.fhj.app.retriever.FHPIRetriever;
import at.fhj.app.retriever.RequestFinishedListener;
import at.fhj.app.util.ConnectionInformation;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import at.fhj.app.R;
import at.fhj.app.model.Mark;
import at.fhj.app.model.MarkContentHandler;

/**
 * Display a list of all marks

 * @author Markus Deutsch <markus.deutsch@edu.fh-joanneum.at>
 */
public class MarksActivity extends Activity implements RequestFinishedListener {
	private ListView listview;
	private ArrayList<Mark> marks;
	private ArrayList<Object> listitems;
    public ProgressDialog prog;
    private SharedPreferences prefs;
	
	private static final int TYPE_SEPARATOR = 0;
	private static final int TYPE_MARK = 1;

    protected void onCreate(Bundle savedInstanceState){
    	// Set property for XML parser
    	System.setProperty("org.xml.sax.driver","org.xmlpull.v1.sax2.Driver");
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marks);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        startRoutine();
    }

    /**
     * Based on the availability of login data the webservice request
     * is triggered or the login screen is shown.
     */
    private void startRoutine(){
        if(hasLoginData()){
            fetchMarks();
        } else {
            Intent login = new Intent(this, LoginActivity.class);
            startActivityForResult(login, LoginActivity.REQUEST_LOGIN);
        }
    }

    /**
     * Checks if the user has provided a username and password.
     * @return boolean whether the user has provided credentials
     */
    public boolean hasLoginData(){
        return !(prefs.getString("username", "").equals("") || prefs.getString("password", "").equals(""));
    }

    /**
     * Download the list of marks.
     *
     * After the download onSuccess or onFail are called.
     */
    private void fetchMarks(){
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
        listview.setAdapter(new MarksAdapter());
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == LoginActivity.REQUEST_LOGIN && resultCode == RESULT_OK){
            startRoutine();
        }

        if(requestCode == LoginActivity.REQUEST_REAUTH && resultCode == RESULT_OK){
            startRoutine();
        }

        /**
         * If the user cancelled the login screen then we most likely cannot proceed.
         */
        if(resultCode == RESULT_CANCELED){
            finish();
        }

    }

    /**
     * MarksAdapter is used to display Marks in a ListView.
     * 
     * @author Markus Deutsch <Markus.Deutsch.ITM09@fh-joanneum.at>
     *
     */
    private class MarksAdapter extends BaseAdapter {
    	private Mark current;

		public int getCount() {
			return listitems.size();
		}

		public Object getItem(int position) {
			return listitems.get(position);
		}

		public long getItemId(int position) {
			return position;
		}
		
		public int getItemViewType(int position) {
			return (listitems.get(position) instanceof String) ? TYPE_SEPARATOR : TYPE_MARK;
		}
		
		public int getViewTypeCount() {
			return 2;
		}
		
		public boolean isEnabled(int position) {
			return getItemViewType(position) != TYPE_SEPARATOR;
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			int img = 0;
			final int type = getItemViewType(position);
			
			/**
			 * Set a type flag.
			 */
			if(convertView == null) {
				final LayoutInflater inflater = LayoutInflater.from(MarksActivity.this);
				final int layout = type == TYPE_SEPARATOR ? R.layout.item_separator : R.layout.item_mark;
				convertView = inflater.inflate(layout, parent, false);
			} 

			if(type == TYPE_SEPARATOR){
				/**
				 * If it's a separator (and the array item is a String)
				 * set the array text (term) as content of the separator.
				 */
				((TextView) convertView.findViewById(R.id.separator)).setText((String) listitems.get(position));
			} else {
				/**
				 * Populate the regular mark item layout.
				 */
				current = (Mark) getItem(position);
				TextView course = ((TextView) convertView.findViewById(R.id.item1));
				ImageView mark = ((ImageView) convertView.findViewById(R.id.mark));
				
				/**
				 * Depending on the mark, there's a different drawable.
				 */
				switch(current.getMark()){
					case 1:
						img = R.drawable.mark_1;
						break;
					case 2:
						img = R.drawable.mark_2;
						break;
					case 3:
						img = R.drawable.mark_3;
						break;
					case 4:
						img = R.drawable.mark_4;
						break;
					case 5:
						img = R.drawable.mark_5;
						break;
				}

				course.setText(current.getCourse());
				mark.setImageResource(img);
			}
			

			return convertView;
		}
    	
    }

    /**
     * Thread to retrieve marks from server and redirect to MarksActivity
     *
     * @author Markus Deutsch <Markus.Deutsch.ITM09@fh-joanneum.at>
     *
     */
    private class MarksRetriever extends AsyncTask<String, String, Boolean> {
        RequestFinishedListener rfl;
        String status;

        public MarksRetriever(RequestFinishedListener rfl){
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
            } catch(Exception e) {
                e.printStackTrace();
                return false;
            }

            /*
             * The marks have to be loaded into a new array.
             * The array consists of Strings (term names) and Marks objects.
             * Right now, this is the easiest ways to draw separators for terms.
             */
                listitems = new ArrayList<Object>();
                if(marks != null && marks.size() > 0){
                    for(int i = 0; i < marks.size(); i++){
                        if(i == 0 || !marks.get(i).getTerm().equals(marks.get(i-1).getTerm())){
                            listitems.add(marks.get(i).getTerm());
                        }
                        listitems.add(marks.get(i));
                    }
                }

            return true;

         }

        protected void onPostExecute(Boolean boo) {
            prog.dismiss();
            if(status.equals("OK")){
                rfl.onSuccess(null);
            } else {
                rfl.onFail(status);
            }

        }

    }
	
}

package at.fhj.app.activity;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
//import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import at.fhj.app.R;
import at.fhj.app.model.NewsItem;
import at.fhj.app.parser.NewsContentHandler;

/**
 * Display a list of all the news items
 * 
 * @author Markus Deutsch <Markus.Deutsch.ITM09@fh-joanneum.at>
 */
public class NewsActivity extends ListActivity {
	private ArrayList<NewsItem> news;
	private int maxItems = 10;

    protected void onCreate(Bundle savedInstanceState){
    	// Set property for XML parser
    	System.setProperty("org.xml.sax.driver","org.xmlpull.v1.sax2.Driver");
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        Bundle bundle = this.getIntent().getExtras();
		String values = bundle.getString("values");
		
		try {
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			NewsContentHandler nch = new NewsContentHandler();
			xmlReader.setContentHandler(nch);
			xmlReader.parse(new InputSource(new StringReader(values)));
			values = nch.news.toString();
			news = nch.news;
		} catch(Exception e) {
			values = getString(R.string.msg_error);
			e.printStackTrace();
		}
		
		if(news == null){
			//Log.i("NewsActivity", "No news available.");
		} else {
			List<HashMap<String, Object>> content = new ArrayList<HashMap<String, Object>>();
			String[] from = new String[] {"col_1"};
			int[] to = new int[] {R.id.item1 };
			HashMap<String, Object> map = new HashMap<String, Object>();
			
			for(int i=0; i<news.size() && i < this.maxItems; i++){
				map = new HashMap<String, Object>();
		    	map.put("col_1", news.get(i).getTitle());
		    	content.add(map);
			}
			
			SimpleAdapter adapter = new SimpleAdapter(this, content, R.layout.item_news, from, to);
	        setListAdapter(adapter);
		}
			
    }
    
    /**
     * Listen to clicks and redirect to news website.
     */
    protected void onListItemClick(ListView l, View v, int position, long id){
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(news.get(position).getLink()));
		startActivity(i);
    }
	
}

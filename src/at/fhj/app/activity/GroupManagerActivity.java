package at.fhj.app.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import at.fhj.app.R;
import at.fhj.app.util.EnterGroupFragment;
import at.fhj.app.util.GroupFilter;
import at.fhj.app.util.OnGroupEnteredListener;

public class GroupManagerActivity extends FragmentActivity implements OnItemClickListener, OnClickListener, OnGroupEnteredListener {
	
	private ArrayList<String> groups;
	private ListView listview;
	private TextView empty;
	private GroupFilter azm;
	private ImageView add;
	
	@Override
	public void onCreate(Bundle savedInstanceState)	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groupmanager);
		
		listview = (ListView) findViewById(R.id.list);
		empty = (TextView) findViewById(R.id.empty);
		add = (ImageView) findViewById(R.id.menu_add);
		listview.setEmptyView(empty);
		registerForContextMenu(listview);
		add.setOnClickListener(this);
		
		azm = new GroupFilter(getApplicationContext());
		
		populateList();
		
	}
	
	public void onResume(){
		super.onResume();
		populateList();
	}
	
	public void populateList(){

		groups = azm.getGroups();
		
		if(groups != null && groups.size() > 0){
			listview.setVisibility(View.VISIBLE);
			empty.setVisibility(View.GONE);
            List<HashMap<String, Object>> content = new ArrayList<HashMap<String, Object>>();
            String[] from = new String[] {"col_1"};
            int[] to = new int[] { R.id.item1 };
            HashMap<String, Object> map = new HashMap<String, Object>();
                        
            for(int i=0; i<groups.size(); i++){
                    map = new HashMap<String, Object>();
                    map.put("col_1", groups.get(i));
                    content.add(map);
            }
            
            SimpleAdapter adapter = new SimpleAdapter(this, content, R.layout.item_group, from, to);
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(this);
		} else {
			empty.setVisibility(View.VISIBLE);
			listview.setVisibility(View.GONE);
		}
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		/*
		Bundle bundle = new Bundle();
		try {
			bundle.putSerializable("msg", msgs.get(arg2));
			bundle.putBoolean("first", false);
			Intent ma = new Intent(this, MessageHost.class);
			ma.putExtras(bundle);                    
			startActivity(ma);
		} catch (ArrayIndexOutOfBoundsException e){
			Toast.makeText(getApplicationContext(), "Dieser Einsatz ist nicht mehr vorhanden.", Toast.LENGTH_LONG).show();
			populateList();
		}
		*/
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.context_group, menu);
	    
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case R.id.menu_unhide:
	        	try {
	        		azm.removeGroup(groups.get(info.position));
	        	} catch (Exception e) {
	        	} finally {
	        		populateList();
	        	}
	            
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	    
	}

	public void onClick(View v) {
		if(add.isPressed()){
			showEnterDialog();
		}
	}

	
	public void showEnterDialog(){
		EnterGroupFragment newFragment = new EnterGroupFragment();
		newFragment.setOnGroupEnteredListener(this);
	    newFragment.show(getSupportFragmentManager(), "ENTERGROUP");
	}

	public void onGroupEntered(String key) {
		azm.addGroup(key);
		populateList();
		
	}

}

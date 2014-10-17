package at.fhj.app.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;
import at.fhj.app.R;
import at.fhj.app.model.Event;
import at.fhj.app.util.Configuration;
import at.fhj.app.util.GroupFilter;
import at.fhj.app.util.ScheduleProvider;

import java.util.ArrayList;

/**
 * Markus Deutsch
 * <markus.deutsch@moop.at>
 * 17.10.14
 */
public class ScheduleFragment extends Fragment implements AdapterView.OnItemClickListener {

    private String currentDate;
    private long currentDateMillis;

    private String course;
    private String year;

    private ArrayList<Event> events;
    private ListView listview;
    private TextView empty;
    private ScheduleProvider sp;

    // Schedule Dialog
    private TextView scheduleDetail;
    private ImageView scheduleImageView;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private LayoutInflater mInflater;
    private View layout;


    public static ScheduleFragment newInstance(String currentDate, long currentDateMillis){
        ScheduleFragment f = new ScheduleFragment();
        f.setCurrentDate(currentDate);
        f.setCurrentDateMillis(currentDateMillis);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_schedule, container, false);

        listview = (ListView) v.findViewById(R.id.listview);
        empty = (TextView) v.findViewById(R.id.empty);

        listview.setOnItemClickListener(this);
        registerForContextMenu(listview);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        course = prefs.getString(Configuration.PREFERENCE_COURSE, "");
        year = prefs.getString(Configuration.PREFERENCE_YEAR, "");

        sp = ScheduleProvider.getInstance(getActivity());
        updateDataset();

        populateList();

        /**
         * Prepare the detailed Schedule Dialog
         */
        mInflater = (LayoutInflater) getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        // TODO: This will crash
        layout = inflater.inflate(R.layout.schedule_dialog, (ViewGroup) v.findViewById(R.id.layout_root));
        scheduleDetail = (TextView) layout.findViewById(R.id.text);
        scheduleImageView = (ImageView) layout.findViewById(R.id.image);
        scheduleImageView.setImageResource(R.drawable.btn_ele_schedule);

        builder = new AlertDialog.Builder(getActivity());
        builder.setView(layout);
        alertDialog = builder.create();

        return v;
    }

    private void updateDataset(){
        events = sp.getSchedule(course, year, currentDateMillis);
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public long getCurrentDateMillis() {
        return currentDateMillis;
    }

    public void setCurrentDateMillis(long currentDateMillis) {
        this.currentDateMillis = currentDateMillis;
    }

    /**
     * Populate ListView with this day's schedule items.
     */
    private void populateList(){
        //updateLastUpdate();

        if(events != null && events.size() > 0){
            listview.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);

            // Initialize event colors, so each type has its own color.
            Configuration.initEventColors();

            listview.setAdapter(new ScheduleAdapter());
        } else {
            empty.setVisibility(View.VISIBLE);
            listview.setVisibility(View.GONE);
        }

    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        StringBuilder sb = new StringBuilder();
        sb.append(Configuration.SIMPLE_DATE_DAY.format(events.get(position).getStart()));
        sb.append("\n");
        sb.append(Configuration.SIMPLE_DATE_TIME.format(events.get(position).getStart())+" - "+Configuration.SIMPLE_DATE_TIME.format(events.get(position).getEnd())+"\n");
        sb.append(events.get(position).getSubject()+" ("+events.get(position).getType()+")\n");
        sb.append(getString(R.string.lbl_location) + ": " + events.get(position).getLocation()+"\n");
        sb.append(getString(R.string.lbl_lecturer) + ": " + events.get(position).getLecturer());

        scheduleDetail.setText(sb.toString());
        alertDialog.setTitle(getString(R.string.lbl_schedule_dialog));
        alertDialog.show();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_event, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menu_hide:
                try {
                    GroupFilter f = new GroupFilter(getActivity());
                    f.addGroup(events.get(info.position).getType());
                    updateDataset();
                    populateList();
                } catch (Exception e) {
                    // Something went wrong
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private class ScheduleAdapter extends BaseAdapter {
        private Event current;

        public int getCount() {
            return events.size();
        }

        public Object getItem(int position) {
            return events.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public int getItemViewType(int position) {
            return 0;
        }

        public int getViewTypeCount() {
            return 1;
        }

        public boolean isEnabled(int position) {
            return true;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            /**
             * Set a type flag.
             */
            if(convertView == null) {
                final LayoutInflater inflater = LayoutInflater.from(getActivity());
                final int layout = R.layout.item_schedule;
                convertView = inflater.inflate(layout, parent, false);
            }

            current = (Event) getItem(position);
            TextView col1 = ((TextView) convertView.findViewById(R.id.item1));
            TextView col2 = ((TextView) convertView.findViewById(R.id.item2));
            View col3 = ((View) convertView.findViewById(R.id.item3));

            Integer bgcolor = Configuration.EVENT_COLORS.get(current.getType().toUpperCase());
            if(bgcolor == null){bgcolor = R.color.white;}

            col1.setText(Configuration.SIMPLE_DATE_TIME.format(current.getStart())+" - "+Configuration.SIMPLE_DATE_TIME.format(current.getEnd()));
            col2.setText(current.getSubject() + " ("+ current.getType()+ ")");
            col3.setBackgroundColor(getActivity().getResources().getColor(bgcolor));

            return convertView;
        }

    }
}

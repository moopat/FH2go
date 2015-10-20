package at.fhj.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import at.fhj.app.R;

/**
 * @author Markus Deutsch
 */
public class DegreeProgrammeAdapter extends ArrayAdapter<String> {

    String[] programmeAbbreviations;
    String[] programmeNames;

    public DegreeProgrammeAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_2, android.R.id.text1,
                context.getResources().getStringArray(R.array.courses));

        init();
    }

    private void init(){
        programmeAbbreviations = getContext().getResources().getStringArray(R.array.courses);
        programmeNames = getContext().getResources().getStringArray(R.array.coursesName);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        if(v != null){
            String degreeProgrammeAbbrev = getItem(position);
            TextView line2 = (TextView) v.findViewById(android.R.id.text2);
            line2.setText(getCourseNameForAbbreviation(degreeProgrammeAbbrev));
        }

        return v;
    }

    public String getCourseNameForAbbreviation(String abbreviation){
        int indexOfCourse = getIndexOfCourse(abbreviation);
        if(indexOfCourse < 0){
            return null;
        } else {
            return programmeNames[indexOfCourse];
        }
    }

    private int getIndexOfCourse(String abbreviation){
        for (int i = 0; i < programmeAbbreviations.length; i++) {
            if(programmeAbbreviations[i].equalsIgnoreCase(abbreviation)){
                return i;
            }
        }

        return -1;
    }
}
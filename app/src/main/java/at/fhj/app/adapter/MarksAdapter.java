package at.fhj.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import at.fhj.app.R;
import at.fhj.app.model.Mark;

/**
 * @author Markus Deutsch <markus.deutsch@edu.fh-joanneum.at>
 */

public class MarksAdapter extends BaseAdapter {

    private static final int TYPE_SEPARATOR = 0;
    private static final int TYPE_MARK = 1;

    private final List<Object> marks;
    private final Context context;

    public MarksAdapter(List<Object> marks, Context context) {
        this.marks = marks;
        this.context = context;
    }

    public int getCount() {
        return marks.size();
    }

    public Object getItem(int position) {
        return marks.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public int getItemViewType(int position) {
        return (marks.get(position) instanceof String) ? TYPE_SEPARATOR : TYPE_MARK;
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
        if (convertView == null) {
            final LayoutInflater inflater = LayoutInflater.from(context);
            final int layout = type == TYPE_SEPARATOR ? R.layout.item_separator : R.layout.item_mark;
            convertView = inflater.inflate(layout, parent, false);
        }

        if (type == TYPE_SEPARATOR) {
            /**
             * If it's a separator (and the array item is a String)
             * set the array text (term) as content of the separator.
             */
            ((TextView) convertView.findViewById(R.id.separator)).setText((String) marks.get(position));
        } else {
            /**
             * Populate the regular mark item layout.
             */
            Mark current = (Mark) getItem(position);
            TextView course = ((TextView) convertView.findViewById(R.id.item1));
            ImageView mark = ((ImageView) convertView.findViewById(R.id.mark));

            /**
             * Depending on the mark, there's a different drawable.
             */
            switch (current.getMark()) {
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
                default:
                    img = R.drawable.star;
            }

            course.setText(current.getCourse());
            mark.setImageResource(img);
        }


        return convertView;
    }

}

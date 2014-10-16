package at.fhj.app.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import at.fhj.app.R;

public class EnterGroupFragment extends DialogFragment {
	
	private OnGroupEnteredListener l;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
				
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_entergroup, null);
        
        final EditText taskkey = (EditText) v.findViewById(R.id.group);

        builder.setView(v)
        	.setPositiveButton(R.string.lblSave, new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int id) {
        			if(l != null){
        				l.onGroupEntered(taskkey.getText().toString());
        			}
        		}
        	})
        	.setNegativeButton(R.string.lblCancel, new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int id) {
        			// User cancelled the dialog
        		}
        	});
        
        return builder.create();
        
    }
	
	public void setOnGroupEnteredListener(OnGroupEnteredListener l){
		this.l = l;
	}


}

package pt.unl.fct.di.www.eat.ui.login;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

import pt.unl.fct.di.www.eat.R;

public class RestaurantTagsDialog extends DialogFragment {
    String[] tags;
    boolean[] selectedTags;
    ArrayList<String> sTags;
    RestaurantTagsListener listener;

    public RestaurantTagsDialog(String[] tags, boolean[] selectedTags, ArrayList<String> sTags) {
        this.selectedTags = selectedTags;
        this.tags = tags;
        this.sTags = sTags;
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (RestaurantTagsListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(List_restaurantsActivity.class.toString()
                    + " must implement RestaurantTagsListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.restaurant_dialog)
                .setMultiChoiceItems(tags, selectedTags,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    if (!sTags.contains(tags[which])) {
                                        sTags.add(tags[which]);
                                    }
                                    selectedTags[which] = true;
                                } else {
                                    if (sTags.contains(tags[which])) {
                                        sTags.remove(tags[which]);
                                    }
                                    selectedTags[which] = false;
                                }
                            }
                        })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogPositiveClick(RestaurantTagsDialog.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogNegativeClick(RestaurantTagsDialog.this);
                    }
                });

        return builder.create();
    }

    public interface RestaurantTagsListener {
        void onDialogPositiveClick(RestaurantTagsDialog dialog);
        void onDialogNegativeClick(RestaurantTagsDialog dialog);
    }
}
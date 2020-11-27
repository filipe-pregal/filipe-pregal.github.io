package pt.unl.fct.di.www.eat.ui.login;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import pt.unl.fct.di.www.eat.R;

public class RestaurantTagsDialog extends DialogFragment {
    //ArrayList<Integer> selectedTags = new ArrayList();
    String tags[];
    boolean selectedTags[];

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        getRestaurantTags();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle(R.string.restaurant_dialog)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(tags, selectedTags,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    selectedTags[which] = true;
                                } else if (selectedTags[which]) {
                                    // Else, if the item is already in the array, remove it
                                    selectedTags[which] = false;
                                }
                            }
                        })
                // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the selectedItems results somewhere
                        // or return them to the component that opened the dialog
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        return builder.create();
    }

    private void getRestaurantTags() {
        DatabaseReference restaurant_tags = FirebaseDatabase.getInstance().getReference()
                .child("select_lists").child("restaurant_tags");
        restaurant_tags.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    tags = new String[(int) dataSnapshot.getChildrenCount()];
                    selectedTags = new boolean[(int) dataSnapshot.getChildrenCount()];
                    int counter = 0;
                    for (DataSnapshot tag : dataSnapshot.getChildren()) {
                        tags[counter] = tag.getValue(String.class);
                        selectedTags[counter] = true;
                        counter++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}

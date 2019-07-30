package com.memmori.memmoriview;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

public class LocationDialog extends AppCompatDialogFragment {

    private TextView txtLocationName;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_location_info, null);

        builder.setView(view)
                .setTitle("Test")
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("View In AR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getActivity(), ARActivity.class);
                        startActivity(intent);
                    }
                });

        txtLocationName = view.findViewById(R.id.txtLocationName);
        Bundle bundle = getArguments();
        Location location = bundle.getParcelable("LocationInfo");
        txtLocationName.setText(location.getName());
        return builder.create();
    }
}

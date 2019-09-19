package com.memmori.memmoriview.Location;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.memmori.memmoriview.ARActivity;
import com.memmori.memmoriview.R;

import static com.memmori.memmoriview.Constants.STORAGE_REF;

public class LocationDialog extends AppCompatDialogFragment implements View.OnClickListener {

    private TextView txtDateTaken;
    private TextView txtPhotographer;
    private ImageView imgPicture;
    private Button btnMoreInfo;
    private ImageButton btnARView;
    private Button btnLocationDialogClose;
    private TextView txtLocationDialogTitle;

    private Location location;
    private double distance = 0;
    private double maxDistance = 100;

    private Uri t;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        location = bundle.getParcelable("LocationInfo");
        distance = bundle.getDouble("Distance");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogStyle);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_location_info, null);

        builder.setView(view);
                /*.setTitle(location.getBuildingName());
                .setPositiveButton("View In AR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(distance <= maxDistance) {
                            Intent intent = new Intent(getActivity(), ARActivity.class);
                            intent.putExtra("LocationInfo", location);
                            intent.putExtra("uri", t);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(getContext(), "Not Within Required Distance, Get Closer", Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/
        txtDateTaken = view.findViewById(R.id.txtDescription);
        txtPhotographer = view.findViewById(R.id.txtPhotographer);
        imgPicture = view.findViewById(R.id.imgPicture);
        btnMoreInfo = view.findViewById(R.id.btnMoreInfo);
        btnMoreInfo.setOnClickListener(this);
        txtLocationDialogTitle = view.findViewById(R.id.txtLocationTitle);
        txtLocationDialogTitle.setText(location.getBuildingName());
        btnARView = view.findViewById(R.id.btnViewAR);
        btnARView.setOnClickListener(this);
        btnLocationDialogClose = view.findViewById(R.id.btnLocationDialogClose);
        btnLocationDialogClose.setOnClickListener(this);

        txtDateTaken.setText(location.getDateTakenString());
        txtPhotographer.setText(location.getPhotographer());
        imgPicture.getLayoutParams().height = 500;

        StorageReference storageRef = STORAGE_REF.getReference();
        StorageReference imagesRef = storageRef.child(location.getPicture());

        imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getActivity())
                        .load(uri)
                        .into(imgPicture);
                t = uri;
            }
        });



        return builder.create();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.btnMoreInfo:
            {
                Intent intent = new Intent(getActivity(), LocationInfoActivity.class);
                intent.putExtra("LocationInfo", location);
                startActivity(intent);
                break;
            }
            case R.id.btnLocationDialogClose:
            {
                dismiss();
                break;
            }
            case R.id.btnViewAR:
            {
                //Toast.makeText(getContext(), "Test", Toast.LENGTH_SHORT).show();
                if(distance <= maxDistance) {
                    Intent intent = new Intent(getActivity(), ARActivity.class);
                    intent.putExtra("LocationInfo", location);
                    intent.putExtra("uri", t);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getContext(), "Not Within Required Distance, Get Closer", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }

    }
}

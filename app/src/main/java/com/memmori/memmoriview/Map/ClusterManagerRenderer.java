package com.memmori.memmoriview.Map;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.memmori.memmoriview.R;

import java.util.Set;

import static com.memmori.memmoriview.Constants.FIREBASE_STORAGE;

public class ClusterManagerRenderer extends DefaultClusterRenderer<ClusterMarker> {

    private final IconGenerator iconGenerator;
    public ImageView imageview;
    private final int markerWidth;
    private final int markerHeight;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Context context;

    public ClusterManagerRenderer(Context context, GoogleMap map, ClusterManager<ClusterMarker> clusterManager) {
        super(context, map, clusterManager);

        this.context = context;
        iconGenerator = new IconGenerator(context.getApplicationContext());
        imageview = new ImageView(context.getApplicationContext());
        markerWidth = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        markerHeight = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        imageview.setLayoutParams(new ViewGroup.LayoutParams(markerWidth, markerHeight));
        int padding = (int) context.getResources().getDimension(R.dimen.custom_marker_padding);
        imageview.setPadding(padding, padding, padding, padding);
        iconGenerator.setContentView(imageview);
        //iconGenerator.setColor(getColor(clusterManager.get));

        storage = FirebaseStorage.getInstance(FIREBASE_STORAGE);
        storageRef = storage.getReference();
        
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterMarker item, MarkerOptions markerOptions)
    {
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())).title(item.getTitle());
        iconGenerator.setColor(getColor(item.getLocation().getFilter().equals("User") ? 1 : 2));
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<ClusterMarker> cluster) {
        return false;
    }

    @Override
    public void onClustersChanged(Set<? extends Cluster<ClusterMarker>> clusters) {
        super.onClustersChanged(clusters);
    }

    @Override
    protected int getColor(int clusterSize) {
        return clusterSize == 1 ? Color.WHITE : Color.GREEN;
    }

    public void setUpdateMarker(ClusterMarker marker)
    {
        marker.setIconPicture(marker.getIconPicture());
    }

    @Override
    protected void onClusterRendered(Cluster<ClusterMarker> cluster, Marker marker) {
        super.onClusterRendered(cluster, marker);
    }

    @Override
    protected void onClusterItemRendered(ClusterMarker clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
        iconGenerator.setColor(getColor(clusterItem.getLocation().getFilter().equals("User") ? 1 : 2));
        StorageReference imgRef = storageRef.child(clusterItem.getIconPicture());
        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).into(imageview);
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon()));
            }
        });
    }
}
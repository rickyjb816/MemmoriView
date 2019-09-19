package com.memmori.memmoriview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.NotTrackingException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;

import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.memmori.memmoriview.Controls.JoyStick;
import com.memmori.memmoriview.Location.DemoUtils;
import com.memmori.memmoriview.Location.Location;
import com.memmori.memmoriview.Map.MapsActivity;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import uk.co.appoly.arcorelocation.LocationMarker;
import uk.co.appoly.arcorelocation.LocationScene;
import uk.co.appoly.arcorelocation.utils.ARLocationPermissionHelper;


public class ARActivity extends AppCompatActivity implements
        View.OnClickListener,
        JoyStick.JoystickListener{

    private ArFragment arFragment;
    private LocationScene locationScene;

    AnchorNode anchorNode;
    TransformableNode transformableNode;

    private ModelRenderable modelRenderable;
    private ViewRenderable LayoutRenderable;

    private boolean installRequested;
    private boolean hasFinishedLoading = false;
    private boolean isImageLocked = false;

    private Snackbar loadingMessageSnackbar = null;

    private Location location;

    private float opacity = 1;


    private JoyStick jsMovement;
    private JoyStick jsRotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        Button btnBack = findViewById(R.id.btnARBack);
        ImageButton ibtnImageLock = findViewById(R.id.ibtnImageLock);
        jsMovement = findViewById(R.id.jsMovement);
        jsRotation = findViewById(R.id.jsRotation);

        btnBack.setOnClickListener(this);
        ibtnImageLock.setOnClickListener(this);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arfragment);

        arFragment.getArSceneView().getPlaneRenderer().setShadowReceiver(false);

        location = getIntent().getParcelableExtra("LocationInfo");
        Uri link = getIntent().getParcelableExtra("uri");

        // Build a renderable from a 2D View.
        CompletableFuture<ViewRenderable> exampleLayout =
                ViewRenderable.builder()
                        .setView(this, R.layout.example_layout)
                        .build();

        // When you build a Renderable, Sceneform loads its resources in the background while returning
        // a CompletableFuture. Call thenAccept(), handle(), or check isDone() before calling get().
        CompletableFuture<ModelRenderable> model = ModelRenderable.builder()
                .setSource(this, Uri.parse("image_plane.sfb"))
                .build();

        CompletableFuture<Texture> texture = Texture.builder()
                .setSource(getApplicationContext(), link)
                .setUsage(Texture.Usage.DATA)
                .setSampler(
                        Texture.Sampler.builder()
                                .setMagFilter(Texture.Sampler.MagFilter.LINEAR)
                                .setMinFilter(Texture.Sampler.MinFilter.LINEAR_MIPMAP_LINEAR)
                                .build()
                ).build();

        CompletableFuture.allOf(
                model)
                .handle(
                        (notUsed, throwable) -> {
                            // When you build a Renderable, Sceneform loads its resources in the background while
                            // returning a CompletableFuture. Call handle(), thenAccept(), or check isDone()
                            // before calling get().

                            if (throwable != null) {
                                DemoUtils.displayError(this, "Unable to load renderables", throwable);
                                return null;
                            }

                            try {
                                LayoutRenderable = exampleLayout.get();
                                modelRenderable = model.get();
                                hasFinishedLoading = true;
                                modelRenderable.getMaterial().setTexture("baseColor", texture.get());
                            } catch (InterruptedException | ExecutionException ex) {
                                DemoUtils.displayError(this, "Unable to load renderables", ex);
                            }

                            return null;
                        });

        // Set an update listener on the Scene that will hide the loading message once a Plane is
        // detected.

        arFragment.getArSceneView().getScene().addOnUpdateListener(new Scene.OnUpdateListener() {
            @Override
            public void onUpdate(FrameTime frameTime) {
                if(anchorNode == null && arFragment.getArSceneView().getSession() != null)
                {
                    //Toast.makeText(ARActivity.this, "Should Add Model", Toast.LENGTH_SHORT).show();
                    addModelToScene(modelRenderable);
                }
            }
        });

        arFragment.getArSceneView()
                .getScene()
                .addOnUpdateListener(
                        frameTime -> {
                            if (!hasFinishedLoading) {
                                return;
                            }

                            if (locationScene == null) {
                                // If our locationScene object hasn't been setup yet, this is a good time to do it
                                // We know that here, the AR components have been initiated.
                                locationScene = new LocationScene(this, arFragment.getArSceneView());

                                // Now lets create our location markers.
                                //Needs to be removed before proper release
                                /*LocationMarker layoutLocationMarker = new LocationMarker(
                                        location.getLocation().getLongitude(),
                                        location.getLocation().getLatitude(),
                                        getExampleView()
                                );*/

                                // An example "onRender" event, called every frame
                                // Updates the layout with the markers distance
                                /*layoutLocationMarker.setRenderEvent(new LocationNodeRender() {
                                    @Override
                                    public void render(LocationNode node) {
                                        View eView = LayoutRenderable.getView();
                                        TextView distanceTextView = eView.findViewById(R.id.textView2);
                                        distanceTextView.setText(node.getDistance() + "M");
                                        rot+=10;

                                        node.setLocalRotation(new Quaternion(new Vector3(0f, rot, 0f)));

                                        Log.d("ARActivity", "render: Frame");
                                    }
                                });//*/
                                // Adding the marker
                                //locationScene.mLocationMarkers.add(layoutLocationMarker);


                                // Adding a simple location marker of a 3D model
                                /*locationScene.mLocationMarkers.add(
                                        new LocationMarker(
                                                location.getLocation().getLongitude(),
                                                location.getLocation().getLatitude(),
                                                getModel()));
                                locationScene.mLocationMarkers.get(0).setScalingMode(LocationMarker.ScalingMode.FIXED_SIZE_ON_SCREEN);
                                locationScene.setAnchorRefreshInterval(100000);*/
                            }

                            Frame frame = arFragment.getArSceneView().getArFrame();
                            if (frame == null) {
                                return;
                            }

                            if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
                                return;
                            }

                            if (locationScene != null) {
                                if(!isImageLocked) {
                                    locationScene.processFrame(frame);
                                    //addModelToScene(modelRenderable);
                                }
                            }

                            if (loadingMessageSnackbar != null) {
                                for (Plane plane : frame.getUpdatedTrackables(Plane.class)) {
                                    if (plane.getTrackingState() == TrackingState.TRACKING) {
                                        hideLoadingMessage();
                                    }
                                }
                            }
                        });

        //addModelToScene(modelRenderable);
        // Lastly request CAMERA & fine location permission which is required by ARCore-Location.
        ARLocationPermissionHelper.requestPermission(this);
    }

    /**
     * Example node of a layout
     *
     * @return
     */
    private Node getExampleView() {
        Node base = new Node();
        base.setRenderable(LayoutRenderable);
        Context c = this;
        // Add  listeners etc here
        View eView = LayoutRenderable.getView();
        eView.setOnTouchListener((v, event) -> {
            Toast.makeText(
                    c, "Location marker touched.", Toast.LENGTH_LONG)
                    .show();
            return false;
        });
        return base;
    }

    /***
     * Example Node of a 3D model
     *
     * @return
     */
    private Node getModel() {
        Node base = new Node();
        base.setRenderable(modelRenderable);
        Context c = this;
        base.setOnTapListener((v, event) -> {
            Toast.makeText(
                    c, "touched.", Toast.LENGTH_LONG)
                    .show();
        });
        return base;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (arFragment.getArSceneView().getSession() == null) {
            // If the session wasn't created yet, don't resume rendering.
            // This can happen if ARCore needs to be updated or permissions are not granted yet.
            try {
                Session session = DemoUtils.createArSession(this, installRequested);
                if (session == null) {
                    installRequested = ARLocationPermissionHelper.hasPermission(this);

                    return;
                } else {
                    //arSceneView.setupSession(session);
                    //addModelToScene(modelRenderable);
                }
            } catch (UnavailableException e) {
                DemoUtils.handleSessionException(this, e);
            }
        }

        try {
            arFragment.getArSceneView().resume();
        } catch (CameraNotAvailableException ex) {
            DemoUtils.displayError(this, "Unable to get camera", ex);
            finish();
            return;
        }

        /*if(arSceneView.getSession() != null) {
            //showLoadingMessage();
        }*/
    }

    /**
     * Make sure we call locationScene.pause();
     */
    /*@Override
    public void onPause() {
        super.onPause();

        if (locationScene != null) {
            locationScene.pause();
        }

        arSceneView.pause();
        Toast.makeText(this, "Paused", Toast.LENGTH_SHORT).show();
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        //arSceneView.destroy();
    }

    private void showLoadingMessage() {
        if (loadingMessageSnackbar != null && loadingMessageSnackbar.isShownOrQueued()) {
            return;
        }

        loadingMessageSnackbar =
                Snackbar.make(
                        ARActivity.this.findViewById(android.R.id.content),
                        R.string.plane_finding,
                        Snackbar.LENGTH_INDEFINITE);
        loadingMessageSnackbar.getView().setBackgroundColor(0xbf323232);
        loadingMessageSnackbar.show();
    }

    private void hideLoadingMessage() {
        if (loadingMessageSnackbar == null) {
            return;
        }

        loadingMessageSnackbar.dismiss();
        loadingMessageSnackbar = null;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        if (!ARLocationPermissionHelper.hasPermission(this)) {
            if (!ARLocationPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                ARLocationPermissionHelper.launchPermissionSettings(this);
            } else {
                Toast.makeText(
                        this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                        .show();
            }
            finish();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Standard Android full-screen functionality.
            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.btnARBack:{
                Intent intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.ibtnImageLock:{
                isImageLocked = !isImageLocked;
                Toast.makeText(this, String.valueOf(isImageLocked), Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    @Override
    public void onJoystickMoved(float xPercent, float yPercent, int source) {
        switch (source)
        {
            case R.id.jsMovement:
            {
                if(anchorNode == null)
                {
                    addModelToScene(modelRenderable);
                }
                else
                {
                    if(!isImageLocked)
                    {
                        transformableNode.setWorldPosition(new Vector3(transformableNode.getWorldPosition().x + xPercent / 50, 0, transformableNode.getWorldPosition().z + yPercent / 50));
                    }
                }
                break;
            }
            case R.id.jsRotation:
            {
                if(anchorNode == null)
                {
                    addModelToScene(modelRenderable);
                }
                else
                {
                    if(!isImageLocked)
                    {
                        transformableNode.setWorldRotation(new Quaternion(new Vector3(0, transformableNode.getWorldRotation().y + xPercent / 10, 0), 90f));
                    }
                    setModelOpacity(yPercent*-1);
                }
                break;
            }
        }
    }

    private void setModelOpacity(float opacity)
    {
        this.opacity += this.opacity < 0 || this.opacity > 1 ? 0 : opacity/10;
        this.opacity = this.opacity < 0 ? 0 : this.opacity;
        this.opacity = this.opacity > 1 ? 1 : this.opacity;
        modelRenderable.getMaterial().setFloat("opacity", this.opacity);
    }

    private void addModelToScene(ModelRenderable modelRenderable) {
        float[] position = {0f, 0f, -0.75f};
        float[] rotation = {0f, 0f, 0f, 0f};
        Pose pose = new Pose(position, rotation);

        if(arFragment.getArSceneView().getSession() != null && anchorNode == null) {
            //Add Try Catch
            try
            {
                Anchor anchor = arFragment.getArSceneView().getSession().createAnchor(pose);
                anchorNode = new AnchorNode(anchor);
                transformableNode = new TransformableNode(arFragment.getTransformationSystem());
                transformableNode.setParent(anchorNode);
                transformableNode.setRenderable(modelRenderable);
                arFragment.getArSceneView().getScene().addChild(anchorNode);
                transformableNode.select();
            }
            catch(NotTrackingException e)
            {
                //Toast.makeText(this, "No Trackable Surface. Please Scan Environment", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
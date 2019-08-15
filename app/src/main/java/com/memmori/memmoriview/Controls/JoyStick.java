package com.memmori.memmoriview.Controls;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.memmori.memmoriview.R;

public class JoyStick extends SurfaceView implements
        SurfaceHolder.Callback,
        View.OnTouchListener {

    private float centerX;
    private float centerY;
    private float baseRadius;
    private float hatRadius;
    public @DrawableRes int VectorDrawable = R.drawable.ic_lockicon;

    JoystickListener joystickCallback;

    public JoyStick(Context context, @DrawableRes int Drawable) {
        super(context);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        VectorDrawable = R.drawable.ic_opacitytop;
        if(context instanceof JoystickListener) {
            joystickCallback = (JoystickListener) context;
        }
    }

    public JoyStick(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if(context instanceof JoystickListener) {
            joystickCallback = (JoystickListener) context;
        }
    }

    public JoyStick(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if(context instanceof JoystickListener) {
            joystickCallback = (JoystickListener) context;
        }
    }

    public JoyStick(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        setupDimensions();
        drawJoystick(centerX,centerY);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    private void setupDimensions()
    {
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        baseRadius = Math.min(getWidth(), getHeight()) / 3;
        hatRadius = Math.min(getWidth(), getHeight()) / 5;
        if(getId() == R.id.jsMovement)
        {
            VectorDrawable = R.drawable.ic_opacitytop;
        }
        else if(getId() == R.id.jsRotation)
        {
            VectorDrawable = R.drawable.ic_rotationtop;
        }
    }

    private void drawJoystick(float newX, float newY)
    {
        if(getHolder().getSurface().isValid()) {
            Canvas myCanvas = this.getHolder().lockCanvas();
            Paint colors = new Paint();

            myCanvas.drawColor(Color.YELLOW, PorterDuff.Mode.CLEAR);

            colors.setARGB(255, 100, 100, 100);
            myCanvas.drawCircle(centerX, centerY, baseRadius, colors);

            VectorDrawableCompat mMyVectorDrawable = VectorDrawableCompat.create(getContext().getResources(), VectorDrawable, null);
            mMyVectorDrawable.setBounds(0 ,0, (int)centerX, (int)centerY);

            myCanvas.translate(newX - (getWidth()/4), newY - (getHeight()/4));
            mMyVectorDrawable.draw(myCanvas);

            getHolder().unlockCanvasAndPost(myCanvas);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(view.equals(this))
        {

            if(motionEvent.getAction() != motionEvent.ACTION_UP)
            {
                float displacement = (float) Math.sqrt(Math.pow(motionEvent.getX() - centerX, 2) + Math.pow(motionEvent.getY() - centerY, 2));
                if(displacement < baseRadius) {
                    drawJoystick(motionEvent.getX(), motionEvent.getY());
                    joystickCallback.onJoystickMoved((motionEvent.getX() - centerX)/baseRadius, ((motionEvent.getY() - centerY)/baseRadius)*-1, getId());
                }
                else
                {
                    float ratio = baseRadius / displacement;
                    float constrainedX = centerX + (motionEvent.getX() - centerX) * ratio;
                    float constrainedY = centerY + (motionEvent.getY() - centerY) * ratio;
                    drawJoystick(constrainedX, constrainedY);
                    joystickCallback.onJoystickMoved((constrainedX - centerX)/baseRadius, ((constrainedY - centerY)/baseRadius)*-1, getId());
                }
            }
            else{
                drawJoystick(centerX, centerY);
                joystickCallback.onJoystickMoved(0,0, getId() );
            }
        }
        return true;
    }

    public interface JoystickListener

    {

        void onJoystickMoved(float xPercent, float yPercent, int source);

    }
}

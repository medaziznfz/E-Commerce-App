package x7030.nefzi.tjinitawpanel.Common;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Property;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;


public class MarkerAnimation {
    public static void animationMarkerToGB(final Marker marker, LatLng finalPosition,LatLngInterpolator latLngInterpolator)
    {
        LatLng startPosition = marker.getPosition();
        long start = SystemClock.uptimeMillis();
        Handler handler = new Handler() ;
        Interpolator interpolator = new AccelerateDecelerateInterpolator();
        float durationInMs = 3000;


        handler.post(new Runnable() {
            long elapsed;
            float t,v;
            @Override
            public void run() {
                elapsed = SystemClock.uptimeMillis() - start;
                 t =elapsed/durationInMs;
                 v = interpolator.getInterpolation(t);
                 marker.setPosition(latLngInterpolator.interpolate(v,startPosition,finalPosition));

                 if (t<1)
                 {
                     handler.postDelayed(this,16);
                 }


            }
        });




    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void animationMarkerToHC(final Marker marker, LatLng finalPosition,LatLngInterpolator latLngInterpolator)
    {
        LatLng startLocation = marker.getPosition();
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.addUpdateListener(valueAnimator1 -> {
            float v = valueAnimator1.getAnimatedFraction();
            LatLng newPosition = latLngInterpolator.interpolate(v,startLocation,finalPosition);
            marker.setPosition(newPosition);

        });
        valueAnimator.setFloatValues(0,1);
        valueAnimator.setDuration(3000);
        valueAnimator.start();


    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void animationMarkerToICS(final Marker marker, LatLng finalPosition, LatLngInterpolator latLngInterpolator)
    {

        TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
            @Override
            public LatLng evaluate(float v, LatLng latLng, LatLng t1) {
                return latLngInterpolator.interpolate(v,latLng,t1);
            }
        };
        Property<Marker,LatLng> property = Property.of(Marker.class,LatLng.class,"Position");
        ObjectAnimator animator = ObjectAnimator.ofObject(marker,property,typeEvaluator,finalPosition);
        animator.setDuration(3000);
        animator.start();


    }


}

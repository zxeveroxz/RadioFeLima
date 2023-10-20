package org.radiofeperu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class AnimationUtil {

    private static ObjectAnimator rotate;
    public static void startPeriodicRotationAnimation(ImageView imageView, int rotationDuration, int interval) {
        rotate = ObjectAnimator.ofFloat(imageView, "rotationY", 0f, 360f);
        int rotation = (1000*rotationDuration);
        rotate.setDuration(rotation);
        //rotate.setRepeatCount(ObjectAnimator.INFINITE);

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                rotate.start();
                handler.postDelayed(this, interval*1000);
            }
        };

        handler.post(runnable);
    }

    public static void stoptPeriodicRotationAnimation(){
        rotate.cancel();
    }


    public static void startPeriodicAlphaAnimation(ImageView imageView, int interval) {
        final AlphaAnimation alphaAnimationOut = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimationOut.setDuration(200); // 1 segundo de desaparición
        alphaAnimationOut.setFillAfter(true);

        final AlphaAnimation alphaAnimationIn = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimationIn.setDuration(100); // 1 segundo de aparición
        alphaAnimationIn.setFillAfter(true);

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                imageView.startAnimation(alphaAnimationOut);
                handler.postDelayed(() -> imageView.startAnimation(alphaAnimationIn), 1000); // Espera 1 segundo y luego muestra la imagen
                handler.postDelayed(this, interval*1000); // Repite cada 14 segundos
            }
        };

        handler.post(runnable);
    }
}
package com.cibotechnology.animation;

import java.lang.ref.WeakReference;

import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

/**
 * emulates card flip iPhone animation
 * 
 * @see com.example.android.apis.animation.Transition3d
 * 
 */
public class CardFlipper {
    private WeakReference<CardFlipperDelegate> mDelegate;

    /**
     * This class is responsible for swapping the views and start the second
     * half of the animation.
     */
    private final class SwapViewRunnable implements Runnable {
        private final int mPosition;
        private final ViewGroup mContainer;

        public SwapViewRunnable(int position, ViewGroup container) {
            mPosition = position;
            mContainer = container;
        }

        @Override
        public void run() {
            if (-1 == mPosition) {
                getDelegate().hideFrontFace();
                getDelegate().showBackFace();
            } else {
                getDelegate().showFrontFace();
                getDelegate().hideBackFace();
            }

            final float centerX = mContainer.getWidth() / 2.0f;
            final float centerY = mContainer.getHeight() / 2.0f;

            Rotate3dAnimation rotation = new Rotate3dAnimation(90.0f * mPosition * -1, 0.0f, centerX, centerY, 310.0f, false);
            rotation.setDuration(500);
            rotation.setFillAfter(true);
            rotation.setInterpolator(new DecelerateInterpolator());

            mContainer.startAnimation(rotation);
        }
    }

    /**
     * This class listens for the end of the first half of the animation. It
     * then posts a new action that effectively swaps the views when the
     * container is rotated 90 degrees and thus invisible.
     */
    private final class ViewSwappingAnimationListener implements Animation.AnimationListener {
        private final int mPosition;
        private final ViewGroup mContainer;

        private ViewSwappingAnimationListener(int position, ViewGroup container) {
            mPosition = position;
            mContainer = container;
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mContainer.post(new SwapViewRunnable(mPosition, mContainer));
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }

    /**
     * Begins the rotation
     * 
     * @param container
     *            the parent view that holds both the front face and back face
     * @param position
     *            1 for clockwise, -1 for counter clockwise along the y-axis
     */
    private void startRotation(ViewGroup container, int position) {
        // Find the center of the container
        final float centerX = container.getWidth() / 2.0f;
        final float centerY = container.getHeight() / 2.0f;

        // Create a new 3D rotation with the supplied parameter
        // The animation listener is used to trigger the next animation
        final Rotate3dAnimation rotation = new Rotate3dAnimation(0.0f, 90.0f * position, centerX, centerY, 310.0f, true);
        rotation.setDuration(500);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new ViewSwappingAnimationListener(position, container));

        container.startAnimation(rotation);
    }

    public void flipFromLeftToRight() {
        startRotation(getDelegate().getContainer(), 1);
    }

    public void flipFromRightToLeft() {
        startRotation(getDelegate().getContainer(), -1);
    }

    public void setDelegate(CardFlipperDelegate delegate) {
        this.mDelegate = new WeakReference<CardFlipperDelegate>(delegate);
    }

    public CardFlipperDelegate getDelegate() {
        return this.mDelegate.get();
    }
}

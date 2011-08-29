package com.cibotechnology.animation;

import android.view.ViewGroup;

public interface CardFlipperDelegate {
    public ViewGroup getContainer();

    public void showFrontFace();

    public void hideFrontFace();

    public void showBackFace();

    public void hideBackFace();
}

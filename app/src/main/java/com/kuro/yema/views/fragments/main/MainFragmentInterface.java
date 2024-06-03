package com.kuro.yema.views.fragments.main;

import android.view.View;

public interface MainFragmentInterface {
    View.OnClickListener onNavHomeClicked();

    View.OnClickListener onNavSearchClicked();

    View.OnClickListener onNavWishlistClicked();

    View.OnClickListener onNavProfileClicked();
}

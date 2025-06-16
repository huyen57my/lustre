package com.example.lustre.activities;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.lustre.R;

import adapter.TabFragmentAdapter;

public class MainActivity extends AppCompatActivity {

    private FrameLayout tabHome, tabCart, tabHeart, tabProfile, tabVoucher;
    private ImageView iconHome, iconCart, iconHeart, iconProfile, iconVoucher;
    private ViewPager2 viewPager;
    private TabFragmentAdapter adapter;
    private boolean isUserSwiping = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadComponent();

        tabHome.setOnClickListener(view -> {
            viewPager.setCurrentItem(0, true);
            selectTab(tabHome);
        });
        tabCart.setOnClickListener(view -> {
            viewPager.setCurrentItem(1, true);
            selectTab(tabCart);
        });
        tabHeart.setOnClickListener(view -> {
            viewPager.setCurrentItem(2, true);
            selectTab(tabHeart);
        });
        tabVoucher.setOnClickListener(view -> {
            viewPager.setCurrentItem(3, true);
            selectTab(tabVoucher);
        });
        tabProfile.setOnClickListener(view -> {
            viewPager.setCurrentItem(4, true);
            selectTab(tabProfile);
        });

        selectTab(tabHome);
    }

    private void loadComponent() {
        tabHome = findViewById(R.id.tab_home);
        tabCart = findViewById(R.id.tab_cart);
        tabHeart = findViewById(R.id.tab_heart);
        tabProfile = findViewById(R.id.tab_profile);
        tabVoucher = findViewById(R.id.tab_voucher);

        // Get ImageViews inside each tab
        iconHome = tabHome.findViewById(R.id.icon_home);
        iconCart = tabCart.findViewById(R.id.icon_cart);
        iconHeart = tabHeart.findViewById(R.id.icon_heart);
        iconProfile = tabProfile.findViewById(R.id.icon_profile);
        iconVoucher = tabVoucher.findViewById(R.id.icon_voucher);

        // Setup ViewPager2
        viewPager = findViewById(R.id.viewPager);
        adapter = new TabFragmentAdapter(this);
        viewPager.setAdapter(adapter);

        // Setup ViewPager2 page change callback
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (isUserSwiping) {
                    updateTabSelection(position);
                    isUserSwiping = false;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    isUserSwiping = true;
                }
            }
        });
    }

    private void resetTab() {
        int[] tabIds = {R.id.tab_home, R.id.tab_cart, R.id.tab_heart, R.id.tab_voucher, R.id.tab_profile};
        int[] indicatorIds = {R.id.indicator_home, R.id.indicator_cart, R.id.indicator_heart, R.id.indicator_voucher ,R.id.indicator_profile};
        ImageView[] icons = {iconHome, iconCart, iconHeart, iconVoucher, iconProfile};

        // Get inactive color
        int inactiveColor = ContextCompat.getColor(this, android.R.color.white);

        for (int i = 0; i < tabIds.length; i++) {
            FrameLayout tab = findViewById(tabIds[i]);
            tab.setBackgroundResource(R.drawable.tab_background_inactive);

            View indicator = findViewById(indicatorIds[i]);
            // Animate indicator fade out
            if (indicator.getVisibility() == View.VISIBLE) {
                animateIndicatorFadeOut(indicator);
            }

            // Animate icon color change to inactive
            if (icons[i] != null) {
                animateIconColorChange(icons[i], inactiveColor);
            }
        }
    }

    private void selectTab(FrameLayout selectedTab) {
        resetTab();

        // Animate background change
        animateTabBackgroundChange(selectedTab);

        View indicator = null;
        ImageView selectedIcon = null;

        int selectedId = selectedTab.getId();
        if (selectedId == R.id.tab_home) {
            indicator = findViewById(R.id.indicator_home);
            selectedIcon = iconHome;
        } else if (selectedId == R.id.tab_cart) {
            indicator = findViewById(R.id.indicator_cart);
            selectedIcon = iconCart;
        } else if (selectedId == R.id.tab_heart) {
            indicator = findViewById(R.id.indicator_heart);
            selectedIcon = iconHeart;
        } else if (selectedId == R.id.tab_profile) {
            indicator = findViewById(R.id.indicator_profile);
            selectedIcon = iconProfile;
        } else if (selectedId == R.id.tab_voucher) {
            indicator = findViewById(R.id.indicator_voucher);
            selectedIcon = iconVoucher;
        }

        if (indicator != null) {
            // Animate indicator fade in
            animateIndicatorFadeIn(indicator);
        }

        // Animate selected icon color change
        if (selectedIcon != null) {
            int activeColor = ContextCompat.getColor(this, R.color.primary); // #7A5C3B
            animateIconColorChange(selectedIcon, activeColor);

            // Add scale animation for selected icon
            animateIconScale(selectedIcon);
        }
    }

    private void animateTabBackgroundChange(FrameLayout tab) {
        // Scale animation for tab selection
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(tab, "scaleX", 0.95f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(tab, "scaleY", 0.95f, 1.0f);

        scaleX.setDuration(200);
        scaleY.setDuration(200);
        scaleX.setInterpolator(new DecelerateInterpolator());
        scaleY.setInterpolator(new DecelerateInterpolator());

        scaleX.start();
        scaleY.start();

        // Set background after a slight delay
        tab.postDelayed(() -> tab.setBackgroundResource(R.drawable.tab_background_active), 50);
    }

    private void animateIndicatorFadeIn(View indicator) {
        indicator.setVisibility(View.VISIBLE);
        indicator.setAlpha(0f);
        indicator.setScaleX(0.5f);
        indicator.setScaleY(0.5f);

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(indicator, "alpha", 0f, 1f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(indicator, "scaleX", 0.5f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(indicator, "scaleY", 0.5f, 1f);

        fadeIn.setDuration(300);
        scaleX.setDuration(300);
        scaleY.setDuration(300);

        fadeIn.setInterpolator(new DecelerateInterpolator());
        scaleX.setInterpolator(new DecelerateInterpolator());
        scaleY.setInterpolator(new DecelerateInterpolator());

        fadeIn.start();
        scaleX.start();
        scaleY.start();
    }

    private void updateTabSelection(int position) {
        FrameLayout selectedTab = null;
        switch (position) {
            case 0:
                selectedTab = tabHome;
                break;
            case 1:
                selectedTab = tabCart;
                break;
            case 2:
                selectedTab = tabHeart;
                break;
            case 3 :
                selectedTab = tabVoucher;
                break;
            case 4:
                selectedTab = tabProfile;
                break;
        }
        if (selectedTab != null) {
            selectTab(selectedTab);
        }
    }

    private void animateIndicatorFadeOut(View indicator) {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(indicator, "alpha", 1f, 0f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(indicator, "scaleX", 1f, 0.5f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(indicator, "scaleY", 1f, 0.5f);

        fadeOut.setDuration(200);
        scaleX.setDuration(200);
        scaleY.setDuration(200);

        fadeOut.setInterpolator(new DecelerateInterpolator());
        scaleX.setInterpolator(new DecelerateInterpolator());
        scaleY.setInterpolator(new DecelerateInterpolator());

        fadeOut.start();
        scaleX.start();
        scaleY.start();

        fadeOut.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                indicator.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void animateIconColorChange(ImageView icon, int targetColor) {
        // Get current color
        ColorStateList currentColorList = ImageViewCompat.getImageTintList(icon);
        int currentColor = currentColorList != null ?
                currentColorList.getDefaultColor() :
                ContextCompat.getColor(this, android.R.color.white);

        // Create color animation
        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), currentColor, targetColor);
        colorAnimator.setDuration(250);
        colorAnimator.setInterpolator(new DecelerateInterpolator());

        colorAnimator.addUpdateListener(animator -> {
            int animatedColor = (int) animator.getAnimatedValue();
            ImageViewCompat.setImageTintList(icon, ColorStateList.valueOf(animatedColor));
        });

        colorAnimator.start();
    }

    private void animateIconScale(ImageView icon) {
        // Bounce effect for selected icon
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(icon, "scaleX", 1f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(icon, "scaleY", 1f, 1.2f, 1f);

        scaleX.setDuration(300);
        scaleY.setDuration(300);
        scaleX.setInterpolator(new DecelerateInterpolator());
        scaleY.setInterpolator(new DecelerateInterpolator());

        scaleX.start();
        scaleY.start();
    }
}
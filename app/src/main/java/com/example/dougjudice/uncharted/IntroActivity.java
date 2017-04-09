package com.example.dougjudice.uncharted;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by dougjudice on 4/8/17.
 */

public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        /*
        addSlide(firstFragment);
        addSlide(secondFragment);
        addSlide(thirdFragment);
        addSlide(fourthFragment);
    */
        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        //addSlide(AppIntroFragment.newInstance(title, description, image, backgroundColor));

        addSlide(AppIntroFragment.newInstance("Welcome to CrowdForce!", "In this short tutorial, you'll learn the basics of the game and how to climb the leaderboards...",
                R.drawable.gemlogo1, Color.parseColor("#08ffd6")));
        addSlide(AppIntroFragment.newInstance("How to Get Gems", "'Gems' are the resource you'll be finding in this game. They appear at nodes at any of your favorite Rutgers hangouts on a special scheduling system.",
                R.drawable.gemlogo1, Color.parseColor("#E63C35")));

        // OPTIONAL METHODS
        // Override bar/separator color.
        //setBarColor(Color.GRAY);
        //setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        //setVibrate(true);
        //setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        final Intent intent = new Intent(this, MapsActivity.class);
        String placesJson = getIntent().getStringExtra("placesJson");
        intent.putExtra("placesJson", placesJson);
        startActivity(intent);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        final Intent intent = new Intent(this, MapsActivity.class);
        String placesJson = getIntent().getStringExtra("placesJson");
        intent.putExtra("placesJson", placesJson);
        startActivity(intent);
        // Do something when users tap on Done button.
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

}



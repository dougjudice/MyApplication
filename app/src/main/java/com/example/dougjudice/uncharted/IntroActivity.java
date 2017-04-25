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
        addSlide(AppIntroFragment.newInstance("How to Get Gems", "'Gems' are the resource you'll be finding in this game. They appear randomly at stores with Nodes. Nodes show the number of other people mining, and clicking them will show how many resources are left.",
                R.drawable.tut1, Color.parseColor("#E63C35")));
        addSlide(AppIntroFragment.newInstance("Start your Group", "To make things easier, you can start a group with your Facebook friends. Just add them under the 'My Group' tab. Friends won't appear unless they also have CrowdForce installed!",
                R.drawable.user_group, Color.parseColor("#42f468")));
        addSlide(AppIntroFragment.newInstance("Craft Items", "With gems, you can craft items. Use items to get an edge on other groups competing for the same resources. Most items are more effective if you're out with your group!",
                R.drawable.mineral_scanner_rare, Color.parseColor("#f4419b")));
        addSlide(AppIntroFragment.newInstance("Climb the Leaderboard", "All groups are ranked by how many gems they have. Commonite is worth 1 point, Rareium worth 3, and Legendgem worth 5. There will be a prize for the top group at the end of our testing!",
                R.drawable.legendgem, Color.parseColor("#5041f4")));

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
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        final Intent intent = new Intent(this, MapsActivity.class);
        String placesJson = getIntent().getStringExtra("placesJson");
        intent.putExtra("placesJson", placesJson);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

}



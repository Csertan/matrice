package com.nosebite.matrice;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;
import com.github.appintro.AppIntroPageTransformerType;
import com.github.appintro.model.SliderPage;
import com.github.appintro.model.SliderPagerBuilder;



public class AppIntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SliderPage slideFirst = new SliderPagerBuilder()
                .title(getString(R.string.title_appintro_slidefirst))
                .description(getString(R.string.description_appintro_slidefirst))
                .imageDrawable(R.drawable.ic_matrice_logo_main)
                .backgroundColor(getColor(R.color.colorPrimary))
                .build();

        SliderPage slideTwo = new SliderPagerBuilder()
                .title(getString(R.string.title_appintro_slide2))
                .description(getString(R.string.descritpion_appintro_slide2))
                .imageDrawable(R.drawable.ic_intro_goal)
                .backgroundColor(getColor(R.color.colorIntro1))
                .build();

        SliderPage slideThree = new SliderPagerBuilder()
                .title(getString(R.string.title_appintro_slide34))
                .description(getString(R.string.description_appintro_slide3))
                .imageDrawable(R.drawable.ic_intro_swipe_lefttop)
                .backgroundColor(getColor(R.color.colorIntro2))
                .build();

        SliderPage slideFour = new SliderPagerBuilder()
                .title(getString(R.string.title_appintro_slide34))
                .description(getString(R.string.description_appintro_slide4))
                .imageDrawable(R.drawable.ic_intro_swipe_rightbottom)
                .backgroundColor(getColor(R.color.colorIntro3))
                .build();

        SliderPage slideFive = new SliderPagerBuilder()
                .title(getString(R.string.title_appintro_slide5))
                .description(getString(R.string.description_appintro_slide5))
                .imageDrawable(R.drawable.ic_twotone_help_240)
                .backgroundColor(getColor(R.color.colorIntro4))
                .build();

        SliderPage slideLast = new SliderPagerBuilder()
                .title(getString(R.string.title_appintro_slidelast))
                .description(getString(R.string.description_appintro_slidelast))
                .imageDrawable(R.drawable.ic_intro_sign_in)
                .backgroundColor(getColor(R.color.colorIntrolast))
                .build();

        addSlide(AppIntroFragment.newInstance(slideFirst));
        addSlide(AppIntroFragment.newInstance(slideTwo));
        addSlide(AppIntroFragment.newInstance(slideThree));
        addSlide(AppIntroFragment.newInstance(slideFour));
        addSlide(AppIntroFragment.newInstance(slideFive));
        addSlide(AppIntroFragment.newInstance(slideLast));

        setTransformer(new AppIntroPageTransformerType
                .Parallax(1.0, -1.0, 2.0));
        setColorTransitionsEnabled(true);

        setProgressIndicator();
    }

    @Override
    protected void onDonePressed(@Nullable Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finishIntro();
    }

    @Override
    protected void onSkipPressed(@Nullable Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finishIntro();
    }

    private void finishIntro() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
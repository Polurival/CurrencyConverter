package com.github.polurival.cc;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

/**
 * Created by Polurival
 * on 23.07.2016.
 *
 * <p>See <a href="https://github.com/codepath/android_guides/wiki/UI-Testing-with-Espresso">Guide</a></p>
 */
public class MainActivityInstrumentationTest {

    // Preferred JUnit 4 mechanism of specifying the activity to be launched before each test
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void validateEditFromAmountText() {
        Espresso.onView(ViewMatchers.withId(R.id.edit_from_amount))
                .perform(ViewActions.clearText())
                .perform(ViewActions.typeText("538"))
                .check(ViewAssertions.matches(ViewMatchers.withText("538")));
    }
}

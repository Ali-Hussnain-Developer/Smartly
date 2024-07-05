package com.example.smartly

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.smartly.R
import com.example.smartly.Util.SharedPreferencesHelper
import com.example.smartly.presentation.view.fragments.ResultScreen
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResultScreenFragmentTest {

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    @Before
    fun setup() {
        // Initialize SharedPreferencesHelper with test context
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        sharedPreferencesHelper = SharedPreferencesHelper(context)
        sharedPreferencesHelper.setUserCategory("Test Category")
        sharedPreferencesHelper.setUserTotalScore("5")
    }

    @Test
    fun testResultScreenUI() {
        // Launch the fragment
        val scenario: FragmentScenario<ResultScreen> = launchFragmentInContainer()

        // Wait for the fragment to be in a stable state
        Espresso.onView(ViewMatchers.withId(R.id.userCategoryTextView))
            .check(ViewAssertions.matches(ViewMatchers.withText("Test Category")))

        // Check if the total score TextView displays the correct score
        Espresso.onView(ViewMatchers.withId(R.id.totalScoreTextViewResultScreen))
            .check(ViewAssertions.matches(ViewMatchers.withText("5")))

        // Perform a click action on the "Start Quiz" button
        Espresso.onView(ViewMatchers.withId(R.id.btnStartQuiz))
            .perform(ViewActions.click())

        // Verify that the QuizSetupScreen fragment is launched by checking its container
        Espresso.onView(ViewMatchers.withId(R.id.fragment_container))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}

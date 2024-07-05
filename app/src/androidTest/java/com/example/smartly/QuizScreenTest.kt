package com.example.smartly

import android.os.Bundle
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
import com.example.smartly.presentation.view.fragments.QuizScreen
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuizScreenFragmentTest {

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    @Before
    fun setup() {
        // Initialize SharedPreferencesHelper with test context
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        sharedPreferencesHelper = SharedPreferencesHelper(context)
    }

    @Test
    fun testQuizScreenUI() {
        // Save some mock data to SharedPreferencesHelper
        sharedPreferencesHelper.setUserCategory("Test Category")
        sharedPreferencesHelper.setUserTotalScore("5")

        // Launch the fragment with arguments
        val bundle = Bundle().apply {
            putInt("categoryId", 9)  // Mock categoryId
            putString("selectedDifficulty", "easy")  // Mock selectedDifficulty
            putString("selectedQuestionType", "multiple")  // Mock selectedQuestionType
        }
        val scenario: FragmentScenario<QuizScreen> = launchFragmentInContainer(themeResId = R.style.Theme_Smartly) {
            QuizScreen().apply {
                arguments = bundle
            }
        }

        // Wait for the fragment to be in a stable state
        Espresso.onView(ViewMatchers.withId(R.id.totalQuestionTextView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Perform a click action on the submit answer button
        Espresso.onView(ViewMatchers.withId(R.id.submitAnswerButton))
            .perform(ViewActions.click())

        // Verify that the "No internet connection" toast is displayed

    }
}

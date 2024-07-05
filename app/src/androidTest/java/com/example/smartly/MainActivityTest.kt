package com.example.smartly

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.smartly.Util.SharedPreferencesHelper
import com.example.smartly.presentation.view.activities.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith



/*@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testNavigationToResultFragment() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        intent.putExtra("fragment_to_open", "ResultFragment")
        ActivityScenario.launch<MainActivity>(intent)

        onView(withId(R.id.fragment_container)).check(matches(isDisplayed()))
        // Check if ResultScreen is displayed
        onView(withId(R.id.result_screen_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun testNavigationToProfileFragment() {
        onView(withId(R.id.btn_profile)).perform(click())
        onView(withId(R.id.fragment_container)).check(matches(isDisplayed()))
        // Check if ProfileScreen is displayed
        onView(withId(R.id.profile_screen_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun testLogout() {
        onView(withId(R.id.btn_logout)).perform(click())
        onView(withId(R.id.fragment_container)).check(matches(isDisplayed()))
        // Check if SignUpScreen is displayed
        onView(withId(R.id.signup_screen_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun testLoadSignUpScreenWhenNotLoggedIn() {
        val sharedPreferencesHelper = SharedPreferencesHelper(ApplicationProvider.getApplicationContext())
        sharedPreferencesHelper.setLoggedIn(false)

        ActivityScenario.launch<MainActivity>(Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java))

        onView(withId(R.id.fragment_container)).check(matches(isDisplayed()))
        // Check if SignUpScreen is displayed
        onView(withId(R.id.signup_screen_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun testLoadResultScreenWhenLoggedIn() {
        val sharedPreferencesHelper = SharedPreferencesHelper(ApplicationProvider.getApplicationContext())
        sharedPreferencesHelper.setLoggedIn(true)

        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        intent.putExtra("fragment_to_open", "ResultFragment")
        ActivityScenario.launch<MainActivity>(intent)

        onView(withId(R.id.fragment_container)).check(matches(isDisplayed()))
        // Check if ResultScreen is displayed
        onView(withId(R.id.result_screen_layout)).check(matches(isDisplayed()))
    }
}*/

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testNavigationToResultFragment() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        intent.putExtra("fragment_to_open", "ResultFragment")
        ActivityScenario.launch<MainActivity>(intent)

        onView(withId(R.id.fragment_container)).check(matches(isDisplayed()))
        // Check if ResultScreen is displayed
        onView(withId(R.id.result_screen_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun testNavigationToProfileFragment() {
        onView(withId(R.id.btn_profile)).perform(click())
        onView(withId(R.id.fragment_container)).check(matches(isDisplayed()))
        // Check if ProfileScreen is displayed
        onView(withId(R.id.profile_screen_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun testLogout() {
        onView(withId(R.id.btn_logout)).perform(click())
        onView(withId(R.id.fragment_container)).check(matches(isDisplayed()))
        // Check if SignUpScreen is displayed
        onView(withId(R.id.signup_screen_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun testLoadSignUpScreenWhenNotLoggedIn() {
        val sharedPreferencesHelper = SharedPreferencesHelper(ApplicationProvider.getApplicationContext())
        sharedPreferencesHelper.setLoggedIn(false)

        ActivityScenario.launch<MainActivity>(Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java))

        onView(withId(R.id.fragment_container)).check(matches(isDisplayed()))
        // Check if SignUpScreen is displayed
        onView(withId(R.id.signup_screen_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun testLoadResultScreenWhenLoggedIn() {
        val sharedPreferencesHelper = SharedPreferencesHelper(ApplicationProvider.getApplicationContext())
        sharedPreferencesHelper.setLoggedIn(true)

        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        intent.putExtra("fragment_to_open", "ResultFragment")
        ActivityScenario.launch<MainActivity>(intent)

        onView(withId(R.id.fragment_container)).check(matches(isDisplayed()))
        // Check if ResultScreen is displayed
        onView(withId(R.id.result_screen_layout)).check(matches(isDisplayed()))
    }
}

package com.example.smartly

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.smartly.Util.SharedPreferencesHelper
import com.example.smartly.presentation.view.fragments.SignUpScreen
import org.junit.Test

class SignUpScreenTest {


    @Test
    fun testSignUpScreenDisplayed() {
        launchFragmentInContainer<SignUpScreen>(Bundle(), R.style.Theme_Smartly)

        onView(withId(R.id.usernameTextInputLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.submitButton)).check(matches(isDisplayed()))
    }

    @Test
    fun testUsernameEmptyShowsError() {
        launchFragmentInContainer<SignUpScreen>(Bundle(), R.style.Theme_Smartly)

        onView(withId(R.id.submitButton)).perform(click())
        onView(withId(R.id.usernameTextInputLayout)).check(matches(hasDescendant(withText("Username cannot be empty"))))
    }

    @Test
    fun testValidUsernameNavigatesToQuizSetupScreen() {
        launchFragmentInContainer<SignUpScreen>(Bundle(), R.style.Theme_Smartly)

        onView(withId(R.id.usernameEditText)).perform(typeText("TestUser"))
        onView(withId(R.id.submitButton)).perform(click())
        onView(withId(R.id.quiz_setup_screen_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun testAlreadyLoggedInNavigatesToQuizSetupScreen() {
        val sharedPreferencesHelper = SharedPreferencesHelper(ApplicationProvider.getApplicationContext())
        sharedPreferencesHelper.setLoggedIn(true)

        launchFragmentInContainer<SignUpScreen>(Bundle(), R.style.Theme_Smartly)

        onView(withId(R.id.quiz_setup_screen_layout)).check(matches(isDisplayed()))
    }
}


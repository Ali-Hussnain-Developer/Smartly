package com.example.smartly

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.smartly.presentation.view.fragments.QuizSetupScreen

import org.junit.Test


class QuizSetupScreenTest {

    @Test
    fun testQuizSetupScreenDisplayed() {
        launchFragmentInContainer<QuizSetupScreen>(Bundle(), R.style.Theme_Smartly)

        onView(withId(R.id.spinner_categories)).check(matches(isDisplayed()))
        onView(withId(R.id.spinner_difficulty)).check(matches(isDisplayed()))
        onView(withId(R.id.spinner_question_type)).check(matches(isDisplayed()))
        onView(withId(R.id.button_next)).check(matches(isDisplayed()))
    }




}
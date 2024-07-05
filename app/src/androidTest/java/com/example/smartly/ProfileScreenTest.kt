package com.example.smartly

import android.content.Context
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.smartly.R
import androidx.test.espresso.action.ViewActions.*
import com.example.smartly.Util.SharedPreferencesHelper
import com.example.smartly.presentation.view.fragments.ProfileScreen
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/*
@RunWith(AndroidJUnit4::class)
class ProfileScreenFragmentTest {

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    @Before
    fun setup() {
        // Initialize SharedPreferencesHelper with test context
        sharedPreferencesHelper = SharedPreferencesHelper(ApplicationProvider.getApplicationContext())
        sharedPreferencesHelper.setUserName("Test User")
    }

    @Test
    fun testProfileScreenUI() {
        // Launch the fragment
        val scenario: FragmentScenario<ProfileScreen> = launchFragmentInContainer()

        // Wait for the fragment to be in a stable state
        Espresso.onView(ViewMatchers.withId(R.id.txt_user_name))
            .check(ViewAssertions.matches(ViewMatchers.withText("Test User")))

        // Check if EditText for thought posting is visible
        Espresso.onView(ViewMatchers.withId(R.id.editTextThought))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Perform a click action on the post button
        Espresso.onView(ViewMatchers.withId(R.id.buttonPost))
            .perform(ViewActions.click())

    }
}
*/



@RunWith(AndroidJUnit4::class)
class ProfileScreenTest {

    @Before
    fun setUp() {
        // Setup any required state here
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sharedPreferencesHelper = SharedPreferencesHelper(context)
        sharedPreferencesHelper.setUserName("Test User")
        sharedPreferencesHelper.setUserProfilePic("android.resource://com.example.smartly/drawable/person_avatar")

        // Launch the fragment in a container
        launchFragmentInContainer<ProfileScreen>()
    }

    @Test
    fun testViewsAreDisplayed() {
        onView(withId(R.id.user_profile_pic)).check(matches(isDisplayed()))
        onView(withId(R.id.txt_user_name)).check(matches(isDisplayed()))
        onView(withId(R.id.editTextThought)).check(matches(isDisplayed()))
        onView(withId(R.id.buttonPost)).check(matches(isDisplayed()))
        onView(withId(R.id.recycler_view_post)).check(matches(isDisplayed()))
    }

    @Test
    fun testUserNameIsDisplayed() {
        onView(withId(R.id.txt_user_name)).check(matches(withText("Test User")))
    }

    @Test
    fun testPostButtonFunctionality() {
        // Enter text in the EditText
        onView(withId(R.id.editTextThought)).perform(typeText("This is a test thought"))
        closeSoftKeyboard()

        // Click the Post button
        onView(withId(R.id.buttonPost)).perform(click())

        // Check if the EditText is cleared
        onView(withId(R.id.editTextThought)).check(matches(withText("")))
    }

    @Test
    fun testRecyclerViewIsUpdated() {
        // Post a thought
        onView(withId(R.id.editTextThought)).perform(typeText("This is a test thought"))
        closeSoftKeyboard()
        onView(withId(R.id.buttonPost)).perform(click())

        // Check if RecyclerView is updated
        onView(withId(R.id.recycler_view_post)).check(matches(hasDescendant(withText("This is a test thought"))))
    }


}

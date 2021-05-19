package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    private lateinit var dataSource: FakeDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private lateinit var context: Application
    private lateinit var reminder: ReminderDataItem

    //TODO: provide testing to the SaveReminderView and its live data objects
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    private var mainCoroutineScopeRule = MainCoroutineRule()

    @Before
    fun init() {
        context = ApplicationProvider.getApplicationContext()
        dataSource = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(context, dataSource)

        reminder = ReminderDataItem(
            title = "fake title",
            description = "nice fake description",
            location = "Nice fake location",
            latitude = 120.00,
            longitude = 120.00,
            id = "0"
        )
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun saveReminderViewModel_validateAndSaveReminder(){

        val check = saveReminderViewModel.validateEnteredData(reminder)

        MatcherAssert.assertThat(check, CoreMatchers.`is`(true))
    }

    @Test
    fun saveReminderViewModel_validateAndSaveReminderWrongData(){

        val check = saveReminderViewModel.validateEnteredData(ReminderDataItem(null, null, null, null, null, "0"))

        MatcherAssert.assertThat(check, CoreMatchers.`is`(false))
    }

    @Test
    fun saveReminderViewModel_saveReminder() = runBlockingTest {

        saveReminderViewModel.saveReminder(reminder)

        val showToastValue = saveReminderViewModel.showToast.getOrAwaitValue()
        Assert.assertEquals(showToastValue, context.resources.getString(R.string.reminder_saved))
    }

    @Test
    fun validateEnteredData_emptyLocation() {

        reminder.location = ""
        // When validate the reminder and snackbar
        val value = saveReminderViewModel.validateEnteredData(reminder)
        Assert.assertThat(value, Matchers.`is`(false))
    }

    @Test
    fun validateEnteredData_nullLocation() {

        reminder.location = null
        val value = saveReminderViewModel.validateEnteredData(reminder)
        Assert.assertThat(value, Matchers.`is`(false))
    }

    @Test
    fun saveReminder_loading() {
        saveReminderViewModel.saveReminder(reminder)

        mainCoroutineScopeRule.resumeDispatcher()

        val loading = saveReminderViewModel.showLoading.getOrAwaitValue()
        Assert.assertThat(loading, Matchers.`is`(false))
    }


}
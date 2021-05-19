package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.O_MR1])
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    private lateinit var dataSource: FakeDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private lateinit var context: Application

    //TODO: provide testing to the SaveReminderView and its live data objects
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    private var mainCoroutineScopeRule = MainCoroutineRule()

    @Before
    fun init(){
        context = ApplicationProvider.getApplicationContext()
        dataSource = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(context, dataSource)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun saveReminderViewModel_validateAndSaveReminder(){

//        saveReminderViewModel.validateAndSaveReminder()
    }

    @Test
    fun saveReminderViewModel_saveReminder() = runBlockingTest {
        val reminder = ReminderDataItem(
            title = "fake title",
            description = "nice fake description",
            location = "Nice fake location",
            latitude = 120.00,
            longitude = 120.00
        )

        saveReminderViewModel.saveReminder(reminder)

        val showToastValue = saveReminderViewModel.showToast.getOrAwaitValue()
        Assert.assertEquals(showToastValue, context.resources.getString(R.string.reminder_saved))
    }

}
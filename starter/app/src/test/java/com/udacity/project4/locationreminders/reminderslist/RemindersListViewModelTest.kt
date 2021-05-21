package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects

    private lateinit var dataSource: FakeDataSource
    private lateinit var remindersListViewModel: RemindersListViewModel
    private lateinit var context: Application
    private lateinit var reminders: MutableList<ReminderDTO>
    private lateinit var remindersEmpty: MutableList<ReminderDTO>

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun init() {
        context = ApplicationProvider.getApplicationContext()

        reminders = mutableListOf<ReminderDTO>(
            ReminderDTO(
                title = "title", description = "Description",
                location = "Location",
                latitude = 12.0, longitude = 12.0, id = "0"
            ),
            ReminderDTO(
                title = "title", description = "Description",
                location = "Location",
                latitude = 12.0, longitude = 12.0, id = "1"
            ),
            ReminderDTO(
                title = "title", description = "Description",
                location = "Location",
                latitude = 12.0, longitude = 12.0, id = "2"
            )

        )

        remindersEmpty = mutableListOf<ReminderDTO>()
        dataSource = FakeDataSource(reminders)
        remindersListViewModel = RemindersListViewModel(context, dataSource)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun saveRemindersViewModel_notEmptyList() {
        remindersListViewModel.loadReminders()
        val remindersList = remindersListViewModel.remindersList.getOrAwaitValue()
        Assert.assertThat(remindersList.isEmpty(), Matchers.`is`(false))
    }

    @Test
    fun saveRemindersViewModel_emptyList() {
        remindersListViewModel.loadReminders()
        val remindersList = remindersListViewModel.remindersList.getOrAwaitValue()
        Assert.assertThat(!remindersList.isEmpty(), Matchers.`is`(true))

    }

    @Test
    fun saveRemindersViewModel_invalidateShowNoData(){
        remindersListViewModel.loadReminders()
        val showNoData = remindersListViewModel.showNoData.getOrAwaitValue()
        Assert.assertThat(showNoData, Matchers.`is`(false))
    }

    @Test
    fun testShowLoadingReturnsFalseWhenResumed() = runBlockingTest {

        mainCoroutineRule.pauseDispatcher()

        remindersListViewModel.loadReminders()
        val showLoadingValue = remindersListViewModel.showLoading.getOrAwaitValue()

        Assert.assertThat(showLoadingValue, Matchers.`is`(true))

        mainCoroutineRule.resumeDispatcher()

        val showLoadingValueAgain = remindersListViewModel.showLoading.getOrAwaitValue()
        Assert.assertThat(showLoadingValueAgain, Matchers.`is`(false))
    }

    @Test
    fun shouldReturnError() = runBlockingTest {
        remindersListViewModel.loadReminders()

        val list = remindersListViewModel.remindersList.getOrAwaitValue()
        Assert.assertThat(list.isEmpty(), Matchers.`is`(false))

        dataSource.deleteAllReminders()
        remindersListViewModel.loadReminders()
        val noRemindersMessage = remindersListViewModel.showSnackBar.getOrAwaitValue()

        Assert.assertThat(noRemindersMessage, Matchers.`is`("No reminders found"))

    }

    @Test
    fun expectError() = runBlockingTest {
        dataSource.setReturnError(true)
        remindersListViewModel.loadReminders()
        MatcherAssert.assertThat(
            remindersListViewModel.showNoData.getOrAwaitValue(),
            CoreMatchers.`is`(true)
        )
    }

    @Test
    fun expectSuccess() = runBlockingTest {
        dataSource.setReturnError(false)
        remindersListViewModel.loadReminders()
        MatcherAssert.assertThat(
            remindersListViewModel.showNoData.getOrAwaitValue(),
            CoreMatchers.`is`(false)
        )
    }
}
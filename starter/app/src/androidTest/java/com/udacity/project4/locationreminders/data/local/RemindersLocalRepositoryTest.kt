package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

//    TODO: Add testing implementation to the RemindersLocalRepository.kt

    private lateinit var database: RemindersDatabase
    private lateinit var localDataSource: RemindersLocalRepository
    private lateinit var reminder: ReminderDTO
    private lateinit var reminders: MutableList<ReminderDTO>

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {

        reminder = ReminderDTO(
            title = "fake title",
            description = "nice fake description",
            location = "Nice fake location",
            latitude = 120.00,
            longitude = 120.00,
            id = "0"
        )

        reminders = mutableListOf(
            reminder
        )

        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()

        localDataSource =
            RemindersLocalRepository(
                database.reminderDao(),
                Dispatchers.Main
            )
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun getReminderById() = runBlocking {

        localDataSource.saveReminder(reminder)

        val retrievedReminder = localDataSource.getReminder(reminder.id)

        assertThat(retrievedReminder, CoreMatchers.notNullValue())
        retrievedReminder as Result.Success
        assertThat(retrievedReminder.data.id, `is`(reminder.id))

    }

    @Test
    fun getNonExistentReminder() = runBlocking {
        localDataSource.saveReminder(reminder)
        val result = localDataSource.getReminder("-1")

        result as Result.Error
        assertThat(result.message, `is`("Reminder not found!"))
    }

    @Test
    fun deleteAllReminders() = runBlocking {

        localDataSource.saveReminder(reminder)

        localDataSource.deleteAllReminders()

        val allData = localDataSource.getReminders()
        allData as Result.Success
        assertThat(allData.data.size, `is`(0))
    }

    @Test
    fun getAllReminders() = runBlocking {

        localDataSource.saveReminder(reminder)

        val allData = localDataSource.getReminders()
        allData as Result.Success
        assertThat(allData.data.size, `is`(reminders.size))
    }

}
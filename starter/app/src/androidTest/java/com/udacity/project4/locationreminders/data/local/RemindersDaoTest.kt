package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

//    TODO: Add testing implementation to the RemindersDao.kt

    private lateinit var database: RemindersDatabase
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
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun getReminderById() = runBlockingTest {

        database.reminderDao().saveReminder(reminder)

        val retrievedReminder = database.reminderDao().getReminderById(reminder.id)

        assertThat(retrievedReminder, notNullValue())
        assertThat(retrievedReminder?.id, `is`(reminder.id))
    }

    @Test
    fun deleteAllReminders() = runBlockingTest {

        database.reminderDao().saveReminder(reminder)

        database.reminderDao().deleteAllReminders()

        val allData = database.reminderDao().getReminders()

        assertThat(allData.size, `is`(0))
    }

    @Test
    fun getAllReminders() = runBlockingTest {

        database.reminderDao().saveReminder(reminder)

        val allData = database.reminderDao().getReminders()

        assertThat(allData.size, `is`(reminders.size))
    }

}
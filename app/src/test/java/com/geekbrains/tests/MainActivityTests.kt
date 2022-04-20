package com.geekbrains.tests

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.geekbrains.tests.view.details.DetailsActivity.Companion.TOTAL_COUNT_EXTRA
import com.geekbrains.tests.view.search.MainActivity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.annotation.Config


@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.R])
class MainActivityTests {

    private lateinit var scenario: ActivityScenario<MainActivity>
    private lateinit var context: Context


    @Before
    fun setup() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        context = ApplicationProvider.getApplicationContext()
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun activity_AssertNotNull() {
        scenario.onActivity { mainActivity ->
            assertNotNull(mainActivity)
        }
    }

    @Test
    fun activity_Search_EditText_NotNull() {
        scenario.onActivity {
            val detailsActivityBtn = it.findViewById<ProgressBar>(R.id.progressBar)
            assertNotNull(detailsActivityBtn)
        }
    }

    @Test
    fun activityToDetailsActivityButton_HasText() {
        scenario.onActivity {
            val detailsActivityBtn = it.findViewById<Button>(R.id.toDetailsActivityButton)
            assertEquals("to details", detailsActivityBtn.text)
        }
    }

    @Test
    fun activitySearchEditText_HasText() {
        scenario.onActivity {
            val searchEditText = it.findViewById<EditText>(R.id.searchEditText)
            assertEquals("Enter keyword e.g. android", searchEditText.hint)
        }
    }

    @Test
    fun activityProgressBar_IsVisible() {
        scenario.onActivity {
            val progressBar = it.findViewById<ProgressBar>(R.id.progressBar)
            assertEquals(View.GONE, progressBar.visibility)
        }
    }

    @Test
    fun activityElements_AreVisible() {
        scenario.onActivity {
            val progressBar = it.findViewById<ProgressBar>(R.id.progressBar)
            assertEquals(View.GONE, progressBar.visibility)
            val searchEditText = it.findViewById<EditText>(R.id.searchEditText)
            assertEquals(View.VISIBLE, searchEditText.visibility)
            val toDetailsActivityButton = it.findViewById<Button>(R.id.toDetailsActivityButton)
            assertEquals(View.VISIBLE, toDetailsActivityButton.visibility)
            val recyclerView = it.findViewById<RecyclerView>(R.id.recyclerView)
            assertEquals(View.VISIBLE, recyclerView.visibility)
        }
    }

    @Test
    fun activityDisplayLoading_Test() {
        scenario.onActivity {
            it.displayLoading(true)
            val progressBar = it.findViewById<ProgressBar>(R.id.progressBar)
            assertEquals(View.VISIBLE, progressBar.visibility)
            it.displayLoading(false)
            assertEquals(View.GONE, progressBar.visibility)
        }
    }

    @Test
    fun activityToDetailsActivityButton_IsWorking() {
        scenario.onActivity {
            val toDetailsActivityButton = it.findViewById<Button>(R.id.toDetailsActivityButton)
            assertEquals(toDetailsActivityButton.performClick(), true)
        }
    }


    @Test
    fun activityCreateIntent_NotNull() {
        scenario.onActivity {
            val intent: Intent = it.intent.putExtra(TOTAL_COUNT_EXTRA, 0)
            assertNotNull(intent)
        }
    }

    @Test
    fun activityCreateIntent_HasExtras() {
        scenario.onActivity {
            val intent: Intent = it.intent.putExtra(TOTAL_COUNT_EXTRA, 0)
            val bundle: Bundle? = intent.extras
            assertNotNull(bundle)
        }
    }

    @Test
    fun activityCreateIntent_HasCount() {
        scenario.onActivity {
            val count: Int = 4
            val intent: Intent = it.intent.putExtra(TOTAL_COUNT_EXTRA, count)
            val bundle: Bundle? = intent.extras
            assertEquals(count, bundle?.getInt(TOTAL_COUNT_EXTRA, 0))
        }
    }

    @After
    fun close() {
        scenario.close()
    }
}
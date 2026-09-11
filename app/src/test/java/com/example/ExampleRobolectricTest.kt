package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.remote.DistractorGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Quiz App", appName)
  }

  @Test
  fun `test fallback distractor generator generates 3 distinct options`() {
    val options = DistractorGenerator.generateSmartFallbackDistractors(
        question = "What is the capital of France?",
        correctAnswer = "Paris",
        category = "History & Geography"
    )
    assertEquals(3, options.size)
    assertTrue("Options should not contain correct answer", options.none { it.equals("Paris", ignoreCase = true) })
    assertEquals("Options should be distinct", 3, options.toSet().size)
  }

  @Test
  fun `test numeric distractor generator generates valid numeric offsets`() {
    val options = DistractorGenerator.generateSmartFallbackDistractors(
        question = "In what year was Apollo 11 moon landing?",
        correctAnswer = "1969",
        category = "History & Geography"
    )
    assertEquals(3, options.size)
    assertTrue("Should generate distinct numeric options", options.none { it == "1969" })
  }
}


package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.SyllabusAssetLoader
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
  fun `app name and bundled 2027 syllabus with important chapters load correctly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("JEE Tracker", appName)

    val syllabus = SyllabusAssetLoader(context).loadSyllabus()
    assertEquals(2027, syllabus.syllabusYear)
    assertEquals(3, syllabus.subjects.size)

    val physics = syllabus.subjects.first { it.id == "physics" }.branches.flatMap { it.chapters }
    val chemistry = syllabus.subjects.first { it.id == "chemistry" }.branches.flatMap { it.chapters }
    val maths = syllabus.subjects.first { it.id == "maths" }.branches.flatMap { it.chapters }

    assertEquals(20, physics.size)
    assertEquals(20, chemistry.size)
    assertEquals(14, maths.size)

    // Verify high-weightage important chapters from screenshots
    assertEquals(15, physics.count { it.isImportant })
    assertEquals(17, chemistry.count { it.isImportant })
    assertEquals(12, maths.count { it.isImportant })

    assertTrue(physics.first { it.id == "phy_16" }.weightageTag == "3 Q/shift")
    assertTrue(chemistry.first { it.id == "chem_12" }.isImportant)
    assertTrue(maths.first { it.id == "math_10" }.weightageTag == "3-4 Q/shift")
  }
}

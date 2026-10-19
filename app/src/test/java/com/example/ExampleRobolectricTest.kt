package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.diagnostic.DeviceHardwareReader
import com.example.diagnostic.DiagnosticEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Niazi Doctor", appName)
  }

  @Test
  fun `diagnostic engine returns valid health assessment`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val engine = DiagnosticEngine(context)
    val health = engine.runFullCheckup()

    assertNotNull(health)
    assertTrue("Health score must be between 0 and 100", health.healthScore in 0..100)
    assertNotNull(health.statusBadgeUrdu)
    assertNotNull(health.battery)
    assertNotNull(health.performance)
  }

  @Test
  fun `hardware reader reads device identifiers`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val reader = DeviceHardwareReader(context)
    val (brand, model, manufacturer) = reader.getDeviceIdentifiers()

    assertNotNull(brand)
    assertNotNull(model)
    assertNotNull(manufacturer)
  }
}

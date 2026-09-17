package com.orbit.blocker.domain.block

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.util.concurrent.TimeUnit

class DurationFormatterTest {

    @Test
    fun formatsExpired() {
        assertThat(DurationFormatter.format(0)).isEqualTo("expired")
        assertThat(DurationFormatter.format(-5)).isEqualTo("expired")
    }

    @Test
    fun formatsSecondsMinutesHoursDays() {
        assertThat(DurationFormatter.format(TimeUnit.SECONDS.toMillis(30))).isEqualTo("30s")
        assertThat(DurationFormatter.format(TimeUnit.MINUTES.toMillis(45))).isEqualTo("45m")
        assertThat(
            DurationFormatter.format(TimeUnit.HOURS.toMillis(2) + TimeUnit.MINUTES.toMillis(15))
        ).isEqualTo("2h 15m")
        assertThat(
            DurationFormatter.format(TimeUnit.DAYS.toMillis(3) + TimeUnit.HOURS.toMillis(4))
        ).isEqualTo("3d 4h")
    }

    @Test
    fun omitsZeroLowerUnits() {
        assertThat(DurationFormatter.format(TimeUnit.HOURS.toMillis(2))).isEqualTo("2h")
        assertThat(DurationFormatter.format(TimeUnit.DAYS.toMillis(1))).isEqualTo("1d")
    }
}

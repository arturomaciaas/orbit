package com.orbit.blocker.domain.notification

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PeekContentBuilderTest {

    @Test
    fun usesTitleAsSenderAndTextAsPreview() {
        val content = PeekContentBuilder.build(title = "Alice", text = "Are we still on for lunch?")
        assertThat(content.sender).isEqualTo("Alice")
        assertThat(content.preview).isEqualTo("Are we still on for lunch?")
    }

    @Test
    fun fallbackSenderWhenTitleBlank() {
        val content = PeekContentBuilder.build(title = "  ", text = "hi")
        assertThat(content.sender).isEqualTo("New message")
    }

    @Test
    fun collapsesWhitespaceInPreview() {
        val content = PeekContentBuilder.build(title = "Bob", text = "line1\n\n   line2\t tabbed")
        assertThat(content.preview).isEqualTo("line1 line2 tabbed")
    }

    @Test
    fun truncatesLongPreviewWithEllipsis() {
        val long = "x".repeat(200)
        val content = PeekContentBuilder.build(title = "Bob", text = long)
        assertThat(content.preview.length).isEqualTo(PeekContentBuilder.MAX_PREVIEW_CHARS)
        assertThat(content.preview.last()).isEqualTo('\u2026')
    }

    @Test
    fun handlesNullTextAsEmptyPreview() {
        val content = PeekContentBuilder.build(title = "Bob", text = null)
        assertThat(content.preview).isEmpty()
    }
}

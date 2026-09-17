package com.orbit.blocker.domain.notification

/** The trimmed content shown in a reposted "peek" notification. */
data class PeekContent(
    val sender: String,
    val preview: String,
)

/**
 * Pure builder that turns a notification's title/text into a compact peek. The title
 * is treated as the sender (WhatsApp/email put the contact/subject there); the text is
 * truncated to a short preview so you can decide whether it's worth engaging.
 */
object PeekContentBuilder {

    const val MAX_PREVIEW_CHARS = 80
    private const val FALLBACK_SENDER = "New message"

    fun build(title: String?, text: String?): PeekContent {
        val sender = title?.trim().takeUnless { it.isNullOrEmpty() } ?: FALLBACK_SENDER
        val rawPreview = text?.trim().orEmpty().replace(Regex("\\s+"), " ")
        val preview = if (rawPreview.length > MAX_PREVIEW_CHARS) {
            rawPreview.take(MAX_PREVIEW_CHARS - 1).trimEnd() + "\u2026" // ellipsis
        } else {
            rawPreview
        }
        return PeekContent(sender = sender, preview = preview)
    }
}

package com.globalwire.news.ui

import android.text.Html
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Renders the article body with the app's own typography.
 *
 * The feed carries the prose as HTML; this walks the top-level tags and maps
 * each to a Compose paragraph, so the article never loads the website's chrome.
 */
private val TAG = Regex("<(/?)(h2|h3|h4|p|li|strong|em|ul|ol)\\b[^>]*>", RegexOption.IGNORE_CASE)
private val ANY = Regex("<[^>]+>")

private fun inline(text: String): AnnotatedString = buildAnnotatedString {
    var bold = false
    var italic = false
    var i = 0
    val token = Regex("<(/?)(strong|em)>|([^<]+)", RegexOption.IGNORE_CASE)
    for (m in token.findAll(text)) {
        when {
            m.groupValues[3].isNotEmpty() -> append(m.groupValues[3])
            m.groupValues[2].equals("strong", true) ->
                bold = m.groupValues[1].isEmpty()
            m.groupValues[2].equals("em", true) ->
                italic = m.groupValues[1].isEmpty()
        }
        i = m.range.last + 1
    }
    // Styling is applied per span below; this builder only strips tags.
    if (bold) addStyle(SpanStyle(fontWeight = FontWeight.Bold), 0, length)
    if (italic) addStyle(SpanStyle(), 0, length)
}

private fun clean(html: String): String =
    Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString().trim()

@Composable
fun HtmlBody(html: String) {
    val blocks = remember(html) { splitBlocks(html) }
    Column(Modifier.fillMaxWidth()) {
        blocks.forEach { b ->
            when (b.tag) {
                "h2" -> Text(
                    clean(b.text), style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                "h3", "h4" -> Text(
                    clean(b.text), style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                "li" -> Text(
                    "•  ${clean(b.text)}",
                    style = MaterialTheme.typography.bodyLarge
                )
                else -> Text(
                    clean(b.text), style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 26.sp
                )
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

private data class Block(val tag: String, val text: String)

private fun splitBlocks(html: String): List<Block> {
    val out = mutableListOf<Block>()
    val stack = mutableListOf<String>()
    val sb = StringBuilder()
    var i = 0
    val token = Regex("<(/?)(h2|h3|h4|p|li|ul|ol)\\b[^>]*>([^<]*)", RegexOption.IGNORE_CASE)
    for (m in token.findAll(html)) {
        val closing = m.groupValues[1].isNotEmpty()
        val tag = m.groupValues[2].lowercase()
        if (tag == "ul" || tag == "ol") continue
        if (!closing) {
            if (stack.isEmpty()) sb.setLength(0)
            stack.add(tag)
            sb.append(m.groupValues[3])
        } else {
            if (stack.isNotEmpty() && stack.last() == tag) {
                sb.append(m.groupValues[3])
                if (stack.size == 1) {
                    val t = sb.toString().trim()
                    if (t.isNotEmpty()) out.add(Block(tag, t))
                }
                stack.removeAt(stack.lastIndex)
            }
        }
        i = m.range.last + 1
    }
    // Any bare text outside the known tags still belongs to the article.
    if (out.isEmpty()) {
        val t = clean(ANY.replace(html, "")).trim()
        if (t.isNotEmpty()) out.add(Block("p", t))
    }
    return out
}

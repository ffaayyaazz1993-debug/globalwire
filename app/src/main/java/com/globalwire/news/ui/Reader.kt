package com.globalwire.news.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(article: Article, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(article.h1, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(article.ctype.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary)
            androidx.compose.foundation.layout.Spacer(
                Modifier.padding(bottom = 4.dp))
            Text(article.h1, style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold)
            if (article.deck.isNotBlank()) {
                androidx.compose.foundation.layout.Spacer(
                    Modifier.padding(bottom = 6.dp))
                Text(article.deck,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            androidx.compose.foundation.layout.Spacer(
                Modifier.padding(bottom = 8.dp))
            Text("By Y-Squre Research · ${article.published.take(10)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            androidx.compose.foundation.layout.Spacer(
                Modifier.padding(bottom = 12.dp))
            if (article.image.isNotBlank()) {
                AsyncImage(
                    model = article.image,
                    contentDescription = article.image_caption.ifBlank { null },
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().aspectRatio(1.5f)
                )
                if (article.image_caption.isNotBlank()) {
                    androidx.compose.foundation.layout.Spacer(
                        Modifier.padding(bottom = 4.dp))
                    Text(article.image_caption,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                androidx.compose.foundation.layout.Spacer(
                    Modifier.padding(bottom = 12.dp))
            }
            if (article.keypoints.isNotEmpty()) {
                Text("Key points",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold)
                androidx.compose.foundation.layout.Spacer(
                    Modifier.padding(bottom = 6.dp))
                article.keypoints.forEach { kp ->
                    Text("•  $kp",
                        style = MaterialTheme.typography.bodyLarge)
                    androidx.compose.foundation.layout.Spacer(
                        Modifier.padding(bottom = 6.dp))
                }
            }
            HtmlBody(article.body)
            if (article.sources.isNotEmpty()) {
                androidx.compose.foundation.layout.Spacer(
                    Modifier.padding(bottom = 8.dp))
                Text("Sources",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold)
                androidx.compose.foundation.layout.Spacer(
                    Modifier.padding(bottom = 6.dp))
                article.sources.forEach { src ->
                    Text("•  $src",
                        style = MaterialTheme.typography.bodyLarge)
                    androidx.compose.foundation.layout.Spacer(
                        Modifier.padding(bottom = 6.dp))
                }
            }
        }
    }
}


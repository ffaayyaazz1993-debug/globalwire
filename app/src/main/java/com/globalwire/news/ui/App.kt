package com.globalwire.news.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalwireApp() {
    var feed by remember { mutableStateOf<Feed?>(null) }
    var tax by remember { mutableStateOf<Taxonomy?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var group by remember { mutableStateOf<MenuEntry?>(null) }
    var open by remember { mutableStateOf<Article?>(null) }

    LaunchedEffect(Unit) {
        try {
            val f = Remote.feed()
            val t = Remote.taxonomy()
            feed = f
            tax = t
        } catch (e: Exception) {
            error = e.message ?: "load failed"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Globalwire", fontWeight = FontWeight.Bold) },
                actions = {
                    Icon(Icons.Filled.Search, contentDescription = "Search",
                        modifier = Modifier.padding(end = 16.dp))
                }
            )
        }
    ) { padding ->
        val article = open
        if (article != null) {
            ReaderScreen(article) { open = null }
            return@Scaffold
        }
        Column(Modifier.padding(padding).fillMaxSize()) {
            GroupFilter(tax, group) { group = it }
            when {
                error != null -> Text(
                    "Could not load: $error",
                    Modifier.padding(24.dp),
                    color = MaterialTheme.colorScheme.error
                )
                feed == null -> Box(Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                else -> ArticleList(feed!!, group) { open = it }
            }
        }
    }
}

@Composable
fun GroupFilter(tax: Taxonomy?, selected: MenuEntry?, onPick: (MenuEntry?) -> Unit) {
    val groups = tax?.menu?.get("global").orEmpty()
    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selected == null,
            onClick = { onPick(null) },
            label = { Text("All") }
        )
        groups.forEach { g ->
            FilterChip(
                selected = selected?.slug == g.slug,
                onClick = { onPick(g) },
                label = { Text(g.name) }
            )
        }
    }
}

@Composable
fun ArticleList(feed: Feed, group: MenuEntry?, onOpen: (Article) -> Unit) {
    val list = if (group == null) feed.articles
    else feed.articles.filter { it.region == group.slug }
    if (list.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No reports published yet for this group.")
        }
        return
    }
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(list, key = { it.url }) { a -> ArticleCard(a, onOpen) }
    }
}

@Composable
fun ArticleCard(a: Article, onOpen: (Article) -> Unit) {
    Card(Modifier.fillMaxWidth().clickable { onOpen(a) }) {
        Column(Modifier.padding(16.dp)) {
            Text(
                a.ctype.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Text(a.h1, style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Text(a.deck, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Text(a.published.take(10),
                style = MaterialTheme.typography.labelSmall)
        }
    }
}

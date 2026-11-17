package com.globalwire.news.ui

import kotlinx.serialization.Serializable

@Serializable
data class Article(
    val url: String = "",
    val h1: String = "",
    val deck: String = "",
    val desc: String = "",
    val published: String = "",
    val ctype: String = "",
    val section: String = "",
    val region: String = "",
    val subregion: String = "",
    val body: String = ""
)

@Serializable
data class Feed(val articles: List<Article> = emptyList())

@Serializable
data class Country(val name: String = "", val anchor: String = "")

@Serializable
data class Section(val slug: String = "", val name: String = "", val path: String = "")

@Serializable
data class RegionSection(
    val slug: String = "",
    val name: String = "",
    val path: String = "",
    val countries: List<Country> = emptyList()
)

@Serializable
data class Region(
    val slug: String = "",
    val name: String = "",
    val path: String = "",
    val lede: String = "",
    val sections: List<RegionSection> = emptyList()
)

@Serializable
data class MenuEntry(
    val slug: String = "",
    val name: String = "",
    val path: String = ""
)

@Serializable
data class Taxonomy(
    val regions: List<Region> = emptyList(),
    val sections: List<Section> = emptyList(),
    val menu: Map<String, List<MenuEntry>> = emptyMap()
)

# Globalwire

Android news client for the y-squre.com Global News feed.

- Kotlin + Jetpack Compose + Material 3
- Package: `com.globalwire.news`
- Feed: `https://y-squre.com/assets/data/feed/global.json`
- Taxonomy: `https://y-squre.com/assets/data/taxonomy.json`

The region filter is driven by `taxonomy.menu.global`, matching the three
Global News hubs on y-squre.com:

- Asia/Oceania/Pacific
- Europe/UK/Middle East
- North America

Build:

```bash
ANDROID_HOME="$LOCALAPPDATA/Android/Sdk" gradle :app:assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

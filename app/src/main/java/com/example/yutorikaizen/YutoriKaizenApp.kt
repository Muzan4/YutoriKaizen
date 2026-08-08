package com.example.yutorikaizen

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class annotated with @HiltAndroidApp.
 *
 * WHY: Hilt needs to generate a component tree at compile time. This annotation
 * tells Hilt to use this class as the root of that tree. Without it, @Inject
 * and @HiltViewModel won't work anywhere in the app. It MUST be registered in
 * the manifest via android:name=".YutoriKaizenApp".
 */
@HiltAndroidApp
class YutoriKaizenApp : Application()

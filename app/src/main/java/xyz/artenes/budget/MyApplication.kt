/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.artenes.budget

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.artenes.budget.analytics.CrashAnalytics
import xyz.artenes.budget.analytics.ProductionTree
import xyz.artenes.budget.di.FactoryLocator
import xyz.artenes.budget.di.FactoryLocatorMapping
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {

    @Inject
    lateinit var mapping: FactoryLocatorMapping

    @Inject
    lateinit var crashAnalytics: CrashAnalytics

    @Inject
    lateinit var productionTree: ProductionTree

    override fun onCreate() {
        super.onCreate()
        FactoryLocator.instance = mapping
        setupLogging()
        setupCrashAnalytics()
    }

    private fun setupLogging() {
        val tree = if (BuildConfig.DEBUG) Timber.DebugTree() else productionTree
        Timber.plant(tree)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun setupCrashAnalytics() {
        crashAnalytics.setKey("testKeyA", "banana")
        crashAnalytics.setKey("testKeyB", "apple")
        GlobalScope.launch {
            crashAnalytics.setIdentifier(crashAnalytics.getId())
        }
    }

}

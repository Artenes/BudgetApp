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

package xyz.artenes.budget.app

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import xyz.artenes.budget.app.load.InitialLoadScreen
import xyz.artenes.budget.app.transaction.editor.TransactionEditorScreen
import xyz.artenes.budget.app.transaction.list.TransactionsListScreen

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "transactions") {

        composable("transactions") {
            TransactionsListScreen(
                navigateToTransactionEditScreen = {
                    navController.navigate("transactionEditor")
                }
            )
        }

        composable(
            "transactionEditor",
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up, tween(400)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down, tween(400)
                )
            },
        ) {
            TransactionEditorScreen(
                back = {
                    navController.popBackStack()
                }
            )
        }

    }
}

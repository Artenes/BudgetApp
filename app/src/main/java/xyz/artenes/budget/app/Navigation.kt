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
import androidx.navigation.navArgument
import xyz.artenes.budget.app.transaction.editor.TransactionEditorScreen
import xyz.artenes.budget.app.transaction.list.TransactionsListScreen
import xyz.artenes.budget.app.transaction.search.SearchScreen
import java.util.UUID

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "transactions") {

        composable("transactions") {
            TransactionsListScreen(
                navigateToTransactionEditScreen = {
                    navController.navigate("transactionEditor")
                },
                navigateToSearchScreen = {
                    navController.navigate("search")
                },
                navigateToTransaction = { id ->
                    navController.navigate("transactionEditor?id=$id")
                }
            )
        }

        composable(
            "transactionEditor?id={id}",
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
            arguments = listOf(navArgument("id") { nullable = true })
        ) { backStack ->

            val rawId = backStack.arguments?.getString("id")
            val id = if (rawId != null) UUID.fromString(rawId) else null

            TransactionEditorScreen(
                back = {
                    navController.popBackStack()
                },
                id = id
            )
        }

        composable(
            "search",
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

            SearchScreen(
                back = {
                    navController.popBackStack()
                },
                navigateToTransaction = { id ->
                    navController.navigate("transactionEditor?id=$id")
                },
            )

        }

        //TODO add screen to list categories

    }
}

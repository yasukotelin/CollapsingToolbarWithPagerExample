@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.yasukotelin.collapsingtoolbarexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.github.yasukotelin.collapsingtoolbarexample.ui.theme.CollapsingToolbarExampleTheme
import kotlin.math.absoluteValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CollapsingToolbarExampleTheme {

                val density = LocalDensity.current

                val playDistance = with(density) { 12.dp.toPx() }
                var isShowTopBarArea by remember { mutableStateOf(true) }

                val nestedScrollConnection = remember {
                    object : NestedScrollConnection {
                        override fun onPreScroll(
                            available: Offset,
                            source: NestedScrollSource
                        ): Offset {
                            if (available.y.absoluteValue > playDistance) {
                                isShowTopBarArea = available.y > 0
                            }
                            return Offset.Zero
                        }
                    }
                }

                Scaffold(
                    topBar = {
                        AnimatedVisibility(
                            visible = isShowTopBarArea,
                            enter = expandVertically(
                                expandFrom = Alignment.Top
                            ),
                            exit = shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .animateContentSize()
                            ) {
                                TopAppBar(
                                    colors = TopAppBarDefaults.smallTopAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    title = { Text("Toolbar") }
                                )
                                AsyncImage(
                                    model = "https://placehold.jp/350x160.png",
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.inverseOnSurface)
                            .nestedScroll(nestedScrollConnection)
                    ) {
                        LazyColumn {
                            items(100) { index ->
                                Text(
                                    "I'm item $index",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

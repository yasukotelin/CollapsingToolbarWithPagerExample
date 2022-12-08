@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.yasukotelin.collapsingtoolbarexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.yasukotelin.collapsingtoolbarexample.ui.theme.CollapsingToolbarExampleTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CollapsingToolbarExampleTheme {

                val coroutineScope = rememberCoroutineScope()
                val pagerState = rememberPagerState(initialPage = 0)
                val lazyListStates = listOf(
                    rememberLazyListState(), rememberLazyListState(), rememberLazyListState()
                )

                val density = LocalDensity.current

                val playDistance = with(density) { 12.dp.toPx() }
                var isShowTopBarArea by remember { mutableStateOf(true) }

                val nestedScrollConnection = remember {
                    object : NestedScrollConnection {
                        override fun onPreScroll(
                            available: Offset,
                            source: NestedScrollSource
                        ): Offset {
                            lazyListStates.getOrNull(pagerState.currentPage)?.let { lazyListState ->
                                if (available.y > 0 && lazyListState.firstVisibleItemIndex == 0) {
                                    // 一番上の要素が表示されたので表示
                                    isShowTopBarArea = true
                                } else {
                                    if (available.y.absoluteValue > playDistance && available.y < 0) {
                                        isShowTopBarArea = false
                                    }
                                }
                            }

                            return Offset.Zero
                        }
                    }
                }

                Scaffold(
                    modifier = Modifier.nestedScroll(nestedScrollConnection),
                    topBar = {
                        AnimatedVisibility(
                            visible = isShowTopBarArea,
                            enter = expandVertically(
                                expandFrom = Alignment.Top
                            ),
                            exit = shrinkVertically()
                        ) {
                            Column {
                                TopAppBar(
                                    colors = TopAppBarDefaults.smallTopAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    title = { Text("Toolbar") }
                                )
                                Image(
                                    painter = painterResource(id = R.drawable.banner),
                                    contentScale = ContentScale.FillWidth,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        // enable event when scroll image.
                                        .scrollable(
                                            orientation = Orientation.Vertical,
                                            state = rememberScrollableState { it }
                                        )
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
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            TabRow(
                                selectedTabIndex = pagerState.currentPage,
                            ) {
                                Tab(
                                    text = { Text("Tab1") },
                                    selected = pagerState.currentPage == 0,
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(0)
                                        }
                                    },
                                )
                                Tab(
                                    text = { Text("Tab2") },
                                    selected = pagerState.currentPage == 1,
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(1)
                                        }
                                    },
                                )
                                Tab(
                                    text = { Text("Tab3") },
                                    selected = pagerState.currentPage == 2,
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(2)
                                        }
                                    },
                                )
                            }
                            HorizontalPager(
                                count = 3,
                                state = pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { tabIndex ->
                                lazyListStates.getOrNull(tabIndex)?.let { lazyListState ->
                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        state = lazyListState
                                    ) {
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
        }
    }
}

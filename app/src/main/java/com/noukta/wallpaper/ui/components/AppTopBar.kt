package com.noukta.wallpaper.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.noukta.wallpaper.R
import com.noukta.wallpaper.ui.nav.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    screen: Screen,
    query: String,
    isSearchActive: Boolean,
    updateQuery: (String) -> Unit,
    searchByTag: (String) -> Unit,
    modifier: Modifier = Modifier) {
    if (screen in listOf(Screen.Home, Screen.Favorites)) {
        TopAppBar(
            title = {
                if(screen == Screen.Favorites) {
                    Text(
                        stringResource(screen.titleRes!!)
                    )
                }
            },
            modifier = modifier,
            actions = {
                if(screen == Screen.Home) {
                    SearchBar(
                        query = query,
                        onQueryChange = {
                            updateQuery(it)
                        },
                        onSearch = {
                            searchByTag(it)
                        },
                        active = isSearchActive,
                        onActiveChange = {

                        },
                        modifier = Modifier.padding(horizontal = 64.dp),
                        placeholder = {
                            Text(stringResource( R.string.app_name))
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_topbar_foreground),
                                contentDescription = null,
                                tint = Color.Unspecified
                            )
                        }
                    ) {

                    }
                }
            }
        )
    }
}
package compose.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.domain.ApiResult
import compose.navigation.MenuComponent
import data.Menu

@Composable
fun HomeView(menuComponent: MenuComponent) {
//    val menusState by menuComponent.menus.subscribeAsState()
    val menusState by menuComponent.menus.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text("Menus", modifier = Modifier.padding(16.dp))
        Divider(thickness = 2.dp, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))

        when (menusState) {
            is ApiResult.Loading -> {
                Text("Loading...", modifier = Modifier.padding(16.dp))
            }
            is ApiResult.Success -> {
                val menus = (menusState as ApiResult.Success<List<Menu>>).data
                menus?.let {
                    DisplayMenus(it)
                } ?: run {
                    Text("No menus available", modifier = Modifier.padding(16.dp))
                }
            }
            is ApiResult.Error -> {
                val errorMessage = (menusState as ApiResult.Error).error
                Text("Error: $errorMessage", modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
fun DisplayMenus(menus: List<Menu>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        for (rowIndex in menus.indices step 4) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (colIndex in 0 until 4) {
                    val menuIndex = rowIndex + colIndex
                    if (menuIndex < menus.size) {
                        val menuName = menus[menuIndex].menuName ?: "No Name"
                        Button(
                            onClick = { /* Handle button click */ },
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(menuName)
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

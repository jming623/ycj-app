package compose.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.domain.ApiResult
import compose.navigation.MenuComponent
import data.Menu
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import compose.util.LightGreen
import compose.util.PastelBlue
import data.BottomMenu


@Composable
fun HomeView(
    menuComponent: MenuComponent,
    onNavigateToSettings: () -> Unit,
    onAddButtonClicked: () -> Unit
) {
//    val menusState by menuComponent.menus.subscribeAsState()
    val menusState by menuComponent.menus.collectAsState()
    val bottomMenusState by menuComponent.bottomMenus.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }

    LaunchedEffect(menuComponent) {
        menuComponent.refreshData()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        Header(
            title = "My App",
            notificationsCount = 3,
            onNavigateToSettings = onNavigateToSettings,
            onAddButtonClicked = onAddButtonClicked
        )

        // SecondHeader
        SecondHeader(
            menusState = menusState,
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { index -> selectedTabIndex = index }
        )

        // MainContent
        MainContent(
            modifier = Modifier.weight(7f),
            content = {
                Text("Content for ${selectedTabIndex}", modifier = Modifier.padding(16.dp))
            }
        )

        // Footer
        Footer(bottomMenusState = bottomMenusState, modifier = Modifier.weight(1f))
    }
}

@Composable
fun Header(
    title: String,
    notificationsCount: Int,
    onNavigateToSettings: () -> Unit,
    onAddButtonClicked: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        },
//        navigationIcon = {
//            IconButton(onClick = onNavigateToSettings) {
//                Icon(Icons.Filled.Settings, contentDescription = "Settings")
//            }
//        },
        actions = {
            IconButton(onClick = onAddButtonClicked) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
            IconButton(onClick = onNavigateToSettings) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings")
            }

            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .size(24.dp)
                    .background(PastelBlue, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White
                )
                if (notificationsCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(16.dp)
                            .background(Color.Red, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = notificationsCount.toString(),
                            color = Color.White,
                            style = MaterialTheme.typography.caption
                        )
                    }
                }
            }
        },
        backgroundColor = PastelBlue,
        contentColor = Color.White
    )
}

@Composable
fun SecondHeader(menusState: ApiResult<List<Menu>>, selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    // 메뉴 이름 목록을 가져옵니다. null이거나 빈 목록일 수 있으므로 기본값을 제공합니다.
    val menuNames = when (menusState) {
        is ApiResult.Success -> {
            // data가 null일 경우 빈 리스트로 처리합니다.
            (menusState as ApiResult.Success<List<Menu>>).data?.map { it.menuName ?: "No Name" } ?: emptyList()
        }
        else -> emptyList()
    }

    // TabRow를 구성합니다.
    TabRow(
        selectedTabIndex = selectedTabIndex, // 기본적으로 첫 번째 탭을 선택합니다.
        backgroundColor = LightGreen,
        contentColor = Color.Black
    ) {
        if (menuNames.isEmpty()) {
            // 메뉴가 없으면 빈 TabRow를 렌더링합니다.
            Tab(
                selected = false,
                onClick = { /* Handle tab click */ },
                text = { Text("No Menus") }
            )
        } else {
            menuNames.forEachIndexed { index, menuName ->
                Tab(
                    selected = selectedTabIndex == index, // 선택된 탭의 로직을 추가할 수 있습니다.
                    onClick = { onTabSelected(index) },
                    text = { Text(menuName) }
                )
            }
        }
    }
}

@Composable
fun MainContent(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        content()
    }
}

@Composable
fun Footer(bottomMenusState: ApiResult<List<BottomMenu>>, modifier: Modifier = Modifier) {
    // API로부터 받은 메뉴 상태를 기반으로 메뉴 이름 목록을 생성합니다.
    val bottomMenuNames = when (bottomMenusState) {
        is ApiResult.Success -> {
            (bottomMenusState as ApiResult.Success<List<BottomMenu>>).data?.map { it.menuName ?: "No Name" } ?: emptyList()
        }
        else -> emptyList()
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .shadow(2.dp), // 그림자 효과 추가
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        bottomMenuNames.forEach { menuName ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp)
            ) {
                val icon = when (menuName) {
                    "Home" -> Icons.Filled.Home
                    "Search" -> Icons.Filled.Search
                    "Chat" -> Icons.Filled.MailOutline
                    "MyPage" -> Icons.Filled.AccountCircle
                    else -> Icons.Default.Home // 기본 아이콘 설정
                }

                Icon(
                    imageVector = icon,
                    contentDescription = menuName,
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = menuName,
                    color = Color.Black,
                    style = MaterialTheme.typography.body2.copy(fontSize = 12.sp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
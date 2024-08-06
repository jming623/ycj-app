package compose.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import compose.util.LightGreen
import compose.util.PastelBlue
import compose.util.White
import io.github.aakira.napier.Napier

@Composable
fun SettingsView(
    onBackButtonClick: () -> Unit,
    menuName: String,
    onMenuNameChange: (String) -> Unit,
    menuOrder: String,
    onMenuOrderChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    notificationMessage: String?
) {
    val scaffoldState = rememberScaffoldState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    var localMenuName by remember { mutableStateOf(menuName) }
    var localMenuOrder by remember { mutableStateOf(menuOrder) }

    // 전달된 key 값이 변경되면 호출
    LaunchedEffect(notificationMessage) {
        notificationMessage?.let {
            val result = scaffoldState.snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    Napier.d("스낵바 Open")
                }
                SnackbarResult.Dismissed -> {
                    Napier.d("스낵바 Close")
                }
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        // snackbar의 기본 위치가 하단이라 상단에 보여주고 싶어서 커스텀을 위해 선언(없어도 하단에 똑같이 뜸.)
        snackbarHost = { hostState ->
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                SnackbarHost(hostState, Modifier.align(Alignment.TopCenter).padding(top=32.dp)) { data ->
                    Snackbar(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp),
                            contentColor = White
                    ) {
                        Text(data.message, color = White)
                    }
                }
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackButtonClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                backgroundColor = PastelBlue,
                contentColor = White,
                elevation = 4.dp
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = localMenuName,
                    onValueChange = {
                        localMenuName = it
                        onMenuNameChange(it)
                    } ,
                    label = { Text("Menu Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = localMenuOrder,
                    onValueChange = {
                        localMenuOrder = it
                        onMenuOrderChange(it)
                    },
                    label = { Text("Menu Order") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        keyboardController?.hide() // 키보드가 열려 있다면 숨김
                        focusManager.clearFocus() // Text Feild에 걸려 있는 포커스 제거
                        onSubmitClick()
                        localMenuName = "" // 입력 받았던 menu 이름 제거
                        localMenuOrder = "" // 입력 받았던 menu 순서 제거
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(backgroundColor = LightGreen)
                ) {
                    Text("Submit")
                }
            }
        }
    )
}
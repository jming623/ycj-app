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
import compose.util.LightGreen
import compose.util.PastelBlue
import compose.util.White

@Composable
fun SettingsView(
    onBackButtonClick: () -> Unit,
    menuName: String,
    onMenuNameChange: (String) -> Unit,
    menuOrder: String,
    onMenuOrderChange: (String) -> Unit,
    onSubmitClick: () -> Unit = {}
) {
    Scaffold(
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
                    value = menuName,
                    onValueChange = onMenuNameChange,
                    label = { Text("Menu Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = menuOrder,
                    onValueChange = onMenuOrderChange,
                    label = { Text("Menu Order") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onSubmitClick,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(backgroundColor = LightGreen)
                ) {
                    Text("Submit")
                }
            }
        }
    )
}
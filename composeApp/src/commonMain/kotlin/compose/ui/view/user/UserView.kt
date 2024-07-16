package compose.ui.view.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import compose.domain.model.ApiResult
import compose.ui.component.NameCardBox

@Composable
fun UserView(
    userVM: UserVM
) {
    val result by userVM.users.collectAsState()
    val nickname by userVM.nickname.collectAsState()
    val searchType by userVM.searchType.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (result) {
                is ApiResult.Loading -> CircularProgressIndicator()
                is ApiResult.Error -> Text(text = "${result.error}", color = Color.Red)
                is ApiResult.Success -> {
                    if(result.data.isNullOrEmpty()) {
                        Text(text = "Data Empty")
                    } else {
                        result.data!!.forEach {
                            NameCardBox(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                user = it
                            )
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Row {
                Column {
                    TextField(value = nickname, onValueChange = { userVM.emitNickname(it) })
                    Button(onClick = { userVM.toggleSearchType() }) {
                        Text(text = "$searchType")
                    }
                }
                Button(onClick = { userVM.getUsers() }) {
                    Text(text = "SEARCH")
                }
            }
        }
    }
}

package compose.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.User

@Composable
fun NameCardBox(
    modifier: Modifier = Modifier,
    user: User
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(2.dp, Color.Gray, RoundedCornerShape(5.dp))
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(end = 4.dp),
            text = "${user.idx}",
            fontWeight = FontWeight.Black,
            fontSize = 24.sp
        )
        Text(
            text = "${user.name}",
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "${user.gender}"
        )
    }
}
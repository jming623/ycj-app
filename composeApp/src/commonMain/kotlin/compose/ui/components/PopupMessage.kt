package compose.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PopupMessage(
    message: String,
    isVisible: Boolean
) {
    // Box 자체에 contentAlignment를 설정해 화면 중앙에 배치
    Box(
        modifier = Modifier.fillMaxSize(),  // 전체 화면 크기 설정
        contentAlignment = Alignment.Center  // 내용(팝업)을 화면 중앙에 배치
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()  // 가로는 화면 전체
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = message,
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}
package compose.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.service.image.loadImageFromFile
import compose.util.SkylineBlue
import io.ktor.http.ContentDisposition.Companion.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

@Composable
fun BoardView() {
    var textState by remember { mutableStateOf("") }
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(Unit) {
        // 로컬 파일 경로를 넣어주세요
        val imagePath = "/storage/emulated/0/DCIM/upload/iu_01.png"

        // 로컬 파일에서 이미지를 불러오는 부분
        withContext(Dispatchers.IO) {
            imageBitmap = loadImageFromFile(imagePath)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            HeaderSection()

            // Content Section
            ContentSection(imageBitmap, textState) { newText ->
                textState = newText
            }

            // Menu Section
            // MenuSection()

            Spacer(modifier = Modifier.weight(1f))
        }

        // 공유 버튼
        ShareButton(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = { /* 뒤로가기 동작 */ }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "뒤로가기",
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "새 게시물",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun ContentSection(imageBitmap: ImageBitmap?, textState: String, onTextChanged: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 이미지가 있으면 표시
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap,
                contentDescription = "로컬에서 불러온 이미지",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // 이미지 크기 조정
                    .padding(bottom = 16.dp)
            )
        }

        // TextField
        BasicTextField(
            value = textState,
            onValueChange = onTextChanged,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(16.dp),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    if (textState.isEmpty()) {
                        Text("문구를 작성하거나 설문을 추가하세요...")
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Composable
fun ShareButton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp) // 좌우 패딩 적용
    ) {
        HorizontalDivider(thickness = 0.5.dp, color = Color.Gray) // 위쪽 경계선
        Spacer(modifier = Modifier.height(4.dp)) // 버튼과 경계선 사이 간격

        Button(
            onClick = { /* 공유 동작 */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 0.dp), // 아래 패딩 없음
            colors = ButtonDefaults.buttonColors(
                containerColor  = SkylineBlue,
                contentColor = Color.White    // 흰색 글씨
            ),
            shape = RoundedCornerShape(8.dp) // 버튼 모서리 둥글게
        ) {
            Text(text = "공유", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp)) // 위쪽 간격을 최소로
    }
}
package compose.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.layout.ContentScale
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
fun BoardView(
    onBackButtonClick: () -> Unit,
) {
    var textState by remember { mutableStateOf("") }
    var imageBitmaps by remember { mutableStateOf<List<ImageBitmap>>(emptyList()) }

    LaunchedEffect(Unit) {
        // 로컬 파일의 경우 에뮬레이터로 사용될 디바이스의 DCIM/upload 디렉터리 내부에 있어야 함.
        // 화면에서 사용될 이미지는 shared/src/commonMain/resouces/images 디렉터리에 존재. 복사해서 사용할 것.
        val imagePaths = listOf(
            "/storage/emulated/0/DCIM/upload/image_01.png",
            "/storage/emulated/0/DCIM/upload/image_02.jpg",
            "/storage/emulated/0/DCIM/upload/image_03.jpg"
        )

        // 로컬 파일에서 이미지를 불러오는 부분
        withContext(Dispatchers.IO) {
            imageBitmaps = imagePaths.mapNotNull { loadImageFromFile(it) }
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
            HeaderSection(onBackButtonClick = onBackButtonClick)

            // Content Section
            ContentSection(imageBitmaps, textState, onTextChanged = { newText ->
                textState = newText
            }, onRemoveImage = { index ->
                val updatedImages = imageBitmaps.toMutableList()
                updatedImages.removeAt(index)
                imageBitmaps = updatedImages
            })

            // Menu Section
            // MenuSection()

            Spacer(modifier = Modifier.weight(1f))
        }

        // 공유 버튼
        ShareButton(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun HeaderSection(onBackButtonClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackButtonClick) {
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
fun ContentSection(
    imageBitmaps: List<ImageBitmap>,
    textState: String,
    onTextChanged: (String) -> Unit,
    onRemoveImage: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 이미지가 있으면 표시
        if (imageBitmaps.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ){
                items(imageBitmaps.size) {index ->
                    val bitmap = imageBitmaps[index]
                    val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat() // 이미지의 비율 계산

                    // 비율에 따라 컨테이너 크기 결정
                    val containerHeight = 200.dp
                    val containerWidth = containerHeight * aspectRatio

                    Box(
                        modifier = Modifier
                            .width(containerWidth)
                            .height(containerHeight)
                    ){
                        Image(
                            bitmap = bitmap,
                            contentDescription = "로컬에서 불러온 이미지",
                            modifier = Modifier
                                .fillMaxSize(),
                            contentScale = ContentScale.Crop // 이미지가 크기에 맞게 잘리도록 설정
                        )
                        // 삭제 버튼
                        IconButton(
                            onClick = { onRemoveImage(index) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(7.dp)
//                                .offset(x = (-12).dp, y = 12.dp) // 사진의 우측 상단 안쪽으로 위치 조정
                                .background(Color.Black, shape = RoundedCornerShape(50))
                                .size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "이미지 삭제",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
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
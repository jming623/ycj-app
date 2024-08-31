package compose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.util.White
import compose.util.PastelBlue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import compose.navigation.GalleryComponent
import compose.util.Black
import compose.util.SvgLoader
import androidx.compose.foundation.Image
import io.github.aakira.napier.Napier

@Composable
fun GalleryView(
    galleryComponent: GalleryComponent,
    onBackButtonClick: () -> Unit,
) {
    val cameraIcon = remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(Unit) {
        Napier.d("안녕")
        val svgLoader = SvgLoader()

        cameraIcon.value = svgLoader.loadSvgIcon("icons/camera_icon.svg")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = Black,
                contentColor = White,
                elevation = 4.dp
            ) {
                // 왼쪽 끝의 X 버튼
                IconButton(onClick = onBackButtonClick) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }

                // 중앙의 "새 게시물" 텍스트
                Spacer(modifier = Modifier.weight(0.1f)) // 공간 확보를 위한 Spacer
                Text(
                    text = "새 게시물",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.weight(1f)) // 공간 확보를 위한 Spacer

                // 오른쪽 끝의 -> 아이콘
                IconButton(onClick = { /* TODO: 작업 추가 */ }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next")
                }
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // 중간 Detail View 영역
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.7f)
                        .background(Color.LightGray), // 일단 배경색으로 영역만 표시
                    contentAlignment = Alignment.Center
                ) {
                    Text("Detail View", color = Color.White) // 임시 텍스트로 영역 표시
                }

                // 아래 갤러리 영역
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.3f)
                        .background(Color.DarkGray)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // "최근" 텍스트와 아래 화살표 아이콘
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "최근",
                                color = Color.White,
                                style = MaterialTheme.typography.subtitle1
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // "여러항목 선택" 텍스트와 아이콘
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "여러 항목 선택",
                                color = Color.White,
                                style = MaterialTheme.typography.subtitle1,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            cameraIcon.value?.let {
                                Image(
                                    bitmap = it,
                                    contentDescription = "Camera Icon",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .border(2.dp, Color.Gray)
                            .background(Color.DarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Gallery Thumbnails", color = Color.White)
                    }
                }
            }
        }
    )
}
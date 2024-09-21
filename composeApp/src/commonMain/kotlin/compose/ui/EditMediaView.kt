package compose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import compose.navigation.EditMediaComponent
import compose.navigation.RootComponent
import compose.service.image.filePathToFile
import compose.util.Black
import compose.util.White
import io.github.aakira.napier.Napier
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun EditMediaView(
    rootComponent: RootComponent,
    editMediaComponent: EditMediaComponent,
) {
    val selectedImages = remember { rootComponent.imageManager.getSelectedImages() }

    LaunchedEffect(Unit) {
        Napier.d("EditMediaView initialized")
        rootComponent.imageManager.getSelectedImagesToString()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = Black,
                contentColor = White,
                elevation = 4.dp
            ) {
                // 왼쪽 끝의 X 버튼
                IconButton(onClick = { editMediaComponent.moveToGalleryView() }) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }

                // 중앙의 "새 게시물" 텍스트
                Spacer(modifier = Modifier.weight(0.1f))
                androidx.compose.material.Text(
                    text = "사진 수정",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.weight(1f))

                // 오른쪽 끝의 -> 아이콘
                IconButton(onClick = { editMediaComponent.moveToBoardView() }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next")
                }
            }
        },
        content = { innerPadding ->
            if (selectedImages.size == 1) {
                val selectedImage = selectedImages.firstOrNull()

                selectedImage?.let { mediaFile ->
                    val file = filePathToFile(mediaFile.filePath)
                    // File을 asyncPainterResource에 전달
                    val painterResource = asyncPainterResource(data = file)

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray)
                    ) {
                        when (painterResource) {
                            is Resource.Loading -> {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                            }
                            is Resource.Success -> {
                                KamelImage(
                                    resource = painterResource,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            is Resource.Failure -> {
                                Napier.e("이미지 로드 실패: ${file.toString()} - 이유: ${painterResource.exception.message}")
                                Text(
                                    text = "이미지를 불러오는 데 실패했습니다.",
                                    color = Color.Red,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(selectedImages.size) { idx ->
                        val file = filePathToFile(selectedImages[idx].filePath)
                        // File을 asyncPainterResource에 전달
                        val painterResource = asyncPainterResource(data = file)

                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(Color.LightGray)
                        ) {
                            when (painterResource) {
                                is Resource.Loading -> {
                                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                                }
                                is Resource.Success -> {
                                    KamelImage(
                                        resource = painterResource,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                is Resource.Failure -> {
                                    Napier.e("이미지 로드 실패: ${file.toString()} - 이유: ${painterResource.exception.message}")
                                    Text(
                                        text = "이미지를 불러오는 데 실패했습니다.",
                                        color = Color.Red,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        }

                    }
                }
            }
        }
    )
}

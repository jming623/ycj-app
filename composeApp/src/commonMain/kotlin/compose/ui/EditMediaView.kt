package compose.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
    var selectedImages by remember { mutableStateOf(rootComponent.imageManager.getSelectedImages()) }

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
                Text(
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Black)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // 상단 이미지 영역
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.5f) // 상단 50% 영역
                            .padding(8.dp)
                    ) {
                        if (selectedImages.size == 1) {
                            val (mediaFile, bitmap) = selectedImages.entries.elementAt(0)

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                                    .background(Color.Black, shape = MaterialTheme.shapes.medium)
                                    .clip(MaterialTheme.shapes.medium) // 둥근 모서리
                                    .border(2.dp, Color.Gray)
                            ) {
                                Image(
                                    bitmap = bitmap,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        } else {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(selectedImages.size) { idx ->
                                    val (mediaFile, bitmap) = selectedImages.entries.elementAt(idx)

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.6f) // 전체 너비의 90%
                                            .aspectRatio(1f)
                                            .padding(8.dp)
                                            .clip(MaterialTheme.shapes.medium)
                                            .background(Color.LightGray)
                                            .border(2.dp, Color.Gray)
                                    ) {
                                        Image(
                                            bitmap = bitmap,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .fillMaxSize(),
                                            contentScale = ContentScale.Fit
                                        )
                                        // 우상단 X 아이콘
                                        IconButton(
                                            onClick = {
                                                rootComponent.imageManager.removeSelectedImage(mediaFile)
                                                selectedImages = rootComponent.imageManager.getSelectedImages()
                                            },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(8.dp)
                                                .size(24.dp)
                                                .background(Color.Black.copy(alpha = 0.8f), shape = MaterialTheme.shapes.small)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Delete Image",
                                                tint = Color.White // 흰색 X 아이콘
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 하단 콘텐츠 영역
                    Spacer(modifier = Modifier.weight(1f)) // 하단 영역을 아래로 밀기 위한 Spacer
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.DarkGray)
                    ) {
                    }
                }
            }
        }
    )
}

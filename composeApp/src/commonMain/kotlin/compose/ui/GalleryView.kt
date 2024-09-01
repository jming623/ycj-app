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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

@Composable
fun GalleryView(
    galleryComponent: GalleryComponent,
    onBackButtonClick: () -> Unit,
) {
    /*
    * @OptIn(ExperimentalMaterialApi::class) <- 실험적(Experimental) API로 분류된 기능을 사용할 때 경고를 무시하기 위해 사용.
    * 현재로서는 ModalBottomSheetLayout의 안정적인 대안이 없다고 하여 우선 위 어노테이션을 박아두고 사용하는데,
    * 이는 향후 릴리스에서 동작이 달라질 수 있고, 다음 버전의 Compose에서 동작하지 않을 수 있음을 의미한다.
    * */
    val cameraIcon = remember { mutableStateOf<ImageBitmap?>(null) }
    @OptIn(ExperimentalMaterialApi::class)
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        Napier.d("안녕")
        val svgLoader = SvgLoader()
        cameraIcon.value = svgLoader.loadSvgIcon("icons/camera_icon.svg")
    }

    // ModalBottomSheetLayout을 통해 하단 시트와 메인 콘텐츠를 분리합니다.
    @OptIn(ExperimentalMaterialApi::class)
    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            // 하단 시트에 들어갈 내용
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
//                    .height(300.dp) // 하단 시트의 높이를 설정
                    .background(Color.DarkGray),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                // 드래그 핸들
                Box(
                    modifier = Modifier
                        .width(50.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.Gray)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "사진첩 선택",
                    color = Color.White,
                    style = MaterialTheme.typography.subtitle1,
//                    modifier = Modifier.padding(16.dp)
                )
                // 여기에서 앨범 리스트 같은 추가 내용을 넣을 수 있음
            }
        }
    ) {
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
                    Spacer(modifier = Modifier.weight(0.1f))
                    Text(
                        text = "새 게시물",
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    // 오른쪽 끝의 -> 아이콘
                    IconButton(onClick = { /* 작업 추가 */ }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Next")
                    }
                }
            },
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(0.dp)
//                        .padding(innerPadding)
                ) {
                    // 중간 Detail View 영역
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.7f)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Detail View", color = Color.White)
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
                                modifier = Modifier.weight(1f).clickable {
                                    scope.launch {
                                        bottomSheetState.show() // 하단 시트 표시
                                    }
                                }
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
}
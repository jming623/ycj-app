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
import androidx.compose.material.icons.Icons
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
import compose.service.image.SvgLoader
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import com.seiko.imageloader.rememberImagePainter
import compose.data.repos.MediaFile
import compose.util.PastelBlue
import compose.util.SkylineBlue
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
    val multiPickListIcon = remember { mutableStateOf<ImageBitmap?>(null) }
    @OptIn(ExperimentalMaterialApi::class)
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    
    // GalleryComponent에서 받아온 MediaFile을 저장할 변수
    var mediaFiles = remember { mutableStateOf<List<MediaFile>>(emptyList()) }
    // 선택된 이미지를 저장할 변수, 상단 DetailView에는 리스트에 가장 마지막 요소가 보여진다.
    val selectedImages = remember { mutableStateOf<List<MediaFile>>(emptyList()) }

    // 여러 항목을 보여주기 위한 상태를 담는 변수
    val isMultiSelectMode = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        Napier.d("실행됨")
        val svgLoader = SvgLoader()
        cameraIcon.value = svgLoader.loadSvgIcon("icons/camera_icon.svg")
        multiPickListIcon.value = svgLoader.loadSvgIcon("icons/multi_picklist_icon.svg")
        val files = galleryComponent.fetchRecentMediaFiles()
        mediaFiles.value = files

        if (files.isNotEmpty()) {
            selectedImages.value = listOf(files.first())
        }
    }
    LaunchedEffect(isMultiSelectMode.value) {
        if (!isMultiSelectMode.value && selectedImages.value.size > 1) {
            // Multi-select 모드가 비활성화되고 여러 항목이 선택되어 있을 때
            selectedImages.value = listOf(selectedImages.value.last())
        }
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
            @OptIn(ExperimentalMaterialApi::class)
            ModalBottomSheetLayout( // ModalBottomSheetLayout을 Scaffold의 content에 위치
                sheetState = bottomSheetState,
                sheetContent = {
                    // 하단 시트에 들어갈 내용
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(fraction = 1f) // 시트의 높이를 화면 전체로 강제 설정
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
                        )
                        // 여기에서 앨범 리스트 같은 추가 내용을 넣을 수 있음
                    }
                }
            ) {
                // 중간 Detail View 영역
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding) // 패딩을 원래대로 적용
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.7f)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        val imageToShow = selectedImages.value.lastOrNull()
                        if (imageToShow != null) {
                            Image(
                                painter = rememberImagePainter(imageToShow.uri),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()  // 꽉 차게 표시
                            )
                        } else {
                            Text("Detail View", color = Color.White)
                        }
                    }

                    // 아래 갤러리 영역
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.3f)
                            .background(Color.Black)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // "최근" 텍스트와 아래 화살표 아이콘
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.weight(1f)
                            ) {
                                // 여러 항목 선택 아이콘 (IconButton 대신 Box로 구현)
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)  // 원의 크기 설정
                                        .clip(CircleShape)  // 원형으로 클리핑
                                        .background(if (isMultiSelectMode.value) SkylineBlue else Color.DarkGray)
                                        .clickable { isMultiSelectMode.value = !isMultiSelectMode.value },  // 클릭 시 모드 전환
                                    contentAlignment = Alignment.Center
                                ) {
                                    multiPickListIcon.value?.let {
                                        Image(
                                            bitmap = it,  // ImageBitmap 사용
                                            contentDescription = "Multi PickList Icon",
                                            modifier = Modifier.size(22.dp),  // 아이콘 크기 설정 (원의 크기보다 약간 작게)
                                            colorFilter = ColorFilter.tint(Color.White)  // 아이콘 색상은 항상 흰색으로 유지
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // 카메라 아이콘 (IconButton 대신 Box로 구현)
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)  // 원의 크기 설정
                                        .clip(CircleShape)  // 원형으로 클리핑
                                        .background(Color.DarkGray)  // 배경색 설정
                                        .clickable { /* 카메라 아이콘 클릭 동작 추가 */ },  // 클릭 이벤트
                                    contentAlignment = Alignment.Center
                                ) {
                                    cameraIcon.value?.let {
                                        Image(
                                            bitmap = it,  // ImageBitmap 사용
                                            contentDescription = "Camera Icon",
                                            modifier = Modifier.size(22.dp),  // 아이콘 크기 설정 (원의 크기보다 약간 작게)
                                            colorFilter = ColorFilter.tint(Color.White)  // 아이콘 색상은 항상 흰색으로 유지
                                        )
                                    }
                                }
                            }
                        }
                        // 갤러리에서 불러온 사진이 표시됨.
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(1.dp)
                                .weight(1f) // 슬라이드 가능하도록 세로 스크롤
                        ){
                            items(mediaFiles.value) { mediaFile ->
                                val isSelected = selectedImages.value.contains(mediaFile)
                                Image(
                                    painter = rememberImagePainter(mediaFile.uri),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .padding(1.dp)
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(4.dp))
                                        .clickable {
                                            if (isMultiSelectMode.value) {
                                                // 여러 항목 선택 모드
                                                selectedImages.value = if (isSelected) {
                                                    // 이미 선택된 경우 리스트에서 제거
                                                    selectedImages.value - mediaFile
                                                } else {
                                                    // 선택되지 않은 경우 리스트에 추가
                                                    selectedImages.value + mediaFile
                                                }
                                            } else {
                                                // 단일 항목 선택 모드: 선택한 이미지로 교체
                                                selectedImages.value = listOf(mediaFile)
                                            }
                                        }
                                        // 선택된 이미지는 하이라이트
                                        .border(
                                            width = if (isSelected) 2.dp else 0.dp,
                                            color = if (isSelected) Color.White else Color.Transparent
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    )


}
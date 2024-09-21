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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.seiko.imageloader.rememberImagePainter
import compose.data.repos.GalleryFolder
import compose.data.repos.MediaFile
import compose.navigation.RootComponent
import compose.ui.components.PopupMessage
import compose.util.SkylineBlue
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GalleryView(
    rootComponent: RootComponent,
    galleryComponent: GalleryComponent,
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
    val mediaFiles = remember { mutableStateOf<List<MediaFile>>(emptyList()) }
    // 선택된 폴더명을 저장할 변수
    var selectedFolderName by remember { mutableStateOf<String?>("최근") }
    // 사용자가 클릭한 DetailView에 보여질 현재 이미지
    var currentDetailImage by remember { mutableStateOf<MediaFile?>(null) }

    // 여러 항목을 보여주기 위한 상태를 담는 변수
    val isMultiSelectMode = remember { mutableStateOf(false) }

    // 사진을 N개 이상 등록하면 사용자에게 보여줄 경고 문구
    var showPopup by remember { mutableStateOf(false) }

    // 사진첩 폴더를 저장하는 변수 (GalleryFolder List)
    var galleryFolders by remember { mutableStateOf<List<GalleryFolder>?>(null) }

    LaunchedEffect(Unit) {
        Napier.d("실행됨")
        val svgLoader = SvgLoader()
        cameraIcon.value = svgLoader.loadSvgIcon("icons/camera_icon.svg")
        multiPickListIcon.value = svgLoader.loadSvgIcon("icons/multi_picklist_icon.svg")
        val files = galleryComponent.getMediaFilesInFolder("recent")
        mediaFiles.value = files

        if (files.isNotEmpty()) {
            currentDetailImage = files.first()
            rootComponent.imageManager.setSelectedImages(listOf(files.first()))
        }
    }
    LaunchedEffect(isMultiSelectMode.value) {
        if (!isMultiSelectMode.value && rootComponent.imageManager.getSelectedImagesSize() > 1) {
            // Multi-select 모드가 비활성화되고 여러 항목이 선택되어 있을 때, 마지막 이미지로 재설정
            rootComponent.imageManager.setSelectedImageToLast()
        }
    }
    // 10개 초과 시 팝업을 보여주는 함수
    fun showSelectionLimitPopup() {
        scope.launch {
            showPopup = true   // 팝업을 표시
            delay(2000)        // 2초 동안 유지
            showPopup = false  // 팝업을 숨김
        }
    }

    // 폴더 데이터를 가져오는 함수
    suspend fun fetchGalleryFolders() {
        if (galleryFolders == null) {
            galleryFolders = galleryComponent.fetchGalleryFolders() // 폴더 데이터를 fetch
        }
    }

    // 폴더 클릭 시 실행할 함수 정의
    fun loadMediaFilesFromFolder(folder: GalleryFolder) {
        scope.launch {
            // 선택된 폴더명 수정
            selectedFolderName = folder.name
            // 선택된 폴더의 파일 Load
            val files = galleryComponent.getMediaFilesInFolder(folder.id)
            mediaFiles.value = files

            // 여러사진을 선택하는 모드가 아니라면 currentDetailImage 변경
            if (!isMultiSelectMode.value) {
                currentDetailImage = mediaFiles.value.first()
            }

            // mediaFiles로드 후 바텀 시트 invisible
            bottomSheetState.hide()
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
                IconButton(onClick = { galleryComponent.onBackButtonClick() }) {
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
                IconButton(onClick = { galleryComponent.moveToEditMediaView() }) {
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
                        // Gallery Folder List View
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.DarkGray)
                                .padding(8.dp)
                        ){
                            galleryFolders?.let { folders ->
                                items(folders) { folder ->
                                    Column(
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .clickable{
                                                loadMediaFilesFromFolder(folder)
                                            },
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        folder.recentImages.firstOrNull()?.let { image ->
                                            Image(
                                                painter = rememberImagePainter(image.uri),
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .aspectRatio(1f)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color.LightGray)
                                                    .padding(4.dp)
                                            )
                                        }
                                        // 앨범 이름
                                        Text(
                                            text = folder.name,
                                            color = Color.White,
                                            style = MaterialTheme.typography.subtitle2,
                                            modifier = Modifier.padding(top = 4.dp),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        // 콘텐츠 개수
                                        Text(
                                            text = "${folder.mediaCount}",
                                            color = Color.Gray,
                                            style = MaterialTheme.typography.body2
                                        )
                                    }
                                }
                            }
                        }
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
                        currentDetailImage?.let {
                            Image(
                                painter = rememberImagePainter(it.uri),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } ?: Text("Detail View", color = Color.White)
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
                                    .weight(0.7f)
                                    .clickable {
                                        scope.launch {
                                            fetchGalleryFolders()
                                            bottomSheetState.show() // 하단 시트 표시
                                        }
                                    }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f) // 전체 가로 공간에서 텍스트가 차지하는 영역
                                ) {
                                    // 텍스트와 아이콘을 한 Row에 배치하여 텍스트가 스크롤되면 아이콘은 붙어 있게 처리
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.horizontalScroll(rememberScrollState())
                                    ) {
                                        // 선택된 폴더 이름 텍스트
                                        selectedFolderName?.let {
                                            Text(
                                                text = it,
                                                color = Color.White,
                                                style = MaterialTheme.typography.subtitle1,
                                                maxLines = 1,
                                                modifier = Modifier.padding(end = 4.dp), // 텍스트와 아이콘 간격 설정
                                                overflow = TextOverflow.Clip // 텍스트가 잘리지 않고 스크롤 가능하게 설정
                                            )
                                        }

                                        // 텍스트 옆에 붙어 있는 Dropdown 아이콘
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = "Dropdown",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.weight(0.3f)
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
                                val isSelected = rootComponent.imageManager.isSelectedImage(mediaFile)
                                val selectionIndex = rootComponent.imageManager.getSelectedImageIndex(mediaFile)
                                Box(
                                    modifier = Modifier
                                        .padding(1.dp)
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(4.dp))
                                        .clickable (
                                            indication = null,  // Ripple 애니메이션 제거
                                            interactionSource = remember { MutableInteractionSource() }  // 클릭 피드백 제거
                                        ){
                                            if (isMultiSelectMode.value) {
                                                if (isSelected && mediaFile == currentDetailImage) {
                                                    // 이미 선택된 이미지이고, DetailView에 표시 중인 이미지인 경우 선택 해제
                                                    rootComponent.imageManager.removeSelectedImage(mediaFile)
                                                    currentDetailImage = rootComponent.imageManager.getLastSelectedImage()
                                                } else if (isSelected && mediaFile != currentDetailImage) {
                                                    // 이미 선택된 이미지지만 현재 DetailView에 표시되지 않은 경우, DetailView에 표시만 변경
                                                    currentDetailImage = mediaFile
                                                } else if (rootComponent.imageManager.getSelectedImagesSize() < 10) {
                                                    rootComponent.imageManager.addSelectedImage(mediaFile)
                                                    currentDetailImage = mediaFile
                                                } else {
                                                    showSelectionLimitPopup()  // 10개 초과시 팝업 표시
                                                }
                                            } else {
                                                rootComponent.imageManager.setSelectedImages(listOf(mediaFile))
                                                currentDetailImage = mediaFile
                                            }
                                        }
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Image(
                                            painter = rememberImagePainter(mediaFile.uri),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )

                                        // 다중 선택 모드가 활성화되어 있고 이미지가 선택된 경우, 우측 상단에 선택 순서 표시
                                        if (isMultiSelectMode.value && isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .padding(5.dp)
                                                    .clip(CircleShape)
                                                    .background(SkylineBlue)
                                                    .size(18.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    modifier = Modifier.offset(y = (-1).dp),
                                                    text = selectionIndex.toString(),
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    style = MaterialTheme.typography.body2
                                                )
                                            }
                                        }
                                        // currentDetailImage와 일치하는 이미지에 투명한 오버레이 추가
                                        if (mediaFile == currentDetailImage) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(Color.White.copy(alpha = 0.5f)) // 투명한 오버레이
                                            )
                                        }
                                        // 선택된 이미지는 하이라이트
                                        if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .border(
                                                        width = 2.dp,
                                                        color = Color.White,
                                                        shape = RoundedCornerShape(4.dp)
                                                    )
                                                    .fillMaxSize()
                                            )
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
            PopupMessage(
                message = "최대 10개의 사진이나 동영상을 포함할 수 있습니다.",
                isVisible = showPopup,  // 팝업을 표시할지 여부
            )
        }
    )
}
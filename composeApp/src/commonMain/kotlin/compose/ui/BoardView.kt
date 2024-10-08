package compose.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.data.repos.MediaFile
import compose.navigation.BoardComponent
import compose.navigation.GalleryComponent
import compose.navigation.RootComponent
import compose.service.image.SvgLoader
import compose.service.image.loadImageFromFile
import compose.ui.components.ConfirmPopup
import compose.util.SkylineBlue
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

@Composable
fun BoardView(
    rootComponent: RootComponent,
    boardComponent: BoardComponent
) {
    var textState by remember { mutableStateOf("") }
    // 이 변수가 RootComponent의 selectedImages로 변경되어야 함.
    var selectedImages by remember { mutableStateOf(rootComponent.imageManager.getSelectedImages()) }
    val icons = remember { mutableStateOf<List<ImageBitmap?>>(emptyList()) }

    LaunchedEffect(Unit) {
        val svgLoader = SvgLoader()
        val galleryIcon = svgLoader.loadSvgIcon("icons/gallery_icon.svg")
        val locationIcon = svgLoader.loadSvgIcon("icons/location_icon.svg")
        val personIcon = svgLoader.loadSvgIcon("icons/person_icon.svg")


        icons.value = listOf(
            galleryIcon,
            locationIcon,
            personIcon
        )
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
            HeaderSection(rootComponent = rootComponent, boardComponent = boardComponent)

            // Content Section
            ContentSection(selectedImages, textState, onTextChanged = { newText ->
                textState = newText
            }, onRemoveImage = { MediaFile ->
                rootComponent.imageManager.removeSelectedImage(MediaFile)
                selectedImages = rootComponent.imageManager.getSelectedImages()
            })

//             Menu Section
            MenuSection(icons = icons.value) { menuIdx ->
                when (menuIdx) {
                    0 -> boardComponent.onGalleryButtonClick()
                    1 -> println("위치 추가 페이지로 이동")
                    2 -> println("사람 태그 페이지로 이동")
                    else -> println("알 수 없는 메뉴 선택")
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        // 공유 버튼
        ShareButton(
            modifier = Modifier.align(Alignment.BottomCenter)
            ,onShareContent = {
                Napier.d("게시글 공유")
                Napier.d("작성한 글: ${textState}")
                Napier.d("업로드한 이미지:")
                rootComponent.imageManager.getSelectedImagesToString()
            }
        )
    }
}

@Composable
fun HeaderSection(rootComponent: RootComponent, boardComponent: BoardComponent) {
    // 팝업이나 경고 상태를 관리할 변수
    var showConfirmPopup by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = {
            /*
            여기서 선택된 이미지나 작성한 글이 있는지 확인하고, 없으면 그냥 HomeView로 이동
            있으면 경고 출력하고 선택됐던 이미지들 삭제 처리,
             */
            if (rootComponent.imageManager.getSelectedImagesSize() > 0) {
                showConfirmPopup = true
            }else{
                boardComponent.removeAllSelectedImages()
                boardComponent.onBackButtonClick()
            }

        } ) {
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
        // 경고 팝업을 표시하는 부분
        ConfirmPopup(
            message = "선택된 이미지가 있습니다. 모두 삭제하시겠습니까?",
            isVisible = showConfirmPopup,
            onConfirm = {
                // 확인을 누르면 이미지 삭제
                boardComponent.removeAllSelectedImages()
                boardComponent.onBackButtonClick()
                showConfirmPopup = false
            },
            onDismiss = {
                // 취소를 누르면 경고 팝업을 닫음
                showConfirmPopup = false
            }
        )
    }
}

@Composable
fun ContentSection(
    selectedImages: Map<MediaFile, ImageBitmap>,
    textState: String,
    onTextChanged: (String) -> Unit,
    onRemoveImage: (MediaFile) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .heightIn(min = 200.dp)
    ) {
        // 이미지가 있으면 표시
        if (selectedImages.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ){
                items(selectedImages.size) {idx ->
                    val (mediaFile, bitmap) = selectedImages.entries.elementAt(idx)

                    val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()

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
                            onClick = { onRemoveImage(mediaFile) },
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
                .padding(2.dp),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
//                        .padding(8.dp)
                ) {
                    if (textState.isEmpty()) {
                        Text(
                            "문구를 작성하거나 설문을 추가하세요...",
                            fontSize = 12.sp,
                            color = Color.Gray // 가이드 문구 스타일 조정
                        )
                    }
                    innerTextField()
                }
            },
            maxLines = Int.MAX_VALUE,
            singleLine = false
        )
    }
}

@Composable
fun MenuSection(icons: List<ImageBitmap?>, onMenuClick: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        // 구분을 위한 얇은 회색 선
//        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
        HorizontalDivider(thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(3.dp))

        // 메뉴 항목들

        if (icons.isNotEmpty() && icons.size >= 3) {
            MenuItem(icon = icons[0]!!, title = "사진 추가") {
                onMenuClick(0)
            }
            MenuItem(icon = icons[1]!!, title = "위치 추가") {
                onMenuClick(1)
            }
            MenuItem(icon = icons[2]!!, title = "사람 태그") {
                onMenuClick(2)
            }
        }
    }
}

@Composable
fun MenuItem(icon: ImageBitmap, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 좌측 아이콘
        Image(
            bitmap = icon,
            contentDescription = title,
            modifier = Modifier
                .size(30.dp)
                .padding(2.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        // 메뉴명
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "화살표",
            modifier = Modifier.size(24.dp),
            tint = Color.Gray
        )
    }
}

@Composable
fun ShareButton(modifier: Modifier = Modifier, onShareContent: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp) // 좌우 패딩 적용
    ) {
        HorizontalDivider(thickness = 0.5.dp, color = Color.Gray) // 위쪽 경계선
        Spacer(modifier = Modifier.height(4.dp)) // 버튼과 경계선 사이 간격

        Button(
            onClick = onShareContent, //버튼 클릭 시, 상위 컴포넌트에서 이벤트 처리
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
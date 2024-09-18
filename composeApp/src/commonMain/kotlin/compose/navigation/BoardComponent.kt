package compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import compose.ui.BoardView
import compose.ui.GalleryView

class BoardComponent(
    private val rootComponent: RootComponent,
    componentContext: ComponentContext,
): ComponentContext by componentContext {

    // 선택된 미디어 파일을 관리하는 리스트
    private var selectedMedia = mutableStateOf<List<String>>(emptyList())

    // 미디어 파일 선택 시 호출
    fun setSelectedMedia(media: List<String>) {
        selectedMedia.value = media
    }

//    // 선택된 미디어 파일을 가져오는 함수
//    fun getSelectedMedia(): List<String> {
//        return selectedMedia.value
//    }

    // 공유 버튼 클릭 시 호출할 처리 함수
    fun shareMedia() {
        println("서버로 전송할 미디어 파일: ${selectedMedia.value}")
    }

    fun onBackButtonClick() {
        rootComponent.pop()
    }
    fun onGalleryButtonClick() {
        rootComponent.navigate(RootComponent.Configuration.GalleryView)
    }

    @Composable
    fun showView() {
        BoardView(
            boardComponent = this
        )
    }
}
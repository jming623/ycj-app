package compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import compose.data.repos.GalleryFolder
import compose.data.repos.GalleryRepository
import compose.data.repos.MediaFile
import compose.permissions.PermissionsController
import io.github.aakira.napier.Napier
import compose.ui.GalleryView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryComponent(
    private val rootComponent: RootComponent,
    componentContext: ComponentContext,
    private val permissionsController: PermissionsController,
    private val galleryRepository: GalleryRepository
): ComponentContext by componentContext {

    private val scope = CoroutineScope(Dispatchers.Main)

    // recentMediaFiles를 상태로 관리
    var recentMediaFiles by mutableStateOf<List<MediaFile>>(emptyList())
        private set

    init {
        Napier.d("GalleryComponent initialized")
    }

    private fun checkPermissionsAndFetchGallery() {
        if (permissionsController.hasGalleryPermission()) {
            Napier.d("Permissions already granted")
        } else {
            permissionsController.checkAndRequestPermissions {
                Napier.d("Permissions granted")
            }
        }
    }

    suspend fun fetchGalleryFolders(): List<GalleryFolder>{
        return withContext(Dispatchers.IO) {
            val folders = galleryRepository.getFoldersWithRecentImages()
            if (folders.isEmpty()) {
                Napier.d("No gallery folders found.")
            } else {
                Napier.d("Gallery folders found: ${folders.size}")
                folders.forEach { folder ->
                    Napier.d("Folder ID: ${folder.id}, Name: ${folder.name}, Media Count: ${folder.mediaCount}")
                }
            }
            return@withContext folders
        }
    }

    suspend fun getMediaFilesInFolder(folderName: String): List<MediaFile> {
        val recentMediaFiles = galleryRepository.getMediaFilesInFolder(folderName)
        if (recentMediaFiles.isNotEmpty()) {
            Napier.d("Recent media files count: ${recentMediaFiles.size}")
        } else {
            Napier.d("No media files found in recent folder.")
        }
        return recentMediaFiles
    }

    // GalleryView에서 미디어 파일을 선택했을 때 BoardComponent에 전달
//    fun onMediaSelected(mediaFiles: List<String>) {
//        rootComponent.boardComponent.setSelectedMedia(mediaFiles) // BoardComponent에 전달
//        rootComponent.pop() // GalleryView에서 돌아가기
//    }

    fun submit(mediaFiles: List<String>) {
        // EditMediaView로 이동
        rootComponent.navigate(RootComponent.Configuration.EditMediaView)
    }

    fun onBackButtonClick() {
        rootComponent.pop()
    }

    @Composable
    fun showView() {
        GalleryView(
            galleryComponent = this,
        )
    }
}
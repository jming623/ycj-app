package compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.active
import compose.data.repos.GalleryFolder
import compose.data.repos.GalleryRepository
import compose.data.repos.MediaFile
import compose.permissions.PermissionsController
import io.github.aakira.napier.Napier
import compose.ui.GalleryView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class GalleryComponent(
    private val rootComponent: RootComponent,
    componentContext: ComponentContext,
    private val permissionsController: PermissionsController,
    private val galleryRepository: GalleryRepository
): ComponentContext by componentContext {

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
//                folders.forEach { folder ->
//                    Napier.d("Folder ID: ${folder.id}, Name: ${folder.name}, Media Count: ${folder.mediaCount}")
//                }
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

    fun moveToEditMediaView() {
        rootComponent.navigate(RootComponent.Configuration.EditMediaView)
    }

    fun onBackButtonClick() {
        rootComponent.imageManager.removeAllSelectedImage()
        rootComponent.navigate(RootComponent.Configuration.BoardView)
    }

    @Composable
    fun showView() {
        GalleryView(
            rootComponent = rootComponent,
            galleryComponent = this,
        )
    }
}
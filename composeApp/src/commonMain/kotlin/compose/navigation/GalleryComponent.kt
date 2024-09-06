package compose.navigation

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import compose.data.repos.GalleryRepository
import compose.permissions.PermissionsController
import io.github.aakira.napier.Napier
import compose.ui.GalleryView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GalleryComponent(
    private val rootComponent: RootComponent,
    componentContext: ComponentContext,
    private val permissionsController: PermissionsController,
    private val galleryRepository: GalleryRepository
): ComponentContext by componentContext {

    init {
        Napier.d("GalleryComponent initialized")
        checkPermissionsAndFetchGallery()
//        permissionsController.checkAndRequestPermissions{
//            Napier.d("Permissions granted")
//            fetchGalleryFolders()
//        }
    }

    private fun checkPermissionsAndFetchGallery() {
        if (permissionsController.hasGalleryPermission()) {
            Napier.d("Permissions already granted")
            fetchGalleryFolders()
        } else {
            permissionsController.checkAndRequestPermissions {
                Napier.d("Permissions granted")
                fetchGalleryFolders()
            }
        }
    }

    private fun fetchGalleryFolders() {
        CoroutineScope(Dispatchers.Main).launch {
            val folders = galleryRepository.getGalleryFolders()

            if (folders.isEmpty()) {
                Napier.d("No gallery folders found.")
            } else {
                Napier.d("Gallery folders found: ${folders.size}")
                folders.forEach { folder ->
                    Napier.d("Folder ID: ${folder.id}, Name: ${folder.name}, Media Count: ${folder.mediaCount}")
                }
            }

            // UI 갱신 로직 추가
        }
    }

    private fun onBackButtonClick() {
        rootComponent.pop()
    }

    @Composable
    fun showView() {
        GalleryView(
            galleryComponent = this,
            onBackButtonClick = { onBackButtonClick() },
        )
    }
}
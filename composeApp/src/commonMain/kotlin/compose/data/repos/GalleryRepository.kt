package compose.data.repos

interface GalleryRepository {
    suspend fun getGalleryFolders(): List<GalleryFolder>
    suspend fun getMediaFilesInFolder(folderId: String): List<MediaFile>
}

data class GalleryFolder(
    val id: String,
    val name: String,
    val mediaCount: Int
)

data class MediaFile(
    val id: String,
    val name: String,
    val uri: String,
    val mediaType: MediaType
)

enum class MediaType {
    IMAGE, VIDEO
}
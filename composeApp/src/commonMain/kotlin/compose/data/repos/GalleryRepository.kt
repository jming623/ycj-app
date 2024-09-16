package compose.data.repos

interface GalleryRepository {
    suspend fun getMediaFilesInFolder(folderId: String): List<MediaFile>
    suspend fun getFoldersWithRecentImages(): List<GalleryFolder>
}

data class GalleryFolder(
    val id: String,
    val name: String,
    val recentImages: List<MediaFile>,
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
package compose.util

import androidx.compose.ui.graphics.ImageBitmap
import compose.data.repos.MediaFile
import compose.navigation.RootComponent
import io.github.aakira.napier.Napier

class ImageManager() {
    private var _selectedImages: List<MediaFile> = emptyList()
    private var _updatedImages: List<ImageBitmap> = emptyList()

    /*
     * 갤러리에서 이미지들을 불러와 화면에 뿌려주기 위한 Image를 관리하는 함수 모음
     * 서로 다르게 유지하는 이유는 타입 특징과 관련이 있음.
     * MediaFile: 가벼움, 여러 정보를 담고있음, 수정이 어려움.
     * ImageBitmap: 비교적 무거움, 이미지에 대한 정보만 담고있음, 수정이 간편함.
     */
    // 선택된 이미지 리스트 반환
    fun getSelectedImages(): List<MediaFile> {
        return _selectedImages
    }
    // 선택된 이미지 리스트의 모든 속성을 String으로 출력
    fun getSelectedImagesToString() {
        _selectedImages.map { mediaFile ->
            Napier.d("ID: ${mediaFile.id}, Name: ${mediaFile.name}, URI: ${mediaFile.uri}, FilePath: ${mediaFile.filePath}, MediaType: ${mediaFile.mediaType}")
        }
    }
    // 이미지 리스트 전체 교체
    fun setSelectedImages(images: List<MediaFile>) {
        _selectedImages = images
    }
    // 이미지 리스트를 마지막 이미지로 재설정
    fun setSelectedImageToLast() {
        _selectedImages = listOf(_selectedImages.last())
    }
    // 이미지 리스트 크기 반환
    fun getSelectedImagesSize(): Int {
        return _selectedImages.size
    }
    // 이미지 리스트에 특정 이미지 포함 여부 확인
    fun isSelectedImage(mediaFile: MediaFile): Boolean {
        return _selectedImages.contains(mediaFile)
    }
    // 이미지 리스트에서 특정 이미지의 인덱스를 반환
    fun getSelectedImageIndex(mediaFile: MediaFile): Int {
        return _selectedImages.indexOf(mediaFile) + 1
    }
    // 이미지 리스트에 특정 이미지 추가
    fun addSelectedImage(mediaFile: MediaFile) {
        _selectedImages += mediaFile
    }
    // 이미지 리스트에 특정 이미지 제거
    fun removeSelectedImage(mediaFile: MediaFile) {
        _selectedImages -= mediaFile
    }
    /*
     * 사진 꾸미기를 통해 Edit되고, 실제 저장소에 저장될 Image를 관리하는 함수 모음
     */

    // 게시글 이미지 리스트 반환
    fun getUpdatedImages(): List<ImageBitmap> {
        return _updatedImages
    }
    // 게시글 이미지 리스트 교체
    fun setUpdatedImages(images: List<ImageBitmap>) {
        _updatedImages = images
    }
    // 게시글 이미지 리스트를 String으로 출력
    fun getUpdatedImagesToString() {
        _updatedImages.map { image ->
            Napier.d(image.toString())
        }
    }
    // 게시글 이미지의 크기 반환
    fun getUpdatedImagesSize(): Int {
        return _updatedImages.size
    }
    // 게시글 이미지 리스트에 특정 이미지 추가
    fun addUpdatedImage(image: ImageBitmap) {
        _updatedImages += image
    }
    // 이미지 리스트에 특정 이미지 제거
    fun removeUpdatedImage(image: ImageBitmap) {
        _updatedImages -= image
    }
}
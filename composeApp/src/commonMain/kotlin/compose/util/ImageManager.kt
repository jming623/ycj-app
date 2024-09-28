package compose.util

import androidx.compose.ui.graphics.ImageBitmap
import compose.data.repos.MediaFile
import compose.navigation.RootComponent
import compose.service.image.loadImageFromFile
import io.github.aakira.napier.Napier

class ImageManager() {
    // MediaFile과 IamgeBitMap을 모두 관리
    // MediaFile은 포함여부 확인 등의 각종 동작들을 하기 위해 사용하고, ImageBitMap은 아미지를 보여줄때만 사용
    private var _selectedImages: List<Pair<MediaFile, ImageBitmap>> = emptyList()

    // MediaFile을 ImageBitMap으로 변환하여 반환
    fun mediaFileToImageBitmap(mediaFile: MediaFile): ImageBitmap? {
        return loadImageFromFile(mediaFile.filePath)
    }

    // 선택된 이미지 리스트 반환
    fun getSelectedImages(): Map<MediaFile, ImageBitmap> {
        // associate함수: Iterable 컬렉션을 입력받아 Map으로 변환해주는 함수, Pair는 to를 기준으로 왼쪽은 Key가되고 오른쪽은 값이 됨.
        return _selectedImages.associate { (mediaFile, imageBitmap) ->
            mediaFile to imageBitmap
        }
    }
    // 선택된 이미지 리스트의 모든 속성을 String으로 출력
    fun getSelectedImagesToString() {
        // (mediaFile, _) 는 Pair에서 mediaFile만 사용하고 ImageBitmap은 사용하지 않음.
        _selectedImages.forEach { (mediaFile, _) ->
            Napier.d("ID: ${mediaFile.id}, Name: ${mediaFile.name}, URI: ${mediaFile.uri}, FilePath: ${mediaFile.filePath}, MediaType: ${mediaFile.mediaType}")
        }
    }
    // 이미지 리스트 전체 교체
    fun setSelectedImages(images: List<MediaFile>) {
        _selectedImages = images.mapNotNull { mediaFile ->
            val imageBitmap = loadImageFromFile(mediaFile.filePath)
            if (imageBitmap != null) {
                mediaFile to imageBitmap // MediaFile과 ImageBitmap을 Pair로 변환
            } else {
                null // ImageBitmap이 null인 경우 제외
            }
        }
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
        return _selectedImages.any { it.first == mediaFile }
    }
    // 이미지 리스트에서 특정 이미지의 인덱스를 반환
    fun getSelectedImageIndex(mediaFile: MediaFile): Int {
        val index = _selectedImages.indexOfFirst { it.first == mediaFile }
        return if (index != -1) index + 1 else -1
    }
    // 이미지 리스트에 특정 이미지 추가
    fun addSelectedImage(mediaFile: MediaFile) {
        val imageBitmap = loadImageFromFile(mediaFile.filePath)
        if (imageBitmap != null) {
            _selectedImages = _selectedImages + (mediaFile to imageBitmap)
        }
    }
    // 이미지 리스트에 특정 이미지 제거
    fun removeSelectedImage(mediaFile: MediaFile) {
        // filterNot함수는 리스트를 순회하며 조건에 맞지않는 항목을 새로운 리스트로 반환
        _selectedImages = _selectedImages.filterNot { it.first == mediaFile }
    }
    // 이미지 리스트에 전체 이미지 제거
    fun removeAllSelectedImage() {
        _selectedImages = emptyList()
    }
    // 이미지 리스트의 첫번째 이미지를 반환
    fun getFirstSelectedImage(): Map<MediaFile, ImageBitmap>? {
        return _selectedImages.firstOrNull()?.let { mapOf(it.first to it.second) }
    }
    // 이미지 리스트의 마지막 이미지를 반환
    fun getLastSelectedImage(): Map<Int, ImageBitmap>? {
        return _selectedImages.lastOrNull()?.let { mapOf(it.first.id.toInt() to it.second) }
    }
    // 이미지 리스트의 마지막 ImageBitMap을 반환
    fun getLastSelectedImageBitmap(): ImageBitmap? {
        return _selectedImages.lastOrNull()?.second
    }
    // 이미지 리스트의 마지막 MediaFile을 반환
    fun getLastSelectedImageMediaFile(): MediaFile? {
        return _selectedImages.lastOrNull()?.first
    }
    /*
     * 사진 꾸미기를 통해 Edit되고, 실제 저장소에 저장될 Image를 관리하는 함수 모음
     */
}
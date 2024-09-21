package compose.util

import compose.data.repos.MediaFile
import compose.navigation.RootComponent
import io.github.aakira.napier.Napier

class ImageManager(
    private val rootComponent: RootComponent,
) {
    // 선택된 이미지 리스트 반환
    fun getSelectedImages(): List<MediaFile> {
        Napier.d("몇번 실행 되니?")
        return rootComponent.selectedImages
    }
    // 선택된 이미지 리스트의 모든 속성을 String으로 변환하여 반환
    fun getSelectedImagesToString() {
        rootComponent.selectedImages.map { mediaFile ->
            Napier.d("ID: ${mediaFile.id}, Name: ${mediaFile.name}, URI: ${mediaFile.uri}, FilePath: ${mediaFile.filePath}, MediaType: ${mediaFile.mediaType}")
        }
    }
    // 이미지 리스트 전체 교체
    fun setSelectedImages(images: List<MediaFile>) {
        rootComponent.selectedImages = images
    }
    // 이미지 리스트를 마지막 이미지로 재설정
    fun setSelectedImageToLast() {
        rootComponent.selectedImages = listOf(rootComponent.selectedImages.last())
    }
    // 이미지 리스트 크기 확인
    fun getSelectedImagesSize(): Int {
        return rootComponent.selectedImages.size
    }
    // 이미지 리스트에 특정 이미지 포함 여부 확인
    fun isSelectedImage(mediaFile: MediaFile): Boolean {
        return rootComponent.selectedImages.contains(mediaFile)
    }
    // 이미지 리스트에서 특정 이미지의 인덱스를 반환
    fun getSelectedImageIndex(mediaFile: MediaFile): Int {
        return rootComponent.selectedImages.indexOf(mediaFile) + 1
    }
    // 이미지 리스트에 특정 이미지 추가
    fun addSelectedImage(mediaFile: MediaFile) {
        rootComponent.selectedImages += mediaFile
    }
    // 이미지 리스트에 특정 이미지 제거
    fun removeSelectedImage(mediaFile: MediaFile) {
        rootComponent.selectedImages -= mediaFile
    }
    // 이미지 리스트의 첫번째 이미지를 반환
    fun getFirstSelectedImage(): MediaFile? {
        return rootComponent.selectedImages.firstOrNull()
    }
    // 이미지 리스트의 마지막 이미지를 반환
    fun getLastSelectedImage(): MediaFile? {
        return rootComponent.selectedImages.lastOrNull()
    }
}
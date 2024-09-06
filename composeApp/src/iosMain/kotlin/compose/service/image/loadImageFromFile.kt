package compose.service.image

import androidx.compose.ui.graphics.ImageBitmap

actual fun loadImageFromFile(filePath: String): ImageBitmap? {
    // iOS 쪽은 나중에 구현할 예정이므로 현재는 null 반환
    return null
}
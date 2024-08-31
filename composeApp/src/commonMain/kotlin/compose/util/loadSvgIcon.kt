package compose.util

import androidx.compose.ui.graphics.ImageBitmap

//expect suspend fun loadSvgIcon(resourcePath: String): ImageBitmap
expect class SvgLoader() {
    suspend fun loadSvgIcon(resourcePath: String): ImageBitmap?
}
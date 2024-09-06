package compose.service.image

import androidx.compose.ui.graphics.ImageBitmap

expect fun loadImageFromFile(filePath: String): ImageBitmap?
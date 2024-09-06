package compose.service.image

import androidx.compose.ui.graphics.ImageBitmap
//import platform.Foundation.NSBundle
//import platform.UIKit.UIImage

//actual suspend fun loadSvgIcon(resourcePath: String): ImageBitmap {
////    val fullPath = NSBundle.mainBundle.pathForResource(resourcePath, ofType = "svg")
////    val svgImage = SVGKImage.imageNamed(fullPath)
////    return svgImage.uiImage.toComposeImageBitmap()
//    return ImageBitmap(1, 1)
//}
actual class SvgLoader {
    actual suspend fun loadSvgIcon(resourcePath: String): ImageBitmap? {
        return ImageBitmap(1, 1)
    }
}
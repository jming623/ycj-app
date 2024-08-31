package compose.util

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import coil.ImageLoader
import coil.request.ErrorResult
import org.koin.core.component.get
import coil.request.ImageRequest
import coil.request.SuccessResult
import io.github.aakira.napier.Napier
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.InputStream

actual class SvgLoader : KoinComponent {
    private val imageLoader: ImageLoader by inject()

    actual suspend fun loadSvgIcon(resourcePath: String): ImageBitmap? {
        Napier.d("여기를 타긴 타는거지??/")
        val context: Context = get()

        // "asset://" 스키마를 사용하여 리소스를 URI로 지정
        val assetUri = "file:///android_asset/$resourcePath"

        val request = ImageRequest.Builder(context)
            .data(assetUri)
            .build()

        val result = imageLoader.execute(request)

        return when (result) {
            is SuccessResult -> {
                val drawable = result.drawable as BitmapDrawable
                drawable.bitmap.asImageBitmap()
            }
            is ErrorResult -> {
                Napier.e("Image loading failed for: $resourcePath, error: ${result.throwable}")
                null
            }
            else -> {
                Napier.e("Unexpected result type: ${result::class.java}")
                null
            }
        }
    }
}
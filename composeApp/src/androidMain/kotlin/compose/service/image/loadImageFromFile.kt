package compose.service.image

import android.graphics.BitmapFactory
import android.os.Environment
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import io.github.aakira.napier.Napier

import java.io.File

actual fun loadImageFromFile(filePath: String): ImageBitmap? {
    // DCIM 경로 가져오기
    val dcimPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
    Napier.d("DCIM 경로: $dcimPath") // Napier로 경로 로그 출력

    val imageFile = File(filePath)
    return if (imageFile.exists()) {
        val bitmap = BitmapFactory.decodeFile(filePath)
        bitmap?.asImageBitmap()
    } else {
        null
    }
}
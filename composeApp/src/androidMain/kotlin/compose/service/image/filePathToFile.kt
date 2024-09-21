package compose.service.image

import java.io.File

actual fun filePathToFile(path: String): File {
    return File(path) // Android에서 경로를 File 객체로 변환
}
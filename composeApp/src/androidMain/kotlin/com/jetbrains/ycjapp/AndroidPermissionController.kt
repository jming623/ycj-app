package com.jetbrains.ycjapp

import android.app.Activity
import android.os.Build
import androidx.core.content.ContextCompat
import compose.permissions.PermissionsController
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import io.github.aakira.napier.Napier

class AndroidPermissionsController(
    private val activity: Activity,
    private val permissionLauncher: ActivityResultLauncher<String>
) : PermissionsController {

    override fun requestPostNotification() {
        val POST_NOTIFICATION_PERMISSION_CODE = 1001

        // TIRAMISU = Android13, 13버전 이하는 별도의 알림 권한을 요청하지 않아도 됨.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), POST_NOTIFICATION_PERMISSION_CODE)
            }
        }
    }

    override fun hasGalleryPermission(): Boolean {
        // UPSIDE_DOWN_CAKE = 14, TIRAMISU = 13, S = 12
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                ) == PackageManager.PERMISSION_GRANTED
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(
                            activity,
                            Manifest.permission.READ_MEDIA_VIDEO
                        ) == PackageManager.PERMISSION_GRANTED
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            }
            else -> true // Android 12 이하 버전에서는 별도의 갤러리 권한이 필요하지 않음
        }
    }

    override fun checkAndRequestPermissions(onPermissionsResult: () -> Unit) {
        // UPSIDE_DOWN_CAKE = 14, TIRAMISU = 13, S = 12
        try {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                    val permission = Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                    if (ContextCompat.checkSelfPermission(activity,permission) == PackageManager.PERMISSION_GRANTED ){
                        Napier.d("$permission 권한이 존재.")
                        onPermissionsResult()
                    } else {
                        Napier.d("$permission 권한 요청.")
//                        ActivityCompat.requestPermissions(activity, arrayOf(permission), 1001)
                        permissionLauncher.launch(permission)
                    }
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    val permissions = arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO
                    )

                    permissions.forEach { permission ->
                        if (ContextCompat.checkSelfPermission(activity,permission) == PackageManager.PERMISSION_GRANTED ){
                            Napier.d("$permission 권한이 존재.")
                        } else{
                            Napier.d("$permission 권한 요청.")
                            permissionLauncher.launch(permission)
                        }
                    }
                    onPermissionsResult()
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    val permission = Manifest.permission.READ_EXTERNAL_STORAGE
                    if (ContextCompat.checkSelfPermission(activity,permission) == PackageManager.PERMISSION_GRANTED ){
                        Napier.d("$permission 권한이 존재.")
                        onPermissionsResult()
                    } else {
                        Napier.d("$permission 권한 요청.")
                        permissionLauncher.launch(permission)
                    }
                }
                else -> {
                    Napier.d("권한이 필요하지 않은 버전입니다.")
//                    onPermissionsGranted()
                    onPermissionsResult()
                }
            }
        }catch (e: Exception) {
            Napier.e("권한 요청 중 예외 발생: ${e.message}")
            onPermissionsDenied()
        }finally {
            onPermissionsResult()
        }
    }
    override fun onPermissionsGranted() {
        Toast.makeText(activity, "권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
        // 권한이 허용된 경우에 수행할 작업 추가 가능
    }

    override fun onPermissionsDenied() {
        Toast.makeText(activity, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
    }
}
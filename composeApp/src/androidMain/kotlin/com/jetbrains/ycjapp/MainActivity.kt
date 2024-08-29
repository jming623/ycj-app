package com.jetbrains.ycjapp

import App
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.arkivanov.decompose.retainedComponent
import compose.navigation.RootComponent
import io.github.aakira.napier.Napier
import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import compose.permissions.PermissionsRequester

class MainActivity : ComponentActivity(), PermissionsRequester {

    private lateinit var permissionsController: AndroidPermissionsController
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            Napier.d("권한 요청 결과 수신됨: $isGranted")
            if (isGranted) {
                Napier.d("onPermissionsGranted실행")
                permissionsController.onPermissionsGranted()
            } else {
                Napier.d("onPermissionsDenied실행")
                permissionsController.onPermissionsDenied()
            }
        }

        // AndroidPermissionsController 초기화
        permissionsController = AndroidPermissionsController(this, permissionLauncher)

        // 알림 권한 요청 (추후 앱 최초 시작 지점에 알림 권한을 요청하지 않으려면, CommonMain쪽으로 호출 위치 이동 가능)
        permissionsController.requestPostNotification()

        val root = retainedComponent {
            RootComponent(it, permissionsController)
        }

        setContent {
            App(root)
        }
    }
    override fun requestGalleryPermissions(onPermissionsResult: () -> Unit) {
        permissionsController.checkAndRequestPermissions {
            onPermissionsResult()
        }
    }
}
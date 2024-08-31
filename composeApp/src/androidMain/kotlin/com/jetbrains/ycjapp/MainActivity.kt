package com.jetbrains.ycjapp

import App
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.arkivanov.decompose.retainedComponent
import compose.navigation.RootComponent
import io.github.aakira.napier.Napier

class MainActivity : ComponentActivity(){

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

        // 권한 요청을 게시글 작성 부분에서 받기 위해 RootComponent쪽으로 PermissionController를 넘겨줌.
        val root = retainedComponent {
            RootComponent(it, permissionsController)
        }

        setContent {
            App(root)
        }
    }
}
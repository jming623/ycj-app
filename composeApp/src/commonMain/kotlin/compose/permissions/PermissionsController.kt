package compose.permissions

interface PermissionsController {
    fun requestPostNotification()
    fun checkAndRequestPermissions(onPermissionsResult: () -> Unit)
    fun hasGalleryPermission():Boolean
    fun onPermissionsGranted()
    fun onPermissionsDenied()
//    var onPermissionsGranted: () -> Unit
//    var onPermissionsDenied: () -> Unit
}


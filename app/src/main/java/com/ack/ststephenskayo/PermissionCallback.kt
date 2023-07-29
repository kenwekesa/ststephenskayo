package com.ack.ststephenskayo

//import android.Manifest
//import android.content.pm.PackageManager
//import android.os.Build
//import androidx.annotation.RequiresApi
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat

// PermissionCallback.kt

interface PermissionCallback {
    fun onPermissionGranted()
    fun onPermissionDenied()
}

//if (checkWriteExternalStoragePermission()) {
//    // Permission already granted, generate PDF
//    isWriteExternalStoragePermissionGranted = true
//} else {
//    // Permission not granted, request it
//    ActivityCompat.requestPermissions(
//        this,
//        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//        WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
//    )
//}
//
//
//private fun checkWriteExternalStoragePermission(): Boolean {
//    val permission = ContextCompat.checkSelfPermission(
//        this,
//        Manifest.permission.WRITE_EXTERNAL_STORAGE
//    )
//    return permission == PackageManager.PERMISSION_GRANTED
//}
//
//@RequiresApi(Build.VERSION_CODES.Q)
//override fun onRequestPermissionsResult(
//    requestCode: Int,
//    permissions: Array<out String>,
//    grantResults: IntArray
//) {
//    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//    if (requestCode == WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE) {
//        if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            // Permission granted
//            isWriteExternalStoragePermissionGranted = true
//        } else {
//            // Permission denied, handle the denial scenario
//            // For example, show a message to the user explaining why the permission is necessary
//            isWriteExternalStoragePermissionGranted = false
//        }
//    }
//}
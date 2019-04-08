package com.shojishunsuke.myapplication

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
//import android.hardware.camera2.CameraDevice
//import android.hardware.camera2.CameraManager
//import android.media.ImageReader
//import android.view.TextureView
//import androidx.core.app.ActivityCompat.requestPermissions
//import androidx.core.content.ContextCompat
//import androidx.core.content.ContextCompat.getSystemService
//import java.util.jar.Manifest
//
//class CameraViewManager(activity: Activity,context: Context) {
//    lateinit var cameraID: String
//    lateinit var cameraManager: CameraManager
//    private var cameraDevice: CameraDevice? = null
//    private var imageReader: ImageReader? = null
//
//    fun init(){
//
//    }
//
//    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener{
//        override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture?, p1: Int, p2: Int) {
//
//        }
//
//        override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {
//
//        }
//
//        override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean {
//
//        }
//
//        override fun onSurfaceTextureAvailable(p0: SurfaceTexture?, width: Int, height: Int) {
//            imageReader = ImageReader.newInstance(width,height, ImageFormat.JPEG,2)
//            imageReader?.setOnImageAvailableListener(mOnImageAvailableListener,)
//            openCamera()
//        }
//    }
//
//   fun openCamera(activity: Activity) {
//        cameraManager = activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
//
//        try {
//            cameraID = cameraManager.cameraIdList[0]
//
//            val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
//            if (permission != PackageManager.PERMISSION_GRANTED) {
//                requestCameraPermission()
//                return
//            }
//            cameraManager.openCamera(cameraID,deviceStateCallbacks,null)
//        }catch (e :Exception){
//            e.printStackTrace()
//        }
//    }
//
//
//
//}
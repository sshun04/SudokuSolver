package com.shojishunsuke.myapplication

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.Surface
import android.view.TextureView
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata

class MainActivity : AppCompatActivity() {
    lateinit var button: Button
    private val textureView: TextureView by lazy {
        findViewById<TextureView>(R.id.textureView)
    }
    private var cameraCaptureSession: CameraCaptureSession? = null
    private val REQUEST_CODE_CAMERA = 1
    private val previewSize: Size = Size(300, 300)
    lateinit var cameraID: String
    private var cameraDevice: CameraDevice? = null
    private var backgroundThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null

    private val cameraManager: CameraManager by lazy {
        getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    private val ORIENTATIONS = SparseIntArray()

    init {
        ORIENTATIONS.append(Surface.ROTATION_0, 90)
        ORIENTATIONS.append(Surface.ROTATION_90, 0)
        ORIENTATIONS.append(Surface.ROTATION_180, 270)
        ORIENTATIONS.append(Surface.ROTATION_270, 180)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        button = findViewById(R.id.captureButton)

        button.setOnClickListener {


        }


    }

    override fun onResume() {
        super.onResume()

        if (textureView.isAvailable) {
            openCamera()
        } else {
            textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture?, p1: Int, p2: Int) {}
                override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {}
                override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean {
                    return false
                }

                override fun onSurfaceTextureAvailable(p0: SurfaceTexture?, width: Int, height: Int) {
                    openCamera()
                }
            }
        }
    }


    private fun openCamera() {

        try {
//            cameraID = cameraManager.cameraIdList[0]

            val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                requestCameraPermission()
                return
            }
            cameraManager.openCamera("0", deviceStateCallbacks, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun requestCameraPermission() {
        requestPermissions(
            arrayOf(android.Manifest.permission.CAMERA),
            200
        )
    }

    private fun createCameraPreviewSession() {
        if (cameraDevice == null) {
            return
        }
        val texture = textureView.surfaceTexture
        texture.setDefaultBufferSize(640,480)
        val surface = Surface(texture)

        val previewRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        previewRequestBuilder.addTarget(surface)


        cameraDevice?.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
            override fun onConfigureFailed(p0: CameraCaptureSession) {
            }
            override fun onConfigured(p0: CameraCaptureSession) {
                cameraCaptureSession = p0
                cameraCaptureSession?.setRepeatingRequest(previewRequestBuilder.build(),null,null)
            }
        }, null)
    }

    private val mOnImageAvailableListener = ImageReader.OnImageAvailableListener {
        Log.d("mainActivity", "imageAvailable")
    }


    private val deviceStateCallbacks = object : CameraDevice.StateCallback() {
        override fun onOpened(p0: CameraDevice) {
            this@MainActivity.cameraDevice = p0
            createCameraPreviewSession()
        }

        override fun onClosed(camera: CameraDevice) {
            super.onClosed(camera)
        }

        override fun onDisconnected(p0: CameraDevice) {
            p0.close()
            this@MainActivity.cameraDevice = null
        }

        override fun onError(p0: CameraDevice, p1: Int) {
            onDisconnected(p0)
            finish()
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Throws(CameraAccessException::class)
    private fun getRationCompasation(cameraID: String, activity: Activity): Int {
        val deviceRotation = activity.windowManager.defaultDisplay.rotation
        var rotationCompensation = ORIENTATIONS.get(deviceRotation)

        val sensorOrientation =
            cameraManager.getCameraCharacteristics(cameraID).get(CameraCharacteristics.SENSOR_ORIENTATION)!!
        rotationCompensation = (rotationCompensation + sensorOrientation + 270) % 360

        val result: Int
        when (rotationCompensation) {
            0 -> result = FirebaseVisionImageMetadata.ROTATION_0
            90 -> result = FirebaseVisionImageMetadata.ROTATION_90
            180 -> result = FirebaseVisionImageMetadata.ROTATION_180
            270 -> result = FirebaseVisionImageMetadata.ROTATION_270
            else -> result = FirebaseVisionImageMetadata.ROTATION_0
        }

        return result

    }


}

package com.shojishunsuke.myapplication

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
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
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var button: Button
    lateinit var captureRequestBuilder: CaptureRequest.Builder
    lateinit var captureRequest: CaptureRequest
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
    lateinit var surface: Surface
    lateinit var imageReader: ImageReader

    lateinit var cameraManager: CameraManager
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

//            takePicture()
        }


    }



    private fun captureStillPicture() {

        if (cameraDevice == null) {
            return
        }
        val takeCaptureBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)

        takeCaptureBuilder.addTarget(surface)

        takeCaptureBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)

        cameraCaptureSession?.stopRepeating()
        cameraCaptureSession?.capture(takeCaptureBuilder.build(),object : CameraCaptureSession.CaptureCallback(){
            override fun onCaptureCompleted(
                session: CameraCaptureSession,
                request: CaptureRequest,
                result: TotalCaptureResult
            ) {
                unlockFocus()
            }
        },null)



    }

    private fun unlockFocus() {
        try {
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                CameraMetadata.CONTROL_AF_TRIGGER_CANCEL)
            cameraCaptureSession?.capture(captureRequestBuilder.build(),null,backgroundHandler)

            cameraCaptureSession?.setRepeatingRequest(captureRequest,null,backgroundHandler)

        }catch (e :CameraAccessException){

            e.printStackTrace()
        }

    }

    private fun readImage() {

    }

    override fun onResume() {
        super.onResume()

        if (textureView.isAvailable) {
            startCamera()
        } else {
            textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture?, p1: Int, p2: Int) {}
                override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {}
                override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean {
                    return false
                }

                override fun onSurfaceTextureAvailable(p0: SurfaceTexture?, width: Int, height: Int) {
                    startCamera()
                }
            }
        }
    }

    private fun startCamera() {
        backgroundThread = HandlerThread("CameraBackThread").also { it.start() }
        backgroundHandler = Handler(backgroundThread?.looper)

        if (textureView.isAvailable) {
            openCamera(800,600)
        } else {
            textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture?, p1: Int, p2: Int) {}
                override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {}
                override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean {
                    return true
                }

                override fun onSurfaceTextureAvailable(p0: SurfaceTexture?, width: Int, height: Int) {

                    openCamera(800,600)
                }
            }
        }
    }


    private fun openCamera(width: Int, height: Int) {


        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission()
            return
        }


        imageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1)
        imageReader.setOnImageAvailableListener(object : ImageReader.OnImageAvailableListener {
            override fun onImageAvailable(p0: ImageReader?) {



                Log.d("Aaaa", "A")
            }
        }, backgroundHandler)


        cameraManager =   getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            cameraManager.openCamera("0", deviceStateCallbacks, backgroundHandler)
        }catch (e : CameraAccessException){
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
       try {
           val texture = textureView.surfaceTexture
           texture.setDefaultBufferSize(800, 600)
           surface = Surface(texture)

           captureRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
           captureRequestBuilder.addTarget(surface)


           cameraDevice?.createCaptureSession(
               Arrays.asList(surface),
               object : CameraCaptureSession.StateCallback() {
                   override fun onConfigureFailed(p0: CameraCaptureSession) {


                   }

                   override fun onConfigured(p0: CameraCaptureSession) {
                       cameraCaptureSession = p0
                       try {
                           captureRequest = captureRequestBuilder.build()
                           cameraCaptureSession?.setRepeatingRequest(captureRequest, null,backgroundHandler)
                       }catch (e:CameraAccessException){
                           e.printStackTrace()
                       }

                   }
               },
               backgroundHandler
           )
       }catch ( e :CameraAccessException){
           e.printStackTrace()
       }




    }


    private fun takePicture(){
        try {
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,CameraMetadata.CONTROL_AF_TRIGGER_START)

            cameraCaptureSession?.capture(captureRequestBuilder.build(),object :CameraCaptureSession.CaptureCallback(){
                override fun onCaptureCompleted(
                    session: CameraCaptureSession,
                    request: CaptureRequest,
                    result: TotalCaptureResult
                ) {
                    captureStillPicture()
                }
            },backgroundHandler)

        }catch (e:CameraAccessException){
            e.printStackTrace()
        }



    }

    private val deviceStateCallbacks = object : CameraDevice.StateCallback() {
        override fun onOpened(p0: CameraDevice) {
            cameraDevice = p0
            createCameraPreviewSession()
        }

        override fun onClosed(camera: CameraDevice) {
            super.onClosed(camera)
            camera.close()
            cameraDevice = null
        }

        override fun onDisconnected(p0: CameraDevice) {
            p0.close()
            this@MainActivity.cameraDevice = null
        }

        override fun onError(p0: CameraDevice, p1: Int) {
            onDisconnected(p0)
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Throws(CameraAccessException::class)
    private fun getRationCompasation(cameraID: String): Int {
        val deviceRotation = this.windowManager.defaultDisplay.rotation
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

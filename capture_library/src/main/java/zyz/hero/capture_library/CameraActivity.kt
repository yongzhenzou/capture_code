package zyz.hero.capture_library

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.extensions.BeautyImageCaptureExtender
import androidx.camera.extensions.BeautyPreviewExtender
import androidx.camera.extensions.HdrImageCaptureExtender
import androidx.camera.extensions.HdrPreviewExtender
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_camera.*
import zyz.hero.capture_library.utils.statusbar.StatusBarUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

open class CameraActivity : AppCompatActivity() {
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    var cameraSelector: CameraSelector? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    var backCam = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtils.setStatusBarImmersiveColor(this, Color.TRANSPARENT, false)
        setContentView(R.layout.activity_camera)
        titleView.setOnLeftButtonClickListener {
            finish()
        }
        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Setup the listener for take photo button
        capture.setOnClickListener {
            captureGroup.visibility = View.GONE
            takePhoto()
        }
        changeCamera.setOnClickListener {
            backCam = !backCam
            startCamera()
        }
        cancel.setOnClickListener {
            startCamera()
        }
        viewFinder.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                cameraSelector?.let {
                    var pointFactory = viewFinder.createMeteringPointFactory(it)
                    var point = pointFactory.createPoint(event.x, event.y)
                    var action = FocusMeteringAction.Builder(point).build()
                    camera?.cameraControl?.startFocusAndMetering(action)
                    return@setOnTouchListener true
                }
                false
            } else {
                false
            }
        }
        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    //开启预览
    private fun startCamera() {
        captureGroup.visibility = View.VISIBLE
        selectGroup.visibility = View.GONE
        viewFinder.visibility = View.VISIBLE
        // 选择相机
        cameraSelector = CameraSelector.Builder()
            .requireLensFacing(if (backCam) CameraSelector.LENS_FACING_BACK else CameraSelector.LENS_FACING_FRONT)
            .build()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            // 预览
            var previewBuilder = Preview.Builder()
            //支持hdr的话 开启hdr 选取质量好的照片
            var previewHdr = HdrPreviewExtender.create(previewBuilder)
            if (previewHdr.isExtensionAvailable(cameraSelector!!)) {
                previewHdr.enableExtension(cameraSelector!!)
            }
            //支持美颜的话,开启美颜,感觉自己萌萌哒
            var previewBeauty = BeautyPreviewExtender.create(previewBuilder)
            if (previewBeauty.isExtensionAvailable(cameraSelector!!)) {
                previewBeauty.enableExtension(cameraSelector!!)
            }

            preview = previewBuilder.build()
            //拍照
            var captureBuilder = ImageCapture.Builder()
            var captureHdr = HdrImageCaptureExtender.create(captureBuilder)
            if (captureHdr.isExtensionAvailable(cameraSelector!!)) {
                captureHdr.enableExtension(cameraSelector!!)
            }
            var captureBeauty = BeautyImageCaptureExtender.create(captureBuilder)
            if (captureBeauty.isExtensionAvailable(cameraSelector!!)) {
                captureBeauty.enableExtension(cameraSelector!!)
            }
            imageCapture = captureBuilder
                .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setTargetRotation(windowManager.defaultDisplay.rotation)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build()
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                cameraProvider.unbindAll()
                camera =
                    cameraProvider.bindToLifecycle(this, cameraSelector!!, preview, imageCapture)
                preview?.setSurfaceProvider(viewFinder.createSurfaceProvider())
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    //拍照具体实现
    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT).format(System.currentTimeMillis()) + ".jpg"
        )
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
            .setMetadata(ImageCapture.Metadata().apply {
                //处理前置摄像头 左右翻转
                if (!backCam) isReversedHorizontal = true
            }).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    startCamera()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    selectGroup.visibility = View.VISIBLE
                    viewFinder.visibility = View.GONE
                    Glide.with(this@CameraActivity).load(savedUri.path).into(img)
                    confirm.setOnClickListener {
                        setResult(Activity.RESULT_OK, Intent().apply {
                            putExtra("data", savedUri)
                            putExtra("path", photoFile.path)
                        })
                        finish()
                    }
                }
            })
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun getOutputDirectory(): File =
        File(cacheDir.toString(), "capture_img").apply { mkdirs() }

    companion object {
        private const val TAG = "CameraCode"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
}
package com.github.bobekos.simplebarcodescanner2.camera.v1

import android.hardware.Camera
import android.os.Handler
import android.util.Size
import android.view.TextureView
import com.github.bobekos.simplebarcodescanner2.ScannerConfig
import com.github.bobekos.simplebarcodescanner2.camera.base.CameraBuilder
import com.github.bobekos.simplebarcodescanner2.utils.CameraFacing
import com.google.android.gms.vision.CameraSource
import kotlin.math.abs

class Camera1SourceBuilder(private val config: ScannerConfig, private val displaySize: Size) :
    CameraBuilder<Camera1Preview, Camera1ImageProcessor>() {

    private lateinit var camera: Camera
    private lateinit var cameraInfo: Camera.CameraInfo

    fun build() = apply {
        cameraInfo = Camera.CameraInfo()

        val cameraId = (0..Camera.getNumberOfCameras())
            .find {
                Camera.getCameraInfo(it, cameraInfo)

                cameraInfo.facing == getFacing(config.lensFacing)
            }

        if (cameraId == null) {
            //TODO throw no facing for config type found dont forget camera 2
            throw NullPointerException()
        } else {
            camera = Camera.open(cameraId)
        }
    }

    override fun createPreview(textureView: TextureView, width: Int, height: Int): Camera1Preview {
        val rotation = getRotation()
        val previewSize = getValidPreviewSize(displaySize)

        val preview = Camera1Preview(camera)
            .setPreviewSize(previewSize)
            .setRotation(rotation.first)
            .build()

        camera.setDisplayOrientation(rotation.second)

        updateTextureView(textureView, previewSize, width, height)

        return preview
    }

    override fun createImageAnalyzer(handler: Handler): Camera1ImageProcessor {
        val rotation: Int = getRotation().first.div(90)

        return Camera1ImageProcessor(camera, handler, getValidPreviewSize(displaySize), rotation)
    }

    private fun getFacing(facing: CameraFacing): Int {
        return when (facing) {
            CameraFacing.BACK -> CameraSource.CAMERA_FACING_BACK
            CameraFacing.FRONT -> CameraSource.CAMERA_FACING_FRONT
        }
    }

    private fun getValidPreviewSize(size: Size): Size {
        val supportedPreviewSize = camera.parameters.supportedPreviewSizes

        var result = size
        var minDiff = Int.MAX_VALUE

        supportedPreviewSize.forEach {
            val diff = abs(it.width - size.width) +
                    abs(it.height - size.height)
            if (diff < minDiff) {
                result = Size(it.width, it.height)
                minDiff = diff
            }
        }

        return result
    }

    private fun getRotation(): Pair<Int, Int> {
        val surfaceRotation = displayRotation.getSurfaceRotation()

        val angle: Int
        val displayAngle: Int

        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            angle = (cameraInfo.orientation + surfaceRotation.toInt()) % 360
            displayAngle = (360 - angle) % 360
        } else {
            angle = (cameraInfo.orientation - surfaceRotation.toInt() + 360) % 360
            displayAngle = angle
        }

        return Pair(angle, displayAngle)
    }

    /*
    private void setRotation(Camera camera, Camera.Parameters parameters, int cameraId) {
        WindowManager windowManager =
                (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int degrees = 0;
        int rotation = windowManager.getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
                Log.e(TAG, "Bad rotation value: " + rotation);
        }

        CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);

        int angle;
        int displayAngle;
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            angle = (cameraInfo.orientation + degrees) % 360;
            displayAngle = (360 - angle) % 360; // compensate for it being mirrored
        } else {  // back-facing
            angle = (cameraInfo.orientation - degrees + 360) % 360;
            displayAngle = angle;
        }

        // This corresponds to the rotation constants in {@link Frame}.
        mRotation = angle / 90;

        camera.setDisplayOrientation(displayAngle);
        parameters.setRotation(angle);
    }
     */
}
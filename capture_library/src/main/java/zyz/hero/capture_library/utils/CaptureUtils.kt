package zyz.hero.capture_library.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File

class CaptureUtils {
    companion object {
        const val CAPTURE_REQUEST_CODE = 100
        fun capture(
            context: Activity,
            imgPath: String,
            authority: String
        ) {
            try {
                var imgFile = File(imgPath)
                if (imgFile.exists()) {
                    imgFile.delete()
                }
                imgFile.createNewFile()
                var imgUri = when (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    true -> FileProvider.getUriForFile(context, authority, imgFile)
                    else -> Uri.fromFile(imgFile)
                }

                with( Intent(MediaStore.ACTION_IMAGE_CAPTURE)){
                    putExtra(MediaStore.EXTRA_OUTPUT,imgUri)
                    context.startActivityForResult(this, CAPTURE_REQUEST_CODE)
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}
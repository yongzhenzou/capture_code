package zyz.hero.camera_code

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import zyz.hero.capture_library.CameraActivity
import zyz.hero.capture_library.utils.CaptureUtils
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        capture.setOnClickListener {
//            startActivityForResult(Intent(this, CameraActivity::class.java),1)
            CaptureUtils.capture(this,externalCacheDir?.path+"/"+"capture_temp.png","${BuildConfig.APPLICATION_ID}.fileProvider")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CaptureUtils.CAPTURE_REQUEST_CODE&&resultCode == Activity.RESULT_OK){
            Toast.makeText(this,File(externalCacheDir?.path+"/"+"capture_temp.png").exists().toString(),Toast.LENGTH_SHORT).show()

        }
    }
}

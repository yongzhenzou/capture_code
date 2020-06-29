package zyz.hero.camera_code

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_img_preview.*
import java.io.File

class ImgPreviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_img_preview)
        var uri = intent.getParcelableExtra<Uri>("data")
        Glide.with(this).load(File(uri.path)).into(img)
    }
}

package zyz.hero.capture_library.utils.statusbar

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.Nullable


class StatusBarView @JvmOverloads constructor(context: Context, @Nullable attrs: AttributeSet? = null) :
    View(context, attrs) {

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mStatusBarHeight = getStatusBarHeight(context)
        }
    }

    protected override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mStatusBarHeight)
    }

    companion object {
        private var mStatusBarHeight: Int = 0

        //此处代码可以放到StatusBarUtils
        fun getStatusBarHeight(context: Context): Int {
            if (mStatusBarHeight == 0) {
                val res = context.resources
                val resourceId = res.getIdentifier("status_bar_height", "dimen", "android")
                if (resourceId > 0) {
                    mStatusBarHeight = res.getDimensionPixelSize(resourceId)
                }
            }
            return mStatusBarHeight
        }
    }
}
package zyz.hero.camera_code.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.layout_title.view.*
import zyz.hero.camera_code.R


/**
 * title
 * @author songyaru
 * @date 2020-03-20
 */
class LayoutTitleView : ConstraintLayout {
    private var leftImage: Int = R.drawable.icon_back_black
    private var centerTitle: String? = null
    private var rightTitle: String? = null
    private var centerTitleSize: Float = 16f
    private var rightTitleSize: Float = 14f
    private var centerTitleColor: Int = Color.BLACK
    private var rightTitleColor: Int = Color.BLACK
    private var leftImageVisibility: Int = 1 //1 visible  2 invisible   3 gone
    private var rightTitleVisibility: Int = -1
    private var rightTitleDrawableLeft: Int = -1
    private var rightTitleDrawableRight: Int = -1
    private var rightTitleDrawableTop: Int = -1
    private var rightTitleDrawableBottom: Int = -1
    private var rightTitleDrawablePadding: Int = 0
    private var bgColor: Int = Color.WHITE

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        initView(attributeSet, defStyleAttr)
    }

    private fun initView(attributeSet: AttributeSet?, defStyleAttr: Int) {
        val a = context.obtainStyledAttributes(attributeSet, R.styleable.LayoutTitleView, defStyleAttr, 0)
        leftImage = a.getResourceId(R.styleable.LayoutTitleView_leftImage, R.drawable.icon_back_black)
        centerTitle = a.getString(R.styleable.LayoutTitleView_centerTitle)
        rightTitle = a.getString(R.styleable.LayoutTitleView_rightTitle)
        centerTitleSize =
            a.getDimension(R.styleable.LayoutTitleView_centerTitleSize, dp2px(16))
        rightTitleSize =
            a.getDimension(R.styleable.LayoutTitleView_rightTitleSize, dp2px(14))
        centerTitleColor =
            a.getColor(R.styleable.LayoutTitleView_centerTitleColor, Color.parseColor("#30365A"))
        bgColor = a.getColor(R.styleable.LayoutTitleView_bgColor, Color.WHITE)
        rightTitleColor =
            a.getColor(R.styleable.LayoutTitleView_rightTitleColor, Color.parseColor("#30365A"))
        leftImageVisibility = a.getInt(R.styleable.LayoutTitleView_leftImageVisibility, 1)
        rightTitleVisibility = a.getInt(R.styleable.LayoutTitleView_rightTitleVisibility, -1)
        rightTitleDrawableLeft =
            a.getResourceId(R.styleable.LayoutTitleView_rightTitleDrawableLeft, -1)
        rightTitleDrawableRight =
            a.getResourceId(R.styleable.LayoutTitleView_rightTitleDrawableRight, -1)
        rightTitleDrawableTop =
            a.getResourceId(R.styleable.LayoutTitleView_rightTitleDrawableTop, -1)
        rightTitleDrawableBottom =
            a.getResourceId(R.styleable.LayoutTitleView_rightTitleDrawableBottom, -1)
        rightTitleDrawablePadding =
            a.getDimensionPixelSize(R.styleable.LayoutTitleView_rightTitleDrawablePadding, 0)
        a.recycle()

        LayoutInflater.from(context).inflate(R.layout.layout_title, this)

        clTitlte.setBackgroundColor(bgColor)
        title_left_image.setImageResource(leftImage)
        title_left_image.visibility = visibilityStatus(leftImageVisibility)

        rightTitle?.let {
            title_right_btn_text.text = rightTitle
            title_right_btn_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, rightTitleSize)
            title_right_btn_text.setTextColor(rightTitleColor)
            title_right_btn_text.visibility = visibilityStatus(1)
            if (rightTitleVisibility == -1) title_right_btn_text.visibility = visibilityStatus(1)
        }
        if (rightTitleVisibility != -1) title_right_btn_text.visibility =
            visibilityStatus(rightTitleVisibility)
        val drawableLeft: Drawable? =
            if (rightTitleDrawableLeft != -1) context.getDrawable(rightTitleDrawableLeft) else null
        val drawableRight: Drawable? =
            if (rightTitleDrawableRight != -1) context.getDrawable(rightTitleDrawableRight) else null
        val drawableTop: Drawable? =
            if (rightTitleDrawableTop != -1) context.getDrawable(rightTitleDrawableTop) else null
        val drawableBottom: Drawable? =
            if (rightTitleDrawableBottom != -1) context.getDrawable(rightTitleDrawableBottom) else null
        drawableLeft?.setBounds(0, 0, drawableLeft.minimumWidth, drawableLeft.minimumHeight)
        drawableRight?.setBounds(0, 0, drawableRight.minimumWidth, drawableRight.minimumHeight)
        drawableTop?.setBounds(0, 0, drawableTop.minimumWidth, drawableTop.minimumHeight)
        drawableBottom?.setBounds(0, 0, drawableBottom.minimumWidth, drawableBottom.minimumHeight)
        title_right_btn_text.setCompoundDrawables(
            drawableLeft,
            drawableTop,
            drawableRight,
            drawableBottom
        )
        title_right_btn_text.compoundDrawablePadding = rightTitleDrawablePadding
        if (drawableLeft != null || drawableRight != null || drawableTop != null || drawableBottom != null) {
            if (rightTitleVisibility == -1) title_right_btn_text.visibility = visibilityStatus(1)
        }

        centerTitle?.let {
            title_center_text.text = centerTitle
            title_center_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, centerTitleSize)
            title_center_text.setTextColor(centerTitleColor)
        }
    }

    /**
     * 设置三个点击事件，并回调
     */
    fun setOnClickListener(leftImageClick: () -> Unit = {}, rightTextClick: () -> Unit = {}) {
        title_left_image.setOnClickListener { leftImageClick() }
        title_right_btn_text.setOnClickListener { rightTextClick() }
    }

    fun setOnLeftButtonClickListener(leftButtonClick: () -> Unit = {}) {
        title_left_image.setOnClickListener { leftButtonClick() }
    }

    fun setOnRightButtonClickListener(RightButtonClick: () -> Unit = {}) {
        title_right_btn_text.setOnClickListener { RightButtonClick() }
    }

    fun setDrawables(
        drawableLeft: Drawable?,
        drawableTop: Drawable?,
        drawableRight: Drawable?,
        drawableBottom: Drawable?,
        visibility: Int = View.VISIBLE
    ) {
        drawableLeft?.setBounds(0, 0, drawableLeft.minimumWidth, drawableLeft.minimumHeight)
        drawableRight?.setBounds(0, 0, drawableRight.minimumWidth, drawableRight.minimumHeight)
        drawableTop?.setBounds(0, 0, drawableTop.minimumWidth, drawableTop.minimumHeight)
        drawableBottom?.setBounds(0, 0, drawableBottom.minimumWidth, drawableBottom.minimumHeight)
        title_right_btn_text.setCompoundDrawablesWithIntrinsicBounds(
            drawableLeft,
            drawableTop,
            drawableRight,
            drawableBottom
        )
        title_right_btn_text.visibility = visibility
    }

    /**
     * title右边文字按钮
     */
    fun setRightText(text: String, visibility: Int = View.VISIBLE) {
        rightTitle = text
        title_right_btn_text.text = text
        title_right_btn_text.visibility = visibility
    }

    /**
     * 设置title
     */
    fun setCenterText(text: String) {
        centerTitle = text
        title_center_text.text = text
    }

    private fun visibilityStatus(status: Int): Int {
        return when (status) {
            1 -> View.VISIBLE
            2 -> View.INVISIBLE
            else -> View.GONE
        }
    }

    private fun dp2px(dps: Int): Float {
        return resources.displayMetrics.density * dps + 0.5f
    }
}
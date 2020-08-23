package com.seki.saezurishiki.view.customview

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector

class ZoomPicture @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var mScaleFactor = 1f

    private var offsetX = 0f
    private var offsetY = 0f
    private var currentX = 0
    private var currentY = 0

    private val scaleGestureListener =
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mScaleFactor *= detector.scaleFactor
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 3.0f))
            scaleX *= mScaleFactor
            scaleY *= mScaleFactor
            return true
        }
    }

    private val doubleTapListener = object : GestureDetector.SimpleOnGestureListener() {

        override fun onDoubleTap(e: MotionEvent): Boolean {
            if (scaleX != 1f) {
                scaleX = 1f
                scaleY = 1f
                mScaleFactor = 1f
                layout(0, 0, width, height)
            }
            return true
        }
    }

    private val scaleDetector = ScaleGestureDetector(context, scaleGestureListener)
    private val doubleTapDetector = GestureDetector(context, doubleTapListener)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(ev)
        doubleTapDetector.onTouchEvent(ev)
        if (!scaleDetector.isInProgress) {
            performTouch(ev)
        }
        return true
    }

    private fun performTouch(ev: MotionEvent) {

        //タップされた座標を取得
        val rawX = ev.rawX
        val rawY = ev.rawY

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                // for ViewPager
                if (scaleX != 1.0f) {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                //タップ時点の左上の座標を取得
                currentX = left
                currentY = top
                //タップ座標をOffSet値に設定
                offsetX = rawX
                offsetY = rawY
            }

            MotionEvent.ACTION_MOVE -> {
                //移動距離を算出
                val distanceX = offsetX - rawX
                val distanceY = offsetY - rawY

                //基準となる左上座標の移動距離を算出
                currentX -= distanceX.toInt()
                currentY -= distanceY.toInt()

                //移動
                if (scaleX < 1f) {
                    layout(left, currentY, left + width, currentY + height)
                } else if (scaleX > 1f) {
                    layout(currentX, currentY, currentX + width, currentY + height)
                }

                //タップ座標をOffSet値に設定
                offsetX = rawX
                offsetY = rawY
            }
        }
    }
}
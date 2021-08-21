package io.github.patxibocos.roadcyclingdata.ui.util

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.core.graphics.applyCanvas
import coil.bitmap.BitmapPool
import coil.size.Size
import coil.transform.Transformation
import kotlin.math.min

class CustomCircleCropTransformation(private val borderPercent: Float = 4f) : Transformation {

    private val Bitmap.safeConfig: Bitmap.Config
        get() = config ?: Bitmap.Config.ARGB_8888

    override fun key(): String = CustomCircleCropTransformation::class.java.name

    override suspend fun transform(pool: BitmapPool, input: Bitmap, size: Size): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        val minSize = min(input.width, input.height) + borderPercent.toInt() * 2
        val radius = minSize / 2f
        val output = pool.get(minSize, minSize, input.safeConfig)
        output.applyCanvas {
            drawCircle(radius, radius, radius, paint)
            paint.xfermode = XFERMODE
            drawBitmap(input, radius - input.width / 2f, borderPercent, paint)
            val strokeRadius = this.width * borderPercent / 200
            val borderPaint = Paint().apply {
                color = Color.BLACK
                strokeWidth = strokeRadius
                style = Paint.Style.STROKE
                isAntiAlias = true
                isDither = true
            }
            drawCircle(radius, radius, radius - strokeRadius / 2f, borderPaint)
        }

        return output
    }

    override fun equals(other: Any?) = other is CustomCircleCropTransformation

    override fun hashCode() = javaClass.hashCode()

    override fun toString() = "CustomCircleCropTransformation()"

    private companion object {
        val XFERMODE = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    }
}

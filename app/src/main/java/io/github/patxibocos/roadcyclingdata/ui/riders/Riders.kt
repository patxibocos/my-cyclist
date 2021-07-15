package io.github.patxibocos.roadcyclingdata.ui.riders

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.applyCanvas
import androidx.hilt.navigation.compose.hiltViewModel
import coil.bitmap.BitmapPool
import coil.compose.rememberImagePainter
import coil.size.Size
import coil.transform.Transformation
import io.github.patxibocos.roadcyclingdata.Country
import io.github.patxibocos.roadcyclingdata.data.json.Rider
import io.github.patxibocos.roadcyclingdata.getEmoji
import kotlin.math.min

@Composable
fun RidersScreen() {
    Riders(
        viewModel = hiltViewModel(),
    )
}

@Composable
internal fun Riders(
    viewModel: RidersViewModel
) {
    Column {
        var searchQuery by remember { mutableStateOf("") }
        TextField(modifier = Modifier.fillMaxWidth(), value = searchQuery, onValueChange = {
            searchQuery = it
            viewModel.onSearched(it)
        }, label = {
            Text("Search")
        })
        val riders by viewModel.riders.collectAsState()
        RidersList(riders)
    }
}

@Composable
internal fun RidersList(riders: List<Rider>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(riders) { rider ->
            RiderRow(rider)
        }
    }
}

private class CustomCircleCropTransformation : Transformation {

    private val borderRadius = 4f

    private val Bitmap.safeConfig: Bitmap.Config
        get() = config ?: Bitmap.Config.ARGB_8888

    override fun key(): String = CustomCircleCropTransformation::class.java.name

    override suspend fun transform(pool: BitmapPool, input: Bitmap, size: Size): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val borderPaint = Paint().apply {
            color = Color.BLACK
            strokeWidth = borderRadius
            style = Paint.Style.STROKE
            isAntiAlias = true
            isDither = true
        }

        val minSize = min(input.width, input.height) + borderRadius.toInt() * 2
        val radius = minSize / 2f
        val output = pool.get(minSize, minSize, input.safeConfig)
        output.applyCanvas {
            drawCircle(radius, radius, radius, paint)
            paint.xfermode = XFERMODE
            drawBitmap(input, radius - input.width / 2f, borderRadius, paint)
            drawCircle(radius, radius, radius - borderRadius / 2f, borderPaint)
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

@Composable
@Preview
internal fun RiderRow(rider: Rider = Rider.Preview) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Image(
            modifier = Modifier
                .padding(start = 5.dp, end = 5.dp)
                .size(100.dp, 100.dp),
            painter = rememberImagePainter(data = rider.photo, builder = {
                transformations(CustomCircleCropTransformation())
                crossfade(true)
            }),
            contentDescription = null,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically),
        ) {
            Text(
                text = "${rider.lastName.uppercase()} ${rider.firstName}",
                style = MaterialTheme.typography.body1,
            )
            Country(countryCode = rider.country, modifier = Modifier.align(Alignment.CenterEnd))
        }
    }
}

@Composable
internal fun Country(countryCode: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = "${getEmoji(Country(countryCode))} $countryCode",
        style = MaterialTheme.typography.body1,
    )
}
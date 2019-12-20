package com.transferwise.assignment.moviediscover.databinding

import android.net.Uri
import android.view.View
import androidx.databinding.BindingAdapter
import com.facebook.drawee.view.SimpleDraweeView
import android.view.ViewGroup
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.request.ImageRequestBuilder
import rjsv.circularview.CircleView


@BindingAdapter("imageUrl")
fun setImageUrl(view: SimpleDraweeView, imageUrl: String)  {
    val controller = Fresco.newDraweeControllerBuilder()
        .setImageRequest(
            ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(imageUrl))
                .build())
        .build()
    view.controller = controller
}

@BindingAdapter("layout_marginLeft")
fun setLayoutMarginLeft(view: View, marginLeft: Float) {
    if (view.layoutParams is ViewGroup.MarginLayoutParams) {
        val p = view.layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(marginLeft.toInt(), p.topMargin, p.rightMargin, p.bottomMargin)
        view.requestLayout()
    }
}

@BindingAdapter("value")
fun setRatingValue(view: CircleView, value: Float) {
    view.progressValue = value
    view.isEnabled = false
}
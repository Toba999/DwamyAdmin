package com.dev.dwamyadmin.utils

import android.content.Context
import android.widget.ImageView
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation

object ImageLoaderUtil {

    fun loadRemoteImage(
        imageView: ImageView,
        url: String,
        placeholder: Int? = null,
        error: Int? = null,
        isCircular: Boolean = false
    ) {
        imageView.load(url) {
            placeholder?.let { placeholder(it) }
            error?.let { error(it) }
            if (isCircular) transformations(CircleCropTransformation())
        }
    }

    fun loadLocalImage(
        imageView: ImageView,
        resourceId: Int,
        isCircular: Boolean = false
    ) {
        imageView.load(resourceId) {
            if (isCircular) transformations(CircleCropTransformation())
        }
    }

    fun loadImageWithCustomLoader(
        context: Context,
        imageView: ImageView,
        data: Any,
        placeholder: Int? = null,
        error: Int? = null
    ) {
        val imageLoader = ImageLoader.Builder(context).build()

        val request = ImageRequest.Builder(context)
            .data(data)
            .target(imageView)
            .apply {
                placeholder?.let { placeholder(it) }
                error?.let { error(it) }
            }
            .build()

        imageLoader.enqueue(request)
    }
}

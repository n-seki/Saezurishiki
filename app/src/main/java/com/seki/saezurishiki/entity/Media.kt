package com.seki.saezurishiki.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class Media : Parcelable {
    abstract val thumbnail: String
    companion object {
        @JvmStatic
        fun from(url: String, thumbnailUrl: String? = null, mediaType: String): Media {
            return when (mediaType) {
                "video" -> Video(url, thumbnailUrl ?: "")
                "animated_gif" -> AnimatedGif(url)
                else -> Photo(url)
            }
        }

        @JvmStatic
        fun mapToUrl(medias: List<Media>) = medias.map { it.thumbnail }
    }
}

@Parcelize data class Photo(override val thumbnail: String) : Media()

@Parcelize data class Video(val videoUrl: String, override val thumbnail: String) : Media()

@Parcelize data class AnimatedGif(override val thumbnail: String) : Media()

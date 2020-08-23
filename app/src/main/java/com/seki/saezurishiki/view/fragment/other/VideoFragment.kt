package com.seki.saezurishiki.view.fragment.other

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.seki.saezurishiki.BuildConfig
import com.seki.saezurishiki.R
import com.seki.saezurishiki.entity.Video
import com.seki.saezurishiki.view.fragment.util.DataType

class VideoFragment : Fragment() {

    private lateinit var media: Video

    companion object {
        @JvmStatic
        fun getInstance(media: Video): Fragment {
            return VideoFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(DataType.MEDIA, media)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        media = arguments?.getParcelable(DataType.MEDIA)
                ?: throw IllegalStateException("Not found media")
    }

    override fun onCreateView(layoutInflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = layoutInflater.inflate(R.layout.fragment_video, container, false)
        prepareVideo(view.findViewById(R.id.video_view))
        return view
    }

    private fun prepareVideo(playerView: PlayerView) {
        val dataSourceFactory = DefaultHttpDataSourceFactory(BuildConfig.APPLICATION_ID)
        val videoSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(media.videoUrl))
        val exoPlayer = ExoPlayerFactory.newSimpleInstance(playerView.context).apply {
            prepare(videoSource)
        }
        playerView.player = exoPlayer
    }
}
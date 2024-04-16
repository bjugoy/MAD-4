package com.example.movieappmad24.screens

import android.content.Context
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SimpleExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.example.movieappmad24.R
import com.example.movieappmad24.viewmodels.MoviesViewModel
import com.example.movieappmad24.widgets.HorizontalScrollableImageView
import com.example.movieappmad24.widgets.MovieRow
import com.example.movieappmad24.widgets.SimpleTopAppBar
import android.net.Uri
import androidx.compose.foundation.layout.aspectRatio

@Composable
fun DetailScreen(
    movieId: String?,
    navController: NavController,
    moviesViewModel: MoviesViewModel
) {
    val context = LocalContext.current

    // Get the movie object from the ViewModel based on movieId
    val movie = movieId?.let { moviesViewModel.movies.find { it.id == movieId } }

    movie?.let {
        Scaffold (
            topBar = {
                SimpleTopAppBar(title = movie.title) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                }
            }
        ){ innerPadding ->
            Column {
                MovieRow(modifier = Modifier.padding(innerPadding), movie = movie)
                HorizontalScrollableImageView(movie = movie)
                // Display ExoPlayer component for the movie trailer
                MovieTrailerPlayer(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    context = context,
                    trailerUrl = "android.resource://${context.packageName}/${R.raw.trailer_placeholder}")
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun MovieTrailerPlayer(
    modifier: Modifier = Modifier,
    context: Context,
    trailerUrl: String
) {

    // Create MediaItem from the trailer URL
    val mediaItem = remember(trailerUrl) {
        MediaItem.fromUri(Uri.parse(trailerUrl))
    }

    // Initialize ExoPlayer
    val player = remember(context) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }


    // Handle lifecycle events
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = player) {
        val observer = object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                player.play()
            }

            override fun onStop(owner: LifecycleOwner) {
                player.pause()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                player.release()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Set the media item to be played
    player.setMediaItem(mediaItem)

    // Prepare the player
    player.prepare()

    // ExoPlayerView composable
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(18f/9f),
        factory = {
            PlayerView(context).also {playerView ->
                playerView.player = player
            }
        }
    )
}



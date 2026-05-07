package com.scto.mcs.feature.settings.extension

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.scto.mcs.core.extension.Extension
import com.scto.mcs.core.extension.Review
import com.scto.mcs.core.resources.R
import com.scto.mcs.core.ui.components.StateScreen

sealed interface ReviewsStatus {
    object Loading : ReviewsStatus

    sealed class Error(val stringRes: Int, val drawableRes: Int) : ReviewsStatus {
        object Network : Error(R.strings.network_err, R.drawables.cloud_off)

        object Unknown : Error(R.strings.unknown_err, R.drawables.error)

        object NotSupported : Error(R.strings.reviews_not_supported, R.drawables.comment)
    }

    data class Success(val reviews: List<Review>) : ReviewsStatus
}

@Composable
fun ReviewsPage(extension: Extension, refreshKey: Int, onLoaded: () -> Unit, modifier: Modifier = Modifier) {
    var state by remember(extension) { mutableStateOf<ReviewsStatus>(ReviewsStatus.Loading) }

    LaunchedEffect(extension, refreshKey) {
        state = ReviewsStatus.Loading
        // TODO: Implement
        state = ReviewsStatus.Error.NotSupported
        onLoaded()
    }

    AnimatedContent(targetState = state, modifier = modifier.fillMaxWidth()) { state ->
        when (state) {
            ReviewsStatus.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            ReviewsStatus.Error -> {
                val color =
                    when (state) {
                        is ReviewsStatus.Error.NotSupported -> LocalContentColor.current
                        else -> MaterialTheme.colorScheme.error
                    }
                StateScreen(
                    painter = painterResource(state.drawableRes),
                    text = stringResource(state.stringRes),
                    color = color,
                )
            }

            is ReviewsStatus.Success -> {}
        }
    }
}

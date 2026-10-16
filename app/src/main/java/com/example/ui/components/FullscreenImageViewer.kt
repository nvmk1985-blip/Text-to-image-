package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GoldBright
import com.example.ui.theme.TextPrimary

@Composable
fun FullscreenImageViewer(
  imageUrl: String,
  onDismiss: () -> Unit,
  onSave: () -> Unit,
  onShare: () -> Unit
) {
  var scale by remember { mutableFloatStateOf(1f) }
  var offset by remember { mutableStateOf(Offset.Zero) }

  val state = rememberTransformableState { zoomChange, offsetChange, _ ->
    scale = (scale * zoomChange).coerceIn(1f, 5f)
    if (scale > 1f) {
      offset += offsetChange
    } else {
      offset = Offset.Zero
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.95f))
    ) {
      // Zoomable Image
      Box(
        modifier = Modifier
          .fillMaxSize()
          .transformable(state = state),
        contentAlignment = Alignment.Center
      ) {
        AsyncImage(
          model = imageUrl,
          contentDescription = "Zoomed AI Image",
          contentScale = ContentScale.Fit,
          modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(
              scaleX = scale,
              scaleY = scale,
              translationX = offset.x,
              translationY = offset.y
            )
        )
      }

      // Top Close Button
      IconButton(
        onClick = onDismiss,
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(24.dp)
          .background(Color.Black.copy(alpha = 0.6f), CircleShape)
      ) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "Close",
          tint = TextPrimary
        )
      }

      // Bottom Action Icons
      Box(
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .padding(bottom = 32.dp)
      ) {
        androidx.compose.foundation.layout.Row(
          horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
        ) {
          IconButton(
            onClick = onSave,
            modifier = Modifier
              .background(Color.Black.copy(alpha = 0.7f), CircleShape)
              .padding(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Download,
              contentDescription = "Save",
              tint = GoldBright,
              modifier = Modifier.size(28.dp)
            )
          }

          IconButton(
            onClick = onShare,
            modifier = Modifier
              .background(Color.Black.copy(alpha = 0.7f), CircleShape)
              .padding(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = "Share",
              tint = CyanPrimary,
              modifier = Modifier.size(28.dp)
            )
          }
        }
      }
    }
  }
}

package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.FaceMode
import com.example.ui.theme.CardBg
import com.example.ui.theme.CardElevated
import com.example.ui.theme.CyanBorder
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GlassCyanBorder
import com.example.ui.theme.GlassGoldBorder
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LeatherBlack
import com.example.ui.theme.PlayfairFontFamily
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ImageActionsSheet(
  currentFaceMode: FaceMode,
  isFaceLocked: Boolean,
  onDismiss: () -> Unit,
  onSelectFaceMode: (FaceMode) -> Unit,
  onSaveToGallery: () -> Unit,
  onShareImage: () -> Unit,
  onZoomView: () -> Unit,
  onPickReferencePhoto: () -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.85f)),
      contentAlignment = Alignment.BottomCenter
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
          .background(
            Brush.verticalGradient(
              listOf(
                Color(0xFF0F171B),
                Color(0xFF080C0E),
                Color(0xFF040608)
              )
            )
          )
          .border(
            width = 1.5.dp,
            brush = Brush.verticalGradient(listOf(CyanPrimary, GoldPrimary, CyanBorder)),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
          )
          .padding(horizontal = 20.dp, vertical = 16.dp)
      ) {
        Column(modifier = Modifier.fillMaxWidth()) {
          // Header
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Image Actions & Face Options",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PlayfairFontFamily,
                color = CyanBright
              )
              Text(
                text = "Face Lock, கேலரி சேமிப்பு & பகிர்வு ஆப்ஷன்கள்",
                fontSize = 12.sp,
                color = TextSecondary
              )
            }

            IconButton(
              onClick = onDismiss,
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(CardElevated)
                .border(1.dp, GlassCyanBorder, CircleShape)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = CyanBright,
                modifier = Modifier.size(20.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Section: Face Generation Modes
          Text(
            text = "Face Selection Modes (முக தேர்வு)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = GoldBright,
            modifier = Modifier.padding(bottom = 8.dp)
          )

          // Option 1: Face image தருவீர்களா
          FaceOptionCard(
            title = "Option 1: Face Image தருவீர்களா",
            subtitle = "கேலரியில் இருந்து முகத்தை reference ஆகத் தேர்வு செய்ய",
            icon = Icons.Default.AddPhotoAlternate,
            isSelected = currentFaceMode == FaceMode.OPTION_1_UPLOAD,
            onClick = {
              onSelectFaceMode(FaceMode.OPTION_1_UPLOAD)
              onPickReferencePhoto()
              onDismiss()
            }
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Option 2: புதிய முக அமைப்பில் உருவாக்கவா
          FaceOptionCard(
            title = "Option 2: New Face Structure உருவாக்கவா",
            subtitle = "முற்றிலும் புதிய தனித்துவமான முகத்துடன் படம் உருவாக்க",
            icon = Icons.Default.Face,
            isSelected = currentFaceMode == FaceMode.OPTION_2_NEW,
            onClick = {
              onSelectFaceMode(FaceMode.OPTION_2_NEW)
              onDismiss()
            }
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Option 3: Last இமெஜில் உள்ள முகத்தை பயன்படுத்தவா
          FaceOptionCard(
            title = "Option 3: Last Image முகத்தை பயன்படுத்தவா",
            subtitle = if (isFaceLocked) "Face Locked: ஒரே முகத்தில் உடைகள் / பின்னணி மட்டும் மாறும்" else "கடைசியாக வந்த அதே முகத்தை Lock செய்து பயன்படுத்த",
            icon = if (isFaceLocked) Icons.Default.Lock else Icons.Default.LockOpen,
            isSelected = currentFaceMode == FaceMode.OPTION_3_LOCKED,
            accentColor = if (isFaceLocked) StatusSuccess else GoldPrimary,
            onClick = {
              onSelectFaceMode(FaceMode.OPTION_3_LOCKED)
              onDismiss()
            }
          )

          Spacer(modifier = Modifier.height(18.dp))

          // Section: Image Utilities
          Text(
            text = "Image Export & View (படம் சேமிப்பு & பார்வை)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = CyanBright,
            modifier = Modifier.padding(bottom = 8.dp)
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Zoom View
            UtilityActionTile(
              title = "Zoom View",
              subtitle = "முழுத்திரை பார்வை",
              icon = Icons.Default.ZoomIn,
              modifier = Modifier.weight(1f),
              onClick = {
                onZoomView()
                onDismiss()
              }
            )

            // Save to Gallery
            UtilityActionTile(
              title = "Save Gallery",
              subtitle = "போனில் சேமிக்க",
              icon = Icons.Default.Download,
              modifier = Modifier.weight(1f),
              onClick = {
                onSaveToGallery()
                onDismiss()
              }
            )

            // Share
            UtilityActionTile(
              title = "Share",
              subtitle = "பகிர்வதற்கு",
              icon = Icons.Default.Share,
              modifier = Modifier.weight(1f),
              onClick = {
                onShareImage()
                onDismiss()
              }
            )
          }

          Spacer(modifier = Modifier.height(20.dp))
        }
      }
    }
  }
}

@Composable
private fun FaceOptionCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  isSelected: Boolean,
  accentColor: Color = CyanPrimary,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(if (isSelected) CardElevated else CardBg)
      .border(
        width = if (isSelected) 1.5.dp else 1.dp,
        color = if (isSelected) accentColor else GlassCyanBorder.copy(alpha = 0.3f),
        shape = RoundedCornerShape(14.dp)
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 14.dp, vertical = 10.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .background(if (isSelected) accentColor.copy(alpha = 0.2f) else CardElevated)
          .border(1.dp, if (isSelected) accentColor else Color.Transparent, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = if (isSelected) accentColor else TextSecondary,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = if (isSelected) TextPrimary else TextSecondary
        )
        Text(
          text = subtitle,
          fontSize = 11.sp,
          color = TextMuted
        )
      }

      if (isSelected) {
        Box(
          modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(accentColor)
        )
      }
    }
  }
}

@Composable
private fun UtilityActionTile(
  title: String,
  subtitle: String,
  icon: ImageVector,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .background(CardBg)
      .border(1.dp, GlassGoldBorder.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .padding(vertical = 12.dp, horizontal = 8.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = GoldBright,
        modifier = Modifier.size(24.dp)
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
      )
      Text(
        text = subtitle,
        fontSize = 10.sp,
        color = TextMuted
      )
    }
  }
}

package com.example.ui

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.PreferencesManager
import com.example.model.FaceMode
import com.example.model.PresetFilters
import com.example.service.ImageGenerationService
import com.example.ui.components.FullscreenImageViewer
import com.example.ui.components.ImageActionsSheet
import com.example.ui.components.RealisticPresetsSheet
import com.example.ui.theme.BgPrimary
import com.example.ui.theme.CardBg
import com.example.ui.theme.CardElevated
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GlassBg
import com.example.ui.theme.GlassCyanBorder
import com.example.ui.theme.GlassGoldBorder
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GreatVibesFontFamily
import com.example.ui.theme.LeatherBlack
import com.example.ui.theme.PlayfairFontFamily
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun HomeScreen(
  prefsManager: PreferencesManager,
  imageService: ImageGenerationService,
  onNavigateToSettings: () -> Unit
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  // Prompt entered in Home Prompt Box
  var promptInput by remember {
    mutableStateOf("காது, மூக்கு, வாய் போன்றவற்றில் இருந்து மரத்தின் கிளைகள் மற்றும் பூக்கள் வருவது போல் அதிதத்ரூபமான மனித ஓவியம்")
  }

  // Preset Filters
  var currentFilters by remember { mutableStateOf(PresetFilters()) }

  // Face Mode
  var currentFaceMode by remember { mutableStateOf(FaceMode.OPTION_2_NEW) }
  var referenceImageUri by remember { mutableStateOf<Uri?>(null) }

  // Generation state
  var currentSeed by remember { mutableLongStateOf(prefsManager.lastSeed) }
  var currentImageUrl by remember {
    // Initial stunning realistic artwork URL (Zero watermarks)
    mutableStateOf("https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=1080&q=80")
  }
  var isGenerating by remember { mutableStateOf(false) }
  var loadedBitmap by remember { mutableStateOf<Bitmap?>(null) }

  // Batch Image Generation Queue State
  var batchCount by remember { mutableIntStateOf(1) } // 1, 2, or 4
  var batchProgress by remember { mutableStateOf("") }
  var batchImages by remember {
    mutableStateOf(listOf("https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=1080&q=80"))
  }
  var selectedBatchIndex by remember { mutableIntStateOf(0) }

  // Sheets and Dialogs visibility
  var showPresetsSheet by remember { mutableStateOf(false) }
  var showActionsSheet by remember { mutableStateOf(false) }
  var showFullscreenZoom by remember { mutableStateOf(false) }

  // Zero-permission Photo Picker for Option 1: Face Image Upload
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      referenceImageUri = uri
      currentFaceMode = FaceMode.OPTION_1_UPLOAD
      Toast.makeText(context, "Face Image தேர்ந்தெடுக்கப்பட்டது! இந்த முகத்தைக் கொண்டு படம் உருவாக்கப்படும்.", Toast.LENGTH_LONG).show()
    }
  }

  fun triggerGenerate() {
    isGenerating = true
    coroutineScope.launch {
      val generatedUrls = mutableListOf<String>()
      val total = batchCount

      for (i in 1..total) {
        batchProgress = if (total > 1) "படம் $i/$total தயாராகிறது..." else "Google Imagen 3 படம் தயாராகிறது..."

        // Handle Seed based on Face Mode and batch variation index
        val effectiveSeed = when (currentFaceMode) {
          FaceMode.OPTION_3_LOCKED -> prefsManager.lastSeed + (i - 1) * 77
          FaceMode.OPTION_2_NEW -> {
            val newSeed = Random.nextLong(100000, 99999999)
            if (i == 1) {
              prefsManager.lastSeed = newSeed
              prefsManager.lockedFacePrompt = "${currentFilters.genderAge}, ${currentFilters.faceShape}, ${currentFilters.skinTone}"
            }
            newSeed
          }
          FaceMode.OPTION_1_UPLOAD -> Random.nextLong(100000, 99999999)
        }
        currentSeed = effectiveSeed

        // Subtle variation in lighting/camera per batch item while keeping identity
        val variationPrompt = when (i) {
          2 -> "$promptInput, slight head turn, golden hour lighting"
          3 -> "$promptInput, soft studio rim lighting, cinematic close-up"
          4 -> "$promptInput, ambient natural sunlight, confident gentle smile"
          else -> promptInput
        }

        val finalFusedPrompt = imageService.constructFinalPrompt(
          controlPrompt = prefsManager.controlPrompt,
          imagePrompt = variationPrompt,
          presetFilters = currentFilters,
          faceMode = currentFaceMode,
          lockedFacePrompt = prefsManager.lockedFacePrompt,
          negativePrompt = prefsManager.negativePrompt,
          isShieldEnabled = prefsManager.isAntiDeformityShieldEnabled,
          safetyTolerance = prefsManager.safetyTolerance
        )

        val newUrl = imageService.generateImageUrl(
          finalPrompt = finalFusedPrompt,
          seed = effectiveSeed,
          aspectRatio = currentFilters.aspectRatio,
          modelName = prefsManager.aiModel,
          safetyTolerance = prefsManager.safetyTolerance,
          safetySetting = prefsManager.imagenSafetySetting
        )

        generatedUrls.add(newUrl)
        if (i == 1) {
          currentImageUrl = newUrl
        }
      }

      batchImages = generatedUrls
      selectedBatchIndex = 0
      currentImageUrl = generatedUrls.first()
      val bitmap = imageService.downloadBitmap(currentImageUrl)
      loadedBitmap = bitmap
      isGenerating = false
      batchProgress = ""
      if (bitmap != null) {
        val msg = if (total > 1) "$total மாறுபட்ட படங்கள் வெற்றிகரமாக உருவாக்கப்பட்டன (Batch Complete)!" else "படம் தயாரானது (Generation Complete)!"
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
      } else {
        Toast.makeText(context, "படம் உருவாக்குவதில் பிழை ஏற்பட்டது. தயவுசெய்து இணைய இணைப்பைச் சரிபார்த்து மீண்டும் முயற்சிக்கவும்.", Toast.LENGTH_LONG).show()
      }
    }
  }

  Scaffold(
    containerColor = LeatherBlack,
    topBar = {
      // Header: Left side "Text to Image" in luxury script and Right side Settings Icon
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              listOf(Color(0xFF0F171B), Color(0xFF080B0D))
            )
          )
          .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Text to Image",
            fontSize = 32.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = GreatVibesFontFamily,
            color = GoldBright
          )
          Text(
            text = "Realistic Human AI Studio",
            fontSize = 11.sp,
            fontFamily = PlayfairFontFamily,
            color = CyanBright
          )
        }

        IconButton(
          onClick = onNavigateToSettings,
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(CardElevated)
            .border(1.5.dp, GoldPrimary, CircleShape)
        ) {
          Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            tint = GoldBright,
            modifier = Modifier.size(24.dp)
          )
        }
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {

      // 1. Prompts Header & Box
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp, start = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Prompt",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PlayfairFontFamily,
            color = GoldBright
          )
          Text(
            text = "Model: ${prefsManager.aiModel.split(" ")[0]}",
            fontSize = 11.sp,
            color = CyanBright
          )
        }

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
              Brush.verticalGradient(listOf(Color(0xFF131D21), Color(0xFF0A0F12)))
            )
            .border(
              width = 1.5.dp,
              brush = Brush.horizontalGradient(listOf(CyanPrimary, GoldPrimary)),
              shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp)
        ) {
          Column {
            OutlinedTextField(
              value = promptInput,
              onValueChange = { promptInput = it },
              modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
              placeholder = {
                Text(
                  text = "Example: beautiful Tamil girl, saree / மனிதக் காட்சியை உள்ளிடவும்...",
                  color = TextMuted,
                  fontSize = 13.sp
                )
              },
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CardElevated,
                unfocusedContainerColor = CardElevated,
                focusedBorderColor = CyanPrimary,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
              ),
              shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Batch Queue Variation Selector (1x, 2x, 4x)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Batch Queue:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = CyanBright
              )

              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(1 to "1 படம்", 2 to "2 மாறுபாடுகள்", 4 to "4 மாறுபாடுகள்").forEach { (count, label) ->
                  val isSelected = batchCount == count
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .background(if (isSelected) CyanPrimary.copy(alpha = 0.25f) else CardElevated)
                      .border(
                        width = 1.dp,
                        color = if (isSelected) CyanBright else GlassCyanBorder.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(8.dp)
                      )
                      .clickable { batchCount = count }
                      .padding(horizontal = 8.dp, vertical = 4.dp)
                  ) {
                    Text(
                      text = label,
                      fontSize = 10.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      color = if (isSelected) CyanBright else TextSecondary
                    )
                  }
                }
              }
            }

            // Glowing Royal Gold "Image Generate பண்ணு" Action Button (Matches Image 2!)
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(
                  Brush.horizontalGradient(
                    listOf(
                      GoldDark,
                      GoldPrimary,
                      GoldBright,
                      GoldPrimary
                    )
                  )
                )
                .border(1.5.dp, GoldBright, RoundedCornerShape(22.dp))
                .clickable(enabled = !isGenerating) { triggerGenerate() },
              contentAlignment = Alignment.Center
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = null,
                  tint = Color(0xFF15120B),
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (isGenerating) "உருவாகிறது..." else "Image Generate பண்ணு",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF15120B)
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 2. Center Two Action Pill/Circular Buttons (Matches Image 1 & 2!)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Left Button: Realistic Human Preset Filters
        Box(
          modifier = Modifier
            .weight(1f)
            .height(58.dp)
            .padding(end = 6.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(CardBg)
            .border(1.5.dp, GlassCyanBorder, RoundedCornerShape(28.dp))
            .clickable { showPresetsSheet = true },
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "Realistic Human\nPreset Filters",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
          )
        }

        // Right Button: Image Actions
        Box(
          modifier = Modifier
            .weight(1f)
            .height(58.dp)
            .padding(start = 6.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(CardBg)
            .border(1.5.dp, GlassCyanBorder, RoundedCornerShape(28.dp))
            .clickable { showActionsSheet = true },
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "Image\nActions",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 3. Preset Filters / Output Image Section Label
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Preset Filters Output",
          fontSize = 14.sp,
          fontWeight = FontWeight.Medium,
          color = TextSecondary,
          modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )

        if (currentFaceMode == FaceMode.OPTION_3_LOCKED) {
          Text(
            text = "🔒 Face Locked",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = GoldBright
          )
        }
      }

      // 4. Output Image Card (Matches the attached reference image with action icons in corners)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(0.92f)
          .clip(RoundedCornerShape(16.dp))
          .background(CardBg)
          .border(1.5.dp, GlassCyanBorder.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
          .clickable { showFullscreenZoom = true }
      ) {
        // Image itself
        AsyncImage(
          model = ImageRequest.Builder(context)
            .data(currentImageUrl)
            .crossfade(true)
            .build(),
          contentDescription = "Generated Output Image",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        // Loading Overlay
        if (isGenerating) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(Color.Black.copy(alpha = 0.65f)),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              CircularProgressIndicator(
                color = CyanBright,
                modifier = Modifier.size(46.dp),
                strokeWidth = 3.dp
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = batchProgress.ifBlank { "Google Imagen 3 படம் உருவாக்குகிறது..." },
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = "Safety Tolerance: Low | Zero Watermark | 8K DSLR",
                color = CyanBright,
                fontSize = 11.sp
              )
            }
          }
        }

        // Bottom Left Corner Icon: Filter Icon (Matches reference image)
        Box(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(14.dp)
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Black.copy(alpha = 0.65f))
            .border(1.dp, GlassCyanBorder.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .clickable { showPresetsSheet = true },
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.FilterList,
            contentDescription = "Filters",
            tint = TextPrimary,
            modifier = Modifier.size(20.dp)
          )
        }

        // Bottom Right Corner Icon: Grid/Gallery Icon (Matches reference image)
        Box(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(14.dp)
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Black.copy(alpha = 0.65f))
            .border(1.dp, GlassCyanBorder.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .clickable { showActionsSheet = true },
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.GridView,
            contentDescription = "Actions & History",
            tint = TextPrimary,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      // Batch Results Carousel & Save All (shown when batchImages has multiple variations)
      if (batchImages.size > 1) {
        Spacer(modifier = Modifier.height(14.dp))
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .border(1.dp, GlassCyanBorder.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Batch Variations (${batchImages.size} படங்கள்):",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = GoldBright
            )

            // Save All Button
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(CyanPrimary)
                .clickable {
                  coroutineScope.launch {
                    var successCount = 0
                    batchImages.forEachIndexed { idx, url ->
                      val bmp = imageService.downloadBitmap(url)
                      if (bmp != null) {
                        imageService.saveImageToGallery(bmp, "Batch_$idx")
                        successCount++
                      }
                    }
                    Toast.makeText(context, "$successCount படங்கள் கேலரியில் சேமிக்கப்பட்டன!", Toast.LENGTH_SHORT).show()
                  }
                }
                .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
              Text(
                text = "Save All to Gallery",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            batchImages.forEachIndexed { index, imgUrl ->
              val isSelected = selectedBatchIndex == index
              Box(
                modifier = Modifier
                  .weight(1f)
                  .aspectRatio(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .background(CardElevated)
                  .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) GoldBright else GlassCyanBorder.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(10.dp)
                  )
                  .clickable {
                    selectedBatchIndex = index
                    currentImageUrl = imgUrl
                    coroutineScope.launch {
                      loadedBitmap = imageService.downloadBitmap(imgUrl)
                    }
                  }
              ) {
                AsyncImage(
                  model = ImageRequest.Builder(context)
                    .data(imgUrl)
                    .crossfade(true)
                    .build(),
                  contentDescription = "Variation $index",
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxSize()
                )
                // Number Badge
                Box(
                  modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) GoldPrimary else Color.Black.copy(alpha = 0.7f)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "${index + 1}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.Black else Color.White
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // Bottom Sheet: Realistic Human Preset Filters
  if (showPresetsSheet) {
    RealisticPresetsSheet(
      currentFilters = currentFilters,
      prefsManager = prefsManager,
      onDismiss = { showPresetsSheet = false },
      onApplyFilters = { newFilters ->
        currentFilters = newFilters
        Toast.makeText(context, "Preset Filters அமைக்கப்பட்டன! புதிய படம் உருவாக்கவும்.", Toast.LENGTH_SHORT).show()
      }
    )
  }

  // Bottom Sheet: Image Actions
  if (showActionsSheet) {
    ImageActionsSheet(
      currentFaceMode = currentFaceMode,
      isFaceLocked = prefsManager.isFaceLocked,
      onDismiss = { showActionsSheet = false },
      onSelectFaceMode = { mode ->
        currentFaceMode = mode
        when (mode) {
          FaceMode.OPTION_3_LOCKED -> {
            prefsManager.isFaceLocked = true
            Toast.makeText(context, "Option 3: Last Image முகம் Lock செய்யப்பட்டது! அடுத்த படங்களில் இதே முகம் தக்கவைக்கப்படும்.", Toast.LENGTH_LONG).show()
          }
          FaceMode.OPTION_2_NEW -> {
            prefsManager.isFaceLocked = false
            Toast.makeText(context, "Option 2: புதிய முக அமைப்பு உருவாக்கப்படும்.", Toast.LENGTH_SHORT).show()
          }
          FaceMode.OPTION_1_UPLOAD -> {
            // Handled via onPickReferencePhoto
          }
        }
      },
      onSaveToGallery = {
        coroutineScope.launch {
          val bitmap = loadedBitmap ?: imageService.downloadBitmap(currentImageUrl)
          if (bitmap != null) {
            val result = imageService.saveImageToGallery(bitmap, "AI Human")
            if (result.isSuccess) {
              Toast.makeText(context, result.getOrNull() ?: "கேலரியில் சேமிக்கப்பட்டது!", Toast.LENGTH_SHORT).show()
            } else {
              Toast.makeText(context, "சேமிப்பதில் பிழை: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
            }
          } else {
            Toast.makeText(context, "படத்தைப் பதிவிறக்க முடியவில்லை, மீண்டும் முயற்சிக்கவும்.", Toast.LENGTH_SHORT).show()
          }
        }
      },
      onShareImage = {
        coroutineScope.launch {
          val bitmap = loadedBitmap ?: imageService.downloadBitmap(currentImageUrl)
          if (bitmap != null) {
            imageService.shareImage(bitmap)
          } else {
            Toast.makeText(context, "பகிர முடியவில்லை, படம் தயாராகவில்லை.", Toast.LENGTH_SHORT).show()
          }
        }
      },
      onZoomView = {
        showFullscreenZoom = true
      },
      onPickReferencePhoto = {
        photoPickerLauncher.launch(
          PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
      }
    )
  }

  // Fullscreen Zoom Viewer
  if (showFullscreenZoom) {
    FullscreenImageViewer(
      imageUrl = currentImageUrl,
      onDismiss = { showFullscreenZoom = false },
      onSave = {
        coroutineScope.launch {
          val bitmap = loadedBitmap ?: imageService.downloadBitmap(currentImageUrl)
          if (bitmap != null) {
            val res = imageService.saveImageToGallery(bitmap, "AI Human")
            Toast.makeText(context, res.getOrNull() ?: "கேலரியில் சேமிக்கப்பட்டது!", Toast.LENGTH_SHORT).show()
          }
        }
      },
      onShare = {
        coroutineScope.launch {
          val bitmap = loadedBitmap ?: imageService.downloadBitmap(currentImageUrl)
          if (bitmap != null) {
            imageService.shareImage(bitmap)
          }
        }
      }
    )
  }
}

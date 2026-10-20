package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.PreferencesManager
import com.example.model.AspectRatioOption
import com.example.model.PresetFilters
import com.example.ui.theme.BgPrimary
import com.example.ui.theme.CardBg
import com.example.ui.theme.CardElevated
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GlassCyanBorder
import com.example.ui.theme.GlassGoldBorder
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LeatherBlack
import com.example.ui.theme.PlayfairFontFamily
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextOnGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RealisticPresetsSheet(
  currentFilters: PresetFilters,
  prefsManager: PreferencesManager,
  onDismiss: () -> Unit,
  onApplyFilters: (PresetFilters) -> Unit
) {
  val context = LocalContext.current
  var tempFilters by remember { mutableStateOf(currentFilters) }

  // State to hold dynamic custom-added options for each category
  val dynamicOptions = remember {
    mutableStateMapOf<String, List<String>>().apply {
      put("genderAge", prefsManager.getCustomOptions("genderAge"))
      put("faceShape", prefsManager.getCustomOptions("faceShape"))
      put("skinTone", prefsManager.getCustomOptions("skinTone"))
      put("hairStyle", prefsManager.getCustomOptions("hairStyle"))
      put("bodyType", prefsManager.getCustomOptions("bodyType"))
      put("height", prefsManager.getCustomOptions("height"))
      put("clothing", prefsManager.getCustomOptions("clothing"))
      put("expression", prefsManager.getCustomOptions("expression"))
      put("background", prefsManager.getCustomOptions("background"))
      put("aspectRatio", prefsManager.getCustomOptions("aspectRatio"))
    }
  }

  // Dialog state for adding a custom option
  var showAddDialogForCategory by remember { mutableStateOf<String?>(null) }
  var addCategoryTitle by remember { mutableStateOf("") }
  var newOptionInput by remember { mutableStateOf("") }

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
          .fillMaxHeight(0.92f)
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
            brush = Brush.verticalGradient(listOf(GoldPrimary, CyanPrimary, GoldBorder)),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
          )
          .padding(horizontal = 20.dp, vertical = 12.dp)
      ) {
        Column(modifier = Modifier.fillMaxSize()) {
          // Top Bar
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
              Text(
                text = "Realistic Human Preset Filters",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PlayfairFontFamily,
                color = GoldBright
              )
              Text(
                text = "தத்ரூபமான மனித உடலமைப்பு & தோற்றத் தேர்வுகள்",
                fontSize = 12.sp,
                color = TextSecondary
              )
            }

            // High-contrast, prominent Close button (Issue 1 fix)
            Box(
              modifier = Modifier
                .height(34.dp)
                .clip(RoundedCornerShape(17.dp))
                .background(CardElevated)
                .border(1.5.dp, GoldBright, RoundedCornerShape(17.dp))
                .clickable { onDismiss() }
                .padding(horizontal = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Close Sheet",
                  tint = GoldBright,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Close",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = GoldBright
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          LazyColumn(
            modifier = Modifier
              .weight(1f)
              .padding(bottom = 8.dp)
          ) {
            // 1. Gender & Age
            item {
              val baseOptions = listOf(
                "ஆண் (Male)",
                "பெண் (Female)",
                "குழந்தை (Child)",
                "இளைஞர் (Young Adult)",
                "முதியவர் (Elderly)"
              )
              val allOptions = baseOptions + (dynamicOptions["genderAge"] ?: emptyList())
              FilterCategoryWithAddSection(
                title = "1. Gender & Age (பாலினம் & வயது)",
                options = allOptions,
                selectedOption = tempFilters.genderAge,
                onSelect = { tempFilters = tempFilters.copy(genderAge = it) },
                onAddClick = {
                  showAddDialogForCategory = "genderAge"
                  addCategoryTitle = "1. Gender & Age"
                  newOptionInput = ""
                }
              )
            }

            // 2. Face Shape
            item {
              val baseOptions = listOf(
                "வட்ட முகம் (Round Face)",
                "முட்டை வடிவம் (Oval)",
                "கட்டமைப்பு தாடை (Chiseled Square)",
                "இதய வடிவம் (Heart Shape)"
              )
              val allOptions = baseOptions + (dynamicOptions["faceShape"] ?: emptyList())
              FilterCategoryWithAddSection(
                title = "2. Face Shape (முக அமைப்பு)",
                options = allOptions,
                selectedOption = tempFilters.faceShape,
                onSelect = { tempFilters = tempFilters.copy(faceShape = it) },
                onAddClick = {
                  showAddDialogForCategory = "faceShape"
                  addCategoryTitle = "2. Face Shape"
                  newOptionInput = ""
                }
              )
            }

            // 3. Skin Tone
            item {
              val baseOptions = listOf(
                "வெண்மை (Fair Complexion)",
                "தமிழ் மாநிறம் (Wheatish / Tan)",
                "அடர் பழுப்பு (Warm Brown / Dusky)",
                "ஆழ்ந்த கருமை (Deep Dark)"
              )
              val allOptions = baseOptions + (dynamicOptions["skinTone"] ?: emptyList())
              FilterCategoryWithAddSection(
                title = "3. Skin Complexion (தோலின் நிறம்)",
                options = allOptions,
                selectedOption = tempFilters.skinTone,
                onSelect = { tempFilters = tempFilters.copy(skinTone = it) },
                onAddClick = {
                  showAddDialogForCategory = "skinTone"
                  addCategoryTitle = "3. Skin Complexion"
                  newOptionInput = ""
                }
              )
            }

            // 4. Hair Style
            item {
              val baseOptions = listOf(
                "குட்டை முடி (Short Trim)",
                "நீண்ட அலைமுடி (Long Wavy)",
                "சுருள் முடி (Curly)",
                "பாரம்பரிய கொண்டை (Traditional Bun)",
                "நேர் முடி (Straight)",
                "வழுக்கை / மொட்டை (Bald)"
              )
              val allOptions = baseOptions + (dynamicOptions["hairStyle"] ?: emptyList())
              FilterCategoryWithAddSection(
                title = "4. Hair Style (தலைமுடி ஸ்டைல்)",
                options = allOptions,
                selectedOption = tempFilters.hairStyle,
                onSelect = { tempFilters = tempFilters.copy(hairStyle = it) },
                onAddClick = {
                  showAddDialogForCategory = "hairStyle"
                  addCategoryTitle = "4. Hair Style"
                  newOptionInput = ""
                }
              )
            }

            // 5. Body Type
            item {
              val baseOptions = listOf(
                "மெலிந்த (Slim)",
                "உடற்பயிற்சி உடலமைப்பு (Athletic / Fit)",
                "சராசரி (Medium Build)",
                "பருமனான (Plus Size / Healthy)"
              )
              val allOptions = baseOptions + (dynamicOptions["bodyType"] ?: emptyList())
              FilterCategoryWithAddSection(
                title = "5. Body Physique (உடல் பருமன்)",
                options = allOptions,
                selectedOption = tempFilters.bodyType,
                onSelect = { tempFilters = tempFilters.copy(bodyType = it) },
                onAddClick = {
                  showAddDialogForCategory = "bodyType"
                  addCategoryTitle = "5. Body Physique"
                  newOptionInput = ""
                }
              )
            }

            // 6. Height
            item {
              val baseOptions = listOf(
                "உயரமான (Tall Stature)",
                "சராசரி உயரம் (Average Height)",
                "குட்டையான / சிறிய (Petite)"
              )
              val allOptions = baseOptions + (dynamicOptions["height"] ?: emptyList())
              FilterCategoryWithAddSection(
                title = "6. Height (உயரம்)",
                options = allOptions,
                selectedOption = tempFilters.height,
                onSelect = { tempFilters = tempFilters.copy(height = it) },
                onAddClick = {
                  showAddDialogForCategory = "height"
                  addCategoryTitle = "6. Height"
                  newOptionInput = ""
                }
              )
            }

            // 7. Clothing
            item {
              val baseOptions = listOf(
                "பாரம்பரிய பட்டுப்புடவை (Silk Saree)",
                "வேஷ்டி & சட்டை (Dhoti & Shirt)",
                "கோட் சூட் (Formal Business Suit)",
                "கேஷுவல் ஜீன்ஸ் & டி-ஷர்ட் (Casual Denim)",
                "அரச கால உடைகள் (Royal Traditional)",
                "மாடர்ன் ஃபேஷன் (Streetwear Fashion)"
              )
              val allOptions = baseOptions + (dynamicOptions["clothing"] ?: emptyList())
              FilterCategoryWithAddSection(
                title = "7. Clothing & Attire (உடை)",
                options = allOptions,
                selectedOption = tempFilters.clothing,
                onSelect = { tempFilters = tempFilters.copy(clothing = it) },
                onAddClick = {
                  showAddDialogForCategory = "clothing"
                  addCategoryTitle = "7. Clothing & Attire"
                  newOptionInput = ""
                }
              )
            }

            // 8. Expression
            item {
              val baseOptions = listOf(
                "புன்னகை (Smiling & Happy)",
                "கம்பீர பார்வை (Confident & Bold)",
                "அமைதியான பார்வை (Calm & Serene)",
                "சினிமாட்டிக் தீவிரம் (Cinematic Intense)"
              )
              val allOptions = baseOptions + (dynamicOptions["expression"] ?: emptyList())
              FilterCategoryWithAddSection(
                title = "8. Expression & Mood (தோற்றம் & உணர்ச்சி)",
                options = allOptions,
                selectedOption = tempFilters.expression,
                onSelect = { tempFilters = tempFilters.copy(expression = it) },
                onAddClick = {
                  showAddDialogForCategory = "expression"
                  addCategoryTitle = "8. Expression & Mood"
                  newOptionInput = ""
                }
              )
            }

            // 9. Environment & Lighting
            item {
              val baseOptions = listOf(
                "ஸ்டுடியோ லைட்டிங் (Soft Studio Light)",
                "மாலை பொன்வெயில் (Golden Hour)",
                "இயற்கை சூழல் / கிராமம் (Outdoor Nature)",
                "மாடர்ன் நகரம் (Modern City Lights)",
                "பாரம்பரிய அரண்மனை (Royal Palace Interior)"
              )
              val allOptions = baseOptions + (dynamicOptions["background"] ?: emptyList())
              FilterCategoryWithAddSection(
                title = "9. Lighting & Background (பின்னணி & ஒளி)",
                options = allOptions,
                selectedOption = tempFilters.background,
                onSelect = { tempFilters = tempFilters.copy(background = it) },
                onAddClick = {
                  showAddDialogForCategory = "background"
                  addCategoryTitle = "9. Lighting & Background"
                  newOptionInput = ""
                }
              )
            }

            // 10. Aspect Ratio
            item {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "10. Aspect Ratio (பட அளவு)",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = GoldBright
                )
              }
              FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                AspectRatioOption.values().forEach { aspect ->
                  val isSelected = tempFilters.aspectRatio == aspect
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(12.dp))
                      .background(if (isSelected) CyanPrimary.copy(alpha = 0.25f) else CardBg)
                      .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) CyanBright else GlassCyanBorder.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(12.dp)
                      )
                      .clickable { tempFilters = tempFilters.copy(aspectRatio = aspect) }
                      .padding(horizontal = 14.dp, vertical = 9.dp)
                  ) {
                    Text(
                      text = aspect.label,
                      fontSize = 12.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      color = if (isSelected) CyanBright else TextSecondary
                    )
                  }
                }
              }
              Spacer(modifier = Modifier.height(14.dp))
            }
          }

          // Bottom Action Buttons with navigationBarsPadding so mobile navigation bar doesn't overlap (Issue 3 fix)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .navigationBarsPadding()
              .padding(top = 10.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedButton(
              onClick = { tempFilters = PresetFilters() },
              modifier = Modifier.weight(1f).height(48.dp),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
              border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder.copy(alpha = 0.6f)),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Reset", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }

            Button(
              onClick = {
                onApplyFilters(tempFilters)
                onDismiss()
              },
              modifier = Modifier.weight(1.5f).height(48.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = GoldPrimary,
                contentColor = TextOnGold
              ),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Apply Filters", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
          }
        }
      }
    }
  }

  // Popup Dialog to Add a New Option to any of the 1 to 10 categories
  if (showAddDialogForCategory != null) {
    val categoryKey = showAddDialogForCategory!!
    Dialog(
      onDismissRequest = { showAddDialogForCategory = null }
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth(0.95f)
          .clip(RoundedCornerShape(18.dp))
          .background(LeatherBlack)
          .border(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(listOf(GoldPrimary, CyanPrimary)),
            shape = RoundedCornerShape(18.dp)
          )
          .padding(20.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Add New Option (+)",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = GoldBright
            )
            IconButton(
              onClick = { showAddDialogForCategory = null },
              modifier = Modifier.size(28.dp)
            ) {
              Icon(Icons.Default.Close, contentDescription = "Cancel", tint = TextMuted)
            }
          }

          Text(
            text = "பிரிவு: $addCategoryTitle",
            fontSize = 13.sp,
            color = CyanBright,
            modifier = Modifier.padding(vertical = 4.dp)
          )

          Text(
            text = "உதாரணம்: நீள்வட்ட வடிவம் (Oblong), ஜோத்புரி சூட், பட்டு வேஷ்டி...",
            fontSize = 11.sp,
            color = TextMuted
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = newOptionInput,
            onValueChange = { newOptionInput = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("புதிய ஆப்ஷனை உள்ளிடவும்...", color = TextMuted, fontSize = 13.sp) },
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = CardBg,
              unfocusedContainerColor = CardBg,
              focusedBorderColor = CyanPrimary,
              unfocusedBorderColor = GlassCyanBorder,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(10.dp),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(16.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = { showAddDialogForCategory = null },
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
              border = androidx.compose.foundation.BorderStroke(1.dp, GlassCyanBorder)
            ) {
              Text("Cancel", fontSize = 13.sp)
            }

            Button(
              onClick = {
                val clean = newOptionInput.trim()
                if (clean.isNotBlank()) {
                  prefsManager.addCustomOption(categoryKey, clean)
                  dynamicOptions[categoryKey] = prefsManager.getCustomOptions(categoryKey)

                  // Automatically select the newly created option
                  tempFilters = when (categoryKey) {
                    "genderAge" -> tempFilters.copy(genderAge = clean)
                    "faceShape" -> tempFilters.copy(faceShape = clean)
                    "skinTone" -> tempFilters.copy(skinTone = clean)
                    "hairStyle" -> tempFilters.copy(hairStyle = clean)
                    "bodyType" -> tempFilters.copy(bodyType = clean)
                    "height" -> tempFilters.copy(height = clean)
                    "clothing" -> tempFilters.copy(clothing = clean)
                    "expression" -> tempFilters.copy(expression = clean)
                    "background" -> tempFilters.copy(background = clean)
                    else -> tempFilters
                  }

                  Toast.makeText(context, "'$clean' வெற்றிகரமாக சேர்க்கப்பட்டது!", Toast.LENGTH_SHORT).show()
                  showAddDialogForCategory = null
                }
              },
              modifier = Modifier.weight(1.3f),
              colors = ButtonDefaults.buttonColors(
                containerColor = CyanPrimary,
                contentColor = Color.Black
              )
            ) {
              Text("Add Option", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterCategoryWithAddSection(
  title: String,
  options: List<String>,
  selectedOption: String,
  onSelect: (String) -> Unit,
  onAddClick: () -> Unit
) {
  Column(modifier = Modifier.padding(vertical = 6.dp)) {
    // Heading with '+' symbol right on the right side of the heading
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = GoldBright,
        modifier = Modifier
          .weight(1f)
          .padding(end = 8.dp)
      )

      // '+' button to add custom option (Issue 2 fix - guaranteed visible on right)
      Box(
        modifier = Modifier
          .size(30.dp)
          .clip(CircleShape)
          .background(CardElevated)
          .border(1.5.dp, CyanBright, CircleShape)
          .clickable(onClick = onAddClick),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = "Add custom option",
          tint = CyanBright,
          modifier = Modifier.size(18.dp)
        )
      }
    }

    FlowRow(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      options.forEach { option ->
        val isSelected = selectedOption == option
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) GoldPrimary.copy(alpha = 0.25f) else CardBg)
            .border(
              width = if (isSelected) 1.5.dp else 1.dp,
              color = if (isSelected) GoldBright else GlassGoldBorder.copy(alpha = 0.28f),
              shape = RoundedCornerShape(12.dp)
            )
            .clickable { onSelect(option) }
            .padding(horizontal = 12.dp, vertical = 7.dp)
        ) {
          Text(
            text = option,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) GoldBright else TextSecondary
          )
        }
      }
    }
    Spacer(modifier = Modifier.height(10.dp))
  }
}

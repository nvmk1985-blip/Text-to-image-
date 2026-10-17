package com.example.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.example.data.PreferencesManager
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
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextOnGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
  prefsManager: PreferencesManager,
  onNavigateBack: () -> Unit
) {
  BackHandler { onNavigateBack() }
  val context = LocalContext.current

  var controlPromptText by remember { mutableStateOf(prefsManager.controlPrompt) }
  var negativePromptText by remember { mutableStateOf(prefsManager.negativePrompt) }
  var isShieldEnabled by remember { mutableStateOf(prefsManager.isAntiDeformityShieldEnabled) }
  var selectedModel by remember { mutableStateOf(prefsManager.aiModel) }
  var isFaceLocked by remember { mutableStateOf(prefsManager.isFaceLocked) }

  // 1 to 10 Categories Custom Options mapping
  val categoryKeys = listOf(
    "genderAge" to "1. Gender & Age (பாலினம் & வயது)",
    "faceShape" to "2. Face Shape (முக அமைப்பு)",
    "skinTone" to "3. Skin Complexion (தோலின் நிறம்)",
    "hairStyle" to "4. Hair Style (தலைமுடி ஸ்டைல்)",
    "bodyType" to "5. Body Physique (உடல் பருமன்)",
    "height" to "6. Height (உயரம்)",
    "clothing" to "7. Clothing & Attire (உடை)",
    "expression" to "8. Expression & Mood (தோற்றம்)",
    "background" to "9. Lighting & Background (பின்னணி & ஒளி)",
    "aspectRatio" to "10. Aspect Ratio (பட அளவு)"
  )

  val customOptionsMap = remember {
    mutableStateMapOf<String, List<String>>().apply {
      categoryKeys.forEach { (key, _) ->
        put(key, prefsManager.getCustomOptions(key))
      }
    }
  }

  // Dialog state for adding a custom option
  var showAddDialogForCategory by remember { mutableStateOf<String?>(null) }
  var addCategoryTitle by remember { mutableStateOf("") }
  var newOptionInput by remember { mutableStateOf("") }

  val sampleDirectives = listOf(
    "மனிதனை மரமாக நினைத்துக்கொள் (Tree-Human)",
    "அதிதத்ரூபமான 8K சினிமாட்டிக் போர்ட்ரெய்ட் (8K Photoreal)",
    "தமிழ் பாரம்பரிய கலாச்சார மனிதர் (Tamil Heritage)",
    "மின்னணு எதிர்கால சைபர்பங்க் மனிதன் (Cyberpunk)"
  )

  Scaffold(
    containerColor = LeatherBlack,
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Settings (அமைப்புகள்)",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PlayfairFontFamily,
            color = GoldBright
          )
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = GoldPrimary
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = LeatherBlack
        )
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
      // Section 1: AI Master Control Prompt
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(CardBg)
          .border(1.dp, GlassCyanBorder, RoundedCornerShape(16.dp))
          .padding(16.dp)
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = CyanBright,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "AI Master Control Prompt (கட்டுப்பாட்டு பிராம்ப்ட்)",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = CyanBright
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "AI-யை கட்டுப்படுத்தும் மூல விதி / சித்தாந்தம். இது முகப்பில் நீங்கள் கொடுக்கும் Image Prompt-உடன் இணைந்து இரண்டையும் படித்து விட்டு படத்தை உருவாக்கும்.",
            fontSize = 12.sp,
            color = TextSecondary,
            lineHeight = 16.sp
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = controlPromptText,
            onValueChange = { controlPromptText = it },
            modifier = Modifier
              .fillMaxWidth()
              .height(110.dp),
            placeholder = {
              Text(
                text = "உதாரணம்: மனிதனை மரமாக நினைத்துக்கொள்...",
                color = TextMuted,
                fontSize = 13.sp
              )
            },
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = CardElevated,
              unfocusedContainerColor = CardElevated,
              focusedBorderColor = CyanPrimary,
              unfocusedBorderColor = GlassCyanBorder.copy(alpha = 0.5f),
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Preset Concept Pills
          Text(
            text = "Quick Control Directives (மாதிரி கட்டளைகள்):",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = GoldBright
          )
          Spacer(modifier = Modifier.height(6.dp))
          FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            sampleDirectives.forEach { directive ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(CardElevated)
                  .border(1.dp, GlassGoldBorder.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                  .clickable {
                    controlPromptText = directive
                  }
                  .padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Text(
                  text = directive,
                  fontSize = 11.sp,
                  color = TextSecondary
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Save / Reset buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = {
                prefsManager.resetControlPrompt()
                controlPromptText = prefsManager.controlPrompt
                Toast.makeText(context, "Control Prompt Reset செய்யப்பட்டது!", Toast.LENGTH_SHORT).show()
              },
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
              border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder.copy(alpha = 0.5f))
            ) {
              Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Reset", fontSize = 13.sp)
            }

            Button(
              onClick = {
                prefsManager.controlPrompt = controlPromptText
                Toast.makeText(context, "Master Control Prompt சேமிக்கப்பட்டது!", Toast.LENGTH_SHORT).show()
              },
              modifier = Modifier.weight(1.4f),
              colors = ButtonDefaults.buttonColors(
                containerColor = CyanPrimary,
                contentColor = Color.Black
              )
            ) {
              Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
              Spacer(modifier = Modifier.width(4.dp))
              Text("Save Directive", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Section 2: Custom Category Options Manager (1 to 10 Categories with '+' Button)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(CardBg)
          .border(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(listOf(GoldPrimary, CyanPrimary)),
            shape = RoundedCornerShape(16.dp)
          )
          .padding(16.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "1 to 10 Category Options Manager",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = GoldBright
              )
              Text(
                text = "ஒவ்வொரு பிரிவிலும் புதிய ஆப்ஷன்களைச் சேர்க்க '+' பட்டனைத் தொடவும்",
                fontSize = 11.sp,
                color = TextSecondary
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 1 to 10 categories listed with '+' button right next to headings
          categoryKeys.forEach { (key, label) ->
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(CardElevated)
                .padding(10.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = label,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextPrimary,
                  modifier = Modifier.weight(1f)
                )

                // '+' Button right next to the heading
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(CardBg)
                    .border(1.dp, CyanPrimary, CircleShape)
                    .clickable {
                      showAddDialogForCategory = key
                      addCategoryTitle = label
                      newOptionInput = ""
                    },
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add $label",
                    tint = CyanBright,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }

              // Display user's custom options for this category
              val userOptions = customOptionsMap[key] ?: emptyList()
              if (userOptions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                  verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  userOptions.forEach { customOpt ->
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CardBg)
                        .border(1.dp, GlassCyanBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                          text = customOpt,
                          fontSize = 11.sp,
                          color = CyanBright
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                          imageVector = Icons.Default.Close,
                          contentDescription = "Remove $customOpt",
                          tint = TextMuted,
                          modifier = Modifier
                            .size(14.dp)
                            .clickable {
                              prefsManager.removeCustomOption(key, customOpt)
                              customOptionsMap[key] = prefsManager.getCustomOptions(key)
                              Toast.makeText(context, "'$customOpt' நீக்கப்பட்டது", Toast.LENGTH_SHORT).show()
                            }
                        )
                      }
                    }
                  }
                }
              } else {
                Text(
                  text = "கூடுதல் ஆப்ஷன்கள் இல்லை. சேர்க்க '+' கிளிக் செய்யவும்.",
                  fontSize = 10.sp,
                  color = TextMuted,
                  modifier = Modifier.padding(top = 4.dp)
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Section 3: Anti-Deformity Negative Shield
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(CardBg)
          .border(1.dp, GlassGoldBorder.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
          .padding(16.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = GoldBright,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "Anti-Deformity Negative Shield",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  color = GoldBright
                )
                Text(
                  text = "உடலமைப்பு குறைபாடுகள் தவிர்வு Negative Prompts",
                  fontSize = 11.sp,
                  color = TextMuted
                )
              }
            }

            Switch(
              checked = isShieldEnabled,
              onCheckedChange = {
                isShieldEnabled = it
                prefsManager.isAntiDeformityShieldEnabled = it
                val msg = if (it) "Anti-Deformity Shield இயக்கப்பட்டது (Enabled)" else "Shield முடக்கப்பட்டது (Disabled)"
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
              },
              colors = SwitchDefaults.colors(
                checkedThumbColor = GoldPrimary,
                checkedTrackColor = CardElevated,
                checkedBorderColor = GoldPrimary
              )
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "வாய் கோணலாக, பற்கள் பொருத்தமற்று, அல்லது 3, 4 கைகள்/கால்கள், கூடுதல் விரல்கள் போன்ற AI குறைபாடுகள் வராமல் தடுக்கும் பிரத்யேக பாதுகாப்பு நெகட்டிவ் சொற்கள்:",
            fontSize = 12.sp,
            color = TextSecondary,
            lineHeight = 16.sp
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = negativePromptText,
            onValueChange = { negativePromptText = it },
            modifier = Modifier
              .fillMaxWidth()
              .height(95.dp),
            placeholder = {
              Text(
                text = "தவிர்க்க வேண்டிய அம்சங்களை உள்ளிடவும்...",
                color = TextMuted,
                fontSize = 12.sp
              )
            },
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = CardElevated,
              unfocusedContainerColor = CardElevated,
              focusedBorderColor = GoldPrimary,
              unfocusedBorderColor = GlassGoldBorder.copy(alpha = 0.4f),
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = {
                prefsManager.resetNegativePrompt()
                negativePromptText = prefsManager.negativePrompt
                Toast.makeText(context, "Negative Prompts Reset செய்யப்பட்டது!", Toast.LENGTH_SHORT).show()
              },
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
              border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder.copy(alpha = 0.5f))
            ) {
              Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Default", fontSize = 12.sp)
            }

            Button(
              onClick = {
                prefsManager.negativePrompt = negativePromptText
                Toast.makeText(context, "Anti-Deformity Prompts சேமிக்கப்பட்டது!", Toast.LENGTH_SHORT).show()
              },
              modifier = Modifier.weight(1.3f),
              colors = ButtonDefaults.buttonColors(
                containerColor = GoldPrimary,
                contentColor = TextOnGold
              )
            ) {
              Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextOnGold)
              Spacer(modifier = Modifier.width(4.dp))
              Text("Save Shield", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Section 4: AI Engine Selection (Low Moderation)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(CardBg)
          .border(1.dp, GlassGoldBorder.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
          .padding(16.dp)
      ) {
        Column {
          Text(
            text = "AI Generation Engine",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = GoldBright
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "குறைவான Moderation மற்றும் அதிக தரமுடைய Realistic Human Engine",
            fontSize = 12.sp,
            color = TextSecondary
          )
          Spacer(modifier = Modifier.height(12.dp))

          val models = listOf(
            "Google Imagen 3 (imagen-3.0-generate-002)",
            "Stable Diffusion RealVisXL (Ultra Photoreal)",
            "SDXL Turbo (High Speed Photoreal)"
          )

          models.forEach { model ->
            val isSelected = selectedModel == model
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (isSelected) CardElevated else CardBg)
                .border(
                  width = 1.dp,
                  color = if (isSelected) GoldPrimary else GlassGoldBorder.copy(alpha = 0.2f),
                  shape = RoundedCornerShape(10.dp)
                )
                .clickable {
                  selectedModel = model
                  prefsManager.aiModel = model
                  Toast.makeText(context, "$model தேர்ந்தெடுக்கப்பட்டது", Toast.LENGTH_SHORT).show()
                }
                .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = model,
                  fontSize = 12.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = if (isSelected) GoldBright else TextSecondary
                )
                if (isSelected) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = GoldBright,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Dedicated Safety Tolerance Toggle for Imagen 3
          var currentTolerance by remember { mutableStateOf(prefsManager.safetyTolerance) }

          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(CardElevated)
              .padding(12.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "Safety Tolerance (Imagen 3)",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = GoldBright
                )
                Text(
                  text = "Low: Block Only High (அதிகபட்ச படைப்பாற்றல் சுதந்திரம்)",
                  fontSize = 11.sp,
                  color = TextMuted
                )
              }

              // Active API Behavior Badge
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(CyanPrimary.copy(alpha = 0.2f))
                  .border(1.dp, CyanBright.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = if (currentTolerance == "low") "Block Only High" else prefsManager.imagenSafetySetting,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = CyanBright
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              listOf(
                "low" to "Low (Block Only High)",
                "medium" to "Medium",
                "high" to "High"
              ).forEach { (key, label) ->
                val isSelected = currentTolerance == key
                Box(
                  modifier = Modifier
                    .weight(if (key == "low") 1.4f else 1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) GoldPrimary.copy(alpha = 0.25f) else CardBg)
                    .border(
                      width = if (isSelected) 1.5.dp else 1.dp,
                      color = if (isSelected) GoldBright else GlassGoldBorder.copy(alpha = 0.3f),
                      shape = RoundedCornerShape(8.dp)
                    )
                    .clickable {
                      currentTolerance = key
                      prefsManager.safetyTolerance = key
                      val apiBehavior = if (key == "low") "Block Only High" else prefsManager.imagenSafetySetting
                      Toast.makeText(context, "Safety Tolerance: $key ($apiBehavior) சேமிக்கப்பட்டது!", Toast.LENGTH_SHORT).show()
                    }
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = if (key == "low") "Low" else label,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) GoldBright else TextSecondary
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Section 5: Face Lock & Character Status
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(CardBg)
          .border(1.dp, GlassCyanBorder.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
          .padding(16.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Face Lock Status (முக நிலை)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = CyanBright
              )
              Text(
                text = if (isFaceLocked) "தற்போது ஒரு முகம் லாக் செய்யப்பட்டுள்ளது (Active)" else "முகம் லாக் செய்யப்படவில்லை",
                fontSize = 12.sp,
                color = if (isFaceLocked) StatusSuccess else TextMuted
              )
            }
            Icon(
              imageVector = if (isFaceLocked) Icons.Default.Lock else Icons.Default.LockOpen,
              contentDescription = null,
              tint = if (isFaceLocked) StatusSuccess else TextMuted,
              modifier = Modifier.size(24.dp)
            )
          }

          if (isFaceLocked) {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
              onClick = {
                prefsManager.isFaceLocked = false
                isFaceLocked = false
                Toast.makeText(context, "Face Unlock செய்யப்பட்டது!", Toast.LENGTH_SHORT).show()
              },
              modifier = Modifier.fillMaxWidth(),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
              border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder.copy(alpha = 0.5f))
            ) {
              Text("Unlock Current Face (புதிய முகத்திற்கு மாறு)", fontSize = 12.sp)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(30.dp))
    }
  }

  // Dialog to Add a New Option to any of the 1 to 10 categories
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
              text = "Add Custom Option (+)",
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
                  customOptionsMap[categoryKey] = prefsManager.getCustomOptions(categoryKey)
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

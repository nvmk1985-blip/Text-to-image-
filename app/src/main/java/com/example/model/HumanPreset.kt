package com.example.model

enum class FaceMode(val titleTamil: String, val titleEnglish: String, val description: String) {
  OPTION_1_UPLOAD(
    "Option 1: Face Image தருவீர்களா",
    "Upload Face Photo",
    "கேலரியில் இருந்து முகப் புகைப்படத்தைத் தேர்வு செய்து உருவாக்க"
  ),
  OPTION_2_NEW(
    "Option 2: புதிய முக அமைப்பில் உருவாக்கவா",
    "New Face Structure",
    "முற்றிலும் புதிய தனித்துவமான மனித முகத்தை உருவாக்க"
  ),
  OPTION_3_LOCKED(
    "Option 3: Last இமெஜில் உள்ள முகத்தை பயன்படுத்தவா",
    "Lock & Keep Last Face",
    "கடைசியாக உருவான அதே முகத்தில் உடை, பின்னணியை மட்டும் மாற்றி உருவாக்க"
  )
}

enum class AspectRatioOption(val label: String, val width: Int, val height: Int) {
  PORTRAIT_STATUS("9:16 (Status / Story)", 720, 1280),
  SQUARE("1:1 (Square Post)", 1024, 1024),
  LANDSCAPE("16:9 (Landscape)", 1280, 720),
  PORTRAIT_4_5("4:5 (Portrait)", 864, 1080)
}

data class PresetFilters(
  val genderAge: String = "ஆண் (Male)",
  val faceShape: String = "வட்ட முகம் (Round Face)",
  val skinTone: String = "தமிழ் மாநிறம் (Wheatish / Dusky)",
  val hairStyle: String = "குட்டை முடி (Short Trim)",
  val bodyType: String = "சராசரி (Medium / Fit)",
  val height: String = "சராசரி உயரம் (Average)",
  val clothing: String = "பாரம்பரிய உடை (Traditional Saree / Kurta / Dhoti)",
  val expression: String = "புன்னகை (Smiling & Confident)",
  val background: String = "ஸ்டுடியோ லைட்டிங் (Studio Light)",
  val aspectRatio: AspectRatioOption = AspectRatioOption.PORTRAIT_STATUS
) {
  fun toPromptKeywords(): String {
    return buildString {
      append("Subject: $genderAge, ")
      append("Facial structure: $faceShape, ")
      append("Skin complexion: $skinTone with authentic natural skin texture, ")
      append("Hair: $hairStyle, ")
      append("Physique: $bodyType, $height, ")
      append("Attire: $clothing, ")
      append("Mood & Expression: $expression, ")
      append("Lighting and Environment: $background, ")
      append("Style: photorealistic 8k portrait, natural daylight, shot on 85mm f/1.4 lens, hyper-realistic details, authentic human features")
    }
  }
}

data class GeneratedImageItem(
  val id: String = System.currentTimeMillis().toString(),
  val imageUrl: String,
  val prompt: String,
  val controlPrompt: String,
  val seed: Long,
  val timestamp: Long = System.currentTimeMillis()
)

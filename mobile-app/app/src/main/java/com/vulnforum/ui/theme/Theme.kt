package com.vulnforum.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import com.vulnforum.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val ForumLightColorScheme = lightColorScheme(
    primary = Color(0xFF8A6FFF),       // delikatny fiolet
    onPrimary = Color.White,
    secondary = Color(0xFFB39DDB),     // jaśniejszy liliowy/fiolet
    onSecondary = Color.White,
    background = Color(0xFFF9FAFC),    // bardzo jasny kremowo-niebieski (prawie biały)
    onBackground = Color(0xFF1C1E21),  // ciemnoszary tekst
    surface = Color(0xFFFFFFFF),       // białe karty/panele
    onSurface = Color(0xFF1C1E21),
    error = Color(0xFFB00020),
    onError = Color.White
)

private val ForumTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        color = Color(0xFF8A6FFF),
        fontFamily = FontFamily.SansSerif
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        color = Color(0xFFB39DDB),
        fontFamily = FontFamily.SansSerif
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = Color(0xFF1C1E21),
        fontFamily = FontFamily.SansSerif
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        color = Color(0xFF6D6F7D),
        fontFamily = FontFamily.SansSerif
    )
)

val ForumShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)


@Composable
fun VulnForumTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = ForumLightColorScheme,
        typography = ForumTypography,
        shapes = ForumShapes,
        content = content
    )
}
@Composable
fun AppBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.vulnforum),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.08f)
                .blur(6.dp)
        )

        content()
    }
}
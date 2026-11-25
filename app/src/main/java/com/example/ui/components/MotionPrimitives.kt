package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.delay

object MotionTokens {
    val EmphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
    val EmphasizedAccelerate = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)
    val StandardSpring = spring<Float>(
        dampingRatio = 0.78f,
        stiffness = Spring.StiffnessMediumLow
    )
    val SnappySpring = spring<Float>(
        dampingRatio = 0.72f,
        stiffness = Spring.StiffnessMedium
    )
}

/**
 * Subtle tactile spring compression when pressing interactive cards.
 */
fun Modifier.pressScale(
    interactionSource: MutableInteractionSource,
    pressedScale: Float = 0.968f
): Modifier = composed {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1f,
        animationSpec = MotionTokens.SnappySpring,
        label = "press_scale"
    )
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Clean Material 3 staggered vertical reveal without harsh jumps or flashy effects.
 */
@Composable
fun StaggeredEntrance(
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val delayMs = (index.coerceIn(0, 8) * 32L)
        if (delayMs > 0) delay(delayMs)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(
            animationSpec = tween(durationMillis = 260, easing = MotionTokens.EmphasizedDecelerate)
        ) + slideInVertically(
            animationSpec = tween(durationMillis = 320, easing = MotionTokens.EmphasizedDecelerate),
            initialOffsetY = { it / 5 }
        )
    ) {
        content()
    }
}

/**
 * Smooth vertical digit/counter roll transition for live numbers and percentages.
 */
@Composable
fun AnimatedCounterText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified
) {
    AnimatedContent(
        targetState = text,
        modifier = modifier,
        transitionSpec = {
            ContentTransform(
                targetContentEnter = slideInVertically(
                    animationSpec = tween(220, easing = MotionTokens.EmphasizedDecelerate)
                ) { height -> height / 2 } + fadeIn(tween(180)),
                initialContentExit = slideOutVertically(
                    animationSpec = tween(180, easing = MotionTokens.EmphasizedAccelerate)
                ) { height -> -height / 2 } + fadeOut(tween(140))
            )
        },
        label = "animated_counter"
    ) { targetText ->
        Text(
            text = targetText,
            style = style,
            color = color
        )
    }
}

package com.aethlauncher.kyn.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aethlauncher.kyn.core.preferences.AppSettings
import com.aethlauncher.kyn.core.theme.LocalRenderPolicy
import com.aethlauncher.kyn.core.theme.LocalVisualStyle
import com.aethlauncher.kyn.core.theme.ThemeStyle
import com.aethlauncher.kyn.core.theme.VisualStyle
import com.aethlauncher.kyn.data.models.ShortcutRef
import com.aethlauncher.kyn.launcher.resolver.AppIconLoader
import com.aethlauncher.kyn.launcher.resolver.LaunchableApp
import kotlin.math.roundToInt

@Composable
fun HomeDock(
    refs: List<ShortcutRef>, settings: AppSettings, appsByKey: Map<String, LaunchableApp>,
    iconLoader: AppIconLoader, actions: HomeActions, modifier: Modifier = Modifier
) {
    val visual = LocalVisualStyle.current
    val policy = LocalRenderPolicy.current
    val items = refs.mapNotNull { appsByKey[it.key] }.take(settings.dockCount)
    var editorOpen by remember { mutableStateOf(false) }
    var dragX by remember(settings.dockHorizontalPercent) { mutableFloatStateOf(settings.dockHorizontalPercent.toFloat()) }
    var dragY by remember(settings.dockVerticalPercent) { mutableFloatStateOf(settings.dockVerticalPercent.toFloat()) }
    val widthFraction = (0.92f * settings.dockScalePercent / 100f).coerceIn(0.62f, 1f)
    val dockHeight = if (settings.showDockLabels) 92.dp else 78.dp
    val slotHeight = if (settings.showDockLabels) 76.dp else 64.dp
    val iconSize = if (settings.showDockLabels) 40.dp else 46.dp
    val shape = RoundedCornerShape(32.dp)

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val x = (maxWidth * (1f - widthFraction)) * (dragX / 100f)
        val y = (maxHeight - dockHeight).coerceAtLeast(0.dp) * (dragY / 100f)
        Box(
            modifier = Modifier.offset(x, y).fillMaxWidth(widthFraction).height(dockHeight)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, amount ->
                            change.consume()
                            dragX = (dragX + amount.x / size.width * 100f).coerceIn(0f, 100f)
                            dragY = (dragY + amount.y / size.height * 100f).coerceIn(0f, 100f)
                        },
                        onDragEnd = { actions.onUpdateDockPosition(dragX.roundToInt(), dragY.roundToInt()) }
                    )
                }
                .pointerInput(Unit) { detectTapGestures(onLongPress = { editorOpen = true }) }
                .dockSurface(visual, settings.dockOpacityPercent / 100f, shape)
                .padding(horizontal = 9.dp, vertical = 7.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                repeat(settings.dockCount) { index ->
                    val app = items.getOrNull(index)
                    Box(Modifier.weight(1f).height(slotHeight), contentAlignment = Alignment.Center) {
                        if (app != null) {
                            AppTile(app, iconSize, settings.showDockLabels, 10.sp, iconLoader,
                                settings.animationsEnabled && policy.animationsEnabled,
                                { actions.onMoveDock(app.ref, -1) }, { actions.onMoveDock(app.ref, 1) },
                                { actions.onRemoveDock(app.ref) })
                        } else if (index == items.size) {
                            Box(Modifier.size(iconSize).clip(CircleShape).background(homeContentColor().copy(alpha = 0.12f)).clickable { actions.onAddToDock() }, contentAlignment = Alignment.Center) {
                                Text("+", color = homeContentColor(), fontSize = 22.sp)
                            }
                        }
                    }
                }
            }
        }
    }
    if (editorOpen) DockEditorDialog(settings, { editorOpen = false },
        { x, y -> dragX = x.toFloat(); dragY = y.toFloat(); actions.onUpdateDockPosition(x, y) },
        actions.onUpdateDockScale)
}

@Composable
private fun DockEditorDialog(settings: AppSettings, onDismiss: () -> Unit, onPositionChange: (Int, Int) -> Unit, onScaleChange: (Int) -> Unit) {
    var x by remember(settings.dockHorizontalPercent) { mutableFloatStateOf(settings.dockHorizontalPercent.toFloat()) }
    var y by remember(settings.dockVerticalPercent) { mutableFloatStateOf(settings.dockVerticalPercent.toFloat()) }
    var scale by remember(settings.dockScalePercent) { mutableFloatStateOf(settings.dockScalePercent.toFloat()) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Atur Dock") }, text = {
        Column {
            Text("Posisi X: ${x.roundToInt()}%")
            Slider(x, { x = it; onPositionChange(x.roundToInt(), y.roundToInt()) }, valueRange = 0f..100f)
            Text("Posisi Y: ${y.roundToInt()}%")
            Slider(y, { y = it; onPositionChange(x.roundToInt(), y.roundToInt()) }, valueRange = 0f..100f)
            Text("Ukuran: ${scale.roundToInt()}%")
            Slider(scale, { scale = it; onScaleChange(scale.roundToInt()) }, valueRange = AppSettings.MIN_DOCK_SCALE.toFloat()..AppSettings.MAX_DOCK_SCALE.toFloat())
        }
    }, confirmButton = { TextButton(onClick = onDismiss) { Text("Selesai") } })
}

@Composable
private fun Modifier.dockSurface(visual: VisualStyle, opacity: Float, shape: Shape): Modifier = when {
    visual.safeMode -> background(MaterialTheme.colorScheme.surfaceVariant, shape)
    visual.style == ThemeStyle.TRANSPARENT -> background(Color.Black.copy(alpha = 0.18f * opacity), shape)
        .border(1.dp, Color.White.copy(alpha = 0.20f * opacity), shape)
    visual.style == ThemeStyle.NATIVE_GLASS -> background(Color(0xFF18212B).copy(alpha = 0.34f * opacity), shape)
        .background(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.18f * opacity), Color.Transparent, Color.Black.copy(alpha = 0.20f * opacity))), shape)
        .border(1.dp, Color.White.copy(alpha = 0.54f * opacity), shape)
    visual.style == ThemeStyle.ULTRA_MINIMAL -> background(Color.Black.copy(alpha = 0.22f * opacity), shape)
    visual.style == ThemeStyle.SOFT_LIGHT -> background(Color.White.copy(alpha = 0.58f * opacity), shape)
        .border(1.dp, Color.White.copy(alpha = 0.72f), shape)
    else -> background(Color(0xFF101010).copy(alpha = opacity), shape).border(1.dp, Color(0xFF2A2A2A), shape)
}

package com.aethlauncher.kyn.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.roundToInt
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.aethlauncher.kyn.core.preferences.AppSettings
import com.aethlauncher.kyn.core.recovery.EmergencyExitResult
import com.aethlauncher.kyn.core.theme.LocalRenderPolicy
import com.aethlauncher.kyn.core.theme.LocalVisualStyle
import com.aethlauncher.kyn.core.theme.ThemeStyle
import com.aethlauncher.kyn.core.theme.VisualStyle
import com.aethlauncher.kyn.data.models.HomeLayout
import com.aethlauncher.kyn.data.models.ShortcutRef
import com.aethlauncher.kyn.launcher.resolver.AppIconLoader
import com.aethlauncher.kyn.launcher.resolver.LaunchableApp
import com.aethlauncher.kyn.ui.components.EmergencyExitButton
import com.aethlauncher.kyn.ui.widgets.ClockWidget

/** Semua aksi yang bisa dipicu dari Home Screen. */
class HomeActions(
    val onOpenSettings: () -> Unit,
    val onAddToPage: (page: Int) -> Unit,
    val onAddToDock: () -> Unit,
    val onMoveShortcut: (page: Int, ref: ShortcutRef, delta: Int) -> Unit,
    val onRemoveShortcut: (page: Int, ref: ShortcutRef) -> Unit,
    val onMoveDock: (ref: ShortcutRef, delta: Int) -> Unit,
    val onRemoveDock: (ref: ShortcutRef) -> Unit,
    val onEmergencyExit: () -> EmergencyExitResult,
    val onRefreshApps: () -> Unit,
    val onUpdateDockPosition: (Int, Int) -> Unit,
    val onUpdateDockScale: (Int) -> Unit,
    val onUpdateClockPosition: (Int, Int) -> Unit
)

@Composable
fun HomeScreen(
    settings: AppSettings,
    layout: HomeLayout,
    appsByKey: Map<String, LaunchableApp>,
    iconLoader: AppIconLoader,
    actions: HomeActions,
    modifier: Modifier = Modifier
) {
    val visual = LocalVisualStyle.current
    val policy = LocalRenderPolicy.current
    val contentColor = homeContentColor()
    val pageCount = layout.pages.size
    val pagerState = rememberPagerState(
        initialPage = settings.defaultPage.coerceIn(0, maxOf(0, pageCount - 1))
    ) { pageCount }
    var menuOpen by remember { mutableStateOf(false) }

    // Daftar aplikasi dimuat ulang tiap Home kembali aktif (tidak per recomposition).
    LifecycleResumeEffect(Unit) {
        actions.onRefreshApps()
        onPauseOrDispose { }
    }

    Box(modifier = modifier.fillMaxSize().homeBackground(visual)) {
        Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    text = "\u22EE",
                    color = contentColor,
                    style = homeTextStyle(),
                    fontSize = 22.sp,
                    modifier = Modifier
                        .clickable { menuOpen = true }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }


            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) { page ->
                PageGrid(
                    page = page,
                    refs = layout.pages.getOrElse(page) { emptyList() },
                    appsByKey = appsByKey,
                    settings = settings,
                    iconLoader = iconLoader,
                    actions = actions,
                    onLongPressEmpty = { menuOpen = true }
                )
            }

            if (pageCount > 1) {
                PageIndicator(
                    count = pageCount,
                    current = pagerState.currentPage,
                    color = contentColor
                )
            }

        }
        if (settings.showClock && policy.widgetsEnabled) {
            MovableClock(settings, contentColor, actions.onUpdateClockPosition)
        }
        HomeDock(
            refs = layout.dock,
            settings = settings,
            appsByKey = appsByKey,
            iconLoader = iconLoader,
            actions = actions,
            modifier = Modifier.align(androidx.compose.ui.Alignment.TopStart)
        )

        if (menuOpen) {
            AlertDialog(
                onDismissRequest = { menuOpen = false },
                title = { Text("Menu Home") },
                text = {
                    Column {
                        TextButton(
                            onClick = {
                                menuOpen = false
                                actions.onAddToPage(pagerState.currentPage)
                            }
                        ) { Text("Tambah aplikasi ke halaman ini") }
                        TextButton(
                            onClick = {
                                menuOpen = false
                                actions.onOpenSettings()
                            }
                        ) { Text("Pengaturan") }
                        Spacer(Modifier.height(8.dp))
                        EmergencyExitButton(onOpen = actions.onEmergencyExit)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { menuOpen = false }) { Text("Tutup") }
                }
            )
        }
    }
}

@Composable
private fun PageIndicator(count: Int, current: Int, color: Color, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(count) { index ->
            Box(
                modifier = Modifier
                    .padding(3.dp)
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = if (index == current) 1f else 0.35f))
            )
        }
    }
}

/**
 * Background Home. Wallpaper sistem terlihat di belakang (lewat tema window), dengan
 * scrim tipis agar teks terbaca. AMOLED memakai hitam murni; Safe Mode memakai warna tema.
 */
@Composable
private fun Modifier.homeBackground(visual: VisualStyle): Modifier {
    if (visual.safeMode) return this.background(MaterialTheme.colorScheme.background)
    return when (visual.style) {
        ThemeStyle.TRANSPARENT -> this.background(Color.Black.copy(alpha = 0.08f))
        ThemeStyle.NATIVE_GLASS -> this.background(
            Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.10f), Color.Transparent, Color.Black.copy(alpha = 0.22f)))
        )
        ThemeStyle.ULTRA_MINIMAL -> this.background(Color.Black.copy(alpha = 0.04f))
        ThemeStyle.AMOLED_DARK -> this.background(Color.Black)
        ThemeStyle.SOFT_LIGHT -> this.background(Color.White.copy(alpha = 0.14f))
    }
}

@Composable
private fun MovableClock(settings: AppSettings, color: Color, onPositionChange: (Int, Int) -> Unit) {
    var x by remember(settings.clockHorizontalPercent) { mutableStateOf(settings.clockHorizontalPercent.toFloat()) }
    var y by remember(settings.clockVerticalPercent) { mutableStateOf(settings.clockVerticalPercent.toFloat()) }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val clockWidth = 220.dp
        val clockHeight = 92.dp
        val offsetX = (maxWidth - clockWidth).coerceAtLeast(0.dp) * (x / 100f)
        val offsetY = (maxHeight - clockHeight).coerceAtLeast(0.dp) * (y / 100f)
        Box(Modifier.offset(offsetX, offsetY).pointerInput(Unit) {
            detectDragGestures(
                onDrag = { change, amount ->
                    change.consume()
                    x = (x + amount.x / size.width * 100f).coerceIn(0f, 100f)
                    y = (y + amount.y / size.height * 100f).coerceIn(0f, 100f)
                },
                onDragEnd = { onPositionChange(x.roundToInt(), y.roundToInt()) }
            )
        }) {
            ClockWidget(showDate = settings.showDate, color = color, textStyle = homeTextStyle())
        }
    }
}

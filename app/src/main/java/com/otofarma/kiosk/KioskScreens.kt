package com.otofarma.kiosk

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val CardShape = RoundedCornerShape(20.dp)
private val BarShape = RoundedCornerShape(24.dp)
private val PillShape = RoundedCornerShape(999.dp)
private val PlateShape = RoundedCornerShape(14.dp)

private enum class BackKind { OnGreen, OnLight }

@Composable
internal fun AreaScreen(areas: List<KioskArea>, onPick: (KioskArea) -> Unit) {
    Column(Modifier.fillMaxSize().background(KioskMist)) {
        LogoBar(barHeight = 168.dp, logoHeight = 140.dp)
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(start = 28.dp, end = 28.dp, top = 22.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            AreaIntro()
            ChoiceGrid(areas, Modifier.weight(1f)) { area ->
                ChoiceCard(
                    title = area.title,
                    subtitle = area.subtitle,
                    disc = area.glyph.disc(),
                    icon = area.glyph.icon(),
                    onClick = { onPick(area) },
                )
            }
        }
    }
}

@Composable
internal fun SpecialtyScreen(
    area: KioskArea,
    onBack: () -> Unit,
    onPick: (KioskSpecialty) -> Unit,
) {
    val hasChoices = area.specialties.isNotEmpty()
    Column(Modifier.fillMaxSize().background(KioskMist)) {
        GreenHeader(
            title = area.title,
            showChoices = hasChoices,
            onBack = onBack,
        )
        if (!hasChoices) {
            EmptyLine()
        } else {
            ChoiceGrid(
                area.specialties,
                Modifier
                    .weight(1f)
                    .padding(start = 28.dp, end = 28.dp, top = 20.dp, bottom = 24.dp),
            ) { specialty ->
                ChoiceCard(
                    title = specialty.title,
                    subtitle = specialty.subtitle,
                    disc = specialty.glyph.disc(),
                    icon = specialty.glyph.icon(),
                    onClick = { onPick(specialty) },
                )
            }
        }
    }
}

@Composable
internal fun ServiceScreen(
    specialty: KioskSpecialty,
    onBack: () -> Unit,
    onPick: (KioskService) -> Unit,
) {
    Column(Modifier.fillMaxSize().background(KioskMist)) {
        LogoBar(barHeight = 132.dp, logoHeight = 112.dp)
        SectionBar(title = specialty.title, onBack = onBack)
        if (specialty.services.isEmpty()) {
            EmptyLine()
        } else {
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 28.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                ServiceHint()
                specialty.services.forEach { service ->
                    ServiceBar(service, Modifier.weight(1f)) { onPick(service) }
                }
            }
        }
    }
}

@Composable
internal fun OpenScreen(
    service: KioskService,
    onBack: () -> Unit,
    onOpen: () -> Boolean,
) {
    var blocked by remember(service.id) { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().background(KioskMist)) {
        LogoBar(barHeight = 132.dp, logoHeight = 112.dp)
        SectionBar(title = service.title, onBack = onBack)
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 32.dp, end = 32.dp, top = 20.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            service.blocks.forEachIndexed { index, block ->
                SeesawRow(block, mediaOnLeft = index % 2 == 0)
            }
        }
        if (blocked) {
            Text(
                "Non si apre. Chiedi a chi ti aiuta.",
                color = KioskInk,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 10.dp),
            )
        }
        OpenButton(service.buttonTitle, service.buttonSubtitle) {
            blocked = !onOpen()
        }
    }
}

@Composable
private fun LogoBar(barHeight: Dp, logoHeight: Dp) {
    Box(
        Modifier.fillMaxWidth().height(barHeight).background(KioskWhite),
        contentAlignment = Alignment.Center,
    ) {
        LogoImage(logoHeight)
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(1.dp)
                .background(KioskLine),
        )
    }
}

@Composable
private fun GreenHeader(title: String, showChoices: Boolean, onBack: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(KioskGreen)
            .padding(start = 28.dp, end = 28.dp, top = 18.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            BackChip(BackKind.OnGreen, onBack)
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                OtofarmaLogo(height = 44.dp, onPlate = true)
            }
            Spacer(Modifier.size(width = 140.dp, height = 46.dp))
        }
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                "Sei nella pagina dopo.",
                color = KioskWhite,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                title,
                color = KioskWhite,
                fontSize = 34.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
            if (showChoices) {
                Text(
                    "Tocca una di queste.",
                    color = KioskWhite,
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                )
            }
        }
    }
}

@Composable
private fun SectionBar(title: String, onBack: () -> Unit) {
    Box(Modifier.fillMaxWidth().height(96.dp).background(KioskWhite)) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            BackChip(BackKind.OnLight, onBack)
            Text(
                title,
                color = KioskInk,
                fontSize = 32.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(1.dp)
                .background(KioskLine),
        )
    }
}

@Composable
private fun BackChip(kind: BackKind, onClick: () -> Unit) {
    val onGreen = kind == BackKind.OnGreen
    Surface(
        onClick = onClick,
        color = if (onGreen) KioskWhite else KioskGreenSoft,
        shape = PillShape,
        modifier = if (onGreen) {
            Modifier.size(width = 140.dp, height = 46.dp)
        } else {
            Modifier.height(64.dp)
        },
    ) {
        Row(
            Modifier
                .then(if (onGreen) Modifier.fillMaxSize() else Modifier.fillMaxHeight())
                .padding(
                    start = if (onGreen) 0.dp else 18.dp,
                    end = if (onGreen) 0.dp else 22.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (onGreen) {
                Arrangement.Center
            } else {
                Arrangement.spacedBy(8.dp)
            },
        ) {
            Image(
                painter = painterResource(
                    if (onGreen) R.drawable.ic_kiosk_back else R.drawable.ic_kiosk_back_deep,
                ),
                contentDescription = null,
                modifier = Modifier.size(if (onGreen) 22.dp else 28.dp),
            )
            if (onGreen) Spacer(Modifier.width(6.dp))
            Text(
                "Indietro",
                color = if (onGreen) KioskGreen else KioskGreenDeep,
                fontSize = if (onGreen) 18.sp else 22.sp,
                lineHeight = if (onGreen) 22.sp else 28.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun OtofarmaLogo(height: Dp, onPlate: Boolean) {
    if (onPlate) {
        Surface(color = KioskWhite, shape = PlateShape) {
            Box(Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                LogoImage(height)
            }
        }
    } else {
        LogoImage(height)
    }
}

@Composable
private fun LogoImage(height: Dp) {
    Image(
        painter = painterResource(R.drawable.otofarma_logo),
        contentDescription = "Otofarma",
        contentScale = ContentScale.Fit,
        modifier = Modifier.height(height).aspectRatio(3f, matchHeightConstraintsFirst = true),
    )
}

@Composable
private fun AreaIntro() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Disc(KioskWhite, 48.dp) {
            Glyph(R.drawable.ic_kiosk_finger, 28.dp)
        }
        Column {
            Text(
                "Scegli un\u2019area",
                color = KioskInk,
                fontSize = 32.sp,
                lineHeight = 38.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                "Tocca una di queste.",
                color = KioskMuted,
                fontSize = 18.sp,
                lineHeight = 24.sp,
            )
        }
    }
}

@Composable
private fun ServiceHint() {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Disc(KioskWhite, 56.dp) {
            Glyph(R.drawable.ic_kiosk_finger_large, 36.dp)
        }
        Text(
            "Metti il dito su un colore.",
            color = KioskInk,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun <T> ChoiceGrid(
    items: List<T>,
    modifier: Modifier,
    card: @Composable (T) -> Unit,
) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items.chunked(2).forEach { row ->
            Row(
                Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                row.forEach { item ->
                    Box(Modifier.weight(1f).fillMaxHeight()) { card(item) }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ChoiceCard(
    title: String,
    subtitle: String,
    disc: Color,
    icon: Int,
    onClick: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .shadow(10.dp, CardShape, clip = false, ambientColor = KioskShadow, spotColor = KioskShadow)
            .clip(CardShape)
            .background(KioskWhite)
            .border(1.dp, KioskLine, CardShape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    ) {
        Disc(disc, 72.dp) { Glyph(icon, 34.dp) }
        Text(
            title,
            color = KioskInk,
            fontSize = 26.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            subtitle,
            color = KioskMuted,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
        Text(
            "Tocca",
            color = KioskGreen,
            fontSize = 15.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clip(PillShape)
                .background(KioskGreenSoft)
                .padding(horizontal = 16.dp, vertical = 6.dp),
        )
    }
}

@Composable
private fun ServiceBar(service: KioskService, modifier: Modifier, onClick: () -> Unit) {
    val paint = service.tone.paint()
    Row(
        modifier
            .fillMaxWidth()
            .shadow(8.dp, BarShape, clip = false, ambientColor = KioskBarShadow, spotColor = KioskBarShadow)
            .clip(BarShape)
            .background(paint.bar)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 28.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        Disc(KioskWhite, 72.dp) { Glyph(service.glyph.icon(), 36.dp) }
        Text(
            service.title,
            color = paint.onBar,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Box(
            Modifier
                .height(64.dp)
                .clip(PillShape)
                .background(KioskWhite)
                .padding(horizontal = 22.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "Tocca",
                color = KioskGreenDeep,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun SeesawRow(block: KioskBlock, mediaOnLeft: Boolean) {
    Row(
        Modifier.fillMaxWidth().height(186.dp),
        horizontalArrangement = Arrangement.spacedBy(28.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (mediaOnLeft) {
            MediaPane(block)
            CopyPane(block, Modifier.weight(1f).fillMaxHeight())
        } else {
            CopyPane(block, Modifier.weight(1f).fillMaxHeight())
            MediaPane(block)
        }
    }
}

@Composable
private fun CopyPane(block: KioskBlock, modifier: Modifier) {
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    ) {
        Text(
            block.kicker,
            color = KioskGreen,
            fontSize = 14.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            block.title,
            color = KioskInk,
            fontSize = 28.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            block.body,
            color = KioskMuted,
            fontSize = 18.sp,
            lineHeight = 26.sp,
        )
    }
}

@Composable
private fun MediaPane(block: KioskBlock) {
    val video = block.mediaKind == MediaKind.Video
    Column(
        Modifier
            .width(460.dp)
            .fillMaxHeight()
            .clip(CardShape)
            .background(if (video) KioskGreenDeep else KioskGreenSoft)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            block.mediaLabel,
            color = if (video) KioskGreenDeep else KioskWhite,
            fontSize = 14.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clip(PillShape)
                .background(if (video) KioskWhite else KioskGreen)
                .padding(horizontal = 12.dp, vertical = 6.dp),
        )
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Glyph(
                icon = if (video) R.drawable.ic_kiosk_play else R.drawable.ic_kiosk_photo,
                size = if (video) 72.dp else 64.dp,
            )
        }
        Text(
            block.mediaCaption,
            color = if (video) KioskWhite else KioskGreenDeep,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun OpenButton(title: String, subtitle: String, onClick: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .height(148.dp)
            .background(KioskYellow)
            .clickable(role = Role.Button, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            title,
            color = KioskInk,
            fontSize = 40.sp,
            lineHeight = 48.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            subtitle,
            color = KioskInk,
            fontSize = 20.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun ColumnScope.EmptyLine() {
    Box(Modifier.weight(1f).fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(
            "Qui non c'è ancora niente da toccare.",
            color = KioskInk,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun Disc(color: Color, size: Dp, mark: @Composable () -> Unit) {
    Box(
        Modifier.size(size).clip(CircleShape).background(color),
        contentAlignment = Alignment.Center,
    ) {
        mark()
    }
}

@Composable
private fun Glyph(icon: Int, size: Dp) {
    Image(
        painter = painterResource(icon),
        contentDescription = null,
        modifier = Modifier.size(size),
    )
}

private data class BarPaint(val bar: Color, val onBar: Color)

private fun ServiceTone.paint(): BarPaint = when (this) {
    ServiceTone.Green -> BarPaint(Color(0xFF3AAA3A), KioskWhite)
    ServiceTone.Yellow -> BarPaint(KioskYellow, KioskYellowInk)
    ServiceTone.Red -> BarPaint(Color(0xFFE23B3B), KioskWhite)
}

private fun AreaGlyph.disc(): Color = when (this) {
    AreaGlyph.Cross -> KioskGreenSoft
    AreaGlyph.Camera -> Color(0xFFE5F4F8)
    AreaGlyph.Person -> Color(0xFFFFF1E4)
    AreaGlyph.Desk -> Color(0xFFF3EEF8)
}

private fun AreaGlyph.icon(): Int = when (this) {
    AreaGlyph.Cross -> R.drawable.ic_kiosk_cross
    AreaGlyph.Camera -> R.drawable.ic_kiosk_camera
    AreaGlyph.Person -> R.drawable.ic_kiosk_person
    AreaGlyph.Desk -> R.drawable.ic_kiosk_desk
}

private fun SpecialtyGlyph.disc(): Color = when (this) {
    SpecialtyGlyph.Heart -> Color(0xFFFDE8EC)
    SpecialtyGlyph.Face -> Color(0xFFFFF1E4)
    SpecialtyGlyph.Tooth -> Color(0xFFE5F4F8)
    SpecialtyGlyph.Headphones -> KioskGreenSoft
}

private fun SpecialtyGlyph.icon(): Int = when (this) {
    SpecialtyGlyph.Heart -> R.drawable.ic_kiosk_heart
    SpecialtyGlyph.Face -> R.drawable.ic_kiosk_face
    SpecialtyGlyph.Tooth -> R.drawable.ic_kiosk_tooth
    SpecialtyGlyph.Headphones -> R.drawable.ic_kiosk_headphones
}

private fun ServiceGlyph.icon(): Int = when (this) {
    ServiceGlyph.Glass -> R.drawable.ic_kiosk_glass
    ServiceGlyph.Wave -> R.drawable.ic_kiosk_wave
    ServiceGlyph.Page -> R.drawable.ic_kiosk_page
}

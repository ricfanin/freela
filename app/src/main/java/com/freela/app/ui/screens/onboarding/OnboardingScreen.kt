package com.freela.app.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Euro
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.ViewKanban
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.freela.app.R
import com.freela.app.domain.model.FasePipeline
import com.freela.app.ui.theme.Freela
import com.freela.app.ui.theme.stageColor

@Composable
fun OnboardingScreen(
    onStart: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val tokens = Freela.tokens
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.bg)
            .padding(horizontal = 22.dp),
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Logotype()
            Text(
                text = stringResource(R.string.onboarding_skip),
                color = tokens.muted,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Spacer(Modifier.height(36.dp))

        // Hero glyph + testo + features
        HeroGlyph()

        Spacer(Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.onboarding_title),
            color = tokens.ink,
            style = MaterialTheme.typography.displayLarge,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.onboarding_subtitle),
            color = tokens.muted,
            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 22.sp),
            modifier = Modifier.widthIn(max = 320.dp),
        )

        Spacer(Modifier.height(32.dp))

        FeatureRow(Icons.Outlined.ViewKanban, stringResource(R.string.onboarding_feature1_title), stringResource(R.string.onboarding_feature1_subtitle))
        Spacer(Modifier.height(16.dp))
        FeatureRow(Icons.Outlined.Schedule, stringResource(R.string.onboarding_feature2_title), stringResource(R.string.onboarding_feature2_subtitle))
        Spacer(Modifier.height(16.dp))
        FeatureRow(Icons.Outlined.Euro, stringResource(R.string.onboarding_feature3_title), stringResource(R.string.onboarding_feature3_subtitle))

        Spacer(modifier = Modifier.weight(1f))

        // Dots
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        ) {
            Box(Modifier.height(6.dp).width(22.dp).clip(RoundedCornerShape(3.dp)).background(tokens.accentBase))
            Box(Modifier.size(6.dp).clip(CircleShape).background(tokens.line))
            Box(Modifier.size(6.dp).clip(CircleShape).background(tokens.line))
        }

        // CTA pieno
        com.freela.app.ui.components.FreelaButton(
            text = stringResource(R.string.onboarding_cta_start),
            onClick = { viewModel.completaOnboarding(onStart) },
            variant = com.freela.app.ui.components.FreelaButtonVariant.Primary,
            size = com.freela.app.ui.components.FreelaButtonSize.Large,
            trailing = { Icon(Icons.Outlined.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp)) },
            fillMaxWidth = true,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.onboarding_cta_login),
            color = tokens.muted,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 28.dp).align(Alignment.CenterHorizontally),
        )
    }
}

@Composable
private fun Logotype() {
    val tokens = Freela.tokens
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(tokens.accentBase),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "F",
                color = androidx.compose.ui.graphics.Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Text("Freela", color = tokens.ink, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun FeatureRow(icon: ImageVector, title: String, sub: String) {
    val tokens = Freela.tokens
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(tokens.surfaceLow)
                .border(width = 1.dp, color = tokens.line, shape = CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = tokens.accentBase, modifier = Modifier.size(18.dp))
        }
        Column {
            Text(title, color = tokens.ink, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(sub, color = tokens.muted, fontSize = 12.5f.sp, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun HeroGlyph() {
    val tokens = Freela.tokens
    val ctx = LocalContext.current
    val previewStages = listOf(
        FasePipeline.NUOVO_LEAD,
        FasePipeline.PREVENTIVO_INVIATO,
        FasePipeline.IN_CORSO,
        FasePipeline.CLIENTE_RICORRENTE,
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(tokens.surface)
            .border(width = 1.dp, color = tokens.line, shape = RoundedCornerShape(26.dp))
            .padding(PaddingValues(24.dp)),
    ) {
        previewStages.forEachIndexed { idx, stage ->
            if (idx > 0) Box(Modifier.fillMaxWidth().height(1.dp).background(tokens.lineSoft))
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(Modifier.size(6.dp).clip(CircleShape).background(stageColor(stage)))
                Text(
                    text = ctx.getString(stage.labelRes).uppercase(),
                    color = tokens.muted,
                    style = tokens.typeExtras.monoCap,
                    modifier = Modifier.weight(1f),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val nDots = listOf(3, 2, 4, 2)[idx]
                    repeat(nDots) { k ->
                        Box(
                            Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(com.freela.app.ui.components.oklchToColor(0.65f + k * 0.05f, 0.06f, 30f + idx * 60f))
                                .border(width = 1.5.dp, color = tokens.surface, shape = CircleShape),
                        )
                    }
                }
            }
        }
    }
}

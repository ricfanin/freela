package com.freela.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Euro
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freela.app.R
import com.freela.app.ui.theme.Freela

enum class FreelaTab(val labelRes: Int, val icon: ImageVector) {
    OGGI(R.string.nav_oggi, Icons.Outlined.Home),
    PIPELINE(R.string.nav_pipeline, Icons.Outlined.ViewKanban),
    CLIENTI(R.string.nav_clienti, Icons.Outlined.Person),
    PROGETTI(R.string.nav_progetti, Icons.Outlined.Description),
    FINANZE(R.string.nav_finanze, Icons.Outlined.Euro);
}

/**
 * Bottom nav 5 voci con pill accent.soft sull'icona attiva.
 * Riferimento: design_handoff_freela/ui.jsx:213-257
 */
@Composable
fun FreelaBottomNav(
    active: FreelaTab,
    onTabClick: (FreelaTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = Freela.tokens
    val ctx = LocalContext.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(tokens.surface)
            .border(width = 1.dp, color = tokens.line)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(start = 4.dp, end = 4.dp, top = 8.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FreelaTab.entries.forEach { tab ->
            val isActive = tab == active
            Column(
                modifier = Modifier
                    .widthIn(min = 56.dp)
                    .clickable { onTabClick(tab) }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isActive) tokens.accentSoft else androidx.compose.ui.graphics.Color.Transparent)
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = ctx.getString(tab.labelRes),
                        tint = if (isActive) tokens.accentBase else tokens.muted,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Text(
                    text = ctx.getString(tab.labelRes),
                    color = if (isActive) tokens.ink else tokens.muted,
                    fontSize = 10.5f.sp,
                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

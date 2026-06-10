package com.freela.app.ui.screens.onboarding

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Euro
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.ViewKanban
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.freela.app.R
import com.freela.app.domain.model.FasePipeline
import com.freela.app.ui.components.FreelaButton
import com.freela.app.ui.components.FreelaButtonSize
import com.freela.app.ui.components.FreelaButtonVariant
import com.freela.app.ui.components.oklchToColor
import com.freela.app.ui.theme.Freela
import com.freela.app.ui.theme.stageColor

private const val ONB_STEPS = 4

/**
 * Landing / onboarding a 4 step (FR-12 · NFR-12):
 *   0 · Welcome    posizionamento + feature
 *   1 · Profilo    nome, lavoro, valuta
 *   2 · Notifiche  richiesta POST_NOTIFICATIONS contestualizzata
 *   3 · Pronto     recap dello stato impostato
 * Riferimento design: design_handoff_freela/screens-aux.jsx:471-846
 */
@Composable
fun OnboardingScreen(
    onStart: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val tokens = Freela.tokens
    val context = LocalContext.current

    var step by rememberSaveable { mutableIntStateOf(0) }
    var name by rememberSaveable { mutableStateOf("") }
    var role by rememberSaveable { mutableStateOf(OnbRole.entries.first()) }
    var currency by rememberSaveable { mutableStateOf(OnbCurrency.entries.first()) }

    val notifGiaConcesse = remember {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
    }
    var notifConcesse by rememberSaveable { mutableStateOf(notifGiaConcesse) }

    val permessoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { concesso ->
        notifConcesse = concesso
        step = (step + 1).coerceAtMost(ONB_STEPS - 1)
    }

    val isLast = step == ONB_STEPS - 1

    fun completa() {
        viewModel.completaOnboarding(
            nome = name,
            ruolo = context.getString(role.labelRes),
            valuta = currency.label,
            onDone = onStart,
        )
    }

    fun avanti() {
        when {
            isLast -> completa()
            step == 2 && !notifConcesse && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                permessoLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            else -> step++
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.bg),
    ) {
        // Top bar: logo allo step 0, freccia indietro altrimenti
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 24.dp)
                .height(34.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (step > 0) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(tokens.surface)
                        .border(1.dp, tokens.line, CircleShape)
                        .clickable { step-- },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.onboarding_back),
                        tint = tokens.ink,
                        modifier = Modifier.size(15.dp),
                    )
                }
            } else {
                Logotype()
            }
            if (!isLast) {
                Text(
                    text = stringResource(R.string.onboarding_skip),
                    color = tokens.muted,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .clickable { completa() }
                        .padding(6.dp),
                )
            } else {
                Spacer(Modifier.width(1.dp))
            }
        }

        // Contenuto dello step corrente
        Box(modifier = Modifier.weight(1f).padding(horizontal = 22.dp)) {
            when (step) {
                0 -> StepWelcome()
                1 -> StepProfilo(
                    name = name,
                    onNameChange = { name = it },
                    role = role,
                    onRoleChange = { role = it },
                    currency = currency,
                    onCurrencyChange = { currency = it },
                )
                2 -> StepNotifiche()
                else -> StepPronto(name = name, role = role, currency = currency, notifAttive = notifConcesse)
            }
        }

        // Footer: dots + CTA primaria + azione secondaria
        Column(
            modifier = Modifier.padding(start = 22.dp, end = 22.dp, top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(7.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(ONB_STEPS) { i ->
                    val active = i == step
                    val done = i < step
                    val width by animateDpAsState(if (active) 26.dp else 7.dp, label = "dotWidth")
                    val color by animateColorAsState(
                        when {
                            active -> tokens.accentBase
                            done -> tokens.ink.copy(alpha = 0.55f)
                            else -> tokens.line
                        },
                        label = "dotColor",
                    )
                    Box(Modifier.width(width).height(7.dp).clip(RoundedCornerShape(4.dp)).background(color))
                }
            }

            FreelaButton(
                text = when {
                    isLast -> stringResource(R.string.onboarding_cta_open)
                    step == 2 -> stringResource(R.string.onboarding_cta_notifiche)
                    else -> stringResource(R.string.onboarding_cta_next)
                },
                onClick = { avanti() },
                variant = FreelaButtonVariant.Primary,
                size = FreelaButtonSize.Large,
                trailing = if (isLast) {
                    { Icon(Icons.Outlined.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp)) }
                } else null,
                fillMaxWidth = true,
            )

            when (step) {
                0 -> SecondaryAction(text = stringResource(R.string.onboarding_cta_login)) { completa() }
                2 -> SecondaryAction(text = stringResource(R.string.onboarding_notifiche_not_now)) { step++ }
            }
        }
    }
}

@Composable
private fun SecondaryAction(text: String, onClick: () -> Unit) {
    val tokens = Freela.tokens
    Text(
        text = text,
        color = tokens.muted,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(6.dp),
        textAlign = TextAlign.Center,
    )
}

// ----------------------------------------------------------------
// STEP 0 · Welcome
// ----------------------------------------------------------------
@Composable
private fun StepWelcome() {
    val tokens = Freela.tokens
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
    ) {
        HeroGlyph()
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.onboarding_title),
            color = tokens.ink,
            style = MaterialTheme.typography.displayLarge,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.onboarding_subtitle),
            color = tokens.muted,
            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 22.sp),
            modifier = Modifier.widthIn(max = 320.dp),
        )
        Spacer(Modifier.height(24.dp))
        FeatureRow(Icons.Outlined.ViewKanban, stringResource(R.string.onboarding_feature1_title), stringResource(R.string.onboarding_feature1_subtitle))
        Spacer(Modifier.height(14.dp))
        FeatureRow(Icons.Outlined.Schedule, stringResource(R.string.onboarding_feature2_title), stringResource(R.string.onboarding_feature2_subtitle))
        Spacer(Modifier.height(14.dp))
        FeatureRow(Icons.Outlined.Euro, stringResource(R.string.onboarding_feature3_title), stringResource(R.string.onboarding_feature3_subtitle))
    }
}

// ----------------------------------------------------------------
// STEP 1 · Profilo
// ----------------------------------------------------------------
enum class OnbRole(val labelRes: Int) {
    DESIGNER(R.string.onb_role_designer),
    DEVELOPER(R.string.onb_role_developer),
    FOTOGRAFO(R.string.onb_role_fotografo),
    COPYWRITER(R.string.onb_role_copywriter),
    CONSULENTE(R.string.onb_role_consulente),
    TRADUTTORE(R.string.onb_role_traduttore),
    ARCHITETTO(R.string.onb_role_architetto),
    ALTRO(R.string.onb_role_altro),
}

enum class OnbCurrency(val symbol: String, val label: String) {
    EUR("€", "EUR"),
    USD("$", "USD"),
    GBP("£", "GBP"),
    CHF("CHF", "CHF"),
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StepProfilo(
    name: String,
    onNameChange: (String) -> Unit,
    role: OnbRole,
    onRoleChange: (OnbRole) -> Unit,
    currency: OnbCurrency,
    onCurrencyChange: (OnbCurrency) -> Unit,
) {
    val tokens = Freela.tokens
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(top = 14.dp),
    ) {
        Text(
            text = stringResource(R.string.onb_profilo_title),
            color = tokens.ink,
            style = MaterialTheme.typography.displayLarge,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.onb_profilo_subtitle),
            color = tokens.muted,
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
        )

        // Nome
        Spacer(Modifier.height(22.dp))
        FieldLabel(stringResource(R.string.onb_profilo_name_label))
        Spacer(Modifier.height(8.dp))
        NameField(name = name, onNameChange = onNameChange)

        // Lavoro
        Spacer(Modifier.height(22.dp))
        FieldLabel(stringResource(R.string.onb_profilo_work_label))
        Spacer(Modifier.height(10.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OnbRole.entries.forEach { r ->
                val on = r == role
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (on) tokens.accentBase else tokens.surface)
                        .then(if (on) Modifier else Modifier.border(1.dp, tokens.line, RoundedCornerShape(999.dp)))
                        .clickable { onRoleChange(r) }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    Text(
                        text = stringResource(r.labelRes),
                        color = if (on) Color.White else tokens.ink,
                        fontSize = 13.5f.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }

        // Valuta
        Spacer(Modifier.height(22.dp))
        FieldLabel(stringResource(R.string.onb_profilo_currency_label))
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OnbCurrency.entries.forEach { c ->
                val on = c == currency
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (on) tokens.accentBase else tokens.surface)
                        .then(if (on) Modifier else Modifier.border(1.dp, tokens.line, RoundedCornerShape(14.dp)))
                        .clickable { onCurrencyChange(c) }
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(c.symbol, color = if (on) Color.White else tokens.ink, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Text(c.label, color = if (on) Color.White else tokens.ink, fontSize = 10.5f.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Spacer(Modifier.height(14.dp))
    }
}

@Composable
private fun FieldLabel(text: String) {
    val tokens = Freela.tokens
    Text(
        text = text.uppercase(),
        color = tokens.muted,
        style = tokens.typeExtras.monoCap,
    )
}

@Composable
private fun NameField(name: String, onNameChange: (String) -> Unit) {
    val tokens = Freela.tokens
    var focused by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(tokens.surface)
            .border(1.5.dp, if (focused) tokens.accentBase else tokens.line, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.weight(1f).padding(vertical = 14.dp)) {
            if (name.isEmpty()) {
                Text(
                    text = stringResource(R.string.onb_profilo_name_placeholder),
                    color = tokens.faint,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            BasicTextField(
                value = name,
                onValueChange = onNameChange,
                singleLine = true,
                textStyle = TextStyle(color = tokens.ink, fontSize = 16.sp, fontWeight = FontWeight.Medium),
                cursorBrush = androidx.compose.ui.graphics.SolidColor(tokens.accentBase),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focused = it.isFocused },
            )
        }
        if (name.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(tokens.lineSoft)
                    .clickable { onNameChange("") },
                contentAlignment = Alignment.Center,
            ) {
                Text("×", color = tokens.muted, fontSize = 14.sp)
            }
        }
    }
}

// ----------------------------------------------------------------
// STEP 2 · Notifiche
// ----------------------------------------------------------------
@Composable
private fun StepNotifiche() {
    val tokens = Freela.tokens
    val samples = listOf(
        NotifSample(stringResource(R.string.onb_notif_sample1_tipo), stringResource(R.string.onb_notif_sample1_title), stringResource(R.string.onb_notif_sample1_sub), tokens.accentBase, Icons.Outlined.AutoAwesome),
        NotifSample(stringResource(R.string.onb_notif_sample2_tipo), stringResource(R.string.onb_notif_sample2_title), stringResource(R.string.onb_notif_sample2_sub), tokens.danger, Icons.Outlined.Euro),
        NotifSample(stringResource(R.string.onb_notif_sample3_tipo), stringResource(R.string.onb_notif_sample3_title), stringResource(R.string.onb_notif_sample3_sub), tokens.muted, Icons.Outlined.CalendarMonth),
    )
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(top = 14.dp),
    ) {
        Box(
            modifier = Modifier.size(52.dp).clip(CircleShape).background(tokens.accentSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Notifications, contentDescription = null, tint = tokens.accentBase, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.onb_notif_title),
            color = tokens.ink,
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 31.sp,
            letterSpacing = (-0.025f).sp,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.onb_notif_subtitle),
            color = tokens.muted,
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
        )
        Spacer(Modifier.height(24.dp))
        samples.forEachIndexed { i, s ->
            Box(
                modifier = Modifier
                    .padding(start = (i * 8).dp, end = (i * 8).dp, top = if (i == 0) 0.dp else 6.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(tokens.surface)
                    .border(1.dp, tokens.line, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier.size(28.dp).clip(RoundedCornerShape(7.dp)).background(tokens.accentBase),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("F", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(s.tipo.uppercase(), color = s.color, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.4.sp)
                            Text("9:02", color = tokens.faint, style = tokens.typeExtras.monoMeta)
                        }
                        Spacer(Modifier.height(2.dp))
                        Text(s.title, color = tokens.ink, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, lineHeight = 17.sp)
                        Text(s.sub, color = tokens.muted, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

private data class NotifSample(
    val tipo: String,
    val title: String,
    val sub: String,
    val color: Color,
    val icon: ImageVector,
)

// ----------------------------------------------------------------
// STEP 3 · Pronto
// ----------------------------------------------------------------
@Composable
private fun StepPronto(name: String, role: OnbRole, currency: OnbCurrency, notifAttive: Boolean) {
    val tokens = Freela.tokens
    val nome = name.trim().ifEmpty { stringResource(R.string.app_name) }
    val recap = listOf(
        stringResource(R.string.onb_pronto_recap_nome) to nome,
        stringResource(R.string.onb_pronto_recap_lavoro) to stringResource(role.labelRes),
        stringResource(R.string.onb_pronto_recap_notifiche) to stringResource(
            if (notifAttive) R.string.onb_pronto_notifiche_on else R.string.onb_pronto_notifiche_off,
        ),
        stringResource(R.string.onb_pronto_recap_valuta) to "${currency.label} · ${currency.symbol}",
        stringResource(R.string.onb_pronto_recap_backup) to stringResource(R.string.onb_pronto_backup_value),
        stringResource(R.string.onb_pronto_recap_sync) to stringResource(R.string.onb_pronto_sync_value),
    )
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(top = 14.dp),
    ) {
        Box(
            modifier = Modifier.size(64.dp).clip(CircleShape).background(tokens.accentBase),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
        }
        Spacer(Modifier.height(18.dp))
        Text(
            text = stringResource(R.string.onb_pronto_title, nome),
            color = tokens.ink,
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 31.sp,
            letterSpacing = (-0.025f).sp,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.onb_pronto_subtitle),
            color = tokens.muted,
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
        )
        Spacer(Modifier.height(22.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(tokens.surface)
                .border(1.dp, tokens.line, RoundedCornerShape(14.dp)),
        ) {
            recap.forEachIndexed { i, (k, v) ->
                if (i > 0) Box(Modifier.fillMaxWidth().height(1.dp).background(tokens.lineSoft))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(k, color = tokens.muted, fontSize = 13.5f.sp)
                    Text(v, color = tokens.ink, fontSize = 13.5f.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Spacer(Modifier.height(14.dp))
    }
}

// ----------------------------------------------------------------
// Elementi condivisi
// ----------------------------------------------------------------
@Composable
private fun Logotype() {
    val tokens = Freela.tokens
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier.size(26.dp).clip(RoundedCornerShape(7.dp)).background(tokens.accentBase),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "F", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
                                .background(oklchToColor(0.65f + k * 0.05f, 0.06f, 30f + idx * 60f))
                                .border(width = 1.5.dp, color = tokens.surface, shape = CircleShape),
                        )
                    }
                }
            }
        }
    }
}

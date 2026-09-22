package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Ability
import com.example.data.DEFAULT_ABILITY_SCORE
import com.example.data.AbilityScore
import com.example.data.MAX_ABILITY_SCORE
import com.example.data.MIN_ABILITY_SCORE
import com.example.ui.theme.*

/**
 * One attribute, shown the way a character sheet does it: the total on top, the modifier that
 * total is worth below it, and — only when the gear actually changes something — what the hero
 * brings on their own versus what they are wearing.
 */
@Composable
private fun AbilityBox(
    score: AbilityScore,
    modifier: Modifier = Modifier
) {
    val accent = when {
        score.bonus > 0 -> GothicTextGold
        score.bonus < 0 -> GothicBloodRed
        else -> GothicTextSilver
    }

    Column(
        modifier = modifier
            .width(96.dp)
            .clip(GothicCardShape)
            .background(TooltipBody)
            .background(
                Brush.verticalGradient(listOf(accent.copy(alpha = 0.14f), Color.Transparent))
            )
            .border(1.dp, accent.copy(alpha = 0.45f), GothicCardShape)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = score.ability.statName.uppercase(),
            style = Typography.labelLarge,
            fontSize = 9.sp,
            color = GothicTextMuted,
            maxLines = 1
        )
        Text(
            text = score.total.toString(),
            style = TooltipTitleStyle,
            fontSize = 24.sp,
            color = accent
        )
        Text(
            text = score.modifierLabel,
            style = TooltipRarityStyle,
            color = StatValue
        )
        if (score.bonus != 0) {
            val sign = if (score.bonus > 0) "+" else "−"
            Text(
                text = "${score.base} $sign ${kotlin.math.abs(score.bonus)} z výbavy",
                style = Typography.bodyMedium,
                fontSize = 8.sp,
                color = GothicTextMuted,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

/** The six attributes as a block, with a way into [AbilityEditDialog]. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AbilityPanel(
    scores: List<AbilityScore>,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GothicPanel(modifier = modifier, borderColor = GothicGold, title = "ATRIBUTY") {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            scores.forEach { AbilityBox(score = it) }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Bonusy se berou ze statů oblečených věcí — stat pojmenovaný jako atribut " +
                "(např. „Síla:+2\") se k němu přičte.",
            style = Typography.bodyMedium,
            fontSize = 10.sp,
            color = GothicTextMuted
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onEditClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = GothicDarkSurface,
                contentColor = GothicTextGold
            ),
            modifier = Modifier
                .fillMaxWidth()
                .gothicBorder(GothicGold, 1.dp)
                .testTag("edit_abilities_button")
        ) {
            Text("Upravit vlastní hodnoty", style = Typography.labelLarge)
        }
    }
}

/** Sets the hero's own scores; the equipment bonus on top of them is never editable. */
@Composable
fun AbilityEditDialog(
    scores: List<AbilityScore>,
    onDismiss: () -> Unit,
    onChange: (Ability, Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Vlastní atributy hrdiny", style = Typography.titleLarge, color = GothicTextGold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Hodnoty $MIN_ABILITY_SCORE–$MAX_ABILITY_SCORE. " +
                        "$DEFAULT_ABILITY_SCORE je průměr a dává modifikátor +0.",
                    style = Typography.bodyMedium,
                    color = GothicTextMuted
                )
                scores.forEach { score ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = score.ability.statName,
                            style = Typography.bodyLarge,
                            color = GothicTextSilver,
                            modifier = Modifier.weight(1f)
                        )
                        StepperButton(
                            label = "−",
                            enabled = score.base > MIN_ABILITY_SCORE,
                            onClick = { onChange(score.ability, score.base - 1) }
                        )
                        Text(
                            text = score.base.toString(),
                            style = TooltipHeadlineStyle,
                            color = GothicTextGold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(40.dp)
                        )
                        StepperButton(
                            label = "+",
                            enabled = score.base < MAX_ABILITY_SCORE,
                            onClick = { onChange(score.ability, score.base + 1) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Hotovo", color = GothicTextGold, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = GothicDarkSurface,
        shape = GothicCardShape
    )
}

@Composable
private fun StepperButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val tint = if (enabled) GothicTextGold else GothicBorderGray
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(GothicCardShape)
            .background(GothicLightSurface)
            .border(1.dp, tint.copy(alpha = 0.6f), GothicCardShape)
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, style = Typography.titleMedium, color = tint)
    }
}

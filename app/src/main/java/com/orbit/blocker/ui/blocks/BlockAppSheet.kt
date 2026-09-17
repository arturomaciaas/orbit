package com.orbit.blocker.ui.blocks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.orbit.blocker.data.apps.InstalledApp
import com.orbit.blocker.domain.block.BlockRuleFactory
import com.orbit.blocker.domain.block.DurationUnit

/** Result of the block sheet: either a focus block or a duration block. */
sealed interface BlockChoice {
    data object Focus : BlockChoice
    data class Duration(val amount: Long, val unit: DurationUnit) : BlockChoice
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockAppSheet(
    app: InstalledApp,
    onDismiss: () -> Unit,
    onConfirm: (BlockChoice) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var mode by remember { mutableStateOf(BlockModeOption.DURATION) }
    var amountText by remember { mutableStateOf("2") }
    var unit by remember { mutableStateOf(DurationUnit.HOURS) }

    val amount = amountText.toLongOrNull()
    val amountValid = amount != null && amount >= BlockRuleFactory.MIN_AMOUNT

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Block ${app.label}", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)

            // Mode selection
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ModeRow(
                    selected = mode == BlockModeOption.DURATION,
                    title = "For a set duration",
                    subtitle = "Blocked until the timer runs out.",
                    onSelect = { mode = BlockModeOption.DURATION },
                )
                ModeRow(
                    selected = mode == BlockModeOption.FOCUS,
                    title = "During focus sessions",
                    subtitle = "Blocked only while a focus session is running.",
                    onSelect = { mode = BlockModeOption.FOCUS },
                )
            }

            if (mode == BlockModeOption.DURATION) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter(Char::isDigit).take(4) },
                    label = { Text("Amount") },
                    isError = !amountValid,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DurationUnit.entries.forEach { u ->
                        FilterChip(
                            selected = unit == u,
                            onClick = { unit = u },
                            label = { Text(u.label) },
                        )
                    }
                }
            }

            Button(
                onClick = {
                    when (mode) {
                        BlockModeOption.FOCUS -> onConfirm(BlockChoice.Focus)
                        BlockModeOption.DURATION ->
                            if (amountValid) onConfirm(BlockChoice.Duration(amount!!, unit))
                    }
                },
                enabled = mode == BlockModeOption.FOCUS || amountValid,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Block")
            }
        }
    }
}

private enum class BlockModeOption { DURATION, FOCUS }

@Composable
private fun ModeRow(
    selected: Boolean,
    title: String,
    subtitle: String,
    onSelect: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onSelect)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Column {
            Text(title, style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)
            Text(
                subtitle,
                style = androidx.compose.material3.MaterialTheme.typography.labelLarge,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

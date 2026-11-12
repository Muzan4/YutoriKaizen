package com.example.yutorikaizen.presentation.screens.habits

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.Canvas
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yutorikaizen.domain.model.Habit
import com.example.yutorikaizen.domain.model.HabitFrequency
import com.example.yutorikaizen.domain.model.HabitWithLog
import com.example.yutorikaizen.presentation.theme.*
import com.example.yutorikaizen.presentation.components.tourTarget

private val HABIT_EMOJIS = listOf(
    "\uD83D\uDCA7", "\uD83C\uDFC3", "\uD83E\uDDD8", "\uD83D\uDCDA", "\uD83D\uDE34",
    "\uD83E\uDD57", "\uD83D\uDC8A", "\uD83E\uDDF9", "\uD83C\uDFB5", "\uD83D\uDCDD",
    "\uD83D\uDEB6", "\u2600\uFE0F", "\uD83C\uDF19", "\u26A1", "\uD83E\uDDE0",
    "\uD83D\uDCAA", "\uD83D\uDECC", "\uD83C\uDF4E", "\uD83D\uDEAB", "\uD83D\uDCF5",
    "\uD83E\uDD64", "\uD83E\uDD38", "\uD83E\uDDF4", "\uD83E\uDEB4", "\uD83E\uDDD3",
    "\uD83D\uDD25", "\uD83C\uDF31", "\uD83D\uDCD6", "\u270D\uFE0F", "\uD83C\uDFAF"
)

private val HABIT_COLORS = listOf(
    "#7CB47A", // MossGreen
    "#5BA4CF", // Phase2Blue
    "#D4956A", // CozyAmber
    "#9C7FD4", // Phase4Purple
    "#E05C3A", // SurgeRed
    "#9DC49A", // SoftSage
    "#5B9ED4", // Calm blue
    "#C4A26A"  // Warm gold
)

@Composable
fun HabitTrackerScreen(
    onBack: () -> Unit = {},
    viewModel: HabitTrackerViewModel = hiltViewModel()
) {
    BackHandler(onBack = onBack)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(CozyBlack)) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MossGreen
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().tourTarget("habits_card")) {
                        HabitHeader(
                            completed = uiState.todayCompletedCount,
                            total = uiState.todayTotalCount,
                            onBack = onBack
                        )
                    }
                }

                if (uiState.habitsWithLogs.isEmpty()) {
                    item { EmptyHabitsState() }
                } else {
                    val goodHabits = uiState.habitsWithLogs.filter { it.habit.isGoodHabit }
                    val badHabits = uiState.habitsWithLogs.filter { !it.habit.isGoodHabit }

                    if (goodHabits.isNotEmpty()) {
                        item {
                            SectionLabel(
                                label = "Building Habits",
                                done = goodHabits.count { (it.todayLog?.completedCount ?: 0) >= it.habit.targetCount },
                                total = goodHabits.size
                            )
                        }
                        items(goodHabits, key = { it.habit.id }) { hwl ->
                            HabitCard(
                                habitWithLog = hwl,
                                onToggle = { viewModel.toggleHabit(hwl.habit) },
                                onEdit = { viewModel.showEditDialog(hwl.habit) },
                                onArchive = { viewModel.archiveHabit(hwl.habit) }
                            )
                        }
                    }

                    if (badHabits.isNotEmpty()) {
                        item {
                            SectionLabel(
                                label = "Habits to Break",
                                done = badHabits.count { (it.todayLog?.completedCount ?: 0) == 0 },
                                total = badHabits.size
                            )
                        }
                        items(badHabits, key = { it.habit.id }) { hwl ->
                            HabitCard(
                                habitWithLog = hwl,
                                onToggle = { viewModel.toggleHabit(hwl.habit) },
                                onEdit = { viewModel.showEditDialog(hwl.habit) },
                                onArchive = { viewModel.archiveHabit(hwl.habit) }
                            )
                        }
                    }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = { viewModel.showAddDialog() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp),
            containerColor = MossGreen,
            contentColor = CozyBlack,
            shape = CircleShape
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Add Habit", modifier = Modifier.size(28.dp))
        }
    }

    if (uiState.showAddDialog) {
        HabitDialog(
            editingHabit = uiState.editingHabit,
            onDismiss = { viewModel.dismissDialog() },
            onSave = { name, desc, emoji, color, isGood, freq, target, unit ->
                viewModel.saveHabit(name, desc, emoji, color, isGood, freq, target, unit)
            }
        )
    }
}

@Composable
private fun HabitHeader(
    completed: Int,
    total: Int,
    onBack: () -> Unit = {}
) {
    val progress = if (total > 0) completed.toFloat() / total else 0f
    val animProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "progress"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    listOf(CozyBlack2, CozyBlack)
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 14.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(38.dp)
                    .background(CozyBlack3.copy(alpha = 0.85f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = CreamWhite,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                "Habits",
                style = MaterialTheme.typography.titleMedium,
                color = SoftTan,
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Progress ring
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                Canvas(modifier = Modifier.size(80.dp)) {
                    drawArc(CozyBlack3, -90f, 360f, false, style = Stroke(8.dp.toPx(), cap = StrokeCap.Round))
                    val color = when {
                        animProgress >= 1f -> NciHigh
                        animProgress >= 0.5f -> NciMid
                        else -> NciLow
                    }
                    if (animProgress > 0f)
                        drawArc(color, -90f, animProgress * 360f, false, style = Stroke(8.dp.toPx(), cap = StrokeCap.Round))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "$completed",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = CreamWhite
                    )
                    Text("of $total", fontSize = 10.sp, color = SoftTan)
                }
            }

            Column {
                Text(
                    "Today's Habits",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = CreamWhite
                )
                Spacer(Modifier.height(4.dp))
                val message = when {
                    total == 0 -> "Add your first habit below \uD83D\uDC47"
                    completed == total -> "All done! You're on fire \uD83D\uDD25"
                    completed == 0 -> "Let's get started. You've got this \uD83D\uDCAA"
                    else -> "${total - completed} more to go — keep building"
                }
                Text(message, style = MaterialTheme.typography.bodyMedium, color = SoftTan)
            }
        }
    }
}

@Composable
private fun SectionLabel(label: String, done: Int, total: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = SoftTan, fontWeight = FontWeight.SemiBold)
        Text("$done/$total", style = MaterialTheme.typography.labelMedium, color = DimTan)
    }
}

@Composable
private fun HabitCard(
    habitWithLog: HabitWithLog,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onArchive: () -> Unit
) {
    val habit = habitWithLog.habit
    val log = habitWithLog.todayLog
    val completedCount = log?.completedCount ?: 0
    val isDone = completedCount >= habit.targetCount

    val accentColor = remember(habit.colorHex) {
        try { Color(android.graphics.Color.parseColor(habit.colorHex)) }
        catch (e: Exception) { MossGreen }
    }

    val bgColor by animateColorAsState(
        targetValue = if (isDone) accentColor.copy(alpha = 0.10f) else CozyBlack2,
        animationSpec = tween(300),
        label = "habit_bg_${habit.id}"
    )
    val scale by animateFloatAsState(
        targetValue = if (isDone) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale_${habit.id}"
    )

    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .scale(scale),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(1.dp, if (isDone) accentColor.copy(alpha = 0.4f) else CozyBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Checkbox / Emoji toggle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            if (isDone) accentColor.copy(alpha = 0.2f) else CozyBlack3,
                            CircleShape
                        )
                        .border(2.dp, if (isDone) accentColor else CozyBorder, CircleShape)
                        .clickable { onToggle() }
                ) {
                    if (isDone) {
                        Text("\u2713", color = accentColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Text(habit.emoji, fontSize = 20.sp)
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        habit.name,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isDone) accentColor else CreamWhite,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (habit.description.isNotBlank()) {
                        Text(
                            habit.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = SoftTan,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Options menu
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Rounded.MoreVert, contentDescription = "Options", tint = SoftTan, modifier = Modifier.size(18.dp))
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        containerColor = CozyBlack3
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit", color = CreamWhite) },
                            onClick = { showMenu = false; onEdit() },
                            leadingIcon = { Icon(Icons.Rounded.Edit, null, tint = MossGreen, modifier = Modifier.size(16.dp)) }
                        )
                        DropdownMenuItem(
                            text = { Text("Archive", color = SoftTan) },
                            onClick = { showMenu = false; onArchive() },
                            leadingIcon = { Icon(Icons.Rounded.Archive, null, tint = SoftTan, modifier = Modifier.size(16.dp)) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Bottom row: streak, count progress, 7-day dots
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Streak
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("${habitWithLog.streak}d streak", style = MaterialTheme.typography.labelSmall, color = CozyAmber, fontWeight = FontWeight.Bold)
                    Text("\u00B7", color = CozyBorder, fontSize = 12.sp)
                    Text("${habitWithLog.totalDays} total", style = MaterialTheme.typography.labelSmall, color = DimTan)
                }

                // Count progress if targetCount > 1
                if (habit.targetCount > 1) {
                    Surface(color = accentColor.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp)) {
                        Text(
                            "$completedCount/${habit.targetCount}${if (habit.unit.isNotBlank()) " ${habit.unit}" else ""}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = accentColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 7-day dots
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    habitWithLog.last7Days.forEach { done ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    if (done) accentColor else CozyBlack4,
                                    CircleShape
                                )
                                .border(1.dp, if (done) accentColor.copy(alpha = 0.5f) else CozyBorder, CircleShape)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyHabitsState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("\uD83C\uDF31", fontSize = 56.sp)
        Text(
            "No habits yet",
            style = MaterialTheme.typography.titleLarge,
            color = CreamWhite,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Tap the + button to add your first habit. Track what you're building or breaking — every day counts.",
            style = MaterialTheme.typography.bodyMedium,
            color = SoftTan,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun HabitDialog(
    editingHabit: Habit?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, Boolean, HabitFrequency, Int, String) -> Unit
) {
    var name by remember { mutableStateOf(editingHabit?.name ?: "") }
    var description by remember { mutableStateOf(editingHabit?.description ?: "") }
    var selectedEmoji by remember { mutableStateOf(editingHabit?.emoji ?: "\uD83C\uDF31") }
    var selectedColor by remember { mutableStateOf(editingHabit?.colorHex ?: "#7CB47A") }
    var isGoodHabit by remember { mutableStateOf(editingHabit?.isGoodHabit ?: true) }
    var frequency by remember { mutableStateOf(editingHabit?.frequency ?: HabitFrequency.DAILY) }
    var targetCount by remember { mutableStateOf(editingHabit?.targetCount?.toString() ?: "1") }
    var unit by remember { mutableStateOf(editingHabit?.unit ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CozyBlack2)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    if (editingHabit != null) "Edit Habit" else "New Habit",
                    style = MaterialTheme.typography.titleLarge,
                    color = CreamWhite,
                    fontWeight = FontWeight.Bold
                )

                // Good/Bad toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(true to "\uD83C\uDF31 Building", false to "\uD83D\uDEAB Breaking").forEach { (value, label) ->
                        val selected = isGoodHabit == value
                        Surface(
                            modifier = Modifier.weight(1f).clickable { isGoodHabit = value },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) MossGreenDim else CozyBlack3,
                            border = BorderStroke(1.dp, if (selected) MossGreen else CozyBorder)
                        ) {
                            Text(
                                label,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                color = if (selected) MossGreen else SoftTan,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                // Emoji picker
                Text("Choose emoji", style = MaterialTheme.typography.labelMedium, color = SoftTan)
                val emojiRows = HABIT_EMOJIS.chunked(6)
                emojiRows.forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        row.forEach { emoji ->
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        if (emoji == selectedEmoji) MossGreenDim else CozyBlack3,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (emoji == selectedEmoji) MossGreen else CozyBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedEmoji = emoji }
                            ) {
                                Text(emoji, fontSize = 18.sp)
                            }
                        }
                    }
                }

                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Habit name", color = SoftTan) },
                    placeholder = { Text("e.g. Drink water", color = DimTan) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MossGreen,
                        unfocusedBorderColor = CozyBorder,
                        focusedTextColor = CreamWhite,
                        unfocusedTextColor = CreamWhite,
                        cursorColor = MossGreen
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)", color = SoftTan) },
                    placeholder = { Text("Why does this matter?", color = DimTan) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MossGreen,
                        unfocusedBorderColor = CozyBorder,
                        focusedTextColor = CreamWhite,
                        unfocusedTextColor = CreamWhite,
                        cursorColor = MossGreen
                    ),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 2
                )

                // Target count + unit
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = targetCount,
                        onValueChange = { targetCount = it.filter { c -> c.isDigit() } },
                        label = { Text("Target", color = SoftTan) },
                        modifier = Modifier.width(80.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MossGreen,
                            unfocusedBorderColor = CozyBorder,
                            focusedTextColor = CreamWhite,
                            unfocusedTextColor = CreamWhite,
                            cursorColor = MossGreen
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unit (optional)", color = SoftTan) },
                        placeholder = { Text("glasses, pages...", color = DimTan) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MossGreen,
                            unfocusedBorderColor = CozyBorder,
                            focusedTextColor = CreamWhite,
                            unfocusedTextColor = CreamWhite,
                            cursorColor = MossGreen
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                // Color picker
                Text("Color", style = MaterialTheme.typography.labelMedium, color = SoftTan)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HABIT_COLORS.forEach { hex ->
                        val color = remember(hex) {
                            try { Color(android.graphics.Color.parseColor(hex)) }
                            catch (e: Exception) { MossGreen }
                        }
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(color, CircleShape)
                                .border(
                                    width = if (hex == selectedColor) 2.dp else 0.dp,
                                    color = CreamWhite,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = hex }
                        )
                    }
                }

                // Frequency
                Text("Frequency", style = MaterialTheme.typography.labelMedium, color = SoftTan)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    HabitFrequency.entries.forEach { freq ->
                        val selected = frequency == freq
                        Surface(
                            modifier = Modifier.clickable { frequency = freq },
                            shape = RoundedCornerShape(8.dp),
                            color = if (selected) MossGreenDim else CozyBlack3,
                            border = BorderStroke(1.dp, if (selected) MossGreen else CozyBorder)
                        ) {
                            Text(
                                freq.label,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (selected) MossGreen else SoftTan
                            )
                        }
                    }
                }

                // Save/Cancel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = SoftTan)
                    }
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onSave(
                                    name.trim(),
                                    description.trim(),
                                    selectedEmoji,
                                    selectedColor,
                                    isGoodHabit,
                                    frequency,
                                    targetCount.toIntOrNull() ?: 1,
                                    unit.trim()
                                )
                            }
                        },
                        enabled = name.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = MossGreen, contentColor = CozyBlack),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Habit", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

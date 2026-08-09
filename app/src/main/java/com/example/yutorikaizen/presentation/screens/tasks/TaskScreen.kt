package com.example.yutorikaizen.presentation.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yutorikaizen.domain.model.Task
import com.example.yutorikaizen.presentation.theme.*

@Composable
fun TaskScreen(viewModel: TaskViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CozyBlack)
            .padding(horizontal = 24.dp, vertical = 28.dp)
    ) {
        Text(
            text       = "Today's Tasks",
            style      = MaterialTheme.typography.headlineMedium,
            color      = CreamWhite,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(2.dp))

        // Cute pill showing task count
        Surface(
            color = if (uiState.tasks.size >= 5) MossGreenDim else CozyBlack4,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text     = "${uiState.tasks.size} / 5 tasks",
                style    = MaterialTheme.typography.labelMedium,
                color    = if (uiState.tasks.size >= 5) MossGreen else SoftTan,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier            = Modifier.weight(1f)
        ) {
            items(uiState.tasks, key = { it.id }) { task ->
                TaskRow(task = task, onToggle = { viewModel.toggle(task) })
            }
        }

        Spacer(Modifier.height(16.dp))

        // Add task row
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value         = uiState.newTaskTitle,
                onValueChange = { viewModel.onTitleChange(it) },
                placeholder   = {
                    Text(
                        if (uiState.isAtLimit) "Daily limit reached (5)" else "Add a task…",
                        color = DimTan
                    )
                },
                enabled  = !uiState.isAtLimit,
                modifier = Modifier.weight(1f),
                shape    = RoundedCornerShape(14.dp),
                colors   = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = MossGreen,
                    unfocusedBorderColor = CozyBorder,
                    focusedTextColor     = CreamWhite,
                    unfocusedTextColor   = CreamWhite,
                    cursorColor          = MossGreen
                ),
                singleLine = true
            )
            IconButton(
                onClick  = { viewModel.addNewTask() },
                enabled  = !uiState.isAtLimit && uiState.newTaskTitle.isNotBlank()
            ) {
                Icon(
                    imageVector        = Icons.Rounded.Add,
                    contentDescription = "Add task",
                    tint               = if (!uiState.isAtLimit) MossGreen else DimTan
                )
            }
        }
    }
}

@Composable
private fun TaskRow(task: Task, onToggle: () -> Unit) {
    Card(
        shape  = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CozyBlack3)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked         = task.completed,
                onCheckedChange = { onToggle() },
                colors          = CheckboxDefaults.colors(
                    checkedColor   = MossGreen,
                    uncheckedColor = SoftTan
                )
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text           = task.title,
                style          = MaterialTheme.typography.bodyLarge,
                color          = if (task.completed) DimTan else WarmCream,
                textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None,
                fontWeight     = if (task.completed) FontWeight.Normal else FontWeight.Medium
            )
        }
    }
}

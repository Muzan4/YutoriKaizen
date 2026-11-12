package com.example.yutorikaizen.presentation.screens.expenses

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yutorikaizen.domain.model.EXPENSE_CATEGORIES
import com.example.yutorikaizen.domain.model.Expense
import com.example.yutorikaizen.presentation.screens.account.AccountViewModel
import com.example.yutorikaizen.presentation.theme.*
import com.example.yutorikaizen.presentation.components.tourTarget

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpenseTrackerScreen(
    viewModel: AccountViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    BackHandler(onBack = onBack)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedFilterCategory by remember { mutableStateOf<String?>(null) }

    val filteredExpenses = remember(uiState.expenses, selectedFilterCategory) {
        if (selectedFilterCategory == null) uiState.expenses
        else uiState.expenses.filter { it.category == selectedFilterCategory }
    }

    Scaffold(
        containerColor = CozyBlack,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddExpenseDialog() },
                containerColor = MossGreen,
                contentColor = CozyBlack,
                shape = CircleShape
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add Expense", modifier = Modifier.size(28.dp))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CozyBlack)
        ) {
            // ── Top Bar ─────────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = SoftTan,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Expense Tracker",
                        style = MaterialTheme.typography.titleLarge,
                        color = CreamWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Track your mindless & mindful spending",
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftTan
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── Monthly Summary Card ───────────────────────────────────────
                item {
                    Box(modifier = Modifier.fillMaxWidth().tourTarget("expense_summary_card")) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = CozyBlack2),
                            border = BorderStroke(1.dp, MossGreen.copy(alpha = 0.25f))
                        ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(MossGreenDim.copy(alpha = 0.35f), Color.Transparent),
                                        radius = 450f
                                    )
                                )
                                .padding(24.dp)
                        ) {
                            Text(
                                text = "TOTAL SPENT THIS MONTH",
                                style = MaterialTheme.typography.labelSmall,
                                color = SoftTan,
                                letterSpacing = 1.sp
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "₹${"%.2f".format(uiState.monthlyTotal)}",
                                style = MaterialTheme.typography.headlineLarge,
                                color = CreamWhite,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(14.dp))

                            // Breakdown chips
                            if (uiState.categoryBreakdown.isNotEmpty()) {
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(uiState.categoryBreakdown.entries.toList()) { (cat, amt) ->
                                        val isSelected = selectedFilterCategory == cat
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (isSelected) MossGreen else MossGreenDim,
                                            border = BorderStroke(1.dp, MossGreen.copy(alpha = 0.5f)),
                                            modifier = Modifier.clickable {
                                                selectedFilterCategory = if (isSelected) null else cat
                                            }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                val catEmoji = EXPENSE_CATEGORIES.find { it.label == cat }?.emoji ?: "💬"
                                                Text(text = catEmoji, fontSize = 13.sp)
                                                Text(
                                                    text = "₹${"%.0f".format(amt)}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = if (isSelected) CozyBlack else CreamWhite,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

                // ── Filter reset hint ──────────────────────────────────────────
                if (selectedFilterCategory != null) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Showing only: $selectedFilterCategory",
                                style = MaterialTheme.typography.labelMedium,
                                color = MossGreen
                            )
                            TextButton(onClick = { selectedFilterCategory = null }) {
                                Text("Clear filter", color = SoftTan, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }

                // ── Expense items ──────────────────────────────────────────────
                if (filteredExpenses.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Rounded.AccountBalanceWallet, contentDescription = null, tint = SoftTan.copy(alpha = 0.5f), modifier = Modifier.size(48.dp))
                            Text(
                                "No expenses logged yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = CreamWhite,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Tap the + button to log expenses and stay aware of your spending.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SoftTan,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Transactions (${filteredExpenses.size})",
                                style = MaterialTheme.typography.titleSmall,
                                color = CreamWhite,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Long-press to delete",
                                style = MaterialTheme.typography.labelSmall,
                                color = DimTan
                            )
                        }
                    }

                    items(filteredExpenses, key = { it.id }) { expense ->
                        val catEmoji = EXPENSE_CATEGORIES.find { it.label == expense.category }?.emoji ?: "💬"

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = {},
                                    onLongClick = { viewModel.deleteExpense(expense) }
                                ),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CozyBlack2),
                            border = BorderStroke(1.dp, CozyBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(CozyBlack3, CircleShape)
                                        .border(1.dp, CozyBorder, CircleShape)
                                ) {
                                    Text(text = catEmoji, fontSize = 20.sp)
                                }
                                Spacer(Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = expense.category,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = CreamWhite,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    if (!expense.note.isNullOrBlank()) {
                                        Text(
                                            text = expense.note,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = SoftTan,
                                            maxLines = 1
                                        )
                                    }
                                }
                                Text(
                                    text = "₹${"%.2f".format(expense.amount)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MossGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    if (uiState.showAddExpense) {
        AddExpenseDialog(
            onDismiss = { viewModel.dismissAddExpenseDialog() },
            onConfirm = { amount, category, note ->
                viewModel.addExpense(amount, category, note)
            }
        )
    }
}

@Composable
private fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, category: String, note: String) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(EXPENSE_CATEGORIES.first().label) }
    var note by remember { mutableStateOf("") }

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
                    text = "Log Expense",
                    style = MaterialTheme.typography.titleLarge,
                    color = CreamWhite,
                    fontWeight = FontWeight.Bold
                )

                // Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Amount (₹)", color = SoftTan) },
                    placeholder = { Text("0.00", color = DimTan) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MossGreen,
                        unfocusedBorderColor = CozyBorder,
                        focusedTextColor = CreamWhite,
                        unfocusedTextColor = CreamWhite,
                        cursorColor = MossGreen
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Category
                Text("Category", style = MaterialTheme.typography.labelMedium, color = SoftTan)
                val chunkedCats = EXPENSE_CATEGORIES.chunked(3)
                chunkedCats.forEach { rowCats ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowCats.forEach { cat ->
                            val isSelected = cat.label == selectedCategory
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) MossGreenDim else CozyBlack3,
                                border = BorderStroke(1.dp, if (isSelected) MossGreen else CozyBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedCategory = cat.label }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(text = cat.emoji, fontSize = 14.sp)
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = cat.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) MossGreen else SoftTan,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                // Note
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (optional)", color = SoftTan) },
                    placeholder = { Text("What was this for?", color = DimTan) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MossGreen,
                        unfocusedBorderColor = CozyBorder,
                        focusedTextColor = CreamWhite,
                        unfocusedTextColor = CreamWhite,
                        cursorColor = MossGreen
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = SoftTan)
                    }
                    Button(
                        onClick = {
                            val amount = amountText.toDoubleOrNull() ?: 0.0
                            if (amount > 0) {
                                onConfirm(amount, selectedCategory, note)
                            }
                        },
                        enabled = (amountText.toDoubleOrNull() ?: 0.0) > 0,
                        colors = ButtonDefaults.buttonColors(containerColor = MossGreen, contentColor = CozyBlack),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Add Expense", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

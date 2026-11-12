package com.example.yutorikaizen.domain.model

data class Expense(
    val id: Long,
    val amount: Double,
    val category: String,
    val note: String?,
    val timestamp: Long,
    val epochMonth: Int
)

data class ExpenseCategory(val emoji: String, val label: String)

val EXPENSE_CATEGORIES = listOf(
    ExpenseCategory("🍔", "Food"),
    ExpenseCategory("🚌", "Transport"),
    ExpenseCategory("🎮", "Entertainment"),
    ExpenseCategory("🛍️", "Shopping"),
    ExpenseCategory("💊", "Health"),
    ExpenseCategory("📚", "Learning"),
    ExpenseCategory("🏠", "Housing"),
    ExpenseCategory("⚡", "Utilities"),
    ExpenseCategory("💬", "Other")
)

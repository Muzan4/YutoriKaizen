package com.example.yutorikaizen.domain.repository

import com.example.yutorikaizen.domain.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    suspend fun addExpense(expense: Expense)
    suspend fun deleteExpense(expense: Expense)
    fun getExpensesForCurrentMonth(): Flow<List<Expense>>
    fun getRecentExpenses(): Flow<List<Expense>>
}

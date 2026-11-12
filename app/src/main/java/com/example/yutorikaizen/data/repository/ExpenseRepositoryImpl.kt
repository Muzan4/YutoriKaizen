package com.example.yutorikaizen.data.repository

import com.example.yutorikaizen.data.local.dao.ExpenseDao
import com.example.yutorikaizen.data.local.entity.ExpenseEntity
import com.example.yutorikaizen.domain.model.Expense
import com.example.yutorikaizen.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val dao: ExpenseDao
) : ExpenseRepository {

    private fun currentEpochMonth(): Int {
        val cal = Calendar.getInstance()
        return (cal.get(Calendar.YEAR) * 12) + cal.get(Calendar.MONTH)
    }

    private fun Expense.toEntity() = ExpenseEntity(
        id = id,
        amount = amount,
        category = category,
        note = note,
        timestamp = timestamp,
        epochMonth = epochMonth
    )

    private fun ExpenseEntity.toDomain() = Expense(
        id = id,
        amount = amount,
        category = category,
        note = note,
        timestamp = timestamp,
        epochMonth = epochMonth
    )

    override suspend fun addExpense(expense: Expense) =
        dao.insertExpense(expense.toEntity())

    override suspend fun deleteExpense(expense: Expense) =
        dao.deleteExpense(expense.toEntity())

    override fun getExpensesForCurrentMonth(): Flow<List<Expense>> =
        dao.getExpensesForMonth(currentEpochMonth()).map { list -> list.map { it.toDomain() } }

    override fun getRecentExpenses(): Flow<List<Expense>> =
        dao.getRecentExpenses(10).map { list -> list.map { it.toDomain() } }
}

package com.expensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.expensetracker.data.database.AppDatabase
import com.expensetracker.data.entity.CategoryEntity
import com.expensetracker.data.entity.TransactionEntity
import com.expensetracker.util.CsvExporter
import com.expensetracker.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).expenseDao()

    // ── Categories ──────────────────────────────────────────────

    val categories: StateFlow<List<CategoryEntity>> = dao.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addCategory(name: String, type: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            dao.insertCategory(CategoryEntity(name = name.trim(), type = type))
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            dao.deleteCategory(category)
        }
    }

    // ── Transactions ────────────────────────────────────────────

    val recentTransactions: StateFlow<List<TransactionEntity>> =
        dao.getRecentTransactions(50)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val totalIncome: StateFlow<Double> = dao.getTotalIncome()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    val totalExpenses: StateFlow<Double> = dao.getTotalExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    fun addTransaction(
        title: String,
        amount: Double,
        type: String,
        categoryId: Long,
        categoryName: String,
        dateMillis: Long
    ) {
        if (title.isBlank() || amount <= 0.0) return
        viewModelScope.launch {
            dao.insertTransaction(
                TransactionEntity(
                    title = title.trim(),
                    amount = amount,
                    type = type,
                    categoryId = categoryId,
                    categoryName = categoryName,
                    dateMillis = dateMillis
                )
            )
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            dao.deleteTransaction(transaction)
        }
    }

    // ── CSV Export ───────────────────────────────────────────────

    private val _exportResult = MutableStateFlow<ExportResult?>(null)
    val exportResult: StateFlow<ExportResult?> = _exportResult.asStateFlow()

    fun exportCsv(startMillis: Long, endMillis: Long) {
        viewModelScope.launch {
            try {
                val normalizedStart = DateUtils.toStartOfDay(minOf(startMillis, endMillis))
                val normalizedEnd = DateUtils.toEndOfDay(maxOf(startMillis, endMillis))

                val transactions = dao.getTransactionsInRange(normalizedStart, normalizedEnd)
                if (transactions.isEmpty()) {
                    _exportResult.value = ExportResult.NoData
                    return@launch
                }
                val fileName = CsvExporter.export(
                    context = getApplication(),
                    transactions = transactions,
                    startDate = normalizedStart,
                    endDate = normalizedEnd
                )
                _exportResult.value = ExportResult.Success(fileName)
            } catch (e: Exception) {
                _exportResult.value = ExportResult.Error(e.message ?: "Export failed")
            }
        }
    }

    fun clearExportResult() {
        _exportResult.value = null
    }
}

sealed class ExportResult {
    data class Success(val fileName: String) : ExportResult()
    data object NoData : ExportResult()
    data class Error(val message: String) : ExportResult()
}

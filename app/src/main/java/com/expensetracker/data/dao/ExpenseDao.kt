package com.expensetracker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.expensetracker.data.entity.CategoryEntity
import com.expensetracker.data.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    // ── Categories ──────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Query("SELECT * FROM categories ORDER BY type ASC, name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE type = :type ORDER BY name ASC")
    fun getCategoriesByType(type: String): Flow<List<CategoryEntity>>

    // ── Transactions ────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY dateMillis DESC, id DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY dateMillis DESC, id DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int): Flow<List<TransactionEntity>>

    @Query(
        """
        SELECT * FROM transactions 
        WHERE dateMillis >= :startMillis AND dateMillis <= :endMillis 
        ORDER BY dateMillis ASC, id ASC
        """
    )
    suspend fun getTransactionsInRange(startMillis: Long, endMillis: Long): List<TransactionEntity>

    // ── Aggregates ──────────────────────────────────────────────

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM transactions WHERE type = 'INCOME'")
    fun getTotalIncome(): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM transactions WHERE type = 'EXPENSE'")
    fun getTotalExpenses(): Flow<Double>
}

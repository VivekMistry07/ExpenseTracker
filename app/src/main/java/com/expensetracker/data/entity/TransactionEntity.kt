package com.expensetracker.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["categoryId"]), Index(value = ["dateMillis"])]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    /** "INCOME" or "EXPENSE" */
    val type: String,
    val categoryId: Long,
    /** Category name denormalized for quick display & CSV export */
    val categoryName: String,
    /** Epoch millis — midnight of the selected date */
    val dateMillis: Long
)

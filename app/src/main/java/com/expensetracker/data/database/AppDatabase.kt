package com.expensetracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.expensetracker.data.dao.ExpenseDao
import com.expensetracker.data.entity.CategoryEntity
import com.expensetracker.data.entity.TransactionEntity

@Database(
    entities = [CategoryEntity::class, TransactionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expense_tracker.db"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Default Expense categories
                            db.execSQL("INSERT INTO categories (name, type) VALUES ('Food & Dining', 'EXPENSE')")
                            db.execSQL("INSERT INTO categories (name, type) VALUES ('Transportation', 'EXPENSE')")
                            db.execSQL("INSERT INTO categories (name, type) VALUES ('Housing', 'EXPENSE')")
                            db.execSQL("INSERT INTO categories (name, type) VALUES ('Groceries', 'EXPENSE')")
                            db.execSQL("INSERT INTO categories (name, type) VALUES ('Utilities', 'EXPENSE')")
                            db.execSQL("INSERT INTO categories (name, type) VALUES ('Entertainment', 'EXPENSE')")
                            db.execSQL("INSERT INTO categories (name, type) VALUES ('Healthcare', 'EXPENSE')")
                            db.execSQL("INSERT INTO categories (name, type) VALUES ('Shopping', 'EXPENSE')")

                            // Default Income categories
                            db.execSQL("INSERT INTO categories (name, type) VALUES ('Salary', 'INCOME')")
                            db.execSQL("INSERT INTO categories (name, type) VALUES ('Freelance', 'INCOME')")
                            db.execSQL("INSERT INTO categories (name, type) VALUES ('Investments', 'INCOME')")
                            db.execSQL("INSERT INTO categories (name, type) VALUES ('Other Income', 'INCOME')")
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

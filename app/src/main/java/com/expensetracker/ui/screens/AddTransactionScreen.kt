package com.expensetracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.expensetracker.data.entity.CategoryEntity
import com.expensetracker.ui.theme.ExpenseRed
import com.expensetracker.ui.theme.IncomeGreen
import com.expensetracker.util.DateUtils
import com.expensetracker.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var selectedType by rememberSaveable { mutableStateOf("EXPENSE") }
    var title by rememberSaveable { mutableStateOf("") }
    var amountText by rememberSaveable { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var selectedDateMillis by rememberSaveable {
        mutableStateOf(DateUtils.getTodayStartOfDay())
    }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var categoryDropdownExpanded by rememberSaveable { mutableStateOf(false) }

    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val filteredCategories = categories.filter { it.type == selectedType }

    // Auto-select first matching category if current selection is invalid or null
    LaunchedEffect(filteredCategories, selectedType) {
        if (selectedCategory == null || selectedCategory?.type != selectedType) {
            selectedCategory = filteredCategories.firstOrNull()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Transaction") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // ── Type toggle ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChip(
                    selected = selectedType == "EXPENSE",
                    onClick = {
                        selectedType = "EXPENSE"
                        selectedCategory = null
                    },
                    label = { Text("Expense") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ExpenseRed.copy(alpha = 0.15f),
                        selectedLabelColor = ExpenseRed
                    ),
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedType == "INCOME",
                    onClick = {
                        selectedType = "INCOME"
                        selectedCategory = null
                    },
                    label = { Text("Income") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = IncomeGreen.copy(alpha = 0.15f),
                        selectedLabelColor = IncomeGreen
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            // ── Title ──
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title / Note") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            // ── Amount ──
            OutlinedTextField(
                value = amountText,
                onValueChange = { newVal ->
                    // Allow only valid decimal input
                    if (newVal.isEmpty() || newVal.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                        amountText = newVal
                    }
                },
                label = { Text("Amount") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            // ── Category picker ──
            ExposedDropdownMenuBox(
                expanded = categoryDropdownExpanded,
                onExpandedChange = { categoryDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                ExposedDropdownMenu(
                    expanded = categoryDropdownExpanded,
                    onDismissRequest = { categoryDropdownExpanded = false }
                ) {
                    if (filteredCategories.isEmpty()) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "No categories. Add one in Categories screen.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            onClick = { categoryDropdownExpanded = false }
                        )
                    } else {
                        filteredCategories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name) },
                                onClick = {
                                    selectedCategory = cat
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // ── Date picker field ──
            OutlinedTextField(
                value = DateUtils.formatDisplayDate(selectedDateMillis),
                onValueChange = {},
                readOnly = true,
                label = { Text("Date") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Outlined.CalendarToday, contentDescription = "Pick date")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(Modifier.height(8.dp))

            // ── Save button ──
            val amount = amountText.toDoubleOrNull() ?: 0.0
            val canSave = title.isNotBlank() && amount > 0.0 && selectedCategory != null

            Button(
                onClick = {
                    selectedCategory?.let { cat ->
                        viewModel.addTransaction(
                            title = title,
                            amount = amount,
                            type = selectedType,
                            categoryId = cat.id,
                            categoryName = cat.name,
                            dateMillis = selectedDateMillis
                        )
                        onBack()
                    }
                },
                enabled = canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Save Transaction", style = MaterialTheme.typography.labelLarge)
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    // ── Date picker dialog ──
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = DateUtils.localMidnightToUtcMidnight(selectedDateMillis)
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { utcMillis ->
                        selectedDateMillis = DateUtils.utcMidnightToLocalMidnight(utcMillis)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

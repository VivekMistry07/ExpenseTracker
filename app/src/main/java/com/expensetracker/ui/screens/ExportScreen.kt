package com.expensetracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.expensetracker.util.DateUtils
import com.expensetracker.viewmodel.ExportResult
import com.expensetracker.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val exportResult by viewModel.exportResult.collectAsStateWithLifecycle()

    var startDateMillis by rememberSaveable {
        mutableStateOf(DateUtils.getFirstDayOfCurrentMonth())
    }
    var endDateMillis by rememberSaveable {
        mutableStateOf(DateUtils.getTodayEndOfDay())
    }
    var showStartPicker by rememberSaveable { mutableStateOf(false) }
    var showEndPicker by rememberSaveable { mutableStateOf(false) }

    // Handle export result feedback
    LaunchedEffect(exportResult) {
        when (val result = exportResult) {
            is ExportResult.Success -> {
                snackbarHostState.showSnackbar("Saved to Downloads/${result.fileName}")
                viewModel.clearExportResult()
            }
            is ExportResult.NoData -> {
                snackbarHostState.showSnackbar("No transactions found in selected range.")
                viewModel.clearExportResult()
            }
            is ExportResult.Error -> {
                snackbarHostState.showSnackbar("Export failed: ${result.message}")
                viewModel.clearExportResult()
            }
            null -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Export to CSV") },
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            Text(
                "Select date range",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            // ── Start date ──
            OutlinedTextField(
                value = DateUtils.formatDisplayDate(startDateMillis),
                onValueChange = {},
                readOnly = true,
                label = { Text("Start Date") },
                trailingIcon = {
                    IconButton(onClick = { showStartPicker = true }) {
                        Icon(Icons.Outlined.CalendarToday, contentDescription = "Pick start date")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            // ── End date ──
            OutlinedTextField(
                value = DateUtils.formatDisplayDate(endDateMillis),
                onValueChange = {},
                readOnly = true,
                label = { Text("End Date") },
                trailingIcon = {
                    IconButton(onClick = { showEndPicker = true }) {
                        Icon(Icons.Outlined.CalendarToday, contentDescription = "Pick end date")
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

            Button(
                onClick = { viewModel.exportCsv(startDateMillis, endDateMillis) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Export to CSV", style = MaterialTheme.typography.labelLarge)
            }
        }
    }

    // ── Start Date picker dialog ──
    if (showStartPicker) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = DateUtils.localMidnightToUtcMidnight(startDateMillis)
        )
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { utcMillis ->
                        startDateMillis = DateUtils.utcMidnightToLocalMidnight(utcMillis)
                    }
                    showStartPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showStartPicker = false }) { Text("Cancel") }
            }
        ) { DatePicker(state = state) }
    }

    // ── End Date picker dialog ──
    if (showEndPicker) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = DateUtils.localMidnightToUtcMidnight(endDateMillis)
        )
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { utcMillis ->
                        endDateMillis = DateUtils.utcMidnightToLocalMidnight(utcMillis)
                    }
                    showEndPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showEndPicker = false }) { Text("Cancel") }
            }
        ) { DatePicker(state = state) }
    }
}

package dev.klarkengkoy.triptrack.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val showDeleteDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val userName by viewModel.userNameFlow.collectAsState(initial = "")
    val userEmail by viewModel.userEmailFlow.collectAsState(initial = "")

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "User Name: $userName")
        Text(text = "User Email: $userEmail")

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { viewModel.logout() }) {
            Text("Log Out")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { showDeleteDialog.value = true }) {
            Text("Delete Account")
        }
    }

    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text("Delete Account") },
            text = { Text("Are you sure you want to delete your account? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog.value = false
                        viewModel.deleteAccount(context)
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog.value = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

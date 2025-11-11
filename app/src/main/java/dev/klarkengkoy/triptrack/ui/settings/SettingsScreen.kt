package dev.klarkengkoy.triptrack.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.klarkengkoy.triptrack.ui.MainViewModel
import dev.klarkengkoy.triptrack.ui.theme.TripTrackTheme

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    mainViewModel: MainViewModel
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val userName by viewModel.userNameFlow.collectAsStateWithLifecycle(initialValue = "")
    val userEmail by viewModel.userEmailFlow.collectAsStateWithLifecycle(initialValue = "")

    LaunchedEffect(Unit) {
        mainViewModel.setTopAppBarState(
            title = { Text("Settings") },
            navigationIcon = {},
            actions = {}
        )
    }

    SettingsScreenContent(
        userName = userName,
        userEmail = userEmail,
        onLogoutClick = { viewModel.logout() },
        onDeleteAccountClick = { showDeleteDialog = true },
        showDeleteDialog = showDeleteDialog,
        onDismissDialog = { showDeleteDialog = false },
        onConfirmDelete = {
            showDeleteDialog = false
            viewModel.deleteAccount(context)
        }
    )
}

@Composable
private fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    userName: String?,
    userEmail: String?,
    onLogoutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    showDeleteDialog: Boolean,
    onDismissDialog: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "User Name: ${userName ?: ""}")
        Text(text = "User Email: ${userEmail ?: ""}")

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onLogoutClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Log Out")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onDeleteAccountClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        ) {
            Text("Delete Account")
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = onDismissDialog,
            title = { Text("Delete Account") },
            text = { Text("Are you sure you want to delete your account? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = onConfirmDelete) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDialog) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenContentPreview() {
    TripTrackTheme {
        SettingsScreenContent(
            userName = "Clark",
            userEmail = "klarkengkoy@gmail.com",
            onLogoutClick = {},
            onDeleteAccountClick = {},
            showDeleteDialog = false,
            onDismissDialog = {},
            onConfirmDelete = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenContentWithDialogPreview() {
    TripTrackTheme {
        SettingsScreenContent(
            userName = "K Larken",
            userEmail = "klarkengkoy@gmail.com",
            onLogoutClick = {},
            onDeleteAccountClick = {},
            showDeleteDialog = true,
            onDismissDialog = {},
            onConfirmDelete = {}
        )
    }
}

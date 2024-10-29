package dev.achmad.ownsafe.ui.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.achmad.ownsafe.common.components.Base64Image
import dev.achmad.ownsafe.common.components.LoadingBlock
import dev.achmad.ownsafe.ui.theme.LocalMainViewModel
import kotlinx.serialization.Serializable

@Serializable
object Profile

fun NavGraphBuilder.profileDestination(
    onLogout: () -> Unit,
    onRefresh: () -> Unit,
    onSaveProfile: (String, String?) -> Unit,
    onBack: () -> Unit,
) {
    composable<Profile> {
        ProfileScreen(
            onLogout = onLogout,
            onRefresh = onRefresh,
            onSaveProfile = onSaveProfile,
            onBack = onBack
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileScreen(
    onLogout: () -> Unit,
    onRefresh: () -> Unit,
    onSaveProfile: (String, String?) -> Unit,
    onBack: () -> Unit,
) {
    val viewModel = hiltViewModel<ProfileViewModel>()
    val mainState by LocalMainViewModel.current.state.collectAsState()
    val user = mainState.user
    val userLoading = mainState.userLoading
    val focusManager = LocalFocusManager.current

    BackHandler { onBack() }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        if (user != null) {
            var username by remember { mutableStateOf(user.username) }
            var password by remember { mutableStateOf("") }
            var passwordVisible by remember { mutableStateOf(false) }
            var usernameError by remember { mutableStateOf(false) }
            var showLogoutDialog by remember { mutableStateOf(false) }

            fun validateSaveProfile() {
                focusManager.clearFocus()
                if (username.isEmpty()) usernameError = true
                if (usernameError) return
                onSaveProfile(username, password.ifEmpty { null })
            }

            if (showLogoutDialog) {
                AlertDialog(
                    title = {
                        Text(text = "Logout")
                    },
                    text = {
                        Text(text = "Are you sure?")
                    },
                    onDismissRequest = { showLogoutDialog = false },
                    dismissButton = {
                        TextButton(onClick = { showLogoutDialog = false }) {
                            Text(text = "Cancel")
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showLogoutDialog = false
                                viewModel.logout()
                                onLogout()
                            }
                        ) {
                            Text(text = "Confirm")
                        }
                    }
                )
            }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        title = {
                            Text(text = "Detail Profile")
                        },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Base64Image(
                        modifier = Modifier
                            .size(128.dp)
                            .clip(CircleShape),
                        base64String = user.avatar
                    )

                    Column {
                        OutlinedTextField(
                            label = {
                                Text(text = "Username")
                            },
                            value = username,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            isError = usernameError,
                            supportingText = {
                                if (usernameError) {
                                    Text(text = "Username cannot be empty!")
                                }
                            },
                            onValueChange = {
                                username = it
                                usernameError = false
                            }
                        )
                        OutlinedTextField(
                            label = {
                                Text(text = "Password")
                            },
                            supportingText = {
                                Text(text = "Leave blank to keep your old password")
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            value = password,
                            trailingIcon = {
                                val icon =
                                    if (!passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                IconToggleButton(
                                    checked = passwordVisible,
                                    onCheckedChange = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = icon, contentDescription = null)
                                }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = { validateSaveProfile() }
                            ),
                            onValueChange = {
                                password = it
                            }
                        )
                    }
                    Button(
                        modifier = Modifier.width(OutlinedTextFieldDefaults.MinWidth),
                        onClick = { validateSaveProfile() }
                    ) {
                        Text(text = "Save")
                    }
                    OutlinedButton(
                        modifier = Modifier.width(OutlinedTextFieldDefaults.MinWidth),
                        onClick = { showLogoutDialog = true },
                    ) {
                        Text(text = "Logout", color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }
        } else if (!userLoading) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "Something wrong happened")
                TextButton(onClick = onRefresh) {
                    Text(text = "Retry")
                }
            }
        }
        if (userLoading) {
            LoadingBlock()
        }
    }
}
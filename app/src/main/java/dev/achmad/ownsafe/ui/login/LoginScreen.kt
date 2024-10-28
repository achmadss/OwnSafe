package dev.achmad.ownsafe.ui.login

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.achmad.core.OAuthType
import dev.achmad.ownsafe.LocalDarkTheme
import dev.achmad.ownsafe.R
import dev.achmad.ownsafe.common.components.LoadingBlock
import dev.achmad.ownsafe.common.components.rememberResourceBitmapPainter
import dev.achmad.ownsafe.common.extension.toast
import kotlinx.serialization.Serializable
import soup.compose.material.motion.animation.materialSharedAxisX
import soup.compose.material.motion.animation.materialSharedAxisZ
import soup.compose.material.motion.animation.rememberSlideDistance

@Serializable
object Login

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.loginDestination(
    onLogin: () -> Unit,
) {
    composable<Login> {
        val viewModel = hiltViewModel<LoginViewModel>()
        val context = LocalContext.current
        val oauthModel by viewModel.oauthModel.collectAsState()
        val showWebView by viewModel.showWebView.collectAsState()
        var pageProgress by remember { mutableIntStateOf(0) }
        var webViewTitle by remember { mutableStateOf("Loading...") }
        val showLoading by viewModel.loading.collectAsState()
        var isRegister by rememberSaveable { mutableStateOf(false) }
        val slideDistance = rememberSlideDistance()

        BackHandler(showWebView) { viewModel.showWebView(false) }
        BackHandler(isRegister) { isRegister = false }
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "OwnSafe",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
                AnimatedContent(
                    targetState = isRegister,
                    transitionSpec = {
                        materialSharedAxisX(
                            slideDistance = slideDistance,
                            forward = !isRegister
                        )
                    },
                    label = ""
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (it) {
                            RegisterContent(
                                onRegister = { username, password ->
                                    viewModel.register(username, password) { errorMessage ->
                                        if (errorMessage == null) {
                                            onLogin()
                                            return@register
                                        }
                                        context.toast(errorMessage)
                                    }
                                },
                                onBack = { isRegister = false }
                            )
                        } else {
                            LoginContent(
                                onLogin = { username, password ->
                                    viewModel.login(username, password) { errorMessage ->
                                        if (errorMessage == null) {
                                            onLogin()
                                            return@login
                                        }
                                        context.toast(errorMessage)
                                    }
                                },
                                onOAuth = {
                                    viewModel.updateOAuth(it)
                                    viewModel.showWebView(true)
                                },
                                onRegister = { isRegister = true }
                            )
                        }
                    }
                }
            }
            AnimatedContent(
                modifier = Modifier.fillMaxSize(),
                targetState = showWebView,
                transitionSpec = {
                    materialSharedAxisZ(forward = showWebView)
                },
                label = ""
            ) {
                if (it) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                TopAppBar(
                                    title = {
                                        Text(
                                            modifier = Modifier.padding(end = 8.dp),
                                            text = webViewTitle,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    actions = {
                                        IconButton(
                                            onClick = { viewModel.showWebView(false) }
                                        ) {
                                            Icon(
                                                modifier = Modifier.padding(horizontal = 8.dp),
                                                imageVector = Icons.Filled.Close,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                )
                                if (pageProgress < 100) {
                                    Box(
                                        modifier = Modifier
                                            .height(4.dp)
                                            .fillMaxWidth(pageProgress.div(100f))
                                            .background(Color.Green)
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        oauthModel?.url?.let {
                            OAuthWebView(
                                modifier = Modifier.padding(innerPadding),
                                host = viewModel.host.get(),
                                url = it,
                                onProgressChanged = { pageProgress = it },
                                onPageFinished = { view, url ->
                                    if (view != null && url != null) {
                                        webViewTitle = view.title ?: "Loading..."
                                    }
                                },
                                onCodeReceived = {
                                    viewModel.showWebView(false)
                                    viewModel.confirmLoginOauth(
                                        code = it,
                                        onError = { context.toast(it) },
                                        onSuccess = onLogin
                                    )
                                },
                                onDeny = {
                                    viewModel.showWebView(false)
                                    context.toast(it)
                                }
                            )
                        }
                    }
                }
            }
            if (showLoading) {
                LoadingBlock()
            }
        }
    }
}

@Composable
private fun LoginContent(
    onLogin: (String, String) -> Unit,
    onOAuth: (OAuthType) -> Unit,
    onRegister: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var usernameError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    fun validateLogin() {
        focusManager.clearFocus()
        if (username.isEmpty()) usernameError = true
        if (password.isEmpty()) passwordError = true
        if (usernameError || passwordError) return
        onLogin(username, password)
    }

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
            if (passwordError) {
                Text(text = "Password cannot be empty!")
            }
        },
        isError = passwordError,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        value = password,
        trailingIcon = {
            val icon = if (!passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconToggleButton(
                checked = passwordVisible,
                onCheckedChange = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = icon, contentDescription = null)
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = { validateLogin() }
        ),
        onValueChange = {
            password = it
            passwordError = false
        }
    )
    Spacer(modifier = Modifier.height(4.dp))
    Button(
        modifier = Modifier.defaultMinSize(
            minWidth = OutlinedTextFieldDefaults.MinWidth,
        ),
        onClick = { validateLogin() }
    ) {
        Text(text = "LOGIN")
    }
    Text(
        text = "OR",
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.labelSmall
    )
    Row(
        modifier = Modifier.width(OutlinedTextFieldDefaults.MinWidth)
    ) {
        OutlinedButton(
            modifier = Modifier.weight(1f),
            onClick = { onOAuth(OAuthType.DISCORD) }
        ) {
            val iconRes = if (!LocalDarkTheme.current) {
                R.drawable.discord_black
            } else R.drawable.discord_white
            Icon(
                modifier = Modifier.size(16.dp),
                painter = rememberResourceBitmapPainter(id = iconRes),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Discord")
        }
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedButton(
            modifier = Modifier.weight(1f),
            onClick = { onOAuth(OAuthType.GITHUB) }
        ) {
            Icon(
                modifier = Modifier.size(16.dp),
                painter = rememberResourceBitmapPainter(id = R.drawable.github),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Github")
        }
    }
    Spacer(modifier = Modifier.height(8.dp))

    val registerAnnotatedString = buildAnnotatedString {
        append("Don't have an account? ")
        withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
            val registerClickable = LinkAnnotation.Clickable(
                tag = "register"
            ) {
                onRegister()
            }
            withLink(registerClickable) {
                append("Register")
            }
        }
    }
    Text(
        text = registerAnnotatedString,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun RegisterContent(
    onRegister: (String, String) -> Unit,
    onBack: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var usernameError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }

    fun validateRegister() {
        focusManager.clearFocus()
        if (username.isEmpty()) usernameError = true
        if (password.isEmpty()) passwordError = true
        if (usernameError || passwordError || confirmPasswordError) return
        onRegister(username, password)
    }

    Row(
        modifier = Modifier.width(OutlinedTextFieldDefaults.MinWidth.plus(24.dp))
    ) {
        TextButton(
            onClick = onBack
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Back to login", color = MaterialTheme.colorScheme.onBackground)
        }
    }
    OutlinedTextField(
        label = {
            Text(text = "Username")
        },
        value = username,
        onValueChange = {
            username = it
            usernameError = false
        },
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
    )
    OutlinedTextField(
        label = {
            Text(text = "Password")
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        value = password,
        trailingIcon = {
            val icon = if (!passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconToggleButton(
                checked = passwordVisible,
                onCheckedChange = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = icon, contentDescription = null)
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        isError = passwordError,
        supportingText = {
            if (passwordError) {
                Text(text = "Password cannot be empty!")
            }
        },
        onValueChange = {
            password = it
            passwordError = false
        }
    )
    OutlinedTextField(
        label = {
            Text(text = "Confirm Password")
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        value = confirmPassword,
        trailingIcon = {
            val icon = if (!passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconToggleButton(
                checked = passwordVisible,
                onCheckedChange = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = icon, contentDescription = null)
            }
        },
        isError = password != confirmPassword,
        supportingText = {
            if (password != confirmPassword) {
                Text(text = "Password did not match!", color = MaterialTheme.colorScheme.error)
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = { validateRegister() }
        ),
        onValueChange = { confirmPassword = it }
    )
    Button(
        modifier = Modifier.defaultMinSize(
            minWidth = OutlinedTextFieldDefaults.MinWidth,
        ),
        onClick = { validateRegister() }
    ) {
        Text(text = "REGISTER")
    }
}
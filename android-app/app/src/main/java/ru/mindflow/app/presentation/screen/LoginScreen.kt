package ru.mindflow.app.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import ru.mindflow.app.control.AuthUiState
import ru.mindflow.app.control.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            viewModel.resetState()
            onSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("MindFlow", style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary)

        Spacer(Modifier.height(8.dp))
        Text("Ваш помощник в ментальном здоровье",
            style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (uiState is AuthUiState.Error) {
            Spacer(Modifier.height(8.dp))
            Text((uiState as AuthUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState !is AuthUiState.Loading
        ) {
            if (uiState is AuthUiState.Loading) {
                CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
            } else {
                Text("Войти")
            }
        }

        Spacer(Modifier.height(12.dp))

        TextButton(onClick = onNavigateToRegister) {
            Text("Нет аккаунта? Зарегистрироваться")
        }
    }
}

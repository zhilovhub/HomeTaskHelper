package com.example.hometaskhelper.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hometaskhelper.ui.viewmodels.AuthFieldState
import com.example.hometaskhelper.ui.viewmodels.MainViewModel
import com.example.hometaskhelper.ui.viewmodels.UserState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel(factory = MainViewModel.factory(LocalContext.current.applicationContext))
) {

    val authState by viewModel.authState.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp, bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Авторизация",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 48.dp, end = 48.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
            ) {
                Column {
                    when (authState.nickNameState) {
                        AuthFieldState.SUCCESS -> {
                            Text(
                                modifier = Modifier.alpha(1f),
                                text = "Сойдёт",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D9700),
                            )
                        }
                        AuthFieldState.ERROR -> {
                            Text(
                                modifier = Modifier.alpha(1f),
                                text = "Слишком длинный",
                                fontWeight = FontWeight.Bold,
                                color = Color.Red,
                            )
                        }
                        AuthFieldState.EMPTY -> {
                            Text(
                                modifier = Modifier.alpha(1f),
                                text = "Пусто",
                                fontWeight = FontWeight.Bold,
                                color = Color.Red,
                            )
                        }
                    }
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        value = authState.nickName,
                        label = {
                            Text(text = "Имя (придумай)", fontWeight = FontWeight.Bold)
                        },
                        onValueChange = {
                            if (!it.contains('\n')) {
                                if (it.isEmpty()) {
                                    viewModel.updateAuthState(
                                        authState.copy(
                                            nickName = it,
                                            nickNameState = AuthFieldState.EMPTY
                                        )
                                    )
                                }
                                else if (it.length > 25) {
                                    viewModel.updateAuthState(
                                        authState.copy(
                                            nickName = if (it.length > 30) authState.nickName else it,
                                            nickNameState = AuthFieldState.ERROR
                                        )
                                    )
                                } else {
                                    viewModel.updateAuthState(
                                        authState.copy(
                                            nickName = it,
                                            nickNameState = AuthFieldState.SUCCESS
                                        )
                                    )
                                }
                            }
                        }
                    )
                }
                Column {
                    Text(
                        modifier = Modifier.alpha(1f),
                        text = "Слишком длинный",
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        value = authState.password,
                        label = {
                            Text(text = "Пароль", fontWeight = FontWeight.Bold)
                        },
                        singleLine = true,
                        onValueChange = {
                            if (!it.contains('\n')) {
                                viewModel.updateAuthState(
                                    authState.copy(
                                        password = it
                                    )
                                )
                            }
                        }
                    )
                }
            }
            Button(
                onClick = {

                }
            ) {
                Text(
                    text = "Готово"
                )
            }
        }
    }
}


@Preview
@Composable
fun AuthScreenPreview() {
    AuthScreen()
}
package com.example.hometaskhelper.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.hometaskhelper.R
import com.example.hometaskhelper.ui.viewmodels.MainViewModel
import com.example.hometaskhelper.ui.viewmodels.UserState


@Composable
fun RedactTasks(
    userState: UserState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val (addEnable, deleteEnable) = when (userState) {
        UserState.DEFAULT -> Pair(true, true)
        UserState.REDACTING -> Pair(true, false)
        UserState.DELETING -> Pair(false, false)
    }

    Row(
        modifier = modifier.padding(top = 36.dp, bottom = 24.dp),
    ) {

        Button(
            modifier = Modifier,
            enabled = addEnable,
            onClick = {
                viewModel.updateUserState(UserState.REDACTING)
                viewModel.addNewTask()
            }
        ) {
            Text("Добавить")
        }
        Spacer(
            modifier = Modifier.width(24.dp)
        )
        Button(
            modifier = Modifier,
            enabled = deleteEnable,
            onClick = {
                viewModel.updateUserState(UserState.DELETING)
            }
        ) {
            Text("Удалить")
        }
    }
}


@Composable
fun AcceptCancel(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val action = { viewModel.updateUserState(UserState.DEFAULT) }

    Row(
        modifier = modifier,
    ) {
        IconButton(
            onClick = action
        ) {
            Image(
                painter = painterResource(R.drawable.baseline_check_circle_24),
                contentDescription = null
            )
        }
        IconButton(
            onClick = action
        ) {
            Image(
                painter = painterResource(R.drawable.baseline_cancel_24),
                contentDescription = null
            )
        }
    }
}

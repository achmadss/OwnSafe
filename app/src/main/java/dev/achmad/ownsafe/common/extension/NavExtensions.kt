package dev.achmad.ownsafe.common.extension

import androidx.navigation.NavController

fun NavController.navigateAndPop(
    destination: Any,
    popUpTo: Any
) {
    navigate(destination) {
        popUpTo(popUpTo) { inclusive = true }
        launchSingleTop = true
    }
}
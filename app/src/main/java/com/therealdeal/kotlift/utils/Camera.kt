package com.therealdeal.kotlift.utils

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

@Composable
fun rememberCameraLauncher(
    onPictureTaken: (Uri) -> Unit = {}
) : Pair<Uri?, () -> Unit> {
    var launcherUri by remember { mutableStateOf<Uri?>(null) }
    var pictureUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture(),
    ) { pictureTaken ->
        if (pictureTaken) launcherUri?.let {
            pictureUri = it
            onPictureTaken(it)
        }
    }

    val ctx = LocalContext.current

    val takePicture = {
        val file = File.createTempFile("tmp_image", ".jpg", ctx.externalCacheDir)
        launcherUri = FileProvider.getUriForFile(ctx, "${ctx.packageName}.provider", file)

        launcher.launch(launcherUri!!)
    }

    return pictureUri to takePicture
}

/**
 * Wraps [rememberCameraLauncher] with a runtime CAMERA permission check.
 * If permission is already granted, opens the camera immediately.
 * Otherwise requests it first, then opens the camera if granted.
 */
@Composable
fun rememberCameraLauncherWithPermission(
    onPictureTaken: (Uri) -> Unit = {},
    onPermissionDenied: () -> Unit = {}
): () -> Unit {
    val (_, takePicture) = rememberCameraLauncher(onPictureTaken)
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) takePicture() else onPermissionDenied()
    }

    return {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            takePicture()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}

/**
 * Opens the system Photo Picker (no runtime permission needed on API 33+).
 */
@Composable
fun rememberGalleryLauncher(
    onImagePicked: (Uri) -> Unit = {}
): () -> Unit {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onImagePicked(it) }
    }

    return {
        launcher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }
}
package com.therealdeal.kotlift.ui.composables.headers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.therealdeal.kotlift.model.Profile
import com.therealdeal.kotlift.ui.screens.profile.ProfileViewModel
import com.therealdeal.kotlift.utils.rememberCameraLauncherWithPermission
import com.therealdeal.kotlift.utils.rememberGalleryLauncher
import com.therealdeal.kotlift.utils.saveImageToStorage
import coil.compose.AsyncImage
import com.therealdeal.kotlift.utils.deleteImageFromStorage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileHeaderSection(
    user: Profile?,
    viewModel: ProfileViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val contentResolver = context.contentResolver
    var showPhotoSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val onNewImagePicked: (android.net.Uri) -> Unit = { tempUri ->
        scope.launch {
            uiState.profileImageUri?.let { oldUri ->
                deleteImageFromStorage(context = context, uri = oldUri)
            }

            val savedUri = saveImageToStorage(tempUri, contentResolver)
            viewModel.updateProfileImage(savedUri)
        }
    }

    val takePicture = rememberCameraLauncherWithPermission(
        onPictureTaken = onNewImagePicked
    )

    val pickFromGallery = rememberGalleryLauncher(
        onImagePicked = onNewImagePicked
    )

    val displayUri = uiState.profileImageUri

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
            )
            .padding(24.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
                    .clickable {
                        showPhotoSheet = true
                    },
                contentAlignment = Alignment.Center
            ) {
                if (displayUri != null) {
                    AsyncImage(
                        model = displayUri,
                        contentDescription = "Profile picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Change photo",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = user?.username ?: "Not Loaded",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = user?.email ?: "Not Loaded",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }
    }

    if (showPhotoSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showPhotoSheet = false
            }
        ) {
            Text(
                text = "Profile Photo",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                ListItem(
                    headlineContent = {
                        Text("Take Photo")
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable {
                        showPhotoSheet = false
                        takePicture()
                    }
                )

                ListItem(
                    headlineContent = {
                        Text("Choose from Gallery")
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable {
                        showPhotoSheet = false
                        pickFromGallery()
                    }
                )

                if (displayUri != null) {
                    ListItem(
                        headlineContent = {
                            Text(
                                text = "Delete Photo",
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        modifier = Modifier.clickable {
                            showPhotoSheet = false
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = {
                Text("Delete profile photo?")
            },
            text = {
                Text("This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            displayUri?.let {
                                deleteImageFromStorage(context = context, uri = it)
                            }
                            viewModel.updateProfileImage(null)
                            showDeleteDialog = false
                        }
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
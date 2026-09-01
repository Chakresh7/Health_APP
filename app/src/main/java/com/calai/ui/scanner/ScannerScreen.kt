package com.calai.ui.scanner

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.calai.data.remote.NutritionAnalysis
import java.io.File

private val SoftWhite = Color(0xFFF8F8FA)
private val Ink = Color(0xFF17171B)
private val Muted = Color(0xFF707078)

@Composable
fun ScannerScreen(
	contentResolver: ContentResolver,
	viewModel: ScannerViewModel,
	onAnalysisReady: (NutritionAnalysis, Uri?) -> Unit,
	onAddManually: () -> Unit
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	val context = LocalContext.current
	var cameraVisible by remember { mutableStateOf(false) }
	var cameraDenied by remember { mutableStateOf(false) }
	var description by remember { mutableStateOf("") }
	val galleryLauncher = rememberLauncherForActivityResult(
		ActivityResultContracts.GetContent()
	) { uri: Uri? -> uri?.let { viewModel.analyzeImage(contentResolver, it) } }
	val permissionLauncher = rememberLauncherForActivityResult(
		ActivityResultContracts.RequestPermission()
	) { granted ->
		cameraVisible = granted
		cameraDenied = !granted
	}

	when (val state = uiState) {
		ScannerUiState.Ready -> if (cameraVisible) {
			CameraCapture(
				context = context,
				onCaptured = { viewModel.analyzeImage(contentResolver, it) },
				onClose = { cameraVisible = false }
			)
		} else {
			ScannerActions(
				description = description,
				onDescriptionChange = { description = it },
				onAnalyzeText = { viewModel.analyzeText(description) },
				onChooseImage = { galleryLauncher.launch("image/*") },
				onOpenCamera = { permissionLauncher.launch(android.Manifest.permission.CAMERA) },
				onAddManually = onAddManually,
				cameraDenied = cameraDenied
			)
		}
		ScannerUiState.Analyzing -> LoadingState()
		is ScannerUiState.Success -> {
			LaunchedEffect(state.analysis, state.imageUri) {
				onAnalysisReady(state.analysis, state.imageUri)
			}
			LoadingState()
		}
		is ScannerUiState.Error -> ScannerError(
			message = state.message,
			onRetry = { viewModel.reset() },
			onGallery = {
				viewModel.reset()
				galleryLauncher.launch("image/*")
			},
			onAddManually = {
				viewModel.reset()
				onAddManually()
			}
		)
	}
}

@Composable
private fun ScannerActions(
	description: String,
	onDescriptionChange: (String) -> Unit,
	onAnalyzeText: () -> Unit,
	onChooseImage: () -> Unit,
	onOpenCamera: () -> Unit,
	onAddManually: () -> Unit,
	cameraDenied: Boolean
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(SoftWhite)
			.verticalScroll(rememberScrollState())
			.padding(horizontal = 20.dp, vertical = 16.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		Text("Scan your meal", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
		Text("Snap a photo, pick from gallery, or describe what you ate. Estimates only — not medical advice.", color = Muted)
		if (cameraDenied) {
			Card(
				colors = CardDefaults.cardColors(containerColor = Color.White),
				border = BorderStroke(1.dp, Color(0xFFE2E2E5))
			) {
				Text(
					"Camera access is off. You can still use the gallery, describe the meal, or add it manually.",
					modifier = Modifier.padding(16.dp),
					color = Muted
				)
			}
		}
		Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
			ScanActionCard(
				title = "Take photo",
				subtitle = "Use camera",
				icon = Icons.Outlined.CameraAlt,
				modifier = Modifier.weight(1f),
				onClick = onOpenCamera
			)
			ScanActionCard(
				title = "Gallery",
				subtitle = "Choose image",
				icon = Icons.Outlined.PhotoLibrary,
				modifier = Modifier.weight(1f),
				onClick = onChooseImage
			)
		}
		Card(
			colors = CardDefaults.cardColors(containerColor = Color.White),
			border = BorderStroke(1.dp, Color(0xFFE2E2E5)),
			modifier = Modifier.fillMaxWidth()
		) {
			Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
				Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
					Icon(Icons.Outlined.Edit, contentDescription = null, tint = Ink)
					Text("Describe the meal", fontWeight = FontWeight.Bold)
				}
				OutlinedTextField(
					modifier = Modifier.fillMaxWidth(),
					value = description,
					onValueChange = onDescriptionChange,
					placeholder = { Text("I ate 2 rotis, dal and curd") },
					minLines = 3
				)
				Button(
					modifier = Modifier.fillMaxWidth(),
					onClick = onAnalyzeText,
					colors = ButtonDefaults.buttonColors(containerColor = Ink, contentColor = Color.White)
				) {
					Text("Estimate from description")
				}
			}
		}
		TextButton(modifier = Modifier.fillMaxWidth(), onClick = onAddManually) {
			Text("Add meal manually instead")
		}
		Spacer(modifier = Modifier.height(8.dp))
	}
}

@Composable
private fun ScanActionCard(
	title: String,
	subtitle: String,
	icon: ImageVector,
	modifier: Modifier = Modifier,
	onClick: () -> Unit
) {
	Card(
		modifier = modifier.clickable(onClick = onClick),
		colors = CardDefaults.cardColors(containerColor = Color.White),
		border = BorderStroke(1.dp, Color(0xFFE2E2E5))
	) {
		Column(
			modifier = Modifier.padding(18.dp).fillMaxWidth(),
			verticalArrangement = Arrangement.spacedBy(10.dp)
		) {
			Box(
				modifier = Modifier
					.size(48.dp)
					.background(SoftWhite, CircleShape),
				contentAlignment = Alignment.Center
			) {
				Icon(icon, contentDescription = title, tint = Ink, modifier = Modifier.size(24.dp))
			}
			Text(title, fontWeight = FontWeight.Bold)
			Text(subtitle, color = Muted, style = MaterialTheme.typography.bodySmall)
		}
	}
}

@Composable
private fun LoadingState() {
	Column(
		modifier = Modifier.fillMaxSize().background(SoftWhite).padding(24.dp),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		CircularProgressIndicator(color = Ink)
		Spacer(modifier = Modifier.height(20.dp))
		Text("Identifying food…", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
		Text("Estimating portion and nutrition.", color = Muted, modifier = Modifier.padding(top = 8.dp))
	}
}

@Composable
private fun ScannerError(
	message: String,
	onRetry: () -> Unit,
	onGallery: () -> Unit,
	onAddManually: () -> Unit
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(SoftWhite)
			.padding(20.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		Text("We couldn't analyze that meal", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
		Card(
			colors = CardDefaults.cardColors(containerColor = Color.White),
			border = BorderStroke(1.dp, Color(0xFFE2E2E5))
		) {
			Text(message, modifier = Modifier.padding(16.dp), color = Muted)
		}
		Button(
			modifier = Modifier.fillMaxWidth(),
			onClick = onRetry,
			colors = ButtonDefaults.buttonColors(containerColor = Ink, contentColor = Color.White)
		) { Text("Try again") }
		OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = onGallery) { Text("Choose another photo") }
		TextButton(modifier = Modifier.fillMaxWidth(), onClick = onAddManually) { Text("Enter manually") }
	}
}

@Composable
private fun CameraCapture(
	context: Context,
	onCaptured: (Uri) -> Unit,
	onClose: () -> Unit
) {
	val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
	val imageCapture = remember { ImageCapture.Builder().build() }
	val previewView = remember { PreviewView(context) }

	LaunchedEffect(lifecycleOwner) {
		val cameraProvider = ProcessCameraProvider.getInstance(context).get()
		val preview = Preview.Builder().build().also {
			it.setSurfaceProvider(previewView.surfaceProvider)
		}
		cameraProvider.unbindAll()
		cameraProvider.bindToLifecycle(
			lifecycleOwner,
			CameraSelector.DEFAULT_BACK_CAMERA,
			preview,
			imageCapture
		)
	}

	Column(
		modifier = Modifier.fillMaxSize().background(SoftWhite).padding(20.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		Text("Frame your meal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
		Text("Use good light and fill the frame with the plate.", color = Muted)
		AndroidView(
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f)
				.clip(MaterialTheme.shapes.extraLarge),
			factory = { previewView }
		)
		Button(
			modifier = Modifier.fillMaxWidth().height(56.dp),
			onClick = {
				val file = File.createTempFile("cal_ai_meal_", ".jpg", context.cacheDir)
				val output = ImageCapture.OutputFileOptions.Builder(file).build()
				imageCapture.takePicture(
					output,
					ContextCompat.getMainExecutor(context),
					object : ImageCapture.OnImageSavedCallback {
						override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
							onCaptured(Uri.fromFile(file))
						}

						override fun onError(exception: ImageCaptureException) = Unit
					}
				)
			},
			colors = ButtonDefaults.buttonColors(containerColor = Ink, contentColor = Color.White)
		) { Text("Capture meal") }
		OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = onClose) { Text("Cancel") }
	}
}

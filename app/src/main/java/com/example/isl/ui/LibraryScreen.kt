package com.example.isl.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.isl.data.MediaItem
import com.example.isl.data.MediaType
import androidx.compose.foundation.lazy.items
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.isl.viewmodel.MediaViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    currentScreen: String,
    onScreenChange: (String) -> Unit,
    imageItems: List<MediaItem> = emptyList(),
    videoItems: List<MediaItem> = emptyList(),
    mediaViewModel: MediaViewModel = viewModel()
) {
    val context = LocalContext.current
    
    // Collect data from ViewModel
    val images by mediaViewModel.imageItems.collectAsState()
    val videos by mediaViewModel.videoItems.collectAsState()
    
    // Use ViewModel data if available, otherwise fall back to passed parameters
    val displayImages = if (images.isNotEmpty()) images else imageItems
    val displayVideos = if (videos.isNotEmpty()) videos else videoItems

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Library") },
                actions = {
                    IconButton(onClick = { /* TODO: Search functionality */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        },
        bottomBar = {
            ISLBottomNavigationBar(currentScreen, onScreenChange)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Image Library",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (displayImages.isEmpty()) {
                Text(
                    text = "No images available.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(displayImages) { item ->
                        MediaCard(item)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Video Library",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (displayVideos.isEmpty()) {
                Text(
                    text = "No videos available.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxHeight(),
                    contentPadding = PaddingValues(4.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(displayVideos) { item ->
                        VideoCard(item = item) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.mediaUrl))
                            context.startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MediaCard(item: MediaItem) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            model = item.mediaUrl,
            contentDescription = item.title,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = item.title, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun VideoCard(item: MediaItem, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = item.thumbnailUrl,
            contentDescription = item.title,
            modifier = Modifier
                .height(140.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LibraryScreenPreview() {
    val sampleImages = listOf(
        MediaItem("1", "Hello", "https://img.icons8.com/color/96/hand.png", mediaType = MediaType.IMAGE),
        MediaItem("2", "Goodbye", "https://img.icons8.com/color/96/handshake.png", mediaType = MediaType.IMAGE),
    )

    val sampleVideos = listOf(
        MediaItem(
            "3", "Stop",
            mediaUrl = "https://youtube.com/shorts/eEonBlRRpC0?si=MsKdz-Y_jZDEC6V-",
            thumbnailUrl = "https://img.icons8.com/color/96/hand.png",
            mediaType = MediaType.VIDEO
        ),
        MediaItem(
            "4", "Peace",
            mediaUrl = "https://youtu.be/4RVW29AFib0?si=tyRkwzEW6BM5pb6N",
            thumbnailUrl = "https://img.icons8.com/color/96/peace.png",
            mediaType = MediaType.VIDEO
        )
    )

    LibraryScreen(
        currentScreen = "library",
        onScreenChange = {},
        imageItems = sampleImages,
        videoItems = sampleVideos
    )
}

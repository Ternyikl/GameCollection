package com.example.gamecollection

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

data class Game(
    val id: Int,
    val title: String,
    val platform: String,
    val rating: Int
) {
    init {
        require(rating in 1..10) { "Az értékelés 1 és 10 közötti egész szám kell legyen!" }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                GameMainScreen()
            }
        }
    }
}

@Composable
fun GameMainScreen() {
    val context = LocalContext.current

    val games = remember {
        mutableStateListOf<Game>()
    }

    var nextId: Int by remember {
        mutableStateOf(1)
    }

    var title: String by remember {
        mutableStateOf("")
    }

    var platform: String by remember {
        mutableStateOf("")
    }

    var rating: Int? by remember {
        mutableStateOf(null)
    }

    var titleError: Boolean by remember {
        mutableStateOf(false) }

    var platformError: Boolean by remember {
        mutableStateOf(false)
    }

    var ratingError: Boolean by remember {
        mutableStateOf(false)
    }

    var gameToDelete: Game? by remember {
        mutableStateOf(null)
    }

    var gameToEdit: Game? by remember {
        mutableStateOf(null)
    }

    var searchInput: String by remember {
        mutableStateOf("")
    }

    val searchText: String = searchInput.trim()
    val filteredGames: List<Game> = if (searchText.isEmpty()) {
        games.toList()
    } else {
        games.filter { game: Game ->
            game.title.contains(searchText, ignoreCase = true) ||
                    game.platform.contains(searchText, ignoreCase = true)
        }
    }


    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Játékgyűjtemény",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Kedvenc játékaid egy helyen",
                style = MaterialTheme.typography.bodyLarge
            )

            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = false
                },
                label = { Text("Játék címe") },
                isError = titleError,
                supportingText = {
                    if (titleError)
                        Text("A cím megadása kötelező!")
                                 },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = platform,
                onValueChange = {
                    platform = it
                    platformError = false
                },
                label = { Text("Platform") },
                isError = platformError,
                supportingText = {
                    if (platformError)
                        Text("A platform megadása kötelező!")
                                 },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = rating?.toString() ?: "",
                onValueChange = { input ->
                    rating = input.filter { it.isDigit() }.take(3).toIntOrNull()
                    ratingError = false
                },
                label = { Text("Értékelés") },
                isError = ratingError,
                supportingText = {
                    if (ratingError)
                        Text("Az értékelés 1 és 10 között lehet!")
                                 },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = {
                    val ratingValue: Int? = rating

                    titleError = title.isBlank()
                    platformError = platform.isBlank()
                    if(ratingValue == null || ratingValue !in 1..10)
                        ratingError = true

                    if(titleError || platformError || ratingError  || ratingValue == null){
                        Toast.makeText(
                            context, "Minden mezőt mrg kell megadni!\n" +
                                    "A rating csak 1-10 közötti egész szám lehet!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val newGame = Game(
                            id = nextId,
                            title = title.trim(),
                            platform = platform.trim(),
                            rating = ratingValue
                        )

                        games.add(newGame)
                        nextId++

                        Toast.makeText(
                            context, "Sikeres hozzáadás!",
                            Toast.LENGTH_SHORT
                        ).show()

                        title = ""
                        platform = ""
                        rating = null
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Játék hozzáadása")
            }

            HorizontalDivider()

            Text(
                text = "Játékok száma: ${games.size}",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = searchInput,
                onValueChange = { searchInput = it },
                label = { Text("Keresés cím vagy platform alapján") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (searchText.isNotEmpty()) {
                Text(
                    text = "Találatok: ${filteredGames.size}",
                    style = MaterialTheme.typography.titleSmall
                )
            }

            when {
                games.isEmpty() -> {
                    Text(
                        text = "Még nincs játék a gyűjteményben.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                filteredGames.isEmpty() -> {
                    Text(
                        text = "Nincs a keresésnek megfelelő játék.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items = filteredGames, key = { game: Game -> game.id }) { game: Game ->
                            GameCard(
                                game = game,
                                onEdit = { gameToEdit = game },
                                onDelete = { gameToDelete = game }
                            )
                        }
                    }
                }
            }
        }
    }

    gameToDelete?.let { game ->
        AlertDialog(
            onDismissRequest = { gameToDelete = null },
            title = { Text("Játék törlése") },
            text = { Text("Biztosan törölni szeretnéd a ${game.title} játékot?") },
            confirmButton = {
                TextButton(onClick = {
                    games.removeAll { it.id == game.id }
                    gameToDelete = null
                }) {
                    Text("Törlés")
                }
            },
            dismissButton = {
                TextButton(onClick = { gameToDelete = null }) {
                    Text("Mégse")
                }
            }
        )
    }
    gameToEdit?.let { game ->
        EditGameDialog(
            game = game,
            onDismiss = { gameToEdit = null },
            onSave = { updated ->
                val index: Int = games.indexOfFirst { it.id == updated.id }
                if (index != -1) {
                    games[index] = updated
                }
                gameToEdit = null
                Toast.makeText(context, "Játék sikeresen módosítva!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun EditGameDialog(
    game: Game,
    onDismiss: () -> Unit,
    onSave: (Game) -> Unit
) {
    var editTitle: String by remember {
        mutableStateOf(game.title)
    }

    var editPlatform: String by remember {
        mutableStateOf(game.platform)
    }

    var editRating: Int? by remember {
        mutableStateOf(game.rating)
    }

    var editTitleError: Boolean by remember {
        mutableStateOf(false)
    }

    var editPlatformError: Boolean by remember {
        mutableStateOf(false)
    }

    var editRatingError: Boolean by remember {
        mutableStateOf(false)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Játék szerkesztése") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = editTitle,
                    onValueChange = {
                        editTitle = it
                        editTitleError = false
                    },
                    label = { Text("Játék címe") },
                    isError = editTitleError,
                    supportingText = {
                        if (editTitleError)
                            Text("A cím megadása kötelező!")
                                     },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = editPlatform,
                    onValueChange = {
                        editPlatform = it
                        editPlatformError = false
                    },
                    label = { Text("Platform") },
                    isError = editPlatformError,
                    supportingText = {
                        if (editPlatformError)
                            Text("A platform megadása kötelező!")
                                     },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = editRating?.toString() ?: "",
                    onValueChange = { input ->
                        editRating = input.filter { it.isDigit() }.take(3).toIntOrNull()
                        editRatingError = false
                    },
                    label = { Text("Értékelés") },
                    isError = editRatingError,
                    supportingText = {
                        if (editRatingError)
                            Text("Az értékelés 1 és 10 között lehet!")
                                     },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val ratingValue: Int? = editRating

                editTitleError = editTitle.isBlank()
                editPlatformError = editPlatform.isBlank()
                if(ratingValue == null || ratingValue !in 1..10)
                    editRatingError = true

                if (!editTitleError && !editPlatformError && !editRatingError && ratingValue != null) {
                    onSave(
                        game.copy(
                            title = editTitle.trim(),
                            platform = editPlatform.trim(),
                            rating = ratingValue
                        )
                    )
                }
            }) {
                Text("Mentés")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Mégse")
            }
        }
    )
}

@Composable
fun GameCard(
    game: Game,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GameAvatar(title = game.title)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = game.platform,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Értékelés: ${game.rating} / 10",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = "Szerkesztés"
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "Törlés"
                    )
                }
            }
        }
    }
}

@Composable
fun GameAvatar(title: String, modifier: Modifier = Modifier) {
    val initials: String = title.trim().take(2).uppercase()

    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
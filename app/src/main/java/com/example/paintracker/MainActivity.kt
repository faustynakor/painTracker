package com.example.paintracker

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.paintracker.ui.theme.PainTrackerTheme
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PainTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PainTrackerApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun PainTrackerApp(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("PainTrackerPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putLong("event_date", System.currentTimeMillis()) // Zapisuje timestamp
    editor.apply() // Zatwierdzenie zmian

    // Przechowujemy historię w stanie
    var history by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(Unit) {
        history = loadPainHistory(context)
    }

    // Funkcja, która dodaje aktualną datę i godzinę do historii
    fun addPainRecord() {
        val currentDateTime = getCurrentDateTime()
        history = history + currentDateTime

        val editor = sharedPreferences.edit()
        editor.putStringSet("pain_history", history.toSet())
        editor.apply()
    }

    // Wyświetlamy interfejs
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Przycisk
        Button(onClick = { addPainRecord() }, modifier = Modifier.padding(bottom = 16.dp)) {
            Text("Zarejestruj ból nóg")
        }

        // Historia
        Text("Historia:", modifier = Modifier.padding(bottom = 8.dp))
        for (record in history) {
            Text("- $record")
        }
    }
}

fun loadPainHistory(context: Context): List<String> {
    val sharedPreferences = context.getSharedPreferences("PainTrackerPrefs", Context.MODE_PRIVATE)

    return  sharedPreferences.getStringSet("pain_history", emptySet())?.toList() ?: listOf()
}

// Funkcja do pobrania aktualnej daty i godziny
fun getCurrentDateTime(): String {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
    return dateFormat.format(Date())
}

@Preview(showBackground = true)
@Composable
fun PainTrackerAppPreview() {
    PainTrackerTheme {
        PainTrackerApp()
    }
}

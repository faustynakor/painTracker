package com.example.paintracker

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.paintracker.ui.theme.PainTrackerTheme
import java.text.SimpleDateFormat
import java.util.*

class PainTrackerWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        // Przejdź przez wszystkie instancje widgetu
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_pain_tracker)

            // Dodaj akcję do przycisku
            val intent = Intent(context, PainTrackerWidget::class.java).apply {
                action = "com.example.paintracker.ADD_DATE"
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.add_pain_button, pendingIntent)

            // Zaktualizuj widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        // Obsługa kliknięcia w przycisk
        if (intent.action == "com.example.paintracker.ADD_DATE") {
            addPainRecord(context)
        }
    }

    private fun addPainRecord(context: Context) {
        // Pobierz aktualną historię
        val sharedPreferences = context.getSharedPreferences("PainTrackerPrefs", Context.MODE_PRIVATE)
        val history = sharedPreferences.getStringSet("pain_history", emptySet())?.toMutableSet() ?: mutableSetOf()

        // Dodaj aktualną datę
        val currentDateTime = getCurrentDateTime()
        history.add(currentDateTime)

        // Zapisz zaktualizowaną historię
        val editor = sharedPreferences.edit()
        editor.putStringSet("pain_history", history)
        editor.apply()
    }

    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }
}


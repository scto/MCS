package com.scto.mcs.core.utils

import android.content.Context
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import kotlinx.coroutines.*

/*
class EditorActivity : AppCompatActivity() {
    private lateinit var autoSaveManager: AutoSaveManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        val editor = findViewById<EditText>(R.id.code_editor)
        val currentPath = "/sdcard/WebIDE/Project/Main.java"

        autoSaveManager = AutoSaveManager(this)
        
        // Status-Anzeige in der UI (optional)
        autoSaveManager.onSaveStatusChanged = { isSaved ->
            if (isSaved) {
                supportActionBar?.subtitle = "Alle Änderungen gespeichert"
            } else {
                supportActionBar?.subtitle = "Tippen..."
            }
        }

        autoSaveManager.startMonitoring(editor, currentPath)
    }

    override fun onDestroy() {
        super.onDestroy()
        autoSaveManager.stopMonitoring()
    }
}

### Was dieses System bietet:
1.  **Debouncing**: Der Speicherprozess startet erst, wenn der Nutzer für 3 Sekunden (konfigurierbar) aufgehört hat zu schreiben. Das schont den Akku und den Flash-Speicher.
2.  **Hintergrundverarbeitung**: Durch `Dispatchers.IO` wird der Haupt-Thread niemals blockiert, selbst wenn die Datei sehr groß ist.
3.  **Draft-System**: Neben der eigentlichen Datei wird im `cacheDir` ein Backup erstellt. Wenn die App während eines Schreibvorgangs abstürzt, kann beim nächsten Start geprüft werden, ob ein neuerer Entwurf vorliegt.
4.  **UI-Feedback**: Über den `onSaveStatusChanged` Callback kannst du dem Nutzer visuell anzeigen (z. B. ein kleines Cloud-Icon oder Text im Untertitel), ob sein Fortschritt sicher ist.
*/

/**
 * Manager für das automatische Speichern von Code-Änderungen.
 */
class AutoSaveManager(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + Job())
) {

    private var autoSaveJob: Job? = null
    private var isDirty: Boolean = false
    
    // Callback, um die UI über den Speicherstatus zu informieren (z.B. "Gespeichert"-Icon)
    var onSaveStatusChanged: ((Boolean) -> Unit)? = null

    /**
     * Überwacht einen EditText auf Änderungen und startet den Auto-Save-Timer.
     * Nutzt "Debouncing": Speichert erst, wenn der Nutzer X Sekunden nicht getippt hat.
     */
    fun startMonitoring(editor: EditText, currentFilePath: String, delayMs: Long = 3000) {
        editor.doAfterTextChanged { text ->
            isDirty = true
            onSaveStatusChanged?.invoke(false) // Status: Ungespeichert
            
            // Bestehenden Timer abbrechen und neu starten
            autoSaveJob?.cancel()
            autoSaveJob = scope.launch {
                delay(delayMs)
                if (isDirty) {
                    performAutoSave(currentFilePath, text.toString())
                }
            }
        }
    }

    /**
     * Führt den eigentlichen Speichervorgang im Hintergrund aus.
     */
    private suspend fun performAutoSave(path: String, content: String) {
        withContext(Dispatchers.IO) {
            // 1. In die echte Datei schreiben
            val success = FileManager.saveFile(path, content)
            
            // 2. Zusätzlich einen Sicherheits-Entwurf im Cache anlegen
            val fileName = File(path).name
            FileManager.saveDraft(context, fileName, content)
            
            if (success) {
                isDirty = false
                withContext(Dispatchers.Main) {
                    onSaveStatusChanged?.invoke(true) // Status: Gespeichert
                }
            }
        }
    }

    /**
     * Stoppt das Monitoring und räumt Ressourcen auf.
     */
    fun stopMonitoring() {
        autoSaveJob?.cancel()
        scope.cancel()
    }
}
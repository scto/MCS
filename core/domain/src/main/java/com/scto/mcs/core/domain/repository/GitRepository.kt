package com.scto.mcs.core.domain.repository

import com.scto.mcs.core.domain.model.*

/**
 * Erweitertes Interface für alle Git-Operationen in der MCS-Core-Domain.
 */
interface GitRepository {
    
    /** * Klont ein Repository von einer URL in das Zielverzeichnis.
     * @param recursive Wenn true, werden auch alle Submodule geklont.
     */
    suspend fun clone(url: String, destinationPath: String, recursive: Boolean = false): Result<Unit>

    /** * Liefert den aktuellen Status des Arbeitsverzeichnisses (staged, unstaged, untracked). 
     */
    suspend fun getStatus(repoPath: String): Result<GitStatus>

    /** * Liefert die Commit-Historie des aktuellen Branches. 
     */
    suspend fun getHistory(repoPath: String, limit: Int = 50): Result<List<GitCommit>>

    /** * Liefert eine Liste aller lokalen Branches. 
     */
    suspend fun getBranches(repoPath: String): Result<List<GitBranch>>

    /** * Fügt Dateien dem Index hinzu (git add). 
     */
    suspend fun stageFiles(repoPath: String, filePatterns: List<String>): Result<Unit>
    
    /** * Nimmt Dateien aus dem Index heraus (git reset). 
     */
    suspend fun unstageFiles(repoPath: String, filePatterns: List<String>): Result<Unit>

    /** * Erstellt einen neuen Commit mit der angegebenen Nachricht. 
     */
    suspend fun commit(repoPath: String, message: String): Result<GitCommit>

    /** * Wechselt den Branch oder erstellt einen neuen. 
     */
    suspend fun checkout(repoPath: String, branchName: String, createNew: Boolean = false): Result<Unit>

    /** * Holt Änderungen vom Remote-Repository (Pull). 
     */
    suspend fun pull(repoPath: String): Result<Unit>

    /** * Überträgt lokale Commits an das Remote-Repository (Push). 
     */
    suspend fun push(repoPath: String): Result<Unit>

    /** * Berechnet Zeilen-Unterschiede (Diff) für eine spezifische Datei im Vergleich zu HEAD. 
     */
    suspend fun getDiffForFile(repoPath: String, filePath: String): Result<List<LineDiff>>

    /**
     * Markiert ein Verzeichnis global als sicher (git config global safe.directory).
     * Dies verhindert "dubious ownership"-Fehler in Umgebungen wie Android/PRoot.
     */
    suspend fun setSafeDirectory(path: String): Result<Unit>
}
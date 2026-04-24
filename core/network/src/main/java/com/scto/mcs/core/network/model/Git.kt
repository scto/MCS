package core.domain.model

import kotlinx.uuid.UUID

/**
 * Repräsentiert den aktuellen Status des Git-Arbeitsverzeichnisses.
 */
data class GitStatus(
    val staged: List<String>,
    val unstaged: List<String>,
    val untracked: List<String>,
    val conflicts: List<String>
)

/**
 * Repräsentiert einen Git-Commit.
 */
data class GitCommit(
    val id: String,
    val author: String,
    val message: String,
    val timestamp: Long
)

/**
 * Repräsentiert einen Branch.
 */
data class GitBranch(
    val name: String,
    val isCurrent: Boolean,
    val isRemote: Boolean
)

/**
 * Repräsentiert ein Remote-Repository.
 */
data class GitRemote(
    val name: String,
    val url: String
)
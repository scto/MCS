package com.scto.mcs.core.git.repository

import com.scto.mcs.core.domain.model.*
import com.scto.mcs.core.domain.repository.DiffType
import com.scto.mcs.core.domain.repository.GitRepository
import com.scto.mcs.core.domain.repository.LineDiff

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.diff.RawTextComparator
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.StoredConfig
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.storage.file.FileBasedConfig
import org.eclipse.jgit.util.FS
import org.eclipse.jgit.util.SystemReader
import org.eclipse.jgit.util.io.DisabledOutputStream

import java.io.File
import javax.inject.Inject

/**
 * Erweiterte JGit-Implementierung für die Smartphone-IDE.
 */
class JGitRepositoryImpl @Inject constructor() : GitRepository {

    override suspend fun getStatus(repoPath: String): Result<GitStatus> = withContext(Dispatchers.IO) {
        runCatching {
            Git.open(File(repoPath)).use { git ->
                val status = git.status().call()
                GitStatus(
                    staged = status.changed.toList() + status.added.toList() + status.removed.toList(),
                    unstaged = status.modified.toList() + status.missing.toList(),
                    untracked = status.untracked.toList(),
                    conflicts = status.conflicting.toList()
                )
            }
        }
    }

    override suspend fun clone(
        url: String, 
        destinationPath: String, 
        recursive: Boolean
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            Git.cloneRepository()
                .setURI(url)
                .setDirectory(File(destinationPath))
                .setCloneSubmodules(recursive)
                .call()
                .use { /* Repository offen lassen oder schließen */ }
            Unit
        }
    }

    override suspend fun pull(repoPath: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            Git.open(File(repoPath)).use { git ->
                git.pull().call()
                Unit
            }
        }
    }

    override suspend fun push(repoPath: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            Git.open(File(repoPath)).use { git ->
                // Hinweis: Hier könnten Credentials (UsernamePasswordCredentialsProvider) nötig sein
                git.push().call()
                Unit
            }
        }
    }

    /**
     * Konfiguriert das Verzeichnis global als "safe.directory", um Berechtigungsprobleme
     * unter Android/Linux (z.B. in der PRoot-Umgebung) zu vermeiden.
     */
    override suspend fun setSafeDirectory(path: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val userConfig = SystemReader.getInstance().openUserConfig(null, FS.DETECTED)
            // Falls 'path' gleich "*" ist, werden alle Verzeichnisse als sicher markiert
            userConfig.setString("safe", null, "directory", path)
            userConfig.save()
            Unit
        }
    }

    override suspend fun getHistory(repoPath: String, limit: Int): Result<List<GitCommit>> = withContext(Dispatchers.IO) {
        runCatching {
            Git.open(File(repoPath)).use { git ->
                git.log().setMaxCount(limit).call().map { it.toDomain() }
            }
        }
    }

    override suspend fun getBranches(repoPath: String): Result<List<GitBranch>> = withContext(Dispatchers.IO) {
        runCatching {
            Git.open(File(repoPath)).use { git ->
                val currentBranch = git.repository.branch
                git.branchList().call().map { ref ->
                    val name = ref.name.removePrefix("refs/heads/")
                    GitBranch(
                        name = name,
                        isCurrent = name == currentBranch,
                        isRemote = false
                    )
                }
            }
        }
    }

    override suspend fun checkout(repoPath: String, branchName: String, createNew: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            Git.open(File(repoPath)).use { git ->
                git.checkout()
                    .setName(branchName)
                    .setCreateBranch(createNew)
                    .call()
                Unit
            }
        }
    }

    override suspend fun commit(repoPath: String, message: String): Result<GitCommit> = withContext(Dispatchers.IO) {
        runCatching {
            Git.open(File(repoPath)).use { git ->
                val rev = git.commit().setMessage(message).call()
                rev.toDomain()
            }
        }
    }

    override suspend fun stageFiles(repoPath: String, filePatterns: List<String>): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            Git.open(File(repoPath)).use { git ->
                val add = git.add()
                filePatterns.forEach { add.addFilepattern(it) }
                add.call()
                Unit
            }
        }
    }

    override suspend fun unstageFiles(repoPath: String, filePatterns: List<String>): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            Git.open(File(repoPath)).use { git ->
                val reset = git.reset()
                filePatterns.forEach { reset.addPath(it) }
                reset.call()
                Unit
            }
        }
    }

    override suspend fun getDiffForFile(repoPath: String, filePath: String): Result<List<LineDiff>> = withContext(Dispatchers.IO) {
        runCatching {
            Git.open(File(repoPath)).use { git ->
                val head = git.repository.resolve(Constants.HEAD) ?: return@runCatching emptyList()
                val diffs = mutableListOf<LineDiff>()
                val df = DiffFormatter(DisabledOutputStream.INSTANCE).apply {
                    setRepository(git.repository)
                    setDiffComparator(RawTextComparator.DEFAULT)
                }
                val entries = git.diff()
                    .setOldTree(git.repository.newObjectReader().use { 
                        org.eclipse.jgit.treewalk.CanonicalTreeParser(null, it, head) 
                    })
                    .setPathFilter(org.eclipse.jgit.treewalk.filter.PathFilter.create(filePath))
                    .call()
                for (entry in entries) {
                    df.toFileHeader(entry).toEditList().forEach { edit ->
                        val type = when(edit.type) {
                            org.eclipse.jgit.diff.Edit.Type.INSERT -> DiffType.ADDED
                            org.eclipse.jgit.diff.Edit.Type.DELETE -> DiffType.DELETED
                            else -> DiffType.MODIFIED
                        }
                        for (i in edit.beginB until edit.endB) diffs.add(LineDiff(i + 1, type))
                    }
                }
                diffs
            }
        }
    }

    private fun RevCommit.toDomain() = GitCommit(
        id = name,
        author = authorIdent.name,
        message = shortMessage,
        timestamp = commitTime.toLong() * 1000
    )
}
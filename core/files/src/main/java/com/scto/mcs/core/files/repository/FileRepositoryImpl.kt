// ... (vorheriger Code bleibt gleich, füge diese Methoden hinzu)

    override suspend fun saveInternalScript(name: String, content: String): Result<FileItem> = withContext(Dispatchers.IO) {
        runCatching {
            val binDir = getBinDir().getOrThrow()
            val file = File(binDir.path, name)
            file.writeText(content)
            file.setExecutable(true)
            FileItem(name = name, path = file.absolutePath, isDirectory = false)
        }
    }

    override suspend fun readAsset(path: String): String = withContext(Dispatchers.IO) {
        context.assets.open(path).bufferedReader().use { it.readText() }
    }

package com.scto.mcs.core.editor.tabs.base

import com.scto.mcs.app.activities.MainViewModel
import com.scto.mcs.core.files.BuiltinFileType
import com.scto.mcs.core.files.FileObject
import com.scto.mcs.core.files.FileTypeManager
import com.scto.mcs.core.editor.tabs.image.ImageTab

fun interface TabFactory {
    fun createTab(file: FileObject, projectRoot: FileObject?, viewModel: MainViewModel): Tab
}

object TabRegistry {
    private val registeredTabs = mutableMapOf<String, TabFactory>()

    fun registerTab(tabFactory: TabFactory, fileExtensions: List<String>) {
        fileExtensions.forEach { registeredTabs[it] = tabFactory }
    }

    fun unregisterTab(tabFactory: TabFactory) {
        registeredTabs.values.remove(tabFactory)
    }

    fun getTab(file: FileObject, projectRoot: FileObject?, viewModel: MainViewModel): Tab {
        val ext = file.getExtension()
        val type = FileTypeManager.fromExtension(ext)

        if (registeredTabs.containsKey(ext)) {
            return registeredTabs[ext]!!.createTab(file, projectRoot, viewModel)
        }

        return when (type) {
            BuiltinFileType.IMAGE -> ImageTab(file)
            else -> viewModel.editorManager.createEditorTab(file, projectRoot)
        }
    }
}

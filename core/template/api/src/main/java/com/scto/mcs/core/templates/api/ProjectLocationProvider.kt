package com.scto.mcs.core.templates.api

import java.io.File

interface ProjectLocationProvider {
    fun getRootTargetDir(): File
}

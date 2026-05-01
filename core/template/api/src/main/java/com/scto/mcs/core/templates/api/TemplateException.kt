package com.scto.mcs.core.templates.api

sealed class TemplateException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class DownloadException(message: String, cause: Throwable? = null) : TemplateException(message, cause)
    class InstallationException(message: String, cause: Throwable? = null) : TemplateException(message, cause)
    class TemplateNotFoundException(id: String) : TemplateException("Template with id $id not found")
    class InvalidTemplateException(message: String) : TemplateException(message)
}

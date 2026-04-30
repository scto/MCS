package com.scto.mcs.core.terminalold

import android.content.Context
import com.srvhive.app.utils.ProotProcessWrapper as ModernProotProcessWrapper
import java.io.File
import javax.inject.Inject

/**
 * Implementierung des ProotProcessWrapper, die den modernen Wrapper delegiert.
 */
class ProotProcessWrapper @Inject constructor(
    private val context: Context
) {

    fun createBuilder(): ModernProotProcessWrapper.Builder {
        return ModernProotProcessWrapper.Builder(context)
    }

    // Beispiel für eine direkte Delegierung, falls benötigt
    fun start(builder: ModernProotProcessWrapper.Builder, vararg command: String): Process {
        return builder.build().start(*command)
    }
}

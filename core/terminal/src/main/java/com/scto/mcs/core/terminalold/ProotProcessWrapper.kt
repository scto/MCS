package com.scto.mcs.core.terminalold

import android.content.Context
import com.srvhive.app.utils.ProotProcessWrapper as ModernProotProcessWrapper
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

    // Delegierung der Start-Funktionalität
    fun start(builder: ModernProotProcessWrapper.Builder, vararg command: String): Process {
        return builder.build().start(*command)
    }
}

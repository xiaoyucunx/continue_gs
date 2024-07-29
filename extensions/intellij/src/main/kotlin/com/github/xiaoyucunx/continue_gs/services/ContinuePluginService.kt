package com.github.xiaoyucunx.continue_gs.services

import com.github.xiaoyucunx.continue_gs.`continue`.CoreMessenger
import com.github.xiaoyucunx.continue_gs.`continue`.IdeProtocolClient
import com.github.xiaoyucunx.continue_gs.`continue`.uuid
import com.github.xiaoyucunx.continue_gs.toolWindow.ContinueBrowser
import com.github.xiaoyucunx.continue_gs.toolWindow.ContinuePluginToolWindowFactory
import com.google.gson.Gson
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.ui.jcef.executeJavaScriptAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.UUID

@Service(Service.Level.PROJECT)
class ContinuePluginService(project: Project) : Disposable, DumbAware {
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    var continuePluginWindow: ContinuePluginToolWindowFactory.ContinuePluginWindow? = null

    var ideProtocolClient: IdeProtocolClient? = null
    var coreMessenger: CoreMessenger? = null
    var workspacePaths: Array<String>? = null
    var windowId: String = UUID.randomUUID().toString()

    override fun dispose() {
        coroutineScope.cancel()
    }

    fun launchInScope(block: suspend CoroutineScope.() -> Unit) {
        coroutineScope.launch {
            block()
        }
    }

    fun sendToWebview(
        messageType: String,
        data: Any?,
        messageId: String = uuid()
    ) {
        continuePluginWindow?.browser?.sendToWebview(messageType, data, messageId)
    }
}
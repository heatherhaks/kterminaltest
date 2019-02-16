@file:JvmName("DesktopLauncher")

package com.github.heatherhaks.kterminal.desktop

import com.badlogic.gdx.Files
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.github.heatherhaks.kterminal.KTerminal


/** Launches the desktop (LWJGL) application. */
fun main() {
    LwjglApplication(KTerminal(), LwjglApplicationConfiguration().apply {
        title = "kterminal"
        width = 640
        height = 480
        resizable = false
        intArrayOf(128, 64, 32, 16).forEach{
            addIcon("libgdx$it.png", Files.FileType.Internal)
        }
    })
}

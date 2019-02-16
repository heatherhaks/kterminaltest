@file:JvmName("DesktopLauncher")

package com.heatherhaks.kterminal.desktop

import com.badlogic.gdx.Files
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration

import com.heatherhaks.kterminal.KTerminal

/** Launches the desktop (LWJGL) application. */
fun main(args: Array<String>) {
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

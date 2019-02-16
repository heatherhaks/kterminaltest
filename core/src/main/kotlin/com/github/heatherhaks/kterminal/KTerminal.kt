package com.github.heatherhaks.kterminal

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.github.heatherhaks.kterminal.kterminal.KTerminalData
import com.github.heatherhaks.kterminal.kterminal.KTerminalRenderer
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.graphics.use

class FirstScreen : KtxScreen {
    private val batch = SpriteBatch()

    private val kTerminalRenderer = KTerminalRenderer(batch, "fontSheet.png")
    private val kTerminalData = KTerminalData(16, 16)

    override fun render(delta: Float) {
        clearScreen(0.0f, 0.0f, 0.0f)
        kTerminalData.clearAll()
        kTerminalData[0,0].write("Hello, World!")

        batch.use {
            kTerminalRenderer.render(0f, 0f, kTerminalData)
        }
    }

    override fun dispose() {
        batch.dispose()
    }
}

class KTerminal : KtxGame<KtxScreen>() {
    override fun create() {
        addScreen(FirstScreen())
        setScreen<FirstScreen>()
    }
}

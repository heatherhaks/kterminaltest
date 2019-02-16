package com.github.heatherhaks.kterminal.kterminal

import com.badlogic.gdx.graphics.Color

data class KTerminalGlyph @JvmOverloads constructor(var value: Int,
                          var foregroundColor: Color,
                          var backgroundColor: Color,
                          var rotation: Float = 0f,
                          var scale: Float = 1f,
                          var isFlippedX: Boolean = false,
                          var isFlippedY: Boolean = false) {

    @JvmOverloads constructor(char: Char,
                              foregroundColor: Color,
                              backgroundColor: Color,
                              rotation: Float = 0f,
                              scale: Float = 1f,
                              isFlippedX: Boolean = false,
                              isFlippedY: Boolean = false) : this(char.toInt(), foregroundColor, backgroundColor, rotation, scale, isFlippedX, isFlippedY)


    var char: Char
        set(input) {
            value = input.toInt()
        }
        get() {
            return value.toChar()
        }

    @JvmOverloads fun set(value: Int, foregroundColor: Color, backgroundColor: Color, rotation: Float = 0f, scale: Float = 1f, isFlippedX: Boolean = false, isFlippedY: Boolean = false) {
        this.value = value
        this.foregroundColor = foregroundColor
        this.backgroundColor = backgroundColor
        this.rotation = rotation
        this.scale = scale
        this.isFlippedX = isFlippedX
        this.isFlippedY = isFlippedY
    }

    @JvmOverloads fun set(char: Char, foregroundColor: Color, backgroundColor: Color, rotation: Float = 0f, scale: Float = 1f, isFlippedX: Boolean = false, isFlippedY: Boolean = false) {
        set(char.toInt(), foregroundColor, backgroundColor, rotation, scale, isFlippedX, isFlippedY)
    }


    fun set(glyph: KTerminalGlyph) {
        set(glyph.char, glyph.foregroundColor, glyph.backgroundColor, glyph.rotation, glyph.scale, glyph.isFlippedX, glyph.isFlippedY)
    }

    fun copy() : KTerminalGlyph {
        return KTerminalGlyph(value, foregroundColor, backgroundColor, rotation, scale, isFlippedX, isFlippedY)
    }
}

package com.heatherhaks.kterminal

import com.badlogic.gdx.graphics.Color
import com.heatherhaks.kterminal.KTerminalShapePlotter.Companion.plotCircle
import com.heatherhaks.kterminal.KTerminalShapePlotter.Companion.plotEllipse
import com.heatherhaks.kterminal.KTerminalShapePlotter.Companion.plotLine
import com.heatherhaks.kterminal.KTerminalShapePlotter.Companion.plotRect
import com.heatherhaks.kterminal.KTerminalShapePlotter.Companion.plotTriangle
import ktx.graphics.copy

@Suppress("unused", "MemberVisibilityCanBePrivate")
class KTerminalData(width: Int,
                    height: Int,
                    var defaultForegroundColor: Color = Color.WHITE.copy(),
                    var defaultBackgroundColor: Color = Color.BLACK.copy()
) {
    var width: Int = width
        set(value) {
            if(value > 0) {
                field = value
            } else {
                throw IllegalArgumentException("Width can't be 0 or below.")
            }
        }
    var height: Int = height
        set(value) {
            if(value > 0) {
                field = value
            } else {
                throw IllegalArgumentException("Height can't be 0 or below.")
            }
        }

    var terminal = Array(width) {
        Array(height) {
            KTerminalGlyph(' ', defaultForegroundColor, defaultBackgroundColor)
        }
    }

    //will not preserve terminal data
    fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height

        terminal = Array(width) {
            Array(height) {
                KTerminalGlyph(' ', defaultForegroundColor, defaultBackgroundColor)
            }
        }
    }

    inner class Cursor(x: Int, y: Int, var foregroundColor: Color, var backgroundColor: Color, var rotation: Float, var scale: Float, var isFlippedX: Boolean, var isFlippedY: Boolean) {
        var x: Int = x
            set(value) {
                var tempValue = value

                while(tempValue < 0 || tempValue >= width) {
                    if(tempValue < 0) {
                        tempValue += width
                    } else if(tempValue >= width) {
                        tempValue -= width
                    }
                }

                field = tempValue
            }
        var y: Int = y
            set(value) {
                var tempValue = value

                while(tempValue < 0 || tempValue >= height) {
                    if(tempValue < 0) {
                        tempValue += height
                    } else if(tempValue >= height) {
                        tempValue -= height
                    }
                }

                field = tempValue
            }

        fun set(x: Int, y: Int, foregroundColor: Color, backgroundColor: Color, rotation: Float, scale: Float, isFlippedX: Boolean, isFlippedY: Boolean) {
            this.x = x
            this.y = y
            this.foregroundColor = foregroundColor
            this.backgroundColor = backgroundColor
            this.rotation = rotation
            this.scale = scale
            this.isFlippedX = isFlippedX
            this.isFlippedY = isFlippedY
        }

        fun set(x: Int, y: Int) {
            this.x = x
            this.y = y
        }

        fun set(cursor: Cursor) {
            set(cursor.x, cursor.y, cursor.foregroundColor, cursor.backgroundColor, cursor.rotation, cursor.scale, cursor.isFlippedX, cursor.isFlippedY)
        }
    }

    private val cursor: Cursor = Cursor(0, 0, defaultForegroundColor, defaultBackgroundColor, 0f, 1f, false, false)
    private val workingCursor: Cursor = Cursor(0, 0, defaultForegroundColor, defaultBackgroundColor, 0f, 1f, false, false)

    @JvmOverloads fun setCursor(x: Int = 0, y: Int = 0, foregroundColor: Color = defaultForegroundColor, backgroundColor: Color = defaultBackgroundColor, rotation: Float = 0f, scale: Float = 1f, isFlippedX: Boolean = false, isFlippedY: Boolean = false) {
        cursor.set(x, y, foregroundColor, backgroundColor, rotation, scale,  isFlippedX, isFlippedY)
        workingCursor.set(cursor)
    }

    fun resetCursor() {
        setCursor(0, 0, defaultForegroundColor, defaultBackgroundColor, 0f, 1f, false, false)
    }

    fun setCursorPosition(x: Int, y: Int) : KTerminalData {
        cursor.x = x
        cursor.y = y
        workingCursor.set(cursor)

        return this
    }

    fun setCursorColor(foregroundColor: Color, backgroundColor: Color) : KTerminalData {
        cursor.foregroundColor = foregroundColor
        cursor.backgroundColor = backgroundColor

        workingCursor.foregroundColor = foregroundColor
        workingCursor.backgroundColor = backgroundColor
        return this
    }

    fun setCursorScale(scale: Float) : KTerminalData {
        cursor.scale = scale
        workingCursor.scale = scale

        return this
    }

    fun setCursorRotation(rotation: Float) : KTerminalData {
        cursor.rotation = rotation
        workingCursor.rotation = rotation

        return this
    }

    fun setCursorFlip(isFlippedX: Boolean, isFlippedY: Boolean) : KTerminalData {
        cursor.isFlippedX = isFlippedX
        cursor.isFlippedY = isFlippedY
        workingCursor.isFlippedX = isFlippedX
        workingCursor.isFlippedY = isFlippedY

        return this
    }

    //position brackets
    operator fun get(x: Int, y: Int) : KTerminalData {
        return setCursorPosition(x, y)
    }

    //color brackets, can't do floats because rotation and scale use floats
    operator fun get(foregroundColor: Color, backgroundColor: Color) : KTerminalData {
        return setCursorColor(foregroundColor, backgroundColor)
    }

    //rotation and scale brackets
    operator fun get(rotation: Float, scale: Float) : KTerminalData {
        return setCursorRotation(rotation).setCursorScale(scale)
    }

    //flip status brackets
    operator fun get(isFlippedX: Boolean, isFlippedY: Boolean) : KTerminalData {
        return setCursorFlip(isFlippedX, isFlippedY)
    }

    //override any cursor data given by the glyph
    fun write(glyph: KTerminalGlyph) {
        workingCursor.foregroundColor = glyph.foregroundColor
        workingCursor.backgroundColor = glyph.backgroundColor
        workingCursor.rotation = glyph.rotation
        workingCursor.scale = glyph.scale
        workingCursor.isFlippedX = glyph.isFlippedX
        workingCursor.isFlippedY = glyph.isFlippedY

        write(glyph.value)
    }

    fun write(value: Int) {
        terminal[workingCursor.x][workingCursor.y].apply {
            this.value = value
            this.foregroundColor = workingCursor.foregroundColor
            this.backgroundColor = workingCursor.backgroundColor
            this.rotation = workingCursor.rotation
            this.scale = workingCursor.scale
            this.isFlippedX = workingCursor.isFlippedX
            this.isFlippedY = workingCursor.isFlippedY
        }

        workingCursor.set(cursor)
    }

    fun write(char: Char) {
        write(char.toInt())
    }

    @JvmOverloads fun write(string: String, direction: Int = WRITE_LEFT_TO_RIGHT, wrapping: Int = WRAP_NONE) {
        var posX = cursor.x
        var posY = cursor.y
        var isWriting = true

        string.toCharArray().forEach {
            if(isWriting) {
                workingCursor.set(posX, posY)
                write(it)

                when(direction) {
                    WRITE_LEFT_TO_RIGHT -> posX++
                    WRITE_TOP_TO_BOTTOM -> posY++
                    WRITE_RIGHT_TO_LEFT -> posX--
                    WRITE_BOTTOM_TO_TOP -> posY--
                }

                when(direction) {
                    WRITE_LEFT_TO_RIGHT, WRITE_RIGHT_TO_LEFT -> {
                        if(posX >= width) {
                            posX = 0

                            when(wrapping) {
                                WRAP_NONE -> isWriting = false
                                WRAP_POSITIVE_SHIFT -> posY++
                                WRAP_NEGATIVE_SHIFT -> posY--
                            }
                        } else if(posX < 0) {
                            posX = width - 1

                            when(wrapping) {
                                WRAP_NONE -> isWriting = false
                                WRAP_POSITIVE_SHIFT -> posY++
                                WRAP_NEGATIVE_SHIFT -> posY--
                            }
                        }
                    }
                    WRITE_TOP_TO_BOTTOM, WRITE_BOTTOM_TO_TOP -> {
                        if(posY >= height) {
                            posY = 0

                            when(wrapping) {
                                WRAP_NONE -> isWriting = false
                                WRAP_POSITIVE_SHIFT -> posX++
                                WRAP_NEGATIVE_SHIFT -> posX--
                            }
                        } else if(posY < 0) {
                            posY = height - 1

                            when(wrapping) {
                                WRAP_NONE -> isWriting = false
                                WRAP_POSITIVE_SHIFT -> posX++
                                WRAP_NEGATIVE_SHIFT -> posX--
                            }
                        }
                    }
                }
            }
        }
    }

    @JvmOverloads fun clear(width: Int = 1, height: Int = 1) {
        workingCursor.foregroundColor = defaultForegroundColor
        workingCursor.backgroundColor = defaultBackgroundColor
        workingCursor.rotation = 0f
        workingCursor.scale = 1f

        drawRect(width, height, 0, true)
    }

    fun clearAll() {
        workingCursor.set(0, 0, defaultForegroundColor, defaultBackgroundColor, 0f, 1f, false, false)
        clear(width, height)
    }

    private fun fillShape(shapeList: List<Pair<Int, Int>>, value: Int, foregroundColor: Color, backgroundColor: Color) {
        val shapeListSorted = shapeList.sortedWith(compareBy<Pair<Int, Int>>{it.second}.thenBy { it.first })

        val firstY = shapeListSorted.first().second
        val lastY = shapeListSorted.last().second

        shapeListSorted.forEachIndexed { index, pair ->
            if(index + 1 < shapeListSorted.size) {
                if(pair.second != firstY && pair.second != lastY) {
                    if( pair.second == shapeListSorted[index + 1].second
                            && pair.first <= shapeListSorted[index + 1].first - 2) {
                        for(i in pair.first + 1 until shapeListSorted[index + 1].first) {
                            workingCursor.x = i
                            workingCursor.y = pair.second
                            workingCursor.foregroundColor = foregroundColor
                            workingCursor.backgroundColor = backgroundColor
                            write(value)
                        }
                    }
                }
            }
        }
    }

    private fun drawShape(shapeList: List<Pair<Int, Int>>, value: Int) {
        shapeList.forEach {
            workingCursor.x = it.first
            workingCursor.y = it.second
            write(value)
        }
    }

    fun drawRect(width: Int,
                 height: Int,
                 char: Char = ' ',
                 isFilled: Boolean = false,
                 fillChar: Char = char,
                 fillForeground: Color,
                 fillBackground: Color) {
        drawRect(width, height, char.toInt(), isFilled, fillChar.toInt(), fillForeground, fillBackground)
    }

    @JvmOverloads fun drawRect(width: Int,
                               height: Int,
                               value: Int,
                               isFilled: Boolean = false,
                               fillValue: Int = value,
                               fillForeground: Color = cursor.foregroundColor,
                               fillBackground: Color = cursor.backgroundColor) {
        val plotList = plotRect(cursor.x, cursor.y, width, height)

        drawShape(plotList, value)
        if(isFilled) fillShape(plotList, fillValue, fillForeground, fillBackground)
    }

    fun drawLine(endX: Int, endY: Int, char: Char) {
        drawLine(endX, endY, char.toInt())
    }
    fun drawLine(endX: Int, endY: Int, value: Int) {
        val linePlot = plotLine(cursor.x, cursor.y, endX, endY).toList()
        drawShape(linePlot, value)
    }

    @JvmOverloads fun drawEllipse(width: Int,
                                  height: Int,
                                  char: Char,
                                  isFilled: Boolean = false,
                                  fillChar: Char = ' ',
                                  fillForeground: Color = cursor.foregroundColor,
                                  fillBackground: Color = cursor.backgroundColor) {
        drawEllipse(width, height, char.toInt(), isFilled, fillChar.toInt(), fillForeground, fillBackground)
    }

    @JvmOverloads fun drawEllipse(width: Int,
                                  height: Int,
                                  value: Int,
                                  isFilled: Boolean = false,
                                  fillValue: Int = value,
                                  fillForeground: Color = cursor.foregroundColor,
                                  fillBackground: Color = cursor.backgroundColor) {
        val ellipsePlot = plotEllipse(cursor.x, cursor.y, cursor.x + width, cursor.y + height)

        drawShape(ellipsePlot, value)
        if(isFilled) fillShape(ellipsePlot, fillValue, fillForeground, fillBackground)
    }

    @JvmOverloads fun drawCircle(radius: Int,
                                 char: Char,
                                 isFilled: Boolean = false,
                                 fillChar: Char = char,
                                 fillForeground: Color = cursor.foregroundColor,
                                 fillBackground: Color = cursor.backgroundColor) {
        drawCircle(radius, char.toInt(), isFilled, fillChar.toInt(), fillForeground, fillBackground)
    }

    @JvmOverloads fun drawCircle(radius: Int,
                                 value: Int,
                                 isFilled: Boolean = false,
                                 fillValue: Int = value,
                                 fillForeground: Color = cursor.foregroundColor,
                                 fillBackground: Color = cursor.backgroundColor) {
        val circlePlot = plotCircle(cursor.x, cursor.y, radius)

        drawShape(circlePlot, value)
        if(isFilled) fillShape(circlePlot, fillValue, fillForeground, fillBackground)
    }
    
    @JvmOverloads fun drawTriangle(leftX: Int,
                                   leftY: Int,
                                   rightX: Int,
                                   rightY: Int,
                                   char: Char,
                                   isFilled: Boolean = false,
                                   fillChar: Char = char,
                                   fillForeground: Color = cursor.foregroundColor,
                                   fillBackground: Color = cursor.backgroundColor) {
        drawTriangle(leftX, leftY, rightX, rightY, char.toInt(), isFilled, fillChar.toInt(), fillForeground, fillBackground)
    }

    @JvmOverloads fun drawTriangle(leftX: Int,
                                   leftY: Int,
                                   rightX: Int,
                                   rightY: Int,
                                   value: Int,
                                   isFilled: Boolean = false,
                                   fillValue: Int = value,
                                   fillForeground: Color = cursor.foregroundColor,
                                   fillBackground: Color = cursor.backgroundColor) {
        val trianglePlot = plotTriangle(cursor.x, cursor.y, leftX, leftY, rightX, rightY)

        drawShape(trianglePlot, value)
        if(isFilled) fillShape(trianglePlot, fillValue, fillForeground, fillBackground)
    }

    @JvmOverloads fun drawBox(width: Int,
                              height: Int,
                              topLeft: Char,
                              topRight: Char,
                              bottomLeft: Char,
                              bottomRight: Char,
                              horizontal: Char,
                              vertical: Char,
                              isFilled: Boolean = false,
                              fillChar: Char = ' ',
                              fillForeground: Color = cursor.foregroundColor,
                              fillBackground: Color = cursor.backgroundColor) {
        drawBox(width, height, topLeft.toInt(), topRight.toInt(), bottomLeft.toInt(), bottomRight.toInt(),
                horizontal.toInt(), vertical.toInt(), isFilled, fillChar.toInt(), fillForeground, fillBackground)
    }

    @JvmOverloads fun drawBox(width: Int,
                              height: Int,
                              topLeft: Int,
                              topRight: Int,
                              bottomLeft: Int,
                              bottomRight: Int,
                              horizontal: Int,
                              vertical: Int,
                              isFilled: Boolean = false,
                              fillValue: Int = 0,
                              fillForeground: Color = cursor.foregroundColor,
                              fillBackground: Color = cursor.backgroundColor) {
        val plotList = plotRect(cursor.x, cursor.y, width, height)

        plotList.forEach {
            workingCursor.x = it.first
            workingCursor.y = it.second

            when(it.first) {
                cursor.x ->
                    when(it.second) {
                        cursor.y -> write(topLeft)
                        cursor.y + height - 1 -> write(bottomLeft)
                        else -> write(vertical)
                    }
                cursor.x + width - 1 ->
                    when(it.second) {
                        cursor.y -> write(topRight)
                        cursor.y + height - 1 -> write(bottomRight)
                        else -> write(vertical)
                    }
                else -> write(horizontal)
            }
        }

        if(isFilled) fillShape(plotList, fillValue, fillForeground, fillBackground)
    }

    @JvmOverloads fun drawDoubleBox(width: Int,
                                    height: Int,
                                    isFilled: Boolean,
                                    fillChar: Char,
                                    fillForeground: Color = cursor.foregroundColor,
                                    fillBackground: Color = cursor.backgroundColor) {
        drawDoubleBox(width, height, isFilled, fillChar.toInt(), fillForeground, fillBackground)
    }

    @JvmOverloads fun drawDoubleBox(width: Int,
                                    height: Int,
                                    isFilled: Boolean = false,
                                    fillValue: Int = 0,
                                    fillForeground: Color = cursor.foregroundColor,
                                    fillBackground: Color = cursor.backgroundColor) {
        drawBox(width, height,
                KTerminalData.BOX_DOUBLE_DOWN_RIGHT,
                KTerminalData.BOX_DOUBLE_DOWN_LEFT,
                KTerminalData.BOX_DOUBLE_UP_RIGHT,
                KTerminalData.BOX_DOUBLE_UP_LEFT,
                KTerminalData.BOX_DOUBLE_HORIZONTAL,
                KTerminalData.BOX_DOUBLE_VERTICAL,
                isFilled, fillValue, fillForeground, fillBackground)
    }

    fun drawSingleBox(width: Int,
                      height: Int,
                      isFilled: Boolean,
                      fillChar: Char,
                      fillForeground: Color,
                      fillBackground: Color) {
        drawSingleBox(width, height, isFilled, fillChar, fillForeground, fillBackground)
    }

    @JvmOverloads fun drawSingleBox(width: Int,
                                    height: Int,
                                    isFilled: Boolean = false,
                                    fillValue: Int = 0,
                                    fillForeground: Color = cursor.foregroundColor,
                                    fillBackground: Color = cursor.backgroundColor) {
        drawBox(width, height,
                KTerminalData.BOX_SINGLE_DOWN_RIGHT,
                KTerminalData.BOX_SINGLE_DOWN_LEFT,
                KTerminalData.BOX_SINGLE_UP_RIGHT,
                KTerminalData.BOX_SINGLE_UP_LEFT,
                KTerminalData.BOX_SINGLE_HORIZONTAL,
                KTerminalData.BOX_SINGLE_VERTICAL,
                isFilled, fillValue, fillForeground, fillBackground)
    }

    override fun toString(): String {
        var output = "[width=$width,height=$height,defaultForegroundColor=$defaultForegroundColor," +
            "defaultBackgroundColor=$defaultBackgroundColor,cursor={x=${cursor.x},y=${cursor.y}," +
            "foreground=${cursor.foregroundColor},background=${cursor.backgroundColor}},terminal={\n"

        for(j in 0 until height) {
            for(i in 0 until width) {
                output += "${terminal[i][j].char}"
            }
            output += "\n"
        }

        output += "}]"
        return output
    }

    companion object {
        const val WRITE_LEFT_TO_RIGHT = 0
        const val WRITE_TOP_TO_BOTTOM = 1
        const val WRITE_RIGHT_TO_LEFT = 2
        const val WRITE_BOTTOM_TO_TOP = 3

        const val WRAP_NONE = -1
        const val WRAP_NO_SHIFT = 0
        const val WRAP_POSITIVE_SHIFT = 1
        const val WRAP_NEGATIVE_SHIFT = 2

        const val NULL = 0
        const val SMILE = 1
        const val INVERTED_SMILE = 2
        const val HEART = 3
        const val DIAMOND = 4
        const val CLUB = 5
        const val SPADE = 6
        const val BULLET = 7
        const val INVERTED_BULLET = 8
        const val CIRCLE = 9
        const val INVERTED_CIRCLE = 10
        const val MALE = 11
        const val FEMALE = 12
        const val PAIR_EIGHTH_NOTE = 13
        const val EIGHTH_NOTE = 14
        const val SOLAR = 15
        const val RIGHT_TRIANGLE = 16
        const val LEFT_TRIANGLE = 17
        const val UP_DOWN_ARROW = 18
        const val DOUBLE_EXCLAMATION = 19
        const val PARAGRAPH = 20
        const val SECTION = 21
        const val LOWER_BLACK_RECTANGLE = 22
        const val UP_DOWN_BAR_ARROW = 23
        const val UP_ARROW = 24
        const val DOWN_ARROW = 25
        const val RIGHT_ARROW = 26
        const val LEFT_ARROW = 27
        const val RIGHT_ANGLE = 28
        const val LEFT_RIGHT_ARROW = 29
        const val UP_TRIANGLE = 30
        const val DOWN_TRIANGLE = 31
        const val SPACE = 32
        const val HOUSE = 127
        const val UPPER_CEDILLA_C = 128
        const val LOWER_UMLAUT_U = 129
        const val ACUTE_E = 130
        const val CIRCUMFLEX_A = 131
        const val LOWER_UMLAUT_A = 132
        const val GRAVE_A = 133
        const val LOWER_RING_A = 134
        const val LOWER_CEDILLA_C = 135
        const val CIRCUMFLEX_E = 136
        const val UMLAUT_E = 137
        const val GRAVE_E = 138
        const val UMLAUT_I = 139
        const val CIRCUMFLEX_I = 140
        const val GRAVE_I = 141
        const val UPPER_UMLAUT_A = 142
        const val UPPER_RING_A = 143
        const val UPPER_ACUTE_E = 144
        const val LOWER_ASH = 145
        const val UPPER_ASH = 146
        const val CIRCUMFLEX_O = 147
        const val LOWER_UMLAUT_O = 148
        const val GRAVE_O = 149
        const val CIRCUMFLEX_U = 150
        const val GRAVE_U = 151
        const val UMLAUT_Y = 152
        const val UPPER_UMLAUT_O = 153
        const val UPPER_UMLAUT_U = 154
        const val CENT = 155
        const val POUND = 156
        const val YEN = 157
        const val PESETA = 158
        const val HOOK_F = 159
        const val ACUTE_A = 160
        const val ACUTE_I = 161
        const val ACUTE_O = 162
        const val ACUTE_U = 163
        const val LOWER_TILDE_N = 164
        const val UPPER_TILDE_N = 165
        const val ORDINAL_FEMININE = 166
        const val ORDINAL_MASCULINE = 167
        const val INVERTED_QUESTION = 168
        const val INVERTED_NOT = 169
        const val NOT = 170
        const val ONE_HALF = 171
        const val ONE_FOURTH = 172
        const val INVERTED_EXCLAMATION = 173
        const val ANGLE_LEFT = 174
        const val ANGLE_RIGHT = 175
        const val SHADE_LIGHT = 176
        const val SHADE_MEDIUM = 177
        const val SHADE_DARK = 178
        const val BOX_SINGLE_VERTICAL = 179
        const val BOX_SINGLE_VERTICAL_LEFT = 180
        const val BOX_VERTICAL_SINGLE_LEFT_DOUBLE = 181
        const val BOX_VERTICAL_DOUBLE_LEFT_SINGLE = 182
        const val BOX_DOWN_DOUBLE_LEFT_SINGLE = 183
        const val BOX_DOWN_SINGLE_LEFT_DOUBLE = 184
        const val BOX_DOUBLE_VERTICAL_LEFT = 185
        const val BOX_DOUBLE_VERTICAL = 186
        const val BOX_DOUBLE_DOWN_LEFT = 187
        const val BOX_DOUBLE_UP_LEFT = 188
        const val BOX_UP_DOUBLE_LEFT_SINGLE = 189
        const val BOX_UP_SINGLE_LEFT_DOUBLE = 190
        const val BOX_SINGLE_DOWN_LEFT = 191
        const val BOX_SINGLE_UP_RIGHT = 192
        const val BOX_SINGLE_HORIZONTAL_UP = 193
        const val BOX_SINGLE_HORIZONTAL_DOWN = 194
        const val BOX_SINGLE_VERTICAL_RIGHT = 195
        const val BOX_SINGLE_HORIZONTAL = 196
        const val BOX_SINGLE_VERTICAL_HORIZONTAL = 197
        const val BOX_VERTICAL_SINGLE_RIGHT_DOUBLE = 198
        const val BOX_VERTICAL_DOUBLE_RIGHT_SINGLE = 199
        const val BOX_DOUBLE_UP_RIGHT = 200
        const val BOX_DOUBLE_DOWN_RIGHT = 201
        const val BOX_DOUBLE_HORIZONTAL_UP = 202
        const val HOX_DOUBLE_HORIZONTAL_DOWN = 203
        const val BOX_DOUBLE_VERTICAL_RIGHT = 204
        const val BOX_DOUBLE_HORIZONTAL = 205
        const val BOX_DOUBLE_VERTICAL_HORIZONTAL = 206
        const val BOX_UP_SINGLE_HORIZONTAL_DOUBLE = 207
        const val BOX_UP_DOUBLE_HORIZONTAL_SINGLE = 208
        const val BOX_DOWN_SINGLE_HORIZONTAL_DOUBLE = 209
        const val BOX_DOWN_DOUBLE_HORIZONTAL_SINGLE = 210
        const val BOX_UP_DOUBLE_RIGHT_SINGLE = 211
        const val BOX_UP_SINGLE_RIGHT_DOUBLE = 212
        const val BOX_DOWN_SINGLE_RIGHT_DOUBLE = 213
        const val BOX_DOWN_DOUBLE_RIGHT_SINGLE = 214
        const val BOX_VERTICAL_DOUBLE_HORIZONTAL_SINGLE = 215
        const val BOX_VERTICAL_SINGLE_HORIZONTAL_DOUBLE = 216
        const val BOX_SINGLE_UP_LEFT = 217
        const val BOX_SINGLE_DOWN_RIGHT = 218
        const val BLOCK_FULL = 219
        const val BLOCK_LOWER_HALF = 220
        const val BLOCK_LEFT_HALF = 221
        const val BLOCK_RIGHT_HALF = 222
        const val BLOCK_UPPER_HALF = 223
        const val ALPHA = 224
        const val SHARP_S = 225
        const val GAMMA = 226
        const val PI = 227
        const val UPPER_SIGMA = 228
        const val LOWER_SIGMA = 229
        const val MU = 230
        const val TAU = 231
        const val UPPER_PHI = 232
        const val THETA = 233
        const val OMEGA = 234
        const val DELTA = 235
        const val INFINITY = 236
        const val LOWER_PHI = 237
        const val EPSILON = 238
        const val INTERSECTION = 239
        const val TRIPLE_BAR = 240
        const val PLUS_MINUS = 241
        const val GREATER_EQUAL = 242
        const val LESSER_EQUAL = 243
        const val INTEGRAL_TOP = 244
        const val INTEGRAL_BOTTOM = 245
        const val DIVISION = 246
        const val APROXIMATE = 247
        const val DEGREE = 248
        const val SMALL_BULLET = 249
        const val INTERPUNCT = 250
        const val RADICAL = 251
        const val SUPERSCRIPT = 252
        const val SUPERSCRIPT_SQUARE = 253
        const val SQUARE = 254
        const val NBSP = 255
    }
}

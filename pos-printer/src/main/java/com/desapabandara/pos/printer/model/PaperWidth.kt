package com.desapabandara.pos.printer.model

enum class PaperWidth(
    val width: Int,
    val characters: Int
) {
    W48(48, 32),
    W80(80, 48);

    companion object {
        fun fromWidth(paperWidth: Int) = entries.find {
            it.width == paperWidth
        }
    }
}
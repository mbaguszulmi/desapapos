package com.desapabandara.pos.printer.util

import android.graphics.Bitmap
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import timber.log.Timber

fun imageBitmapToHex(printer: EscPosPrinter, imageBitmap: Bitmap): String {
    return try {
        PrinterTextParserImg.bitmapToHexadecimalString(printer, imageBitmap, false)
    } catch (e: Throwable) {
        Timber.e(e, "Error converting image URL to hex")
        ""
    }
}
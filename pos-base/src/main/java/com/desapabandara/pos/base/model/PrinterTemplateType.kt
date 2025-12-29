package com.desapabandara.pos.base.model

enum class PrinterTemplateType(val id: Int) {
    Receipt(1),
    Docket(2),
    TableChecker(3),
    Other(21);

    companion object {
        fun fromId(id: Int): PrinterTemplateType {
            return entries.firstOrNull { it.id == id } ?: Other
        }
    }
}

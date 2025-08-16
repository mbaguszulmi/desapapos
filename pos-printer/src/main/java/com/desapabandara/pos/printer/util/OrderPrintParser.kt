package com.desapabandara.pos.printer.util

import co.mbznetwork.android.base.extension.replaceLast
import co.mbznetwork.android.base.util.DateUtil
import com.desapabandara.pos.base.model.Order
import com.desapabandara.pos.base.model.PaymentStatus
import com.desapabandara.pos.base.util.CurrencyUtil
import com.desapabandara.pos.local_db.entity.LocationEntity
import com.desapabandara.pos.local_db.entity.PrinterEntity
import com.desapabandara.pos.preference.datastore.AuthDataStore
import com.desapabandara.pos.printer.model.PaperWidth
import com.desapabandara.pos.printer.model.PrintTask
import com.desapabandara.pos_backend.model.response.StoreResponse
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderPrintParser @Inject constructor(
    private val currencyUtil: CurrencyUtil,
    private val authDataStore: AuthDataStore
) {

    suspend fun parseFromTask(task: PrintTask): String {
        val templateStr = task.printerTemplate.template
        val store = authDataStore.getCurrentStoreData().first() ?: return ""

        return templateStr.parseTemplate(task.order, task.reprint, task.location, store, task.printerDevice.printerData)
    }

    private fun String.parseTemplate(
        order: Order,
        reprint: Boolean,
        location: LocationEntity,
        store: StoreResponse,
        printerData: PrinterEntity
    ): String {
        return parseLogo()
            .parseStoreName(store)
            .parseStoreAddress(store)
            .parseStorePhone(store)
            .parseOrderNumber(order)
            .parseOrderTypeData(order)
            .parseStaffName(order)
            .parseOrderTimestamp(order)
            .parseOrderItems(order)
            .parseOrderSubtotal(order)
            .parseOrderTax(order)
            .parseOrderDiscount(order)
            .parseOrderTotal(order)
            .parseOrderCustomer(order)
            .parseLocationName(location)
            .parseReprint(reprint)
            .parsePaymentStatus(order)
            .parseDivider(printerData)
    }

    private fun String.parseLogo(): String {
        return replaceTemplateTag("LOGO", "")
    }

    private fun String.parseStoreName(store: StoreResponse): String {
        return replaceTemplateTag("STORE_NAME", store.name)
    }

    private fun String.parseStoreAddress(store: StoreResponse): String {
        return replaceTemplateTag("STORE_ADDRESS", store.address)
    }

    private fun String.parseStorePhone(store: StoreResponse): String {
        return replaceTemplateTag("STORE_PHONE", store.phone)
    }

    private fun String.parseOrderNumber(order: Order): String {
        return replaceTemplateTag("ORDER_NUMBER", order.orderNumber.toString())
    }

    private fun String.parseOrderTypeData(order: Order): String {
        return parseTemplateTag("ORDER_TYPE_DATA") { text, range, attrs ->
            val orderType = order.orderType.toString()
            val tableNumber = order.orderTable?.table?.name
            val labelAlignment = (attrs[TemplateAttr.LabelAlignment.attrName] ?: "").ifBlank { "L" }
            val fontSize = (attrs[TemplateAttr.FontSize.attrName] ?: "").ifBlank { "normal" }

            val tableNumberDisplay = if (tableNumber.isNullOrBlank()) "" else ": Table $tableNumber"
            val orderTypeData = "[$labelAlignment]<font size='$fontSize'>$orderType$tableNumberDisplay</font>"

            text.replaceRange(range, orderTypeData)
        }
    }

    private fun String.parseStaffName(order: Order): String {
        return replaceTemplateTag("STAFF_NAME", order.staff?.name ?: "-")
    }

    private fun String.parseOrderTimestamp(order: Order): String {
        return replaceTemplateTag("ORDER_TIMESTAMP", DateUtil.formatDateTimeShort(order.createdAt))
    }

    private fun String.parseOrderItems(order: Order): String {
        return parseTemplateTag("ORDER_ITEMS") { text, range, attrs ->
            val fontSize = (attrs[TemplateAttr.FontSize.attrName] ?: "").ifBlank { "normal" }
            val excludePrice = attrs[TemplateAttr.ExcludePrice.attrName] == "true"

            var orderItems = ""
            for (item in order.orderItems) {
                val itemPriceText = if (!excludePrice) {
                    "[R]<font size='$fontSize'>${currencyUtil.getCurrentFormat(item.totalPrice)}</font>"
                } else ""
                val itemText = "[L]<font size='$fontSize'>${item.quantity.toInt()}X  ${item.name}</font>$itemPriceText\n"
                orderItems += itemText

                if (item.isTakeaway) {
                    orderItems += "    (Takeaway)\n"
                }

                if (item.itemNote.isNotBlank()) {
                    orderItems += "    Note: ${item.itemNote}\n"
                }
            }

            text.replaceRange(range, orderItems.replaceLast("\n", ""))
        }
    }

    private fun String.parseOrderSubtotal(order: Order): String {
        return replaceTemplateTag("SUBTOTAL", currencyUtil.getCurrentFormat(order.subtotal))
    }

    private fun String.parseOrderTax(order: Order): String {
        return replaceTemplateTag("TAX", currencyUtil.getCurrentFormat(order.totalTax))
    }

    private fun String.parseOrderDiscount(order: Order): String {
        return parseTemplateTag("DISCOUNT_DATA") { text, range, attrs ->
            val label = (attrs[TemplateAttr.Label.attrName] ?: "").ifBlank { "[L]DISCOUNT" }
            val amountAlignment = (attrs[TemplateAttr.AmountAlignment.attrName] ?: "").ifBlank { "R" }

            val rangeStart = range.first
            var rangeEnd = range.last+1

            val discount = order.discount
            val discountText = if (discount > 0) {
                "$label[$amountAlignment]${currencyUtil.getCurrentFormat(discount)}"
            } else {
                if (rangeEnd < text.length) {
                    if (text[rangeEnd] == '\r') {
                        rangeEnd++
                        if (rangeEnd < text.length && text[rangeEnd] == '\n') {
                            rangeEnd++
                        }
                    } else if (text[rangeEnd] == '\n') {
                        rangeEnd++
                    }
                }
                ""
            }

            text.replaceRange(rangeStart, rangeEnd, discountText)
        }
    }

    private fun String.parseOrderTotal(order: Order): String {
        return replaceTemplateTag("TOTAL", currencyUtil.getCurrentFormat(order.total))
    }

    private fun String.parseOrderCustomer(order: Order): String {
        return parseTemplateTag("CUSTOMER_DATA") { text, range, attrs ->
            val labelAlignment = (attrs[TemplateAttr.LabelAlignment.attrName] ?: "").ifBlank { "L" }
            val dataAlignment = (attrs[TemplateAttr.DataAlignment.attrName] ?: "").ifBlank { "" }

            val rangeStart = range.first
            var rangeEnd = range.last+1

            val customerName = order.customerName
            val customerData = if (customerName.isNotBlank()) {
                "[$labelAlignment]Customer: ${if (dataAlignment.isBlank()) "" else "[$dataAlignment]"}$customerName"
            } else {
                if (rangeEnd < text.length) {
                    if (text[rangeEnd] == '\r') {
                        rangeEnd++
                        if (rangeEnd < text.length && text[rangeEnd] == '\n') {
                            rangeEnd++
                        }
                    } else if (text[rangeEnd] == '\n') {
                        rangeEnd++
                    }
                }
                ""
            }

            text.replaceRange(rangeStart, rangeEnd, customerData)
        }
    }

    private fun String.parseLocationName(location: LocationEntity): String {
        return replaceTemplateTag("LOCATION_NAME", location.name)
    }

    private fun String.parseReprint(reprint: Boolean): String {
        return parseTemplateTag("REPRINT") { text, range, attrs ->
            val rangeStart = range.first
            var rangeEnd = range.last + 1

            val reprintText = if (reprint) {
                "[C]REPRINT"
            } else {
                if (rangeEnd < text.length) {
                    if (text[rangeEnd] == '\r') {
                        rangeEnd++
                        if (rangeEnd < text.length && text[rangeEnd] == '\n') {
                            rangeEnd++
                        }
                    } else if (text[rangeEnd] == '\n') {
                        rangeEnd++
                    }
                }
                ""
            }
            text.replaceRange(rangeStart, rangeEnd, reprintText)
        }
    }

    private fun String.parsePaymentStatus(order: Order): String {
        val text = if (order.paymentStatus == PaymentStatus.Open) "[C]NOT PAID" else "[C]PAID"
        return replaceTemplateTag("PAYMENT_STATUS", text)
    }

    private fun String.parseDivider(printerData: PrinterEntity): String {
        return parseTemplateTag("DIVIDER") { text, range, attrs ->
            val doubleDash = attrs[TemplateAttr.DoubleDash.attrName] == "true"
            val charLength =
                (PaperWidth.fromWidth(printerData.paperWidth) ?: PaperWidth.W48).characters
            val formattedTest = "[C]" + (if (doubleDash) "=" else "-").repeat(charLength)

            text.replaceRange(range, formattedTest)
        }
    }

    private fun String.replaceTemplateTag(tag: String, replacement: String): String {
        return parseTemplateTag(tag) { text, range, _ ->
            text.replaceRange(range, replacement)
        }
    }

    private fun String.parseTemplateTag(tag: String, parser: (String, IntRange, Map<String, String>) -> String): String {
        var result = this
        while (true) {
            Regex("<$tag([^>]+)?/>").find(
                result
            )?.let {
                val attributes = it.groups[1]?.value?.let { attrs -> attributeStrToMap(attrs) } ?: emptyMap()

                result = parser(result, it.range, attributes)
            } ?: break
        }

        return result
    }

    private fun attributeStrToMap(str: String): Map<String, String> {
        return Regex("([A-Za-z\\-]+)='([A-Za-z\\d]+)'").findAll(
            str
        ).mapNotNull {
            val attr = it.groups[1]?.value
            val value = it.groups[2]?.value

            if (attr != null && value != null) {
                attr to value
            } else {
                null
            }
        }.toMap()
    }

}

enum class TemplateAttr(val attrName: String) {
    DoubleDash("double-dash"),
    LabelAlignment("label-alignment"),
    DataAlignment("data-alignment"),
    AmountAlignment("amount-alignment"),
    Label("label"),
    FontSize("font-size"),
    ExcludePrice("exclude-price"),
}

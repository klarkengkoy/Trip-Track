package dev.klarkengkoy.triptrack.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.NumberFormat
import java.util.Locale

class AmountVisualTransformation(
    private val currencySymbol: String
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        // Format the integer part with thousand separators
        val integerPart = originalText.substringBefore('.')
        val decimalPart = originalText.substringAfter('.', "")

        val formattedInteger = if (integerPart.isNotEmpty()) {
            try {
                val number = integerPart.toLong()
                NumberFormat.getNumberInstance(Locale.US).format(number)
            } catch (e: NumberFormatException) {
                // Handle cases where the integer part is not a valid number
                return TransformedText(text, OffsetMapping.Identity)
            }
        } else {
            ""
        }

        val formattedText = if (decimalPart.isNotEmpty() || originalText.endsWith(".")) {
            "$formattedInteger.$decimalPart"
        } else {
            formattedInteger
        }

        val annotatedString = AnnotatedString("$formattedText $currencySymbol")

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val originalIntegerLen = integerPart.length
                val formattedIntegerLen = formattedInteger.length
                val commas = formattedIntegerLen - originalIntegerLen

                return when {
                    offset == 0 -> 0
                    offset <= originalIntegerLen -> {
                        // Count commas before the cursor in the integer part
                        val textBeforeCursor = integerPart.take(offset)
                        if (textBeforeCursor.isNotEmpty()) {
                            try {
                                val number = textBeforeCursor.toLong()
                                val formattedTextBeforeCursor = NumberFormat.getNumberInstance(Locale.US).format(number)
                                offset + formattedTextBeforeCursor.count { it == ',' }
                            } catch (e: NumberFormatException) {
                                offset
                            }
                        } else {
                            offset
                        }
                    }
                    else -> offset + commas // Cursor is in the decimal part
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                val commas = formattedText.take(offset).count { it == ',' }
                return (offset - commas).coerceIn(0, originalText.length)
            }
        }
        return TransformedText(annotatedString, offsetMapping)
    }
}

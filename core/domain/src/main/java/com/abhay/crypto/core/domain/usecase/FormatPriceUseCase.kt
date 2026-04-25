package com.abhay.crypto.core.domain.usecase

import java.util.Locale
import javax.inject.Inject

class FormatPriceUseCase @Inject constructor() {
    operator fun invoke(price: Double): String {
        return if (price >= 1.0) {
            "$${String.format(Locale.US, "%,.2f", price)}"
        } else {
            "$${String.format(Locale.US, "%.8f", price).trimEnd('0').trimEnd('.')}"
        }
    }
}

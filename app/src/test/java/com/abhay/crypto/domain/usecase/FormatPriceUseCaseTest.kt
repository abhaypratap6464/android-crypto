package com.abhay.crypto.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test

class FormatPriceUseCaseTest {

    private val formatPriceUseCase = FormatPriceUseCase()

    @Test
    fun `format price greater than or equal to 1`() {
        assertEquals("$1.00", formatPriceUseCase(1.0))
        assertEquals("$1234.56", formatPriceUseCase(1234.56))
        assertEquals("$50000.00", formatPriceUseCase(50000.0))
    }

    @Test
    fun `format price less than 1`() {
        assertEquals("$0.123", formatPriceUseCase(0.123))
        assertEquals("$0.00001234", formatPriceUseCase(0.00001234))
        assertEquals("$0.01", formatPriceUseCase(0.01))
    }

    @Test
    fun `format price with trailing zeros`() {
        assertEquals("$0.5", formatPriceUseCase(0.500000))
        assertEquals("$0.12345", formatPriceUseCase(0.12345000))
    }
}

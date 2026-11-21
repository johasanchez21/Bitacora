package cl.duoc.Bitacora.validation

import cl.duoc.Bitacora.domain.validation.BitacoraValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BitacoraValidatorTest {

    @Test
    fun `validarAsignatura con texto vacio retorna error`() {
        val res = BitacoraValidator.validarAsignatura("")
        assertEquals("La asignatura es obligatoria", res)
    }

    @Test
    fun `validarAsignatura con menos de 3 caracteres retorna error`() {
        val res = BitacoraValidator.validarAsignatura("Ma")
        assertEquals("La asignatura debe tener al menos 3 caracteres", res)
    }

    @Test
    fun `validarAsignatura con valor valido retorna null`() {
        val res = BitacoraValidator.validarAsignatura("Matemáticas")
        assertNull(res)
    }

    @Test
    fun `validarMes con texto vacio retorna error`() {
        val res = BitacoraValidator.validarMes("")
        assertEquals("El mes es obligatorio", res)
    }

    @Test
    fun `validarMes con mes invalido retorna error`() {
        val res = BitacoraValidator.validarMes("Enerofalso")
        assertEquals("Ingresa un mes válido", res)
    }

    @Test
    fun `validarMes con mes valido retorna null`() {
        val res = BitacoraValidator.validarMes("Marzo")
        assertNull(res)
    }

    @Test
    fun `validarAnio con anio vacio retorna error`() {
        val res = BitacoraValidator.validarAnio("")
        assertEquals("El año es obligatorio", res)
    }

    @Test
    fun `validarAnio con texto no numerico retorna error`() {
        val res = BitacoraValidator.validarAnio("dos mil")
        assertEquals("El año debe ser numérico", res)
    }

    @Test
    fun `validarAnio fuera de rango 2020 a 2100 retorna error`() {
        val res = BitacoraValidator.validarAnio("2010")
        assertEquals("El año debe estar entre 2020 y 2100", res)
    }

    @Test
    fun `validarAnio dentro de rango retorna null`() {
        val res = BitacoraValidator.validarAnio("2025")
        assertNull(res)
    }

    @Test
    fun `validarCurso con texto vacio retorna error`() {
        val res = BitacoraValidator.validarCurso("")
        assertEquals("El curso es obligatorio", res)
    }

    @Test
    fun `validarCurso con menos de 3 caracteres retorna error`() {
        val res = BitacoraValidator.validarCurso("1A")
        assertEquals("El curso debe tener al menos 3 caracteres", res)
    }

    @Test
    fun `validarCurso con valor valido retorna null`() {
        val res = BitacoraValidator.validarCurso("1° Medio A")
        assertNull(res)
    }
}

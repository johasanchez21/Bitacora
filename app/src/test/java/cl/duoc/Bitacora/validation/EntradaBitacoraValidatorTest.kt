package cl.duoc.Bitacora.validation

import cl.duoc.Bitacora.domain.validation.EntradaBitacoraValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EntradaBitacoraValidatorTest {

    @Test
    fun `validarFecha con fecha vacia retorna error`() {
        val res = EntradaBitacoraValidator.validarFecha("")
        assertEquals("Debes seleccionar una fecha", res)
    }

    @Test
    fun `validarFecha con fecha valida retorna vacio`() {
        val res = EntradaBitacoraValidator.validarFecha("30/03/2025")
        assertTrue(res.isEmpty())
    }

    @Test
    fun `validarActividad con texto vacio retorna error`() {
        val res = EntradaBitacoraValidator.validarActividad("")
        assertEquals("Debes ingresar una actividad", res)
    }

    @Test
    fun `validarActividad con menos de 10 caracteres retorna error`() {
        val res = EntradaBitacoraValidator.validarActividad("Corta")
        assertEquals("La actividad debe tener al menos 10 caracteres", res)
    }

    @Test
    fun `validarActividad con 10 o mas caracteres retorna vacio`() {
        val res = EntradaBitacoraValidator.validarActividad("Resolución de guía de álgebra")
        assertTrue(res.isEmpty())
    }

    @Test
    fun `validarAprendizaje con texto vacio retorna error`() {
        val res = EntradaBitacoraValidator.validarAprendizaje("")
        assertEquals("Debes describir qué aprendiste", res)
    }

    @Test
    fun `validarAprendizaje con menos de 10 caracteres retorna error`() {
        val res = EntradaBitacoraValidator.validarAprendizaje("Poco")
        assertEquals("El aprendizaje debe tener al menos 10 caracteres", res)
    }

    @Test
    fun `validarAprendizaje con 10 o mas caracteres retorna vacio`() {
        val res = EntradaBitacoraValidator.validarAprendizaje("Aprendí a despejar ecuaciones de primer grado")
        assertTrue(res.isEmpty())
    }

    @Test
    fun `validarDudas con mas de 500 caracteres retorna error`() {
        val textoLargo = "a".repeat(501)
        val res = EntradaBitacoraValidator.validarDudas(textoLargo)
        assertEquals("Las dudas no pueden superar los 500 caracteres", res)
    }

    @Test
    fun `validarDudas con texto dentro del limite retorna vacio`() {
        val res = EntradaBitacoraValidator.validarDudas("Ninguna duda por el momento.")
        assertTrue(res.isEmpty())
    }
}

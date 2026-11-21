package cl.duoc.Bitacora.validation

import cl.duoc.Bitacora.domain.validation.LoginValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginValidatorTest {

    @Test
    fun `validarCorreo con correo vacio retorna error`() {
        val res = LoginValidator.validarCorreo("")
        assertEquals("El correo es obligatorio", res)
    }

    @Test
    fun `validarCorreo con formato invalido retorna error`() {
        val res = LoginValidator.validarCorreo("correo_invalido")
        assertEquals("Ingresa un correo válido", res)
    }

    @Test
    fun `validarCorreo con correo valido retorna vacio`() {
        val res = LoginValidator.validarCorreo("estudiante@duoc.cl")
        assertTrue(res.isEmpty())
    }

    @Test
    fun `validarPassword con password vacia retorna error`() {
        val res = LoginValidator.validarPassword("")
        assertEquals("La contraseña es obligatoria", res)
    }

    @Test
    fun `validarPassword con menos de 6 caracteres retorna error`() {
        val res = LoginValidator.validarPassword("12345")
        assertEquals("La contraseña debe tener al menos 6 caracteres", res)
    }

    @Test
    fun `validarPassword con password valida retorna vacio`() {
        val res = LoginValidator.validarPassword("123456")
        assertTrue(res.isEmpty())
    }
}

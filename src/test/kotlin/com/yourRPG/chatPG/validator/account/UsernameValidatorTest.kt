package com.yourRPG.chatPG.validator.account

import com.yourRPG.chatPG.exception.auth.username.UsernameContainsIllegalCharactersException
import com.yourRPG.chatPG.exception.auth.username.UsernameTooLongException
import com.yourRPG.chatPG.exception.auth.username.UsernameTooShortException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UsernameValidatorTest {

    private val validator = UsernameValidator()

    @Test
    fun valid() {
        //ARRANGE
        val username = "username_test"

        //ACT + ASSERT
        assertDoesNotThrow {
            validator.validate(t = username)
        }
    }

    @Test
    fun `invalid - less than 3 characters`() {
        //ARRANGE
        val username = "ab"

        //ACT + ASSERT
        assertThrows<UsernameTooShortException> {
            validator.validate(t = username)
        }
    }

    @Test
    fun `invalid - greater than 16 characters`() {
        //ARRANGE
        val username = "a".repeat(n = 17)

        //ACT + ASSERT
        assertThrows<UsernameTooLongException> {
            validator.validate(t = username)
        }
    }

    @Test
    fun `invalid - contains invalid characters`() {
        //ARRANGE
        val username = "!!!"

        //ACT + ASSERT
        assertThrows<UsernameContainsIllegalCharactersException> {
            validator.validate(t = username)
        }
    }

}
package com.yourRPG.chatPG.validator.account

import com.yourRPG.chatPG.exception.auth.password.PasswordContainsIllegalCharactersException
import com.yourRPG.chatPG.exception.auth.password.PasswordDoesNotMeetCharactersOccurrenceCriteriaException
import com.yourRPG.chatPG.exception.auth.password.PasswordTooLongException
import com.yourRPG.chatPG.exception.auth.password.PasswordTooShortException
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import java.util.stream.Stream

class PasswordValidatorTest {

    private val passwordValidator = PasswordValidator()

    @Test
    fun valid() {
        //ARRANGE
        val password = "TeaTest123#"

        //ACT + ASSERT
        assertDoesNotThrow {
            passwordValidator.validate(t = password)
        }
    }

    @Test
    fun `invalid - less than 8 characters`() {
        //ARRANGE
        val password = "Bad!123"

        //ACT + ASSERT
        assertThrows<PasswordTooShortException> {
            passwordValidator.validate(t = password)
        }
    }

    @Test
    fun `invalid - greater than 255 characters`() {
        //ARRANGE
        val password = "seventeenCharL0ng".repeat(n = 15) + "!" //length = 17 * 15 + 1 = 256

        //ACT + ASSERT
        assertThrows<PasswordTooLongException> {
            passwordValidator.validate(t = password)
        }
    }

    @Test
    fun `invalid - contains illegal characters`() {
        //ARRANGE
        val password = "IllegalCharacterH3re=>ç"

        //ACT + ASSERT
        assertThrows<PasswordContainsIllegalCharactersException> {
            passwordValidator.validate(t = password)
        }
    }

    @TestFactory
    fun `invalid - does not meet characters occurrence criteria`(): Stream<DynamicTest> =
        Stream.of(
            "n0uppercase!",
            "N0LOWERCASE@",
            "N0SpecialCharacters",
            "NoNumbers!"
        ).map { password ->
            DynamicTest.dynamicTest("password: $password") {

                //ACT + ASSERT
                assertThrows<PasswordDoesNotMeetCharactersOccurrenceCriteriaException> {
                    passwordValidator.validate(t = password)
                }

            }
        }

}
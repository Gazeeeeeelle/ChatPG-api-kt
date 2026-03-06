package com.chatpg.service.account

import com.chatpg.domain.account.Account
import com.chatpg.dto.account.AccountDto
import com.chatpg.exception.account.AccountIdNotFoundException
import com.chatpg.exception.account.AccountNotFoundException
import com.chatpg.exception.auth.username.UsernameAlreadyRegisteredException
import com.chatpg.exception.email.EmailAlreadyRegisteredException
import com.chatpg.mapper.AccountMapper
import com.chatpg.repository.AccountRepository
import com.chatpg.validator.account.AccountCreationCredentialsValidator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.argThat
import org.mockito.kotlin.given
import java.util.*

@ExtendWith(MockitoExtension::class)
class AccountServiceTest {

    private companion object {
        const val TEST_ID = 1L
        const val USERNAME = "username_test"
        const val EMAIL = "email@email.com"
        const val ENCODED_PASSWORD = "encoded_password_test"
    }

    @InjectMocks
    private lateinit var service: AccountService

    @Mock private lateinit var repository: AccountRepository
    @Mock private lateinit var mapper: AccountMapper
    @Mock private lateinit var accountCreationCredentialsValidator: AccountCreationCredentialsValidator

    @Mock private lateinit var accountDto: AccountDto

    @Test
    fun `getById - success`() {
        //ARRANGE
        val account = Account(USERNAME, EMAIL, ENCODED_PASSWORD)

        given(repository.findById(TEST_ID))
            .willReturn(Optional.of(account))

        //ACT
        val response = service.getById(TEST_ID)

        //ASSERT
        assertEquals(account, response)
    }

    @Test
    fun `getById - account not found`() {
        //ARRANGE
        given(repository.findById(TEST_ID))
            .willReturn(Optional.empty())

        //ACT + ASSERT
        assertThrows<AccountNotFoundException> {
            service.getById(TEST_ID)
        }
    }

    @Test
    fun `getDtoById - success`() {
        //ARRANGE
        val account = Account(USERNAME, EMAIL, ENCODED_PASSWORD)

        given(repository.findById(TEST_ID))
            .willReturn(Optional.of(account))

        given(mapper.toDto(account))
            .willReturn(accountDto)

        //ACT
        val response = service.getDtoById(TEST_ID)

        //ASSERT
        assertEquals(accountDto, response)
    }

    @Test
    fun `getDtoById - account not found`() {
        //ARRANGE
        given(repository.findById(TEST_ID))
            .willReturn(Optional.empty())

        //ACT + ASSERT
        assertThrows<AccountNotFoundException> {
            service.getDtoById(TEST_ID)
        }
    }

    @Test
    fun `getByName - success`() {
        //ARRANGE
        val account = Account(USERNAME, EMAIL, ENCODED_PASSWORD)
        val name = "username_test"

        given(repository.findByNameEquals(name))
            .willReturn(account)

        //ACT
        val response = service.getByName(name)

        //ASSERT
        assertEquals(account, response)
    }

    @Test
    fun `getByName - account not found`() {
        //ARRANGE
        val name = "username_test"

        //ACT + ASSERT
        assertThrows<AccountNotFoundException> {
            service.getByName(name)
        }
    }

    @Test
    fun `getByPublicId - success`() {
        //ARRANGE
        val account = Account(USERNAME, EMAIL, ENCODED_PASSWORD)
        val uuid = account.publicId

        given(repository.qFindByPublicId(uuid))
            .willReturn(account)

        //ACT
        val response = service.getByPublicId(uuid)

        //ASSERT
        assertEquals(account, response)
    }

    @Test
    fun `getByPublicId - account not found`() {
        //ACT + ASSERT
        val uuid = UUID(0L, 0L)
        assertThrows<AccountNotFoundException> {
            service.getByPublicId(uuid)
        }
    }

    @Test
    fun `getByEmail - success`() {
        //ARRANGE
        val account = Account(USERNAME, EMAIL, ENCODED_PASSWORD)
        val email = "email@email.com"

        given(repository.qFindByEmail(email))
            .willReturn(account)

        //ACT
        val response = service.getByEmail(email)

        //ASSERT
        assertEquals(account, response)
    }

    @Test
    fun `getByEmail - account not found`() {
        //ARRANGE
        val email = "email@email.com"

        //ACT + ASSERT
        assertThrows<AccountNotFoundException> {
            service.getByEmail(email)
        }
    }

    @Test
    fun `getByRefreshToken - success`() {
        //ARRANGE
        val account = Account(USERNAME, EMAIL, ENCODED_PASSWORD)
        val refreshToken = "refresh_token_test"

        given(repository.qFindByRefreshToken(refreshToken))
            .willReturn(account)

        //ACT + ASSERT
        val response = service.getByRefreshToken(refreshToken)

        assertEquals(account, response)
    }

    @Test
    fun `getByRefreshToken - account not found`() {
        //ARRANGE
        val refreshToken = "refresh_token_test"

        //ACT + ASSERT
        assertThrows<AccountNotFoundException> {
            service.getByRefreshToken(refreshToken)
        }
    }

    @Test
    fun `getByRequestHandleAndClear - success`() {
        //ARRANGE
        val account = Account(USERNAME, EMAIL, ENCODED_PASSWORD)
        account.id = TEST_ID

        val encodedHandle = "encoded_request_handle_test"
        account.auth.requestHandle = encodedHandle

        given(repository.qFindByRequestHandle(encodedHandle))
            .willReturn(account)

        given(repository.qRemoveHandleById(TEST_ID))
            .willReturn(1)

        //ACT
        val response = service.getByRequestHandleAndClear(encodedHandle)

        //ASSERT
        assertEquals(account, response)
        assertEquals(null, account.auth.requestHandle)
    }

    @Test
    fun `getByRequestHandleAndClear - abnormal - found with request handle but not found for request handle removal`() {
        //ARRANGE
        val account = Account(USERNAME, EMAIL, ENCODED_PASSWORD)
        account.id = TEST_ID

        val encodedHandle = "encoded_request_handle_test"
        account.auth.requestHandle = encodedHandle

        given(repository.qFindByRequestHandle(encodedHandle))
            .willReturn(account)

        //ACT + ASSERT
        assertThrows<AccountNotFoundException> {
            service.getByRequestHandleAndClear(encodedHandle)
        }
    }

    @Test
    fun `getByRequestHandleAndClear - account not found`() {
        //ARRANGE
        val refreshToken = "refresh_token_test"

        //ACT + ASSERT
        assertThrows<AccountNotFoundException> {
            service.getByRequestHandleAndClear(refreshToken)
        }
    }

    @Test
    fun `insertAccount - success`() {
        //ARRANGE
        val account = Account(USERNAME, EMAIL, ENCODED_PASSWORD)

        val sameCredentials: Account = argThat {
            name == USERNAME
                    && password == ENCODED_PASSWORD
                    && auth.credentials.email == EMAIL
        }

        given(repository.save(sameCredentials))
            .willReturn(account)

        //ACT
        service.insertAccount(account)
    }

    @Test
    fun `insertAccount - UsernameAlreadyRegisteredException does propagate`() {
        //ARRANGE
        val account = Account(USERNAME, EMAIL, ENCODED_PASSWORD)

        given(accountCreationCredentialsValidator.validate(account))
            .willThrow(UsernameAlreadyRegisteredException::class.java)

        //ACT + ASSERT
        assertThrows<UsernameAlreadyRegisteredException> {
            service.insertAccount(account)
        }
    }

    @Test
    fun `insertAccount - EmailAlreadyRegisteredException does propagate`() {
        //ARRANGE
        val account = Account(USERNAME, EMAIL, ENCODED_PASSWORD)

        given(accountCreationCredentialsValidator.validate(account))
            .willThrow(EmailAlreadyRegisteredException::class.java)

        //ACT + ASSERT
        assertThrows<EmailAlreadyRegisteredException> {
            service.insertAccount(account)
        }
    }

    @Test
    fun `updateRequestHandle - success`() {
        //ARRANGE
        val account = Account(USERNAME, EMAIL, ENCODED_PASSWORD)
        account.id = TEST_ID

        val encodedHandle = "request_handle_test"
        account.auth.requestHandle = encodedHandle

        given(repository.qUpdateRequestHandle(TEST_ID, encodedHandle))
            .willReturn(1)

        //ACT
        service.updateRequestHandle(account, encodedHandle)

        //ASSERT
        assertEquals(encodedHandle, account.auth.requestHandle)
    }

    @Test
    fun `updateRequestHandle - account not found`() {
        //ARRANGE
        val account = Account(USERNAME, EMAIL, ENCODED_PASSWORD)
        account.id = TEST_ID

        val encodedHandle = "request_handle_test"

        //ACT + ASSERT
        assertThrows<AccountNotFoundException> {
            service.updateRequestHandle(account, encodedHandle)
        }
    }

    @Test
    fun `updateRequestHandle - abnormal - null account id`() {
        //ARRANGE
        val account = Account(USERNAME, EMAIL, ENCODED_PASSWORD)
        val encodedHandle = "request_handle_test"

        //ACT + ASSERT
        assertThrows<AccountIdNotFoundException> {
            service.updateRequestHandle(account, encodedHandle)
        }
    }

    @Test
    fun `updatePassword - success`() {
        //ARRANGE
        val account = Account(USERNAME, EMAIL, ENCODED_PASSWORD)
        account.id = TEST_ID

        val encodedPassword = "another_encoded_password_test"

        given(repository.qUpdateEncodedPassword(TEST_ID, encodedPassword))
            .willReturn(1)

        //ACT
        service.updatePassword(account, encodedPassword)

        //ASSERT
        assertEquals(encodedPassword, account.auth.credentials.password)
    }

    @Test
    fun `deleteById - success`() {
        //ARRANGE
        given(repository.qDeleteById(TEST_ID))
            .willReturn(1)

        //ACT
        service.deleteById(TEST_ID)

    }

    @Test
    fun `deleteById - account not found`() {
        //ACT + ASSERT
        assertThrows<AccountNotFoundException> {
            service.deleteById(TEST_ID)
        }
    }

    @Test
    fun `updateStatus - success`() {
        //ARRANGE
        val account = Account(USERNAME, EMAIL, ENCODED_PASSWORD)
        account.id = TEST_ID

        val status = AccountStatus.ENABLED

        given(repository.qUpdateStatus(TEST_ID, status))
            .willReturn(1)

        //ACT
        service.updateStatus(account, status)

        //ASSERT
        assertEquals(status, account.status)
    }

    @Test
    fun `updateStatus - account not found`() {
        //ARRANGE
        val account = Account(USERNAME, EMAIL, ENCODED_PASSWORD)
        account.id = TEST_ID

        val status = AccountStatus.ENABLED

        //ACT + ASSERT
        assertThrows<AccountNotFoundException> {
            service.updateStatus(account, status)
        }

        assertEquals(status, account.status)
    }

    @Test
    fun `updateRefreshToken - success`() {
        //ARRANGE
        val account = Account(USERNAME, EMAIL, ENCODED_PASSWORD)
        account.id = TEST_ID

        val refreshToken = "refresh_token_test"

        given(repository.qUpdateRefreshToken(TEST_ID, refreshToken))
            .willReturn(1)

        //ACT
        service.updateRefreshToken(account, refreshToken)

        //ASSERT
        assertEquals(refreshToken, account.auth.refreshToken)
    }

    @Test
    fun `updateRefreshToken - account not found`() {
        //ARRANGE
        val account = Account(USERNAME, EMAIL, ENCODED_PASSWORD)
        account.id = TEST_ID

        val refreshToken = "refresh_token_test"

        given(repository.qUpdateRefreshToken(TEST_ID, refreshToken))
            .willReturn(1)

        //ACT
        service.updateRefreshToken(account, refreshToken)

        //ASSERT
        assertEquals(refreshToken, account.auth.refreshToken)
    }

}
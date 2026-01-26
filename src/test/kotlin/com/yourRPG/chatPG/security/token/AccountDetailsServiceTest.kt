package com.yourRPG.chatPG.security.token

import com.yourRPG.chatPG.domain.Account
import com.yourRPG.chatPG.repository.AccountRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class AccountDetailsServiceTest {

    @InjectMocks
    private lateinit var service: AccountDetailsService

    @Mock private lateinit var accountRepository: AccountRepository

    @Test
    fun loadUserByUsername() {
        //ARRANGE
        val username = "username_test"
        val account = mock(Account::class.java)

        given(accountRepository.findByNameEquals(username))
            .willReturn(account)

        //ACT
        val response = service.loadUserByUsername(username)

        //ASSERT
        assertEquals(account, response)

    }

}
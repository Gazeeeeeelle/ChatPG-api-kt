package com.chatpg.security.requesthandle

import com.chatpg.domain.account.Account
import com.chatpg.infra.uuid.UuidHelper
import com.chatpg.security.hashing.HashingService
import com.chatpg.service.account.AccountService
import helper.NullSafeMatchers.any
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import java.util.*

@ExtendWith(MockitoExtension::class)
class RequestHandleServiceTest {

    @InjectMocks
    private lateinit var requestHandleService: RequestHandleService

    @Mock private lateinit var accountService: AccountService
    @Mock private lateinit var uuidHelper: UuidHelper
    @Mock private lateinit var hashingService: HashingService

    @Nested
    inner class Success {

        @Test
        fun newRequestHandle() {
            // ARRANGE
            val account = mock<Account>()
            val subject = RequestHandleSubject.CHANGE_PASSWORD

            val encodedHandle = "encoded-handle-test"

            val uuid = UuidHelper.DUMMY_UUID
            given(uuidHelper.generateUuidV7())
                .willReturn(uuid)

            given(hashingService.hashHandle(uuid, subject))
                .willReturn(encodedHandle)

            //ACT
            val response = requestHandleService.newRequestHandle(account, subject)

            //ASSERT
            verify(accountService)
                .updateRequestHandle(account, encodedHandle)

            assertEquals(uuid, response)
        }

    }

    @Nested
    inner class Failure {

        @Test
        fun newRequestHandle() {
            // ARRANGE
            val account = mock<Account>()
            val subject = RequestHandleSubject.CHANGE_PASSWORD

            val encodedHandle = "encoded-handle-test"

            val uuid = UuidHelper.DUMMY_UUID
            given(uuidHelper.generateUuidV7())
                .willReturn(uuid)

            given(hashingService.hashHandle(uuid, subject))
                .willReturn(encodedHandle)

            //ACT
            val response = requestHandleService.newRequestHandle(account, subject)

            //ASSERT
            verify(accountService)
                .updateRequestHandle(account, encodedHandle)

            assertEquals(uuid, response)
        }

    }

}
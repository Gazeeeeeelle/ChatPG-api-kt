package com.chatpg.exception.account

import com.chatpg.exception.http.sc4xx.NotFoundException

class AccountNotFoundException(message: String): NotFoundException(message)
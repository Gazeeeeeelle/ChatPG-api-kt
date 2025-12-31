package com.yourRPG.chatPG.validator

interface IValidatable<T> {

    fun validate(t: T?)

}
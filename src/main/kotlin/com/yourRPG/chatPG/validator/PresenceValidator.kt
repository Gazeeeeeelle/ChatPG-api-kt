package com.yourRPG.chatPG.validator

import com.yourRPG.chatPG.exception.NotFoundException

class PresenceValidator<T>: IValidatable<T?> {

    private var exception = NotFoundException("Not found")

    constructor(exception: NotFoundException) {
        this.exception = exception
    }

    override fun validate(t: T?): T {
        if (t === null) {
            throw exception
        }

        return t
    }

}
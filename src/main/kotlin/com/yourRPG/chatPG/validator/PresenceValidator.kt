package com.yourRPG.chatPG.validator

import com.yourRPG.chatPG.exception.NotFoundException

class PresenceValidator<T>(
    private var exception: NotFoundException
): IValidatable<T?> {

    constructor(): this(exception = NotFoundException("Not found"))

    override fun validate(t: T?): T {
        if (t === null) {
            throw exception
        }

        return t
    }

}
package com.yourRPG.chatPG.validator

import com.yourRPG.chatPG.exception.NotFoundException

class PresenceValidator<T>: IValidatable<T?> {

    private var ex = NotFoundException("Not found")

    constructor(ex: NotFoundException) {
        this.ex = ex
    }

    override fun validate(t: T?): T {
        if (t === null) {
            throw ex
        }

        return t
    }

}
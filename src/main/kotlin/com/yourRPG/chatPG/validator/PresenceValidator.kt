package com.yourRPG.chatPG.validator

import com.yourRPG.chatPG.exception.NotFoundException

class PresenceValidator<T>: IValidatable<T> {

    var ex: NotFoundException = NotFoundException("Not found")

    constructor(ex: NotFoundException) {
        this.ex = ex
    }

    override fun validate(t: T?) {
        if (t === null) {
            throw ex
        }
    }

}
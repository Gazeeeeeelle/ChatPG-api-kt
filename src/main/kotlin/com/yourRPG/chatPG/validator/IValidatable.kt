package com.yourRPG.chatPG.validator

/**
 * Validators' interface.
 */
interface IValidatable<T> {

    /**
     * Method used to run validation.
     * @param t object to be validated.
     */
    fun validate(t: T)

}
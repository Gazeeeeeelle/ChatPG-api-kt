package com.yourRPG.chatPG.service

/**
 * Offers methods to convert lists from the implementation of a single object conversion method.
 */
interface IConvertible<in C, out D> {

    /**
     * This is the method required to be implemented. The other methods relly on this.
     * Converts [C] into a [D] with the logic provided on implementation.
     *
     * @param c to be converted
     * @return [D] converted [c]
     */
    fun dtoOf(c: C): D

    /**
     * This extension function delegates to [dtoOf] method.
     *
     * @param [this] to be converted
     * @return [D] converted [this]
     */
    fun (C).toDto(): D = dtoOf(this)


    /**
     * This method maps all elements of the [List] using the implemented [dtoOf] for conversion.
     *
     * @param [List] to be converted
     * @return converted [List]
     */
    fun listOfDto(c: List<C>): List<D> {
        return c.map(this::dtoOf)
    }

    /**
     * This extension function delegates to [listOfDto] method.
     *
     * @param [List] to be converted
     * @return converted [List]
     */
    fun (List<C>).toListDto(): List<D> = listOfDto(this)

}
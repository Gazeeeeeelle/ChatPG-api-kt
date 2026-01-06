package com.yourRPG.chatPG.service

/**
 * TODO
 */
interface IConvertible<in C, out D> {

    /**
     * TODO
     */
    fun dtoOf(c: C): D

    /**
     * TODO
     */
    fun (C).toDto(): D = dtoOf(this)


    /**
     * TODO
     */
    fun listOfDto(c: Iterable<C>): List<D> {
        return c.map { c: C -> c.toDto() }
    }

    /**
     * TODO
     */
    fun (Iterable<C>).toListDto(): List<D> = listOfDto(this)

}
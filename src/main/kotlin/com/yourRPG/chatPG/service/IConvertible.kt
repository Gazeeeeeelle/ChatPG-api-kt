package com.yourRPG.chatPG.service

/**
 * TODO
 */
interface IConvertible<in C, out D> {

    /**
     * TODO
     */
    fun dto(c: C): D

    /**
     * TODO
     */
    fun (C).toDto(): D = dto(this)


    /**
     * TODO
     */
    fun listDto(c: Iterable<C>): List<D> {
        return c.map { c: C -> c.toDto() }
    }

    /**
     * TODO
     */
    fun (Iterable<C>).toListDto(): List<D> = listDto(this)

}
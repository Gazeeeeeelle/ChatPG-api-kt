package com.yourRPG.chatPG.service

/**
 * TODO
 */
interface IConvertible<C, D> {

    /**
     * TODO
     */
    fun (C).dto(): D

    /**
     * TODO
     */
    fun dto(c: C): D {
        return c.dto()
    }

    /**
     * TODO
     */
    fun (MutableList<C>).dto(): MutableList<D> {
        return this
            .map { c: C -> c.dto() }
            .toMutableList()
    }

//    fun convertList(c: MutableList<C>): MutableList<D> {
//        return c
//            .map { c: C -> c.dto() }
//            .toMutableList()
//    }

}
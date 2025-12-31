package com.yourRPG.chatPG.service

interface IConvertible<C, D> {

    fun convert(c: C): D

    fun convertList(c: MutableList<C>): MutableList<D> {
        return c.map { c: C -> this.convert(c) }
            .toMutableList()
    }

}
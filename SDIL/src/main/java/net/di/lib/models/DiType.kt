package net.di.lib.models

import net.di.lib.interfaces.IType

class DiType<T>(private val value: T) : IType{
    fun get(): T {
        return value
    }
}
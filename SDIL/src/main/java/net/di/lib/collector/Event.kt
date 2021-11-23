package net.di.lib.collector

import net.di.lib.interfaces.IType

class Event {
    fun onEventData(key: String, data: IType) {
        TasksHelper.publishEventData(key, data)
    }
}
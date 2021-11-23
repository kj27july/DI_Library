package net.di.lib.collector

import net.di.lib.interfaces.IType

internal object TasksHelper {
    fun createTask(pkg: String?): DataCollector {
        return if (pkg == null) DataCollectorImpl() else DataCollectorImpl(pkg)
    }

    fun publishEventData(key: String, data: IType?) {
        return DataCollectorImpl(key, data).deliverEventData()
    }
}
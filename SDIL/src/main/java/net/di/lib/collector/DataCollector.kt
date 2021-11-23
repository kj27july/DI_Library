package net.di.lib.collector

import net.di.lib.interfaces.IType
import org.json.JSONObject

abstract class DataCollector {
    lateinit var pkg: String
    lateinit var key: String
    var input: JSONObject? = null
    var output: IType? = null
    lateinit var outputToShare: String
    var schemaRequest = false

    abstract fun onSchemaRequest()
    abstract fun setData(data: IType?)
    abstract fun onDataRequest(key: String, input: JSONObject?)
    abstract fun onExecute(): String
    abstract fun onFinish()
    abstract fun deliverEventData()
    abstract fun deliverData(isEventData: Boolean)
    abstract fun onValidateRequest(): Boolean
    abstract fun onRequestError()
    abstract fun onValidateResponse(): Boolean
    abstract fun onResponseError()
}
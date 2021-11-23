package net.di.lib.collector

import android.util.Log
import net.di.lib.controllers.DataHandler
import net.di.lib.feature.App
import net.di.lib.interfaces.IType
import net.di.lib.models.DiType
import net.di.lib.utils.FileUtil
import org.json.JSONObject

internal class DataCollectorImpl() : DataCollector() {
    private val TAG = "akshay"
    private var dataHandler = DataHandler(this)

    constructor(key: String, data: IType?) : this() {
        this.key = key
        this.output = data
    }

    constructor(pkg: String) : this() {
        this.pkg = pkg
    }

    override fun onSchemaRequest() {
        this.schemaRequest = true
    }

    override fun setData(data: IType?) {
        this.output = data
    }

    override fun onDataRequest(key: String, input: JSONObject?) {
        this.key = key
        this.input = input
    }

    override fun onExecute(): String {
        Log.d(TAG, "onExecute: isSchemaRequest $schemaRequest")
        if (onValidateRequest()) {
            val instance = App.getInstance()
            if (schemaRequest) {
                val schema =
                    FileUtil(instance.context).readFile("schema_V" + instance.version + ".json")!!
                setData(DiType(JSONObject(schema)))
            } else {
                val data = instance.iFeature.getData(this.key, this.input)
                setData(data)
            }
            onFinish()
        } else {
            setData(DiType("error"))
            onRequestError()
        }
        return outputToShare
    }

    override fun onFinish() {
        Log.d(TAG, "onFinish: ")
        if (onValidateResponse()) {
            if (!schemaRequest) {
                output = getJsonOutput()
            }
            deliverData(false)
        } else {
            onResponseError()
        }
    }

    override fun deliverEventData() {
        Log.d(TAG, "publishEventData: ")
        if (onValidateResponse()) {
            output = getJsonOutput()
            for (subscriber in App.getInstance().subscribers) {
                this.pkg = subscriber.key
                if (dataHandler.isSubscribed()) {
                    deliverData(true)
                }
            }
        } else {
            onResponseError()
        }
    }

    override fun deliverData(isEventData: Boolean) {
        outputToShare = (output as DiType<*>).get().toString()

        if (isEventData && App.getInstance().consumers.containsKey(pkg)) {
            Log.d(TAG, "deliverData: pkgName: $pkg")
            val iResponse = App.getInstance().consumers[pkg]!!
            iResponse.onEvent(outputToShare)
        }
    }

    override fun onValidateRequest(): Boolean {
        Log.d(TAG, "onValidateRequest: ")
        return schemaRequest || dataHandler.validateDataRequest()
    }

    override fun onRequestError() {
        Log.d(TAG, "onRequestError: ")
    }

    override fun onValidateResponse(): Boolean {
        Log.d(TAG, "onValidateResponse: ")
        return schemaRequest || dataHandler.validateDataResponse()
    }

    override fun onResponseError() {
        Log.d(TAG, "onResponseError: ")
    }

    private fun getJsonOutput(): DiType<JSONObject> {
        val jsonObject = JSONObject()
        jsonObject.put(key, (output as DiType<*>).get())
        return DiType(jsonObject)
    }
}
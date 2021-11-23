package net.di.lib.controllers

import android.util.Log
import net.di.lib.collector.DataCollector
import net.di.lib.feature.BaseSchema
import net.di.lib.feature.App
import net.di.lib.models.DiType
import net.di.lib.utils.ReturnType
import org.json.JSONArray
import org.json.JSONObject

class DataHandler(private val dataCollector: DataCollector) : BaseSchema() {
    private val TAG = "akshay"
    private val schema = App.getInstance().jsonSchema

    fun validateDataRequest(): Boolean {
        Log.d(
            TAG,
            "validateDataRequest:consumer: ${isConsumer()},subscribed: ${isSubscribed()},feature: ${verifyKey()},args: ${verifyArguments()}"
        )
        return isConsumer() && isSubscribed() && verifyKey() && verifyArguments()
    }

    private fun isConsumer(): Boolean {
        return App.getInstance().subscribers.contains(dataCollector.pkg)
    }

    fun isSubscribed(): Boolean {
        return App.getInstance().subscribers[dataCollector.pkg]!!.contains(dataCollector.key)
    }

    private fun verifyKey(): Boolean {
        return super.hasKey(schema, dataCollector.key)
    }

    private fun hasArguments(): Boolean {
        val featureDetails = super.getFeatureDetails(schema, dataCollector.key)!!
        return super.hasKey(featureDetails, ARGUMENTS)
    }

    private fun verifyArguments(): Boolean {
        if (hasArguments()) {
            val featureDetails = super.getFeatureDetails(schema, dataCollector.key)!!
            val argsJson = super.arguments(featureDetails)
            for (key in argsJson.keys()) {
                try {
                    //check whether all inputs exist with correct type.
                    when (argsJson.get(key)) {
                        ReturnType.STRING -> dataCollector.input!!.getString(key)
                        ReturnType.INTEGER -> dataCollector.input!!.getInt(key)
                        ReturnType.LONG -> dataCollector.input!!.getLong(key)
                        ReturnType.BOOLEAN -> dataCollector.input!!.getBoolean(key)
                        ReturnType.DOUBLE -> dataCollector.input!!.getDouble(key)
                        //TODO(non primitive data types not support)
                    }
                } catch (e: Exception) {
                    //return false in case of key missing or wrong input type
                    e.printStackTrace()
                    return false
                }
            }
        }
        return true
    }

    fun validateDataResponse(): Boolean {
        Log.e(TAG, "validateDataResponse: ")
        val featureDetails = super.getFeatureDetails(schema, dataCollector.key)!!
        return processOutputType(featureDetails, (dataCollector.output as DiType<*>).get()!!)
    }

    private fun processOutputType(featureDetails: JSONObject, output: Any): Boolean {
        Log.d(TAG, "processOutputType: $featureDetails")
        val returnType = returnType(returnType(featureDetails)) //return type from schema
        return if (super.isNonPrimitiveDataType(returnType)) {
            processNonPrimitiveOutput(featureDetails, returnType, output)
        } else {
            returnType == typeOf(output)
        }
    }

    private fun processNonPrimitiveOutput(
        featureDetails: JSONObject, returnType: ReturnType, output: Any
    ): Boolean {
        Log.d(TAG, "processNonPrimitiveOutput: $featureDetails")
        try {
            if (isArray(returnType) && typeOf(output) == ReturnType.ARRAY) {
                val itemDetails = arrayItem(featureDetails)
                return processOutputType(
                    itemDetails, (output as JSONArray).get(0)
                ) //TODO("null check required")
            } else if (isObject(returnType) && typeOf(output) == ReturnType.OBJECT) { //object
                val referenceDetails = reference(featureDetails)
                var isCorrectOutput = true
                for (key in referenceDetails.keys()) {
                    isCorrectOutput = isCorrectOutput && processOutputType(
                        referenceDetails.getJSONObject(key), (output as JSONObject).get(key)
                    )
                }
                return isCorrectOutput
            }
        } catch (e: Exception) {
            Log.d(TAG, "processNonPrimitiveOutput: ", e)
        }
        return false
    }
}
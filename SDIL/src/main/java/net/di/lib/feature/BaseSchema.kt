package net.di.lib.feature

import net.di.lib.models.Property
import net.di.lib.utils.FeatureType
import net.di.lib.utils.ReturnType
import net.di.lib.utils.ReturnType.*
import org.json.JSONArray
import org.json.JSONObject

abstract class BaseSchema {
    val KEY = "name"
    private val RETURN_TYPE = "returnType"
    private val DESCRIPTION = "description"
    protected val ARGUMENTS = "arguments"
    private val FEATURE_TYPE = "featureType"
    private val ITEM = "item"
    private val REFERENCE = "reference"

    protected fun getFeatureDetails(schema: JSONObject, key: String): JSONObject? {
        return schema.getJSONObject(key)
    }

    protected fun hasKey(jsonObject: JSONObject, key: String): Boolean {
        return jsonObject.has(key)
    }

    protected fun name(property: Property): String {
        return property.name
    }

    private fun returnType(property: Property): ReturnType {
        return property.returnType
    }

    protected fun returnType(jsonObject: JSONObject): String {
        return jsonObject.getString(RETURN_TYPE)
    }

    protected fun returnType(returnType: String): ReturnType {
        return ReturnType.valueOf(returnType)
    }

    private fun description(property: Property): String {
        return property.description
    }

    protected fun description(jsonObject: JSONObject): String {
        return jsonObject.getString(DESCRIPTION)
    }

    private fun arguments(property: Property): ArrayList<Property> {
        return property.args
    }

    protected fun arguments(jsonObject: JSONObject): JSONObject {
        return jsonObject.getJSONObject(ARGUMENTS)
    }

    private fun featureType(property: Property): FeatureType {
        return property.featureType
    }

    private fun featureType(jsonObject: JSONObject): String {
        return jsonObject.getString(FEATURE_TYPE)
    }

    private fun featureType(featureType: String): FeatureType {
        return FeatureType.valueOf(featureType)
    }

    protected fun arrayItem(property: Property): ReturnType? {
        return property.arrayItem
    }

    protected fun arrayItem(jsonObject: JSONObject): JSONObject {
        return jsonObject.getJSONObject(ITEM)
    }

    protected fun reference(jsonObject: JSONObject): JSONObject {
        return jsonObject.getJSONObject(REFERENCE)
    }

    protected fun getSchema(properties: List<Property>): JSONObject {
        val jsonObject = JSONObject()
        for (property in properties) {
            jsonObject.put(property.name, convertPropertyToJson(property))
        }
        return jsonObject
    }

    private fun convertPropertyToJson(property: Property): JSONObject {
        val propertyJson = JSONObject()
        propertyJson.put(RETURN_TYPE, returnType(property))
        propertyJson.put(DESCRIPTION, description(property))
        propertyJson.put(FEATURE_TYPE, featureType(property))
        if (arguments(property).isNotEmpty()) {
            propertyJson.put(ARGUMENTS, processArguments(arguments(property)))
        }
        if (isNonPrimitiveDataType(returnType(property))) {
            val returnTypeSchema = processReturnType(property)
            propertyJson.put(returnTypeSchema.first, returnTypeSchema.second)
        }
        return propertyJson
    }

    protected fun isEventFeature(featureObject: JSONObject): Boolean {
        return featureType(featureType(featureObject)) == FeatureType.EVENT
    }

    protected fun typeOf(value: Any?): ReturnType? {
        return when (value) {
            is String -> STRING
            is Int -> INTEGER
            is Long -> LONG
            is Boolean -> BOOLEAN
            is Double -> DOUBLE
            is JSONArray -> ARRAY
            is JSONObject -> OBJECT
            else -> null
        }
    }

    protected fun isNonPrimitiveDataType(type: ReturnType): Boolean {
        return isArray(type) || isObject(type)
    }

    protected fun isArray(type: ReturnType): Boolean {
        return type == ARRAY
    }

    protected fun isObject(type: ReturnType): Boolean {
        return type == OBJECT
    }

    private fun processArguments(args: ArrayList<Property>): JSONObject {
        val jsonObject = JSONObject()
        for (arg in args) {
            jsonObject.put(arg.name, arg.returnType)
        }
        return jsonObject
    }

    private fun processReturnType(property: Property): Pair<String, JSONObject> {
        val type = returnType(property)
        return if (isArray(type)) {
            returnTypeArraySchema(property)
        } else { //object
            returnTypeObjectSchema(property.objectItems)
        }
    }

    private fun returnTypeArraySchema(property: Property): Pair<String, JSONObject> {
        if (property.arrayItem == null) throw Exception("arrayItem cannot be null")
        val arrayItem = property.arrayItem!!
        val itemJson = JSONObject()
        itemJson.put(RETURN_TYPE, arrayItem)
        if (isObject(arrayItem)) {
            val objectSchema = returnTypeObjectSchema(property.objectItems)
            itemJson.put(objectSchema.first, objectSchema.second)
        }
        return Pair(ITEM, itemJson)
    }

    private fun returnTypeObjectSchema(objectItems: ArrayList<Property>): Pair<String, JSONObject> {
        if (objectItems.isEmpty()) throw Exception("objectItems cannot be empty")
        val referenceJson = JSONObject()
        for (item in objectItems) {
            val itemJson = JSONObject()
            itemJson.put(RETURN_TYPE, item.returnType)
            if (isNonPrimitiveDataType(item.returnType)) {
                val schema = processReturnType(item)
                itemJson.put(schema.first, schema.second)
            }
            referenceJson.put(item.name, itemJson)
        }
        return Pair(REFERENCE, referenceJson)
    }
}
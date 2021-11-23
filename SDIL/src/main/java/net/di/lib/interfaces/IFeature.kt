package net.di.lib.interfaces

import org.json.JSONObject

interface IFeature {

    fun startEvent(feature: String)

    fun stopEvent(feature: String)

    fun getData(property: String, inputArg: JSONObject?): IType
}
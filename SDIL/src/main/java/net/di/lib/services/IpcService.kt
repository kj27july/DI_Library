package net.di.lib.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import net.generic.ipc.IRequest
import net.generic.ipc.model.RequestData
import net.generic.ipc.IResponse
import net.di.lib.collector.TasksHelper
import net.di.lib.controllers.AppController
import net.di.lib.feature.App
import org.json.JSONObject

class IpcService : Service() {
    private val TAG = "akshay"

    override fun onBind(intent: Intent?): IBinder {
        return iRequest
    }

    private val iRequest = object : IRequest.Stub() {

        override fun getSchema(): String {
            Log.d(TAG, "getSchema: ")
            return AppController.getJsonSchema()
        }

        override fun subscribe(pkg: String?, config: String?) {
            AppController.subscribe(pkg, config)
        }

        override fun requestData(pkg: String, requestData: List<RequestData>): List<String> {
            val dataList = ArrayList<String>()
            for (data in requestData) {
                Log.d(TAG, "requestData: ${data.key}")
                val task = TasksHelper.createTask(pkg)
                task.onDataRequest(data.key, JSONObject(data.input1))
                dataList.add(task.onExecute())
            }
            return dataList
        }

        override fun registerCallBack(pkgName: String?, response: IResponse?) {
            Log.d(TAG, "registerCallBack: $response")
            if (response != null) App.getInstance().consumers[pkgName!!] = response
        }

        override fun unregisterCallBack(pkg: String?) {
            Log.d(TAG, "unregisterCallBack: ")
            TODO("Not yet implemented")
            //https://android.googlesource.com/platform/development/+/master/samples/ApiDemos/src/com/example/android/apis/app/RemoteService.java#127
        }
    }
}
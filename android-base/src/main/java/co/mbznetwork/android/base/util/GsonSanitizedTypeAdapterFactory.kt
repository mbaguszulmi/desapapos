package co.mbznetwork.android.base.util

import co.mbznetwork.android.base.extension.cleanUpNulls
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.json.JSONObject
import java.io.StringReader
import kotlin.collections.HashMap

class GsonSanitizedTypeAdapterFactory(
    val isSanitizeRequired: Boolean = true
): TypeAdapterFactory {
    override fun <T : Any?> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T> {
        val delegate = gson.getDelegateAdapter(this, type)

        val isNotKotlinClass = type.rawType.declaredAnnotations.none {
            it.annotationClass.qualifiedName == "kotlin.Metadata"
        }
        return object : TypeAdapter<T>() {
            override fun write(out: JsonWriter, value: T) {
//                if (value is AbsDate) {
//                    val dateDelegate = gson.getDelegateAdapter(
//                        this@GsonSanitizedTypeAdapterFactory,
//                        object : TypeToken<Date>() {}
//                    )
//
//                    when(value) {
//                        is AbsDate.DateSet -> dateDelegate.write(out, (value as AbsDate.DateSet).date)
//                        else -> out.nullValue()
//
//                    }
//                } else 
                if (value is String && value.isEmpty()) {
                    out.nullValue()
                } else {
                    if (isNotKotlinClass) delegate.write(out, value)
                    else {
                        (value as Any?)?.javaClass?.let {
                            if (!it.name.contains("kotlin.collections") && value == it.newInstance()) out.nullValue()
                            else null
                        }?: delegate.write(out, value)
                    }
                }
            }

            override fun read(input: JsonReader?): T {
                return if (isNotKotlinClass) {
                    delegate.read(input)
                }
//                else if (type == object : TypeToken<AbsDate>() {}) {
//                    val dateDelegate = gson.getDelegateAdapter(
//                        this@GsonSanitizedTypeAdapterFactory,
//                        object : TypeToken<Date>() {}
//                    )
//
//                    val value = try {
//                        dateDelegate.read(input)
//                    } catch (e: Throwable) {
//                        null
//                    }
//                    if (value != null) {
//                        AbsDate.DateSet(value) as T
//                    } else AbsDate.NotSet as T
//                } 
                else {
                    delegate.read(
                        if (isSanitizeRequired) {
                            val mapDelegate = gson.getDelegateAdapter(
                                this@GsonSanitizedTypeAdapterFactory,
                                object : TypeToken<HashMap<*, *>>() {}
                            )
                            val data = mapDelegate.read(input).cleanUpNulls()
                            JsonReader(StringReader(JSONObject(data).toString()))
                        } else {
                            input
                        }
                    )
                }
            }
        }
    }

}
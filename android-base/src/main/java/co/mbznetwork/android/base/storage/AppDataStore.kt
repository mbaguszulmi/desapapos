package co.mbznetwork.android.base.storage

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import java.lang.reflect.Type

private const val DATA_STORE_NAME = "abacus_data_store"
private val Context.dataStore by preferencesDataStore(DATA_STORE_NAME)

class AppDataStore(
    context: Context,
    private val gson: Gson
) {
    private val dataStore = context.dataStore

    @Suppress("UNCHECKED_CAST")
    fun <T> getData(key: String, type: Class<T>): Flow<T?> = dataStore.data.catch { exception ->
        when (exception) {
            is IOException -> {
                Timber.e(exception, "Error reading preferences")
                emit(dataStore.updateData { preferences ->
                    // Return existing preferences
                    preferences
                })
            }
        }
    }.catch { exception ->
        // Fallback in case the updateData above also fails
        Timber.e(exception, "Fallback: returning default value")
        emit(dataStore.updateData { emptyPreferences() })
    }.catch {
        // Final fallback - return default value
        emit(emptyPreferences())
    }.map { preferences ->
        Timber.d("Getting data $key, type = $type")
        when (type) {
            String::class.java -> {
                (preferences[stringPreferencesKey(key)] as? T)
            }

            Int::class.java -> {
                (preferences[intPreferencesKey(key)] as? T)
            }

            Double::class.java -> {
                (preferences[doublePreferencesKey(key)] as? T)
            }

            Boolean::class.java -> {
                (preferences[booleanPreferencesKey(key)] as? T)
            }

            Float::class.java -> {
                (preferences[floatPreferencesKey(key)] as? T)
            }

            Long::class.java -> {
                (preferences[longPreferencesKey(key)] as? T)
            }

            Set::class.java -> {
                (preferences[stringSetPreferencesKey(key)] as T)
            }

            else -> {
                Timber.d("Getting general type ($type) from $key")
                preferences[stringPreferencesKey(key)]?.let {
                    gson.fromJson(it, type)
                }
            }
        }
    }.distinctUntilChanged()

//    fun <T: Any>getTypesFromValue(value: T): Class<T> {
//        return when (value) {
//            is String -> String::class.java
//            is Int -> Int::class.java
//            is Double -> Double::class.java
//            is Boolean -> Boolean::class.java
//            is Float -> Float::class.java
//            is Long -> Long::class.java
//            is Set<*> -> Set::class.java
//            else -> value::class.java
//        }
//    }

    fun <T: Any> getDataDefault(key: String, default: T): Flow<T> {
        return getData(
            key, when (default) {
                is String -> String::class.java
                is Int -> Int::class.java
                is Double -> Double::class.java
                is Boolean -> Boolean::class.java
                is Float -> Float::class.java
                is Long -> Long::class.java
                is Set<*> -> Set::class.java
                else -> default::class.java
            } as Class<T>
        ).map {
            (it ?: default)
        }
    }

    suspend fun <T> removeData(key: String, type: Class<T>) {
        dataStore.edit { preferences ->
            val preferencesKey = when (type) {
                String::class.java -> {
                    stringPreferencesKey(key)
                }

                Int::class.java -> {
                    intPreferencesKey(key)
                }

                Double::class.java -> {
                    doublePreferencesKey(key)
                }

                Boolean::class.java -> {
                    booleanPreferencesKey(key)
                }

                Float::class.java -> {
                    floatPreferencesKey(key)
                }

                Long::class.java -> {
                    longPreferencesKey(key)
                }

                Set::class.java -> {
                    stringSetPreferencesKey(key)
                }

                else -> {
                    stringPreferencesKey(key)
                }
            }

            preferences.remove(preferencesKey)
        }
    }

    suspend fun <T> isKeyExists(key: String, type: Class<T>): Boolean {
        return flowIsKeyExists(key, type).first()
    }

    fun <T> flowIsKeyExists(key: String, type: Class<T>): Flow<Boolean> {
        return when (type) {
            String::class.java -> {
                stringPreferencesKey(key)
            }

            Int::class.java -> {
                intPreferencesKey(key)
            }

            Double::class.java -> {
                doublePreferencesKey(key)
            }

            Boolean::class.java -> {
                booleanPreferencesKey(key)
            }

            Float::class.java -> {
                floatPreferencesKey(key)
            }

            Long::class.java -> {
                longPreferencesKey(key)
            }

            Set::class.java -> {
                stringSetPreferencesKey(key)
            }

            else -> {
                stringPreferencesKey(key)
            }
        }.let { preferenceKey ->
            dataStore.data.map {
                it.contains(preferenceKey)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun <T> editData(key: String, value: T) {
        try {
            unsafeEditData(key, value)
        } catch (e: Exception) {
            Timber.e(e, "Error writing to preferences")
            // Attempt to recreate store if corrupt
            if (e is CorruptionException) {
                dataStore.edit { preferences ->
                    preferences.clear()
                    unsafeEditData(key, value)
                }
            }
        }
    }

    private suspend fun <T> unsafeEditData(key: String, value: T) {
        dataStore.edit { preferences ->
            when (value) {
                is String -> {
                    preferences[stringPreferencesKey(key)] = value
                }

                is Int -> {
                    preferences[intPreferencesKey(key)] = value
                }

                is Double -> {
                    preferences[doublePreferencesKey(key)] = value
                }

                is Boolean -> {
                    preferences[booleanPreferencesKey(key)] = value
                }

                is Float -> {
                    preferences[floatPreferencesKey(key)] = value
                }

                is Long -> {
                    preferences[longPreferencesKey(key)] = value
                }

                is Set<*> -> {
                    preferences[stringSetPreferencesKey(key)] = value as Set<String>
                }

                else -> {
                    preferences[stringPreferencesKey(key)] = gson.toJson(value)
                }
            }
        }
    }

    fun <T> getListData(key: String, type: Type): Flow<T> = dataStore.data.map<Preferences, T> {
        Gson().fromJson(it[stringPreferencesKey(key)] ?: "[]", type)
    }.distinctUntilChanged()

//    fun <T : Enum<T>> getEnum(key: String, type: Class<T>, default: T): Flow<T> {
//        return dataStore.data
//            .map { preferences ->
//                val raw = preferences[stringPreferencesKey(key)]
//                try {
//                    java.lang.Enum.valueOf(type, raw ?: "")
//                } catch (e: Exception) {
//                    default
//                }
//            }
//            .distinctUntilChanged()
//    }
//
//    suspend fun <T : Enum<T>> setEnum(key: String, value: T) {
//        dataStore.edit { preferences ->
//            preferences[stringPreferencesKey(key)] = value.name
//        }
//    }

    suspend fun clear() {
        dataStore.edit {
            it.clear()
        }
    }
}

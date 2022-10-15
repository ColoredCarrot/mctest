package info.voidev.mctest.runtimesdk.util

import kotlin.reflect.KProperty

operator fun <T> ThreadLocal<T>.getValue(thisRef: Any?, property: KProperty<*>): T {
    return get()
}

operator fun <T> ThreadLocal<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    set(value)
}

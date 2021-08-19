package de.dude.eventbus

import de.dude.util.add
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

object EventBus {

    // TODO fix unused generic type limitation
    // TODO fix weakreference does not get garbage collected if it is a standalone lambda?

    private val destinations =
        ConcurrentHashMap<KClass<BaseEvent<*>>, MutableSet<WeakReference<(BaseEvent<*>) -> Any?>>>()

    inline fun <reified R, reified E : BaseEvent<in R>> onReceive(noinline invoke: (E) -> R) =
        onReceiveByClass(invoke, E::class)

    @JvmName("onVoidReceive")
    inline fun <reified E : BaseEvent<in Unit>> onReceive(noinline invoke: (E) -> Unit) =
        onReceiveByClass(invoke, E::class)

    inline fun <reified R, E : BaseEvent<in R>> emit(event: E) = emitByClass(event, R::class)

    @Suppress("UNCHECKED_CAST")
    fun <R, E : BaseEvent<in R>> onReceiveByClass(invoke: (E) -> R, eventClass: KClass<E>) {
        val untypedClass = eventClass as KClass<BaseEvent<*>>
        val untypedInvoke = invoke as (BaseEvent<*>) -> Any?
        destinations.add(untypedClass, WeakReference(untypedInvoke))
    }

    @Suppress("UNCHECKED_CAST")
    fun <R, E : BaseEvent<in R>> emitByClass(event: E, resultClass: KClass<*>): List<R> {
        val untypedClass = event::class as KClass<BaseEvent<*>>
        val iterator = destinations[untypedClass]?.iterator() ?: return emptyList()
        val resultList = mutableListOf<R>()

        while (iterator.hasNext()) {
            val receiver = iterator.next().get()

            if (receiver == null) {
                println("a")
                iterator.remove()
            } else {
                println(receiver)
                val result = receiver(event)
                if (resultClass != Unit::class) {
                    resultList.add(result as R)
                }
            }
        }
        return resultList
    }

}


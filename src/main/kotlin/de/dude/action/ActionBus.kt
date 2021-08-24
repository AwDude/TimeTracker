package de.dude.action

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.superclasses

interface Action

object ActionBus {

    private val receivers = ConcurrentHashMap<KClass<out Action>, MutableSet<ActionReference<*>>>()

    inline fun <reified E : Action> call(noinline run: E.() -> Unit) = call(E::class, run)

    fun <E : Action> call(receiverClass: KClass<E>, run: E.() -> Unit) {
        receivers[receiverClass]?.forEach { actionReference ->
            actionReference.get()?.let { event ->
                @Suppress("UNCHECKED_CAST")
                (event as E).run()
            }
        }
    }

    fun <E : Action> hook(receiver: E, vararg receiverClasses: KClass<out E>) =
        receiverClasses.forEach { receiverClass ->
            hook(receiver, receiverClass)
        }

    inline fun <reified E : Action> hook(receiver: E) = hook(receiver, E::class)

    fun <E : Action> hook(receiver: E, receiverClass: KClass<out E>) {
        receivers.compute(receiverClass) { _, set ->
            (set ?: ConcurrentHashMap.newKeySet()).apply {
                add(ActionReference(receiver))
            }
        }
    }

    fun hookAll(receiver: Action) = receiver::class.superclasses.forEach {
        if (it.isSubclassOf(Action::class)) {
            @Suppress("UNCHECKED_CAST")
            hook(receiver, it as KClass<out Action>)
        }
    }

    inline fun <reified E : Action> unhook(receiver: E) = unhook(E::class, receiver)

    fun <E : Action> unhook(receiverClass: KClass<E>, receiver: E) {
        removeReceiver(receiverClass, receiver)
    }

    fun unhookAll(receiver: Action) = receivers.keys.forEach { receiverClass ->
        if (receiverClass.isInstance(receiver)) removeReceiver(receiverClass, receiver)
    }

    private fun removeReceiver(receiverClass: KClass<out Action>, receiver: Action) {
        receivers.computeIfPresent(receiverClass) { _, set ->
            set.removeIf { actionReference ->
                actionReference.get().let { it == null || it == receiver }
            }
            // TODO need lock for Concurrent Set? what if now something gets added?
            if (set.isEmpty()) null else set
        }
    }

}


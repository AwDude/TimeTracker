package de.dude.action

import de.dude.util.addInSet
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

object ActionBus {

    private val receivers = ConcurrentHashMap<KClass<*>, MutableSet<ActionReference<*>>>()

    inline fun <reified E : Any> call(noinline run: E.() -> Unit) = callByClass(E::class, run)

    fun <E : Any> callByClass(receiverClass: KClass<E>, run: E.() -> Unit) {
        receivers[receiverClass]?.forEach { ActionReference ->
            ActionReference.get()?.let { event ->
                @Suppress("UNCHECKED_CAST")
                (event as E).run()
            }
        }
    }

    fun <E : Any> hook(receiver: E, vararg receiverClasses: KClass<out E>) = receiverClasses.forEach { receiverClass ->
        hook(receiver, receiverClass)
    }

    inline fun <reified E : Any> hook(receiver: E) = hook(receiver, E::class)

    @Synchronized
    fun <E : Any> hook(receiver: E, receiverClass: KClass<out E>) {
        if (!receiverClass.java.isInterface) throw Exception("Relay receivers have to be interfaces.")
        receivers.addInSet(receiverClass, ActionReference(receiver))
    }

    inline fun <reified E : Any> unhook(receiver: E) = unhook(E::class, receiver)

    fun <E : Any> unhook(receiverClass: KClass<E>, receiver: E) {
        if (!receiverClass.java.isInterface) throw Exception("Unhook requires an interface. Use unhookAll to remove all interface hooks for one object.")
        removeReceiver(receiverClass, receiver)
    }

    fun unhookAll(receiver: Any) = receivers.keys.forEach { receiverClass ->
        if (receiverClass.isInstance(receiver)) {
            removeReceiver(receiverClass, receiver)
        }
    }

    @Synchronized
    private fun removeReceiver(receiverClass: KClass<*>, receiver: Any) {
        val hooks = receivers[receiverClass] ?: return
        hooks.removeIf { ActionReference ->
            ActionReference.get().let { it == null || it == receiver }
        }
        if (hooks.isEmpty()) {
            receivers.remove(receiverClass)
        }
    }

}


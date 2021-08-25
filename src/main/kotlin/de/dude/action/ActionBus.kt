package de.dude.action

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.superclasses

@Suppress("MemberVisibilityCanBePrivate")
object ActionBus {

    interface Action

    private val receivers = ConcurrentHashMap<KClass<out Action>, MutableSet<ActionReference<*>>>()

    inline fun <reified E : Action> call(noinline run: E.() -> Unit) = call(E::class, run)

    fun <E : Action> call(receiverClass: KClass<E>, run: E.() -> Unit) {
        val callList = mutableListOf<E>()
        receivers.computeIfPresent(receiverClass) { _, set ->
            set.removeIf {
                it.get()?.let { action ->
                    @Suppress("UNCHECKED_CAST")
                    callList.add(action as E)
                    false
                } ?: true
            }
            if (set.isEmpty()) null else set
        }
        callList.forEach(run)
    }

    fun hook(receiver: Action, vararg receiverClasses: KClass<out Action>) =
        receiverClasses.forEach { receiverClass ->
            receivers.compute(receiverClass) { _, set ->
                (set ?: ConcurrentHashMap.newKeySet()).apply {
                    add(ActionReference(receiver))
                }
            }
        }

    fun hook(receiver: Action) {
        @Suppress("UNCHECKED_CAST")
        val classes = receiver::class.superclasses.filter { it.isSubclassOf(Action::class) } as List<KClass<out Action>>
        hook(receiver, *classes.toTypedArray())
    }

    fun unhook(receiver: Action, vararg receiverClasses: KClass<out Action>) =
        receiverClasses.forEach { receiverClass ->
            receivers.computeIfPresent(receiverClass) { _, set ->
                set.removeIf { actionReference ->
                    actionReference.get().let { it == null || it == receiver }
                }
                if (set.isEmpty()) null else set
            }
        }

    fun unhook(receiver: Action) {
        val receiverClasses = receivers.keys.filter { it.isInstance(receiver) }
        unhook(receiver, *receiverClasses.toTypedArray())
    }

}


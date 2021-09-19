package de.dude.library.action

import de.dude.library.extension.className
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.superclasses

@Suppress("MemberVisibilityCanBePrivate", "unused")
object ActionBus {

    interface Action

    @Volatile
    var log: ((String) -> Unit)? = null

    private val receivers = ConcurrentHashMap<KClass<out Action>, MutableSet<ActionReference<Action>>>()

    inline fun <reified E : Action> call(noinline run: E.() -> Unit) = call(E::class, run)

    fun <E : Action> call(receiverClass: KClass<E>, run: E.() -> Unit) {
        receivers[receiverClass]?.forEach { actionReference ->
            actionReference.get()?.let { action ->
                @Suppress("UNCHECKED_CAST")
                (action as E).run()
            }
        }
        log?.invoke("Called ${receiverClass.simpleName} on ${receivers[receiverClass]?.map { it.get().className }}")
    }

    fun hookForever(receiver: Action, vararg receiverClasses: KClass<out Action>) =
        hookReference(receiver, StrongActionReference(receiver), *receiverClasses)

    fun hookForever(receiver: Action) = hookForever(receiver, *extractReceiverClasses(receiver))

    fun hook(receiver: Action, vararg receiverClasses: KClass<out Action>) =
        hookReference(receiver, WeakActionReference(receiver), *receiverClasses)

    fun hook(receiver: Action) = hook(receiver, *extractReceiverClasses(receiver))

    private fun extractReceiverClasses(receiver: Action): Array<KClass<out Action>> {
        val interfaces = receiver::class.superclasses.filter { it.isSubclassOf(Action::class) }
        @Suppress("UNCHECKED_CAST")
        return interfaces.toTypedArray() as Array<KClass<out Action>>
    }

    private fun hookReference(receiver: Action, ref: ActionReference<Action>, vararg interfaces: KClass<out Action>) {
        interfaces.forEach { receiverClass ->
            receivers.compute(receiverClass) { _, set ->
                (set ?: ConcurrentHashMap.newKeySet()).apply { add(ref) }
            }
        }
        log?.invoke("Hooked ${receiver.className} as ${interfaces.map { it.simpleName }}")
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

    fun cleanUp() = receivers.keys.forEach { key ->
        receivers.computeIfPresent(key) { _, set ->
            set.removeIf { it.get() == null }
            if (set.isEmpty()) null else set
        }
    }

}


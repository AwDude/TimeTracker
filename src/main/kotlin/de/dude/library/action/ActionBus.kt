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

    private val receivers = ConcurrentHashMap<KClass<out Action>, MutableSet<ActionReference<*>>>()

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

    fun hook(receiver: Action, vararg receiverClasses: KClass<out Action>) {
        receiverClasses.forEach { receiverClass ->
            receivers.compute(receiverClass) { _, set ->
                (set ?: ConcurrentHashMap.newKeySet()).apply {
                    add(ActionReference(receiver))
                }
            }
        }
        log?.invoke("Hooked ${receiver.className} as ${receiverClasses.map { it.simpleName }}")
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

    fun cleanUp() = receivers.keys.forEach { key ->
        receivers.computeIfPresent(key) { _, set ->
            set.removeIf { it.get() == null }
            if (set.isEmpty()) null else set
        }
    }

}


package de.dude.library.repository.database.memory

import de.dude.library.util.void
import java.util.concurrent.ConcurrentHashMap

class MemoryControl {
    private val addressLocks = ConcurrentHashMap<Long, UShort>()

    fun lockRead(address: Long, numBytes: Long = 1) = ReadLock().lock(address, numBytes)
    fun lockWrite(address: Long, numBytes: Long = 1) = WriteLock().lock(address, numBytes)

    abstract class MemoryLock internal constructor() : AutoCloseable {
        private val addresses = mutableListOf<Long>()

        internal fun lock(address: Long, numBytes: Long): MemoryLock? {
            val endAddress = address - 1 + numBytes
            if (address < 0 || endAddress < address) {
                return null
            }
            for (currentAddress in (address .. endAddress)) {
                if (lock(currentAddress)) {
                    addresses.add(currentAddress)
                } else {
                    unlock()
                    return null
                }
            }
            return this
        }

        @Suppress("MemberVisibilityCanBePrivate")
        fun unlock() {
            addresses.forEach(this::unlock)
            addresses.clear()
        }

        protected abstract fun lock(address: Long): Boolean
        protected abstract fun unlock(address: Long)
        override fun close() = unlock()
    }

    private inner class ReadLock : MemoryLock() {
        override fun lock(address: Long): Boolean {
            var success = true
            addressLocks.compute(address) { _, value ->
                when (value) {
                    null -> 1.toUShort()
                    UShort.MAX_VALUE -> UShort.MAX_VALUE.also { success = false }
                    else -> value.inc()
                }
            }
            return success
        }

        override fun unlock(address: Long) = addressLocks.compute(address) { _, value ->
            when (value) {
                null, 0.toUShort(), 1.toUShort() -> null
                else -> value.dec()
            }
        }.void
    }

    private inner class WriteLock : MemoryLock() {
        override fun lock(address: Long) = addressLocks.putIfAbsent(address, UShort.MAX_VALUE) == null
        override fun unlock(address: Long) = addressLocks.remove(address).void
    }

}
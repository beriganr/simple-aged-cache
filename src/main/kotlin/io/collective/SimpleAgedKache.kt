package io.collective

import java.time.Clock
import java.time.Instant

class SimpleAgedKache(private val clock: Clock = Clock.systemDefaultZone()) {

    private var head: ExpirableEntry? = null
    //constructor(clock: Clock?) {
    //}

    //constructor() {
    //}

    fun put(key: Any, value: Any, retentionInMillis: Int) {
        val expireTime = clock.instant().plusMillis(retentionInMillis.toLong())
        val newEntry = ExpirableEntry(key, value, expireTime)
        newEntry.next = head
        head = newEntry
    }

    fun isEmpty(): Boolean {
        clearExpired()
        println(head == null)
        return head == null
    }

    fun size(): Int {
        clearExpired()
        var count = 0
        var curr = head
        while (curr != null) {
            curr = curr.next
            count++
        }
        return count
    }

    fun get(key: Any): Any? {
        var curr = head
        while (curr != null) {
            if (curr.key == key) {
                return curr.value
            }
            else {
                curr = curr.next
            }
        }
        return null
    }

    private fun clearExpired() {
        var curr = head
        var prev: ExpirableEntry? = null
        while (curr != null) {
            if (curr.isExpired(clock.instant())) {
                if (prev == null) {
                    head = curr.next
                }
                else {
                    prev.next = curr.next
                }
            } else {
                prev = curr
            }
            curr = curr.next
        }
    }
    private class ExpirableEntry(val key: Any, val value: Any, private val expireTime: Instant) {
        var next: ExpirableEntry? = null

        fun isExpired(currTime: Instant): Boolean {
            return currTime.isAfter(expireTime)
        }
    }
}
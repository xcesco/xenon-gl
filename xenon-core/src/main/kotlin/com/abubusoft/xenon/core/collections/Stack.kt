/**
 *
 */
package com.abubusoft.xenon.core.collections

import java.io.Serializable
import java.util.*

/**
 * Rapresents a stack.
 * (908099)
 * @version 1.0.0
 * @param <E>
</E> */
class Stack<E> : Serializable, Iterable<E> {
    /**
     * array of elements
     */
    protected var elements: LinkedList<E>

    /**
     * reset the stack
     */
    fun clear() {
        elements.clear()
    }

    /**
     * return the size of the stack
     *
     * @return the number of the elements inserted into the stack
     */
    fun size(): Int {
        return elements.size
    }

    /**
     * Constructor
     */
    init {
        elements = LinkedList()
    }

    /**
     * Pushes an item onto the top of this stack.
     *
     * @param item
     * item to add
     * @return item added
     */
    fun push(item: E): E {
        elements.addFirst(item)
        return item
    }

    /**
     * Removes the object at the top of this stack and returns that object as
     * the value of this function.
     *
     * @return The object at the top of this stack (the last item of the Vector
     * object).
     * @throws EmptyStackException
     */
    @Throws(EmptyStackException::class)
    fun pop(): E {
        if (elements.size == 0) throw EmptyStackException()
        return elements.removeFirst()
    }

    /**
     * Looks at the object at the top of this stack without removing it from the
     * stack.
     *
     * @return the object at the top of this stack (the last item of the Vector
     * object).
     * @throws EmptyStackException
     */
    @Throws(EmptyStackException::class)
    fun peek(): E {
        if (elements.size == 0) throw EmptyStackException()
        return elements.first
    }

    /**
     * Tests if this stack is empty.
     *
     * @return `true` if and only if this stack contains no items;
     * `false` otherwise.
     */
    fun empty(): Boolean {
        return if (elements.size > 0) false else true
    }

    /**
     * Returns the 1-based position where an object is on this stack. If the
     * object o occurs as an item in this stack, this method returns the
     * distance from the top of the stack of the occurrence nearest the top of
     * the stack; the topmost item on the stack is considered to be at distance
     * 1. The equals method is used to compare o to the items in this stack.
     *
     * @param entity
     * @return the 1-based position from the top of the stack where the object
     * is located; the return value -1 indicates that the object is not
     * on the stack.
     */
    fun search(entity: E): Int {
        var level = elements.size
        for (a in elements) {
            if (a == entity) {
                return level
            }
            level--
        }
        return -1
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
    override fun iterator(): MutableIterator<E> {
        return elements.iterator()
    }

    companion object {
        /**
         * serial id
         */
        private const val serialVersionUID = 7364318993666004978L
    }
}
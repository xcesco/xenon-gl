/**
 *
 */
package com.abubusoft.xenon.core.collections

import java.io.Serializable
import java.util.*

/**
 * Rapresents a bounded stack. The difference between this
 * implementation and the standard data structure in the jdk is that
 * the bounded stack has a maximum number of elements that can be put
 * in. If you try to insert another elements in the stack, the older
 * elements will be discard.
 *
 * @author Francesco Benincasa
 * @version 1.0.0
 * @param <E>
</E> */
class BoundedStack<E> @JvmOverloads constructor(maxcapacity: Int = 10) : Serializable, Iterable<E> {
    /**
     * return the max elements the stack can contains.
     *
     * @return
     * max number of elements stack can contains.
     */
    /**
     * capacity of the stack.
     */
    var capacity: Int
        protected set

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
     * @return
     * the number of the elements inserted into the stack
     */
    fun size(): Int {
        return elements.size
    }
    /**
     * With this constructor, it is possibile to define the
     * maximum capacity of the stack.
     *
     * @param maxcapacity
     * max capacity of the stack.
     */
    /**
     * BinderDefault costructor. By default it creates an bounded stack
     * with a maximum capacity of 10 elements.
     */
    init {
        var maxcapacity = maxcapacity
        if (maxcapacity <= 0) maxcapacity = 10
        capacity = maxcapacity
        elements = LinkedList()
    }

    /**
     * Pushes an item onto the top of this stack.
     *
     * @param item
     * item to add
     * @return
     * item added
     */
    fun push(item: E): E {
        if (elements.size < capacity) {
            elements.addFirst(item)
        } else {
            elements.addFirst(item)
            elements.removeLast()
        }
        return item
    }

    /**
     * Removes the object at the top of this stack and returns that object as the value of this function.
     *
     * @return
     * The object at the top of this stack (the last item of the Vector object).
     * @throws EmptyStackException
     */
    @Throws(EmptyStackException::class)
    fun pop(): E {
        if (elements.size == 0) throw EmptyStackException()
        return elements.removeFirst()
    }

    /**
     * Looks at the object at the top of this stack without removing it from the stack.
     * @return the object at the top of this stack (the last item of the Vector object).
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
     * @return
     * `true` if and only if this stack contains no items; `false` otherwise.
     */
    fun empty(): Boolean {
        return if (elements.size > 0) false else true
    }

    /**
     * Returns the 1-based position where an object is on this stack.
     * If the object o occurs as an item in this stack, this method returns
     * the distance from the top of the stack of the occurrence nearest the top of the stack;
     * the topmost item on the stack is considered to be at distance 1. The equals method
     * is used to compare o to the items in this stack.
     *
     * @param entity
     * @return
     * the 1-based position from the top of the stack where the object is located; the return value -1  indicates that the object is not on the stack.
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

    /* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
    override fun iterator(): MutableIterator<E> {
        return elements.iterator()
    }

    companion object {
        /**
         *
         */
        private const val serialVersionUID = 7364318993666004978L
    }
}
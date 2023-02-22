/**
 *
 */
package com.abubusoft.xenon.core.collections

/**
 *
 *
 * Rappresenta una coda "infinita" di elementi. Gli elementi vengono inseriti normalmente in coda e vengono estratti in testa. (LIFO)
 *
 *
 *
 *
 * In fase di creazione dell'istanza viene definito una dimensione di elementi. Questo numero definisce il numero di elementi della lista che vengono riutilizzati. Questo non vuol
 * dire che c'è un limite massimo di elementi. La struttura può crescere indefinitivamente, fatto sta che quando la struttura decresce, verranno salvati un numero di elementi pari
 * a quello definito all'inizio.
 *
 *
 *
 * Questo risulta molto utile nella gestione dinamica di elementi.
 *
 *
 *
 * **Non è thread-safe**
 *
 *
 * @param <E>
 * tipo
</E> */
class SmartQueue<E> @JvmOverloads constructor(capacity: Int = DEFAULT_CAPACITY) {
    /**
     * cursore, serve a iterare sulla struttura.
     */
    protected var cursor: Item<E>? = null

    /**
     * cancelliamo tutti gli elementi.
     */
    fun clear() {
        // cancelliamo
        if (size == 0) return
        var t: Item<E>?
        while (size > 0) {
            if (size == 1) {
                t = head
                head = null
                last = null
                pool.freeObject(t)
            } else {
                t = head
                // prima di rilasciare spostiamo head
                head = head?.next
                pool.freeObject(t)
            }
            size--
        }
    }

    val isEmpty: Boolean
        get() = size == 0

    /**
     * ripristina il cursore all'inizio
     */
    fun cursorReset() {
        cursor = head
    }

    /**
     * si sposta sul prossimo. Se true indica che in effetti c'è ancora qualche elemento da analizzare. Se false indica che la lista è finita.
     *
     * Se si esegue dopo aver raggiunto la fine, da eccezione.
     *
     * @return
     */
    fun cursorNext(): Boolean {
        if (cursor == null) NoSuchElementException()
        cursor = cursor?.next
        return cursor != null
    }

    fun cursorHasNext(): Boolean {
        if (cursor == null) NoSuchElementException()
        return cursor != null
    }

    /**
     * Recupera il valore sotto il cursore
     *
     * @return
     */
    fun cursorValue(): E {
        if (cursor == null) NoSuchElementException()
        return cursor!!.value!!
    }

    /**
     * riferimento al primo elemento della lista
     */
    protected var head: Item<E>?

    /**
     * riferimento all'ultimo elemento della lista
     */
    protected var last: Item<E>?

    /**
     * numero di elementi della lista
     */
    protected var size: Int

    /**
     * @return the size
     */
    fun size(): Int {
        return size
    }

    /**
     * Aggiunge un elemento in coda alla queue
     *
     * @param item
     */
    fun add(item: E) {
        val e: Item<E> = pool.newObject()
        e.value = item
        if (last == null) {
            last = e
        } else {
            last!!.next = e
            // e.previous = last;
            last = e
        }

        // se non abbiamo niente allora lo mettiamo anche come head
        if (head == null) {
            head = e
        }
        size++
    }

    /**
     * Recupera ma non rimuove il primo elemento.
     */
    fun peek(): E? {
        return if (head == null) null else head?.value
    }

    /**
     * Recupera il primo elemento, rimuovendolo dalla coda.
     *
     * @return il primo elemento recuperato dalla coda
     * @throws NoSuchElementException
     * se la coda è vuota
     */
    fun pop(): E {
        if (size == 0) {
            throw NoSuchElementException()
        }
        val value = head?.value!!
        if (size == 1) {
            val t: Item<E>? = head
            head = null
            last = null
            pool.freeObject(t)
        } else if (size > 1) {
            val t: Item<E>? = head
            // prima di rilasciare spostiamo head
            head = head?.next
            pool.freeObject(t)
        }
        size--
        return value
    }

    protected var pool: ItemPool<E>

    init {
        pool = ItemPool(capacity)
        head = null
        last = null
        size = 0
    }

    companion object {
        const val DEFAULT_CAPACITY = 16
    }
}

/**
 * @author Francesco Benincasa
 */
class Item<E> : PooledObject {
    /**
     * valore salvato.
     */
    var value: E? = null

    /**
     * riferimento al prossimo elemento della lista.
     */
    var next: Item<E>? = null

    override fun initializePoolObject() {
        next = null
    }

    override fun finalizePoolObject() {
        next = null
    }
}

/**
 * Pool degli item che compongono la queue. Serve ad evitare di dover ogni volta creare e poi recuperare con il garbage collector.
 *
 * @author Francesco Benincasa
 */
class ItemPool<E>(maxSize: Int) : ObjectPool<Item<E>>(maxSize) {
    override fun createPooledObject(): Item<E> {
        return Item()
    }
}
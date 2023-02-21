/**
 *
 */
package com.abubusoft.xenon.core.util

/**
 * @author Francesco Benincasa
 */
object ElioCollection {
    /**
     * Dato un insieme parametrico di elementi, li converte in un set
     * @param items
     * @return
     */
    fun <E> createSet(vararg items: E): Set<E> {
        val ret: MutableSet<E> = HashSet()
        for (item in items) {
            ret.add(item)
        }
        return ret
    }

    /**
     * Ordinamento liste con l'algoritmo Insertion Sort
     * http://www.cs.washington.edu/education/courses/cse373/01wi/slides/Measurement/sld010.htm
     * http://en.wikipedia.org/wiki/Sorting_algorithm
     *
     * @param list
     * @param comp
     */
    fun <E> sort(list: MutableList<E>, comp: Comparator<E>) {
        val n = list.size
        var j: Int
        var item: E
        for (i in 1 until n) {
            item = list[i]
            j = i - 1
            while (j >= 0 && comp.compare(item, list[j]) < 0) {
                list[j + 1] = list[j]
                j--
            }
            list[j + 1] = item
        }
    }
}
package data_structures.collections.tree

import java.util.SortedSet
import java.util.TreeSet

//TODO: https://www.baeldung.com/kotlin/binary-tree

fun main() {
    //TODO: https://proglang.su/java/treeset-class

    // https://www.baeldung.com/java-tree-set:

    val treeSet: SortedSet<String> = TreeSet()

    treeSet.add("bbc")
    treeSet.add("b")
    treeSet.add("abc")
    treeSet.add("aa")

    treeSet.forEach { string ->
        print("$string ") // aa abc b bbc
    }

    val treeSetSortedByLength: SortedSet<String> = TreeSet(Comparator.comparing(String::length))

    treeSetSortedByLength.add("bbc")
    treeSetSortedByLength.add("b")
    treeSetSortedByLength.add("abc") // returns false
    treeSetSortedByLength.add("aa")

    treeSetSortedByLength.forEach { string ->
        print("$string ") // b aa bbc
    }
}
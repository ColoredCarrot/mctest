package info.voidev.mctest.engine.util

/**
 * Traverses a tree defined by [root] and [getChildren] via depth-first search.
 *
 * [block] will be called for every node in the tree exactly once.
 */
inline fun <N : Any> traverseTree(root: N, getChildren: (N) -> Sequence<N>, block: (N) -> Unit) {
    val stack = ArrayList<N>()
    stack += root

    while (stack.isNotEmpty()) {
        val top = stack.removeLast()
        block(top)
        getChildren(top).forEach(stack::add)
    }
}

/**
 * Generates a flat sequence of all nodes in a given tree.
 *
 * @see traverseTree
 */
inline fun <N : Any> traverseTree(root: N, crossinline getChildren: (N) -> Sequence<N>): Sequence<N> = sequence {
    traverseTree(root, getChildren) {
        yield(it)
    }
}

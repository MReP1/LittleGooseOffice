package little.goose.common.collections

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class CircularLinkList<T> {

    var size = 0
        private set

    private var headNode: Node<T>? = null
    private var tailNode: Node<T>? = null

    private val _currentNode: MutableStateFlow<Node<T>?> = MutableStateFlow(null)
    val currentNode = _currentNode.map { it?.element }

    fun add(element: T) {
        val node = Node(element)
        val headNode = headNode
        val tailNode = tailNode
        if (headNode == null || tailNode == null) {
            this.headNode = node
            this.tailNode = node
            _currentNode.value = node
        } else {
            tailNode._next = node
            node._next = headNode
            this.tailNode = node
        }
        size++
    }

    fun remove(element: T) {
        var node: Node<T>? = headNode
        if (node == element) {
            headNode?._next = null
            headNode = null
        } else {
            while (node != tailNode || node != null) {
                if (node?._next == element) {
                    node?._next = element?._next
                    if (element == tailNode) {
                        tailNode = node
                    }
                    element?._next = null
                    break
                }
                node = node?._next
            }
        }
        if (node?._next == node) {
            node?._next = null
        }
        size--
    }

    fun next(): T {
        val node = _currentNode.value?.next
        _currentNode.value = node
        return node!!.element
    }

    class Node<T>(
        val element: T,
        internal var _next: Node<T>? = null,
    ) {
        val next get() = _next ?: this
    }
}
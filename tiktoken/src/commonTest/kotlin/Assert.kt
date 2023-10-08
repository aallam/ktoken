import kotlin.test.assertTrue

fun <T> assertNotContains(list: Collection<T>, vararg elements: T) {
    val elementsList = elements.toList()
    assertTrue("the collection $list is not expected to contain $elementsList") { !list.containsAll(elementsList) }
}

fun <T> assertContains(list: Collection<T>, vararg elements: T) {
    val elementsList = elements.toList()
    assertTrue("the collection $list is expected to contain $elementsList") { list.containsAll(elementsList) }
}

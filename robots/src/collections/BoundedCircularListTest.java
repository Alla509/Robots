package collections;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BoundedCircularListTest {

    @Test
    void testAddAndGet() {
        BoundedCircularList<String> list = new BoundedCircularList<>(3);
        list.add("A");
        list.add("B");
        list.add("C");
        assertEquals(3, list.size());
        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
        assertEquals("C", list.get(2));
    }

    @Test
    void testOverflow() {
        BoundedCircularList<String> list = new BoundedCircularList<>(3);
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D"); // перезаписывает A
        assertEquals(3, list.size());
        assertEquals("B", list.get(0));
        assertEquals("C", list.get(1));
        assertEquals("D", list.get(2));
    }

    @Test
    void testSubList() {
        BoundedCircularList<String> list = new BoundedCircularList<>(5);
        for (int i = 0; i < 5; i++) list.add("E" + i);
        List<String> sub = list.subList(1, 4);
        assertEquals(List.of("E1", "E2", "E3"), sub);
    }

    @Test
    void testSnapshotIterator() {
        BoundedCircularList<String> list = new BoundedCircularList<>(3);
        list.add("X");
        list.add("Y");
        var it = list.iterator();
        list.add("Z"); // добавление после создания итератора не мешает
        assertTrue(it.hasNext());
        assertEquals("X", it.next());
        assertEquals("Y", it.next());
        // итератор имеет снимок, поэтому Z не виден
        assertFalse(it.hasNext());
    }

    @Test
    void testThreadSafety() throws InterruptedException {
        BoundedCircularList<Integer> list = new BoundedCircularList<>(100);
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) list.add(i);
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) list.size();
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        assertEquals(100, list.size());
    }
}

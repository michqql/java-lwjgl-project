package me.michqql.game.util.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

public class ReadOnlyList<E> implements List<E> {

    private final List<E> backing;

    public ReadOnlyList(List<E> backing) {
        this.backing = backing;
    }

    @Override
    public int size() {
        return backing.size();
    }

    @Override
    public boolean isEmpty() {
        return backing.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return backing.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return listIterator();
    }

    @Override
    public Object[] toArray() {
        return backing.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return backing.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return backing.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {}

    @Override
    public E get(int index) {
        return backing.get(index);
    }

    @Override
    public E set(int index, E element) {
        return null;
    }

    @Override
    public void add(int index, E element) {}

    @Override
    public E remove(int index) {
        return null;
    }

    @Override
    public int indexOf(Object o) {
        return backing.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return backing.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return new BackedListIterator<>(backing.listIterator());
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new BackedListIterator<>(backing.listIterator(index));
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return backing.subList(fromIndex, toIndex);
    }

    public static class BackedListIterator<E> implements ListIterator<E> {

        private final ListIterator<E> backingIterator;

        private BackedListIterator(ListIterator<E> backingIterator) {
            this.backingIterator = backingIterator;
        }

        @Override
        public boolean hasNext() {
            return backingIterator.hasNext();
        }

        @Override
        public E next() {
            return backingIterator.next();
        }

        @Override
        public boolean hasPrevious() {
            return backingIterator.hasPrevious();
        }

        @Override
        public E previous() {
            return backingIterator.previous();
        }

        @Override
        public int nextIndex() {
            return backingIterator.nextIndex();
        }

        @Override
        public int previousIndex() {
            return backingIterator.previousIndex();
        }

        @Override
        public void remove() {}

        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            backingIterator.forEachRemaining(action);
        }

        @Override
        public void set(E e) {

        }

        @Override
        public void add(E e) {

        }
    }
}

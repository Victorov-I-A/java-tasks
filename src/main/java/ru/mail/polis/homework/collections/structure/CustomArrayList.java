package ru.mail.polis.homework.collections.structure;

import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Необходимо реализовать свой ArrayList (динамический массив).
 * При изменении размера массива помните про метод System.arraycopy()
 *
 * Задание оценивается в 10 тугриков
 */
public class CustomArrayList<E> implements List<E> {
    private static final int DEFAULT_SIZE = 16;
    private Object[] array;
    private int modCount;
    private int size;

    public CustomArrayList() {
        array = new Object[DEFAULT_SIZE];
    }

    public CustomArrayList(Collection<E> c) {
        array = new Object[c.size()];
        Iterator<E> iterator = c.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            array[index++] = iterator.next();
        }
        size = array.length;
    }

    public CustomArrayList(Object[] array) {
        this.array = array.clone();
        size = array.length;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            int modCount = CustomArrayList.this.modCount;
            int position;

            @Override
            public boolean hasNext() {
                return position < size;
            }

            @Override
            public E next() {
                if (modCount != CustomArrayList.this.modCount) {
                    throw new ConcurrentModificationException();
                }
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return (E) array[position++];
            }
        };
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(array, size);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            return (T[]) Arrays.copyOf(array, size, a.getClass());
        }
        System.arraycopy(array, 0, a, 0, size);
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    @Override
    public boolean add(E e) {
        modCount++;
        grow();
        array[size++] = e;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(o, array[i])) {
                modCount++;
                System.arraycopy(array, i + 1, array, i, size - i - 1);
                size--;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object element : c) {
            if (!contains(element)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return addAll(size, c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        modCount++;
        grow(c.size());
        System.arraycopy(array, index, array, index + c.size(), size - index);
        System.arraycopy(c.toArray(), 0, array, index, c.size());
        size += c.size();
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        int oldCount = modCount;
        for (Object element : c) {
            remove(element);
        }
        return modCount != oldCount;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Object[] iterArray = array.clone();
        int oldCount = modCount;
        for (Object element : iterArray) {
            if (!c.contains(element)) {
                remove(element);
            }
        }
        return modCount != oldCount;
    }

    @Override
    public void clear() {
        modCount++;
        array = new Object[DEFAULT_SIZE];
        size = 0;
    }

    @Override
    public E get(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        return (E) array[index];
    }

    @Override
    public E set(int index, E element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        modCount++;
        E oldElement = (E) array[index];
        array[index] = element;
        return oldElement;
    }

    @Override
    public void add(int index, E element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        modCount++;
        grow();
        System.arraycopy(array, index, array, index + 1, size - index);
        array[index] = element;
        size++;
    }

    @Override
    public E remove(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        E oldElement = (E) array[index];
        modCount++;
        System.arraycopy(array, index + 1, array, index, size - index - 1);
        size--;
        return oldElement;
    }

    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(o, array[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        for (int i = size - 1; i >= 0; i--) {
            if (Objects.equals(o, array[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public ListIterator<E> listIterator() {
        return new listIter(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new listIter(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException();
        }
        Object[] newArray = new Object[toIndex - fromIndex + 1];
        System.arraycopy(array, fromIndex, newArray, 0, toIndex - fromIndex + 1);
        return new CustomArrayList<>(newArray);
    }

    private void grow() {
        grow(1);
    }

    private void grow(int capacity) {
        if (array.length >= size + capacity) {
            return;
        }
        Object[] newArray = new Object[(array.length << 1) + capacity];
        System.out.println(newArray.length);
        System.arraycopy(array, 0, newArray, 0, size);
        array = newArray;
    }

    private class listIter implements ListIterator<E> {
        int modCount = CustomArrayList.this.modCount;
        boolean isIter;
        int position;

        listIter(int position) {
            this.position = position;
        }

        @Override
        public boolean hasNext() {
            return position < size - 1;
        }

        @Override
        public E next() {
            if (modCount != CustomArrayList.this.modCount) {
                throw new ConcurrentModificationException();
            }
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            isIter = true;
            return (E) array[++position];
        }

        @Override
        public boolean hasPrevious() {
            return position >= 0;
        }

        @Override
        public E previous() {
            if (modCount != CustomArrayList.this.modCount) {
                throw new ConcurrentModificationException();
            }
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            isIter = true;
            return (E) array[position--];
        }

        @Override
        public int nextIndex() {
            if (!hasNext()) {
                return size;
            }
            return position + 1;
        }

        @Override
        public int previousIndex() {
            if (!hasPrevious()) {
                return -1;
            }
            return position - 1;
        }

        @Override
        public void remove() {
            if (!isIter) {
                throw new IllegalStateException();
            }
            CustomArrayList.this.remove(position);
            modCount++;
            isIter = false;
        }

        @Override
        public void set(E e) {
            if (!isIter) {
                throw new IllegalStateException();
            }
            CustomArrayList.this.set(position, e);
            modCount++;
        }

        @Override
        public void add(E e) {
            CustomArrayList.this.add(position, e);
            modCount++;
            isIter = false;
        }
    }
}

> Java相关姿势学习记录，主要包括 *Java基础*，*Java并发*，*Java虚拟机* 三个部分。
> 资源均来自网络博文，感谢开源！

### 目录
##### 1.  [Java基础](#1) [^1] 
##### 1.1. [Java集合框架](#11) 
##### 1.2. [ArrayList](#12) 
##### 1.3. [LinkedList](#13) 
##### 1.4. [HashMap](#14) 
##### 1.5. [TreeMap](#15) 
##### 1.6. [LinkedHashMap](#16) 
##### 2. [Java并发](#2) [^2] 
##### 3. [Java虚拟机](#3) [^3] 

<h3 id="1">1. Java基础</h3> 
<h4 id="11">1.1. Java集合框架    </h4>

Java集合四大体系：`Set` 、`Queue`、`List`、`Map`；   

前三个的父接口为`Collection`，`Collection `继承自`Iterator`，并实现了方法 `hasNext()` 和 `next()`；   

- `Set`：无序、不可重复；行为与`Collection`相同，无新增方法。
- `List`：有序、可重复；可根据索引位置查找元素，可通过`Comparator`对元素排序。
- `Map`：存储具有映射关系的键值对；内置两个数组，分别存储Key和Value，索引不再是位置数值，而是Key中对象；单独看Key和Value，它们的结构与集合`Set`、`List`类似——Key，无序不可重复；Value，可重复。
- `Queue`：以队列方式存储（First-In-First-Out），不允许随机访问，***Java 5 新增***。

Java集合像一个容器，存储对象（实际是对象的引用，习惯上称为对象）。***Java 5 新增了泛型***，使得数据结构更简洁、更健壮。

常用的集合实现类有：`HashSet`、`TreeSet`，`ArrayList`、`LinkedList`，`HashMap`、`TreeMap`等。   

<h4 id="12">1.2. ArrayList</h4>

- 以数组实现，总容量有限制（`Integer.MAX_VALUE - 8`， 2147483639）；默认创建大小为10的数值；当容量超限时，会增加50%的容量。

    - 自动扩容的核心方法`ensureCapacityInternal()`，*若扩容50%还是不够的话，会直接扩容到对应需要的大小*。

    ```java
    private void ensureCapacityInternal(int minCapacity) {
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        }
        ensureExplicitCapacity(minCapacity);
    }
    private void ensureExplicitCapacity(int minCapacity) {
        modCount++;
        // overflow-conscious code
        if (minCapacity - elementData.length > 0)
            grow(minCapacity);
    }
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;
        // 扩展为原来的1.5倍
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        // 如果扩为1.5倍还不满足需求，直接扩为需求值
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        elementData = Arrays.copyOf(elementData, newCapacity);
    }
    ```

- 按数组下标访问元素，性能很高。

- 直接在末尾添加数据性能高，但是按照下标在其他位置`add(i, e)`，`remove(i)`，`remove(e)`，需要`System.arraycopy()`来移动受影响的原生，性能变差了。


<h4 id="13">1.3. LinkedList</h4>


<h4 id="14">1.4. HashMap</h4>


<h4 id="15">1.5. TreeMap</h4>


<h4 id="16">1.6. LinkedHashMapt</h4>

<h3 id="2">2. Java并发</h3>

<h3 id="3">3. Java虚拟机</h3>

---------
[^1]:https://lrh1993.gitbooks.io/android_interview_guide/content/java/basis.html "Java基础学习资源"
[^2]:https://lrh1993.gitbooks.io/android_interview_guide/content/java/concurrence.html "Java并发学习资源"
[^3]:https://lrh1993.gitbooks.io/android_interview_guide/content/java/virtual-machine.html "JVM学习资源"
> Java相关姿势学习记录，主要包括 *Java基础*，*Java并发*，*Java虚拟机* 三个部分。
------------------------------------------------

## 目录
### 1.[Java基础](#1) [^1] 
#### 1.1.  [Java集合框架](#11) 
##### 1.1.1. [ArrayList](#111) 
##### 1.1.2. [LinkedList](#112) 
##### 1.1.3. [HashMap](#113) 
##### 1.1.4. [TreeMap](#114) 
##### 1.1.5. [LinkedHashMap](#115) 
### 2.[Java并发](#2) [^2] 
#### 2.0. [Java线程池实现原理及其在美团业务中的实践](#20)
### 3.[Java虚拟机](#3) [^3] 
------------------

## 1. Java基础
<h4 id="1">1. Java基础</h4> 

### 1.1 Java集合框架

<h5 id="11">1.1. Java集合框架</h5>

Java集合四大体系：`Set` 、`Queue`、`List`、`Map`；   

前三个的父接口为`Collection`，`Collection `继承自`Iterator`，并实现了方法 `hasNext()` 和 `next()`；   

- `Set`：无序、不可重复；行为与`Collection`相同，无新增方法。
- `List`：有序、可重复；可根据索引位置查找元素，可通过`Comparator`对元素排序。
- `Map`：存储具有映射关系的键值对；内置两个数组，分别存储Key和Value，索引不再是位置数值，而是Key中对象；单独看Key和Value，它们的结构与集合`Set`、`List`类似——Key，无序不可重复；Value，可重复。
- `Queue`：以队列方式存储（First-In-First-Out），不允许随机访问，***Java 5 新增***。

Java集合像一个容器，存储对象（实际是对象的引用，习惯上称为对象）。***Java 5 新增了泛型***，使得数据结构更简洁、更健壮。

常用的集合实现类有：`HashSet`、`TreeSet`，`ArrayList`、`LinkedList`，`HashMap`、`TreeMap`等。   

#### 1.1.1. ArrayList
<h6 id="111">1.1.1. ArrayList</h6>

- 以数组实现，总容量有限制（`Integer.MAX_VALUE - 8`， 2147483639）；默认创建大小为10的数组；当容量超限时，会增加50%的容量。

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

- 直接在末尾添加数据性能高，但是按照下标在其他位置`add(i, e)`，`remove(i)`，`remove(e)`，需要`System.arraycopy()`来移动受影响的元素，性能变差了。

#### 1.1.2. LinkedList
<h6 id="112">1.1.2. LinkedList</h6>

- 以双向链表实现，无容量限制，但是双向链表使用了更多的空间，同时数据操作也需要移动链表指针。
- 按下标访问元素时，需要遍历链表来将指针移动到指定的位置；通过下标判断所在的节点，若在前半区就从头遍历，若在后半区就从尾部遍历，这样性能就从 O(n) 变为 O(n/2)。
- 插入、删除元素时，只需修改元素前后的指针即可，但这之前遍历部分链表移动指针的操作还是少不了；只有在链表的两头或用`Iterator`的`remove()`方法，才能省掉指针的移动。

#### 1.1.3. HashMap
<h6 id="113">1.1.3. HashMap</h6>

> 官方描述：Hash table based **implementation of the Map interface**. This implementation provides all of the optional map operations, and permits null values and the null key. (The HashMap class is roughly equivalent to Hashtable, except that it is **unsynchronized** and **permits nulls**.) This class makes no guarantees as to the order of the map; in particular, it does not guarantee that the order will remain constant over time.

关键的信息：**基于Map接口实现**、**允许null键/值**、**非同步**、**不保证有序(比如插入的顺序)**、**也不保证顺序不随时间变化**。  



> 两个重要参数：容量（capacity）和负载因子（factor）
>
> - **Initial capacity** The capacity is **the number of buckets** in the hash table, The initial capacity is simply the capacity at the time the hash table is created.
> - **Load factor** The load factor is **a measure of how full the hash table is allowed to get** before its capacity is automatically increased.

__当 bucket 中 entries 的数量大于 capacity * factor 时，bucket 会扩容为原来的 2 倍。（factor 默认为 0.75）__



*四个关键实现：*

- **put 实现**

    1. 对 key 的 hashCode 做 hash 计算（高16位不变，低16位与高16位做异或，`hash = h^(h>>>16)`），再计算 index （`index = (n - 1) & hash`）；
    2. 如果没有发生碰撞，就存到 bucket；
    3. 如果发生碰撞，以链表的形式存到 bucket；
    4. 如果碰撞导致链表长度过长（超过 `TREEIFY_THRESHOLD`， 默认 8），就把链表转为红黑树；
    5. 如果对应节点已经存在，就替换 oldValue；
    6. 如果 bucket 已满（超过 `load factor * capacity`），resize。

    ```java
    public V put(K key, V value) {
        // 对key的hashCode()做hash
        return putVal(hash(key), key, value, false, true);
    }
    final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        Node<K,V>[] tab; Node<K,V> p; int n, i;
        // tab为空则创建
        if ((tab = table) == null || (n = tab.length) == 0)
            n = (tab = resize()).length;
        // 计算index，并对null做处理
        if ((p = tab[i = (n - 1) & hash]) == null)
            tab[i] = newNode(hash, key, value, null);
        else {
            Node<K,V> e; K k;
            // 节点存在
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
                e = p;
            // 该链为树
            else if (p instanceof TreeNode)
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
            // 该链为链表
            else {
                for (int binCount = 0; ; ++binCount) {
                    if ((e = p.next) == null) {
                        p.next = newNode(hash, key, value, null);
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            treeifyBin(tab, hash);
                        break;
                    }
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    p = e;
                }
            }
            // 写入
            if (e != null) { // existing mapping for key
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                afterNodeAccess(e);
                return oldValue;
            }
        }
        ++modCount;
        // 超过load factor*current capacity，resize
        if (++size > threshold)
            resize();
        afterNodeInsertion(evict);
        return null;
    }
    ```

    

- **get 实现**

    1. 如果直接命中第一个节点，直接返回结果；

    2. 未命中第一个节点，通过 `key.equals(k)` 去查找相应的 entry：

        若为树，则在树中查找，O(logn)； **Java 8 新增**

        若为链表，则在链表中查找，O(n)。

    ```java
    public V get(Object key) {
        Node<K,V> e;
        return (e = getNode(hash(key), key)) == null ? null : e.value;
    }
    final Node<K,V> getNode(int hash, Object key) {
        Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
        if ((tab = table) != null && (n = tab.length) > 0 &&
            (first = tab[(n - 1) & hash]) != null) {
            // 直接命中
            if (first.hash == hash && // always check first node
                ((k = first.key) == key || (key != null && key.equals(k))))
                return first;
            // 未命中
            if ((e = first.next) != null) {
                // 在树中get
                if (first instanceof TreeNode)
                    return ((TreeNode<K,V>)first).getTreeNode(hash, key);
                // 在链表中get
                do {
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        return e;
                } while ((e = e.next) != null);
            }
        }
        return null;
    }
    ```

    

- **hash 实现**

    对 key 的 hashCode 计算 hash 的具体方法如下：

    *高16位不变，低16位和高16位做了异或。*

    ```java
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
    ```

    其中代码注释如下：

    > Computes key.hashCode() and spreads (XORs) higher bits of hash to lower. Because the table uses power-of-two masking, sets of hashes that vary only in bits above the current mask will always collide. (Among known examples are sets of Float keys holding consecutive whole numbers in small tables.) So we apply a transform that spreads the impact of higher bits downward. There is a tradeoff between **speed, utility, and quality** of bit-spreading. Because many common sets of hashes are already **reasonably distributed** (so don’t benefit from spreading), and because **we use trees to handle large sets of collisions in bins**, we just XOR some shifted bits in the cheapest possible way to reduce systematic lossage, as well as to incorporate impact of the highest bits that would otherwise never be used in index calculations because of table bounds.

    而计算下标的实现如下：

    ```java
    (n -1) & hash
    ```

    设计者认为这方法很容易发生碰撞，为什么这么说？因为目前 table 的长度 n 为2的幂，诸如在 n -1 为15  (0x1111)时，散列值真正有效的只是低4位，这样就很容易发生碰撞了。

    因此，设计者想了一个综合性能的方法，就是把高16位和低16位做了异或处理。仅仅异或处理，既减少了系统的开销，也不会因为高位没有参与下标计算（table长度较小）而引起碰撞。

    如果还是产生了频繁的碰撞，就使用树来处理。

    > Improve the performance of java.util.HashMap under high hash-collision conditions by **using balanced trees rather than linked lists to store map entries**. Implement the same improvement in the LinkedHashMap class.

    

- **resize 实现**

    当put元素时，如果目前bucket使用程度超过 load factor 的比例，就会resize。简单来说就是把bucket扩充为原来的2倍，之后重新计算下标，把节点node放到新的bucket中。

    > Initializes or doubles table size. If null, allocates in accord with initial capacity target held in field threshold. Otherwise, because we are using power-of-two expansion, the elements from each bin must either **stay at same index**, or **move with a power of two offset **in the new table.

    大意是说，当比例超过限制就会resize。又因为使用的是2次幂扩展，所以元素的位置要么在原位置移动2次幂，要么还是在原来的位置。

    因此，在扩充HashMap时，只需要看原来的hash值新增的那位是1还是0，是0的话，索引不变；是1的话，新索引为 原索引 + oldCap （oldCap 为2次幂）

    ```java
    final Node<K,V>[] resize() {
        Node<K,V>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap, newThr = 0;
        if (oldCap > 0) {
            // 超过最大值就不再扩充了，就只好随你碰撞去吧
            if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }
            // 没超过最大值，就扩充为原来的2倍
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                     oldCap >= DEFAULT_INITIAL_CAPACITY)
                newThr = oldThr << 1; // double threshold
        }
        else if (oldThr > 0) // initial capacity was placed in threshold
            newCap = oldThr;
        else {               // zero initial threshold signifies using defaults
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        // 计算新的resize上限
        if (newThr == 0) {
            float ft = (float)newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                      (int)ft : Integer.MAX_VALUE);
        }
        threshold = newThr;
        @SuppressWarnings({"rawtypes","unchecked"})
            Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
        table = newTab;
        if (oldTab != null) {
            // 把每个bucket都移动到新的buckets中
            for (int j = 0; j < oldCap; ++j) {
                Node<K,V> e;
                if ((e = oldTab[j]) != null) {
                    oldTab[j] = null;
                    if (e.next == null)
                        newTab[e.hash & (newCap - 1)] = e;
                    else if (e instanceof TreeNode)
                        ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                    else { // preserve order
                        Node<K,V> loHead = null, loTail = null;
                        Node<K,V> hiHead = null, hiTail = null;
                        Node<K,V> next;
                        do {
                            next = e.next;
                            // 原索引
                            if ((e.hash & oldCap) == 0) {
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            }
                            // 原索引+oldCap
                            else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        // 原索引放到bucket里
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        // 原索引+oldCap放到bucket里
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }
    ```

    **总结**

    **1. 什么时候会使用HashMap？他有什么特点？**
    是基于Map接口的实现，存储键值对时，它可以接收null的键值，是非同步的，HashMap存储着Entry(hash, key, value, next)对象。

    **2. 你知道HashMap的工作原理吗？**
    通过hash的方法，通过put和get存储和获取对象。存储对象时，我们将K/V传给put方法时，它调用hashCode计算hash从而得到bucket位置，进一步存储，HashMap会根据当前bucket的占用情况自动调整容量(超过`Load Facotr`则resize为原来的2倍)。获取对象时，我们将K传给get，它调用hashCode计算hash从而得到bucket位置，并进一步调用equals()方法确定键值对。如果发生碰撞的时候，Hashmap通过链表将产生碰撞冲突的元素组织起来，在Java 8中，如果一个bucket中碰撞冲突的元素超过某个限制(默认是8)，则使用红黑树来替换链表，从而提高速度。

    **3. 你知道get和put的原理吗？equals()和hashCode()的都有什么作用？**
    通过对key的hashCode()进行hashing，并计算下标( `(n-1) & hash`)，从而获得buckets的位置。如果产生碰撞，则利用key.equals()方法去链表或树中去查找对应的节点

    **4. 你知道hash的实现吗？为什么要这样实现？**
    在Java 1.8的实现中，是通过hashCode()的高16位异或低16位实现的：`(h = k.hashCode()) ^ (h >>> 16)`，主要是从速度、功效、质量来考虑的，这么做可以在bucket的n比较小的时候，也能保证考虑到高低bit都参与到hash的计算中，同时不会有太大的开销。

    **5. 如果HashMap的大小超过了负载因子(`load factor`)定义的容量，怎么办？**
    如果超过了负载因子(默认**0.75**)，则会重新resize一个原来长度两倍的HashMap，并且重新调用hash方法。

#### 1.1.4. TreeMap
<h6 id="114">1.1.4. TreeMap</h6>

#### 1.1.5. LinkedHashMap
<h6 id="115">1.1.5. LinkedHashMap</h6>

---------------

## 2. Java并发
<h5 id="2">2. Java并发</h5>

### 2.0. Java线程池实现原理及其在美团业务中的实践[^20]
<h6 id="20">2.0. Java线程池实现原理及其在美团业务中的实践</h6>

> 简述线程池概念和用途，接着结合线程池的源码，帮助读者领略线程池的设计思路，最后回归实践，通过案例讲述使用线程池遇到的问题，并给出了一种动态化线程池解决方案。

#### 2.0.1. 线程池是什么

<h6 id="201">2.0.1. 线程池是什么</h6>







-------------------------------

<h3 id="3">3. Java虚拟机</h3>

---------
[^1]:https://lrh1993.gitbooks.io/android_interview_guide/content/java/basis.html "Java基础学习资源"
[^2]:https://lrh1993.gitbooks.io/android_interview_guide/content/java/concurrence.html "Java并发学习资源"
[^20]:https://mp.weixin.qq.com/s/baYuX8aCwQ9PP6k7TDl2Ww "线程池原理及其在美团业务中的实践"
[^3]:https://lrh1993.gitbooks.io/android_interview_guide/content/java/virtual-machine.html "JVM学习资源"
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

> 简述线程池概念和用途，接着结合线程池的源码，帮助领略线程池的设计思路，最后回归实践，通过案例讲述使用线程池遇到的问题，并给出了一种动态化线程池解决方案。

#### 2.0.1. 线程池是什么

<h6 id="201">2.0.1. 线程池是什么</h6>

线程池（Thread Pool）是一种基于*池化思想管理线程的工具*，经常出现在多线程服务器中，如MySQL。

线程过多会带来额外的开销，其中包括 *创建销毁* 线程的开销、*调度* 线程的开销等等，同时也降低了计算机的整体性能。线程池维护多个线程，等待监督管理者分配可并发执行的任务。这种做法，*一方面避免了处理任务时创建销毁线程开销的代价*，*另一方面避免了线程数量膨胀导致的过分调度问题，保证了对内核的充分利用*。

使用线程池可以带来一系列好处：

- **降低资源消耗**：通过池化技术重复利用已创建的线程，降低线程创建和销毁造成的损耗。
- **提高响应速度**：任务到达时，无需等待线程创建即可立即执行。
- **提高线程的可管理性**：线程是稀缺资源，如果无限制创建，不仅会消耗系统资源，还会因为线程的不合理分布导致资源调度失衡，降低系统的稳定性。使用线程池可以进行统一的分配、调优和监控。
- **提供更多更强大的功能**：线程池具备可拓展性，允许开发人员向其中增加更多的功能。比如延时定时线程池ScheduledThreadPoolExecutor，就允许任务延期执行或定期执行。

#### 2.0.2. 线程池解决什么问题

<h6 id="202">2.0.2. 线程池解决什么问题</h6>

线程池解决的核心问题就是资源管理问题。在并发环境下，系统不能够确定在任意时刻中，有多少任务需要执行，有多少资源需要投入。这种不确定性将带来以下若干问题：

1. 频繁申请/销毁资源和调度资源，将带来额外的消耗，可能会非常巨大。
2. 对资源无限申请缺少抑制手段，易引发系统资源耗尽的风险。
3. 系统无法合理管理内部的资源分布，会降低系统的稳定性。

为解决资源分配这个问题，线程池采用了“池化”（Pooling）思想。池化，顾名思义，是为了最大化收益并最小化风险，而将资源统一在一起管理的一种思想。

> Pooling is the grouping together of resources (assets, equipment, personnel, effort, etc.) for the purposes of maximizing advantage or minimizing risk to the users. The term is used in finance, computing and equipment management.——wikipedia

“池化”思想不仅仅能应用在计算机领域，在金融、设备、人员管理、工作管理等领域也有相关的应用。

在计算机领域中的表现为：统一管理IT资源，包括服务器、存储、和网络资源等等。通过共享资源，使用户在低投入中获益。除去线程池，还有其他比较典型的几种使用策略包括：

1. 内存池(Memory Pooling)：预先申请内存，提升申请内存速度，减少内存碎片。
2. 连接池(Connection Pooling)：预先申请数据库连接，提升申请连接的速度，降低系统的开销。
3. 实例池(Object Pooling)：循环使用对象，减少资源在初始化和释放时的昂贵损耗。

#### 2.0.3. 线程池核心设计与实现

<h6 id="203">2.0.3. 线程池核心设计与实现</h6>

> 线程池是一种通过“池化”思想，帮助我们管理线程而获取并发性的工具，在Java中的体现是ThreadPoolExecutor类。

##### 2.0.3.1 总体设计

<h6 id='2031'>2.0.3.1. 总体设计</h6>

Java 中的线程池核心实现类是 ThreadPoolExecutor，本章基于 JDK 1.8 的源码来分析Java线程池的核心设计与实现。我们首先来看一下ThreadPoolExecutor 的UML类图，了解下 ThreadPoolExecutor 的继承关系。

`Executor` ← `ExecutorService` ← `AbstractExecutorService` ← `ThreadPoolExecutor`

ThreadPoolExecutor 实现的顶层接口是 `Executor`，顶层接口Executor提供了一种思想：**将任务提交和任务执行进行解耦**。用户无需关注如何创建线程，如何调度线程来执行任务，用户只需提供Runnable对象，将任务的运行逻辑提交到执行器(Executor)中，由Executor框架完成线程的调配和任务的执行部分。

`ExecutorService` 接口增加了一些能力：

（1）扩充执行任务的能力，补充可以为一个或一批异步任务生成 Future 的方法；

（2）提供了管控线程池的方法，比如停止线程池的运行。

`AbstractExecutorService` 则是上层的抽象类，将执行任务的流程串联了起来，保证下层的实现只需关注一个执行任务的方法即可。

实现类 `ThreadPoolExecutor` 实现最复杂的运行部分，ThreadPoolExecutor将会一方面维护自身的生命周期，另一方面同时管理线程和任务，使两者良好的结合从而执行并行任务。

其运行机制如下图所示：

![ThreadPoolExecutor 运行流程](https://mmbiz.qpic.cn/mmbiz_png/hEx03cFgUsXAj6OrUTUDRoG5tCBgm4CJNdb1CmKm2eCv995UKQmO4ia1IiaQ1icnN8OhLWTWQadibyFzyjteNicMicRg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1 'ThreadPoolExecutor 运行流程')

线程池在内部实际上*构建了一个生产者消费者模型*，将线程和任务两者解耦，并不直接关联，从而良好的缓冲任务，复用线程。

线程池的运行主要分成两部分：

- **任务管理**

    任务管理部分充当生产者的角色，当任务提交后，线程池会判断该任务后续的流转：

    （1）直接申请线程执行该任务；

    （2）缓冲到队列中等待线程执行；

    （3）拒绝该任务。

- **线程管理**。

    线程管理部分是消费者，它们被统一维护在线程池内，根据任务请求进行线程的分配，当线程执行完任务后则会继续获取新的任务去执行，最终当线程获取不到任务的时候，线程就会被回收。

##### 2.0.3.2 生命周期管理

<h6 id='2032'>2.0.3.2. 生命周期管理</h6>

线程池运行的状态，并不是用户显式设置的，而是伴随着线程池的运行，由内部来维护。

线程池内部使用**一个变量维护两个值**：运行状态(runState) 和 线程数量 (workerCount)。

```java
private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
```

`ctl`这个AtomicInteger 类型，是对线程池的运行状态和线程池中有效线程的数量进行控制的一个字段， 它同时包含两部分的信息：线程池的运行状态 (runState) 和线程池内有效线程的数量 (workerCount)，高3位保存runState，低29位保存workerCount，两个变量之间互不干扰。

用一个变量去存储两个值，可避免在做相关决策时，出现不一致的情况，不必为了维护两者的一致，而占用锁资源。

通过阅读线程池源代码也可以发现，经常出现要同时判断线程池运行状态和线程数量的情况。线程池也提供了若干方法去供用户获得线程池当前的运行状态、线程个数。这里都使用的是位运算的方式，相比于基本运算，速度也会快很多。

关于内部封装的获取生命周期状态、获取线程池线程数量的计算方法如以下代码所示：

```java
private static int runStateOf(int c)     { return c & ~CAPACITY; } //计算当前运行状态
private static int workerCountOf(int c)  { return c & CAPACITY; }  //计算当前线程数量
private static int ctlOf(int rs, int wc) { return rs | wc; }   //通过状态和线程数生成ctl
```

ThreadPoolExecutor的运行状态有5种，分别为：

| 运行状态   | 状态描述                                                     |
| :--------- | :----------------------------------------------------------- |
| RUNNING    | 能接受新提交的任务，也能处理阻塞队列中的任务                 |
| SHUTDOWN   | 关闭状态，不再接受新的任务，但可以继续处理阻塞队列中已保存的任务 |
| STOP       | 不能接受新的任务，也不能处理队列中的任务，会中断正在处理任务的线程 |
| TIDYING    | 所有的任务都终止了，workCount（有效线程数）为 0              |
| TERMINATED | 执行完 terminated() 方法后进入该状态                         |

其生命周期转换如下所示：

![线程池生命周期](https://mmbiz.qpic.cn/mmbiz_png/hEx03cFgUsXAj6OrUTUDRoG5tCBgm4CJt7V0PMUDbbtUlUabsSfZDRB180VFyeEEapHia4SzSoTicBficy65fRibGw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1 '线程池生命周期')

##### 2.0.3.3 任务管理

<h6 id='2033'>2.0.3.3. 任务管理</h6>

###### 2.0.3.3.1 任务调度

<h6 id='20331'>2.0.3.3.1 任务调度</h6>

任务调度是线程池的主要入口，当用户提交了一个任务，接下来这个任务将如何执行都是由这个阶段决定的。了解这部分就相当于了解了线程池的核心运行机制。

所有任务的**调度都是由 `execute` 方法完成的**，这部分完成的工作是：*检查现在线程池的运行状态、运行线程数、运行策略*，*决定接下来执行的流程*，是直接申请线程执行，或是缓冲到队列中执行，亦或是直接拒绝该任务。其执行过程如下：

1. 首先检测线程池运行状态，如果不是RUNNING，则直接拒绝，线程池要保证在RUNNING的状态下执行任务。
2. 如果workerCount < corePoolSize，则创建并启动一个线程来执行新提交的任务。
3. 如果workerCount >= corePoolSize，且线程池内的阻塞队列未满，则将任务添加到该阻塞队列中。
4. 如果workerCount >= corePoolSize && workerCount < maximumPoolSize，且线程池内的阻塞队列已满，则创建并启动一个线程来执行新提交的任务。
5. 如果workerCount >= maximumPoolSize，并且线程池内的阻塞队列已满, 则根据拒绝策略来处理该任务, 默认的处理方式是直接抛异常。

###### 2.0.3.3.2 任务缓冲

<h6 id='20332'>2.0.3.3.2 任务缓冲</h6>

线程池中是以生产者消费者模式，通过一个阻塞队列来实现的。阻塞队列缓存任务，工作线程从阻塞队列中获取任务。

阻塞队列(BlockingQueue)是一个支持两个附加操作的队列。这两个附加的操作是：在队列为空时，获取元素的线程会等待队列变为非空。当队列满时，存储元素的线程会等待队列可用。阻塞队列常用于生产者和消费者的场景，生产者是往队列里添加元素的线程，消费者是从队列里拿元素的线程。阻塞队列就是生产者存放元素的容器，而消费者也只从容器里拿元素。

使用不同的队列可以实现不一样的任务存取策略:

| 名称                  | 描述                                                         |
| --------------------- | ------------------------------------------------------------ |
| ArrayBlockingQueue    | 一个用数组实现的有界队列，按照先入先出（FIFO）的原则对元素排序。支持公平锁和非公平锁。 |
| LinkedBlockingQueue   | 一个由链表结构组成的有界队列，按照先入先出（FIFO）的原则对元素排序。默认长度为Integer.MAX_VALUE，所以默认创建的该队列有容量风险。 |
| PriorityBlockingQueue | 一个支持线程优先级排序的无界队列，默认自然序，也可以自定义compareTo()方法指定排序规则，不能保证同优先级线程的顺序。 |
| DelayQueue            | 一个实现延迟获取的无界队列，在创建元素时可以指定延迟时间，到期后才能取得该元素。 |
| SynchronousQueue      | 一个不存储元素的阻塞队列，每一个put操作必须等take操作，否则无法添加元素。支持公平锁和非公平锁。 |
| LinkedTransferQueue   | 一个由链表结构组成的无界队列，相对于其他队列，它多了 transfer 和 tryTransfer 方法。 |
| LinkedBlockingQueue   | 一个由链表结构组成的双向队列，队列的头和尾都可以添加和移除元素。多线程并发时，可以将锁的竞争最多降到一半。 |



###### 2.0.3.3.3 任务申请

<h6 id='20333'>2.0.3.3.3 任务申请</h6>

由上文的任务分配部分可知，任务的执行有两种可能：一种是任务直接由新创建的线程执行。另一种是线程从任务队列中获取任务然后执行，执行完任务的空闲线程会再次去从队列中申请任务再去执行。第一种情况仅出现在线程初始创建的时候，第二种是线程获取任务绝大多数的情况。

线程需要从任务缓存模块中不断地取任务执行，帮助线程从阻塞队列中获取任务，实现线程管理模块和任务管理模块之间的通信。这部分策略由getTask方法实现，其执行流程如下图所示：

![获取任务流程](https://mmbiz.qpic.cn/mmbiz_png/hEx03cFgUsXAj6OrUTUDRoG5tCBgm4CJMLk6AVVyCgNYN9RJhn4PbHVvwfvUXcp4xurQTY9LCaLXialxvo3laow/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1 '获取任务流程')

getTask这部分进行了多次判断，为的是控制线程的数量，使其符合线程池的状态。如果线程池现在不应该持有那么多线程，则会返回null值。工作线程Worker会不断接收新任务去执行，而当工作线程Worker接收不到任务的时候，就会开始被回收。



###### 2.0.3.3.4 任务拒绝

<h6 id='20334'>2.0.3.3.4 任务拒绝</h6>

任务拒绝模块是线程池的保护部分，线程池有一个最大的容量，当线程池的任务缓存队列已满，并且线程池中的线程数目达到maximumPoolSize时，就需要拒绝掉该任务，采取任务拒绝策略，保护线程池。

拒绝策略是一个接口，其设计如下：

```java
public interface RejectedExecutionHandler {
    void rejectedExecution(Runnable r, ThreadPoolExecutor executor);
}
```

用户可以通过实现这个接口去定制拒绝策略，也可以选择JDK提供的四种已有拒绝策略，其特点如下：

![](https://mmbiz.qpic.cn/mmbiz_png/hEx03cFgUsXAj6OrUTUDRoG5tCBgm4CJKwia8IKhKPVT4TJWU95eUYKqyA9FrdgwK9huZtKOLIwQJpYVBRf64Vw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

##### 2.0.3.4 Worker 线程管理

<h6 id='2034'>2.0.3.4. Worker 线程管理</h6>

> 线程池为了掌握线程的状态并维护线程的生命周期，设计了线程池内的工作线程Worker。

###### 2.0.3.4.1 Worker 线程

<h6 id='20341'>2.0.3.4.1 Worker 线程</h6>

```java
private final class Worker extends AbstractQueuedSynchronizer implements Runnable{
    final Thread thread;//Worker持有的线程
    Runnable firstTask;//初始化的任务，可以为null
}
```

Worker 工作线程结构：

- 实现 Runnable

- 持有一个线程 thread

    在调用构造方法时通过ThreadFactory来创建的线程，可以用来执行任务

- 持有一个初始化任务 firstTask

    用它来保存传入的第一个任务，这个任务可以有也可以为null。如果这个值是非空的，那么线程就会在启动初期立即执行这个任务，也就对应核心线程创建时的情况；如果这个值是null，那么就需要创建一个线程去执行任务列表（workQueue）中的任务，也就是非核心线程的创建。

Worker 执行任务模型图：

![Worker执行任务模型](https://mmbiz.qpic.cn/mmbiz_png/hEx03cFgUsXAj6OrUTUDRoG5tCBgm4CJibFaUsW5YbgOTr7GEoRPekq9NqvnGY92biaMJodpZMFmA1mZtgAKbpMA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1 'Worker执行任务')

线程池需要管理线程的生命周期，需要在线程长时间不运行的时候进行回收。

线程池使用一张**Hash表去持有线程的引用**，这样可以通过添加引用、移除引用这样的操作来控制线程的生命周期。

这个时候重要的就是*如何判断线程是否在运行*。

Worker是通过**继承AQS，使用AQS来实现独占锁**这个功能。没有使用可重入锁ReentrantLock，而是使用AQS，*为的就是实现不可重入的特性去反应线程现在的执行状态。*

1. lock方法一旦获取了独占锁，表示当前线程正在执行任务中。
2. 如果正在执行任务，则不应该中断线程。
3. 如果该线程现在不是独占锁的状态，也就是空闲的状态，说明它没有在处理任务，这时可以对该线程进行中断。
4. 线程池在执行 shutdown 方法或 tryTerminate 方法时会调用 interruptIdleWorkers 方法来中断空闲的线程，interruptIdleWorkers方法会使用tryLock方法来判断线程池中的线程是否是空闲状态；如果线程是空闲状态则可以安全回收。

根据独占锁特性，回收过程图：

![线程池回收过程](https://mmbiz.qpic.cn/mmbiz_png/hEx03cFgUsXAj6OrUTUDRoG5tCBgm4CJXvuCKXicTcSdiaR0nDpeahiblnfrQt0zUQNtpmgC4e1RHexLPuqKOluMA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1 '线程池回收')



###### 2.0.3.4.2 Worker 线程增加

<h6 id='20342'>2.0.3.4.2 Worker 线程增加</h6>

增加线程是通过线程池中的 `addWorker()` 方法，该方法的功能就是增加一个线程，该方法不考虑线程池是在哪个阶段增加的该线程，这个分配线程的策略是在上个步骤完成的，该步骤仅仅完成增加线程，并使它运行，最后返回是否成功这个结果。

addWorker 方法有两个参数：`firstTask`、`core`。**firstTask**参数用于**指定新增的线程执行的第一个任务**，该参数*可以为空*；**core**参数为***true表示在新增线程时会判断当前活动线程数是否少于corePoolSize***，**false表示新增线程前需要判断当前活动线程数是否少于maximumPoolSize**，其执行流程如下图所示：

<img src="https://mmbiz.qpic.cn/mmbiz_png/hEx03cFgUsXAj6OrUTUDRoG5tCBgm4CJhrpJW5JvLZb3gOzPyaBr5UjicLTET0JV01bTmKpVNlkk839cSHib0QSg/640?wx_fmt=png&amp;tp=webp&amp;wxfrom=5&amp;wx_lazy=1&amp;wx_co=1" style="zoom: 50%;" />



###### 2.0.3.4.3 Worker 线程执行

<h6 id='20343'>2.0.3.4.3 Worker 线程执行</h6>

在Worker类中的run方法调用了`runWorker()`方法来执行任务：

1. while循环不断地通过 getTask() 方法获取任务。
2. getTask() 方法从阻塞队列中取任务。
3. 如果线程池正在停止，那么要保证当前线程是中断状态，否则要保证当前线程不是中断状态。
4. 执行任务。
5. 如果 getTask 结果为 null 则跳出循环，执行 processWorkerExit() 方法，销毁线程。

###### 2.0.3.4.4 Worker 线程回收

<h6 id='20344'>2.0.3.4.4 Worker 线程回收</h6>

线程池中线程的销毁依赖JVM自动的回收，线程池做的工作是根据当前线程池的状态维护一定数量的线程引用，防止这部分线程被JVM回收，当线程池决定哪些线程需要回收时，只需要将其引用消除即可。

Worker被创建出来后，就会不断地进行轮询，然后获取任务去执行，核心线程可以无限等待获取任务，非核心线程要限时获取任务。当Worker无法获取到任务，也就是获取的任务为空时，循环会结束，Worker会主动消除自身在线程池内的引用。

```java
try {
  while (task != null || (task = getTask()) != null) {
    //执行任务
  }
} finally {
  processWorkerExit(w, completedAbruptly);//获取不到任务时，主动回收自己
}
```

在`processWorkerExit()`这个方法中，将线程引用移出线程池就已经结束了线程销毁的部分。但由于引起线程销毁的可能性有很多，线程池还要判断是什么引发了这次销毁，是否要改变线程池的现阶段状态，是否要根据新状态，重新分配线程。









-------------------------------
## 3.Java 虚拟机


<h5 id="3">3.Java虚拟机</h5>

---------
[^1]:https://lrh1993.gitbooks.io/android_interview_guide/content/java/basis.html "Java基础学习资源"
[^2]:https://lrh1993.gitbooks.io/android_interview_guide/content/java/concurrence.html "Java并发学习资源"
[^20]:https://mp.weixin.qq.com/s/baYuX8aCwQ9PP6k7TDl2Ww "线程池原理及其在美团业务中的实践"
[^3]:https://lrh1993.gitbooks.io/android_interview_guide/content/java/virtual-machine.html "JVM学习资源"
> Java相关姿势学习记录，主要包括 *Java基础*，*Java并发*，*Java虚拟机* 三个部分。
> 资源均来自网络博文，感谢开源！

### 目录

- [Java基础](#1) [^1]  
    - [1.1. Java集合框架](#1.1)
    - [1.2. ArrayList](#1.2)
    - [1.3. LinkedList](#1.3)
    - [1.4. HashMap](#1.4)
    - [1.5. TreeMap](#1.5)
    - [1.6. LinkedHashMap](#1.6)
- [Java并发](#2) [^2]    
- [Java虚拟机](#3) [^3]  



<h3 id="1">1. Java基础</h3>

<h4 id="1.1">1.1. Java集合框架    </h4>

Java集合四大体系：`Set` 、`Queue`、`List`、`Map`；   

前三个的父接口为`Collection`，`Collection `继承自`Iterator`，并实现了方法 `hasNext()` 和 `next()`；   

- `Set`：无序、不可重复；
- `List`：有序、可重复；
- `Map`：存储具有映射关系的键值对；
- `Queue`：以队列方式存储（First-In-First-Out），***Java 5 新增***。

Java集合像一个容器，存储对象（实际是对象的引用，习惯上称为对象）。***Java 5 新增了泛型***，使得数据结构更简洁、更健壮。

常用的集合实现类有：`HashSet`、`TreeSet`，`ArrayList`、`LinkedList`，`HashMap`、`TreeMap`等。

<h4 id="1.2">1.2. ArrayList</h4>



<h4 id="1.3">1.3. LinkedList</h4>



<h4 id="1.4">1.4. HashMap</h4>



<h4 id="1.5">1.5. TreeMap</h4>



<h4 id="1.6">1.6. LinkedHashMapt</h4>



<h3 id="2">2. Java并发</h3>



<h3 id="3">3. Java虚拟机</h3>



---------
[^1]:https://lrh1993.gitbooks.io/android_interview_guide/content/java/basis.html "Java基础学习资源"
[^2]:https://lrh1993.gitbooks.io/android_interview_guide/content/java/concurrence.html "Java并发学习资源"
[^3]:https://lrh1993.gitbooks.io/android_interview_guide/content/java/virtual-machine.html "JVM学习资源"
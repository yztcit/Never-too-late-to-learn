> 记录 Android 开发学习过程中的各种姿势，淦！
> 放弃幻想，准备斗争！

------------
### 一、Android
#### 1. Android 基础
##### 1.1. UI

##### 1.1.1. 触摸事件的分发
- 被分发的对象：**触摸屏幕产生的点击事件**，被包装为 `MotionEvent`；
- 事件的动作：按下（`Down`）、滑动（`Move`）、抬起（`Up`）、取消（`Cancel`）；
- 分发者：`Activity`、`ViewGroup`、`View`；
- 分发过程涉及的方法：`dispatchTouchEvent()`、`onInteceptTouchEvent()`、`onTouchEvent()`、`requestDisallowInterceptTouchEvent()`;


#### 2. Android 进阶


#### 3. 开源框架

------------



------------
### 二、Gradle自动化构建
#### 1.  简介
Gradle不仅仅是一款强大的构建工具，看起来更像一个编程框架。其组成可以细分为如下三方面：
1. Groovy核心语法：包括 Groovy 基本语法、闭包、数据结构、面向对象等；
2. Android DSL（build script block）：Android所特有的，我们按 block 块来处理业务；
3. Gradle API：包含 Project、Task、Setting 等。

Gradle 的语法是以 Groovy 为基础的，同时它还有自己的API。

[Groovy 筑基](https://juejin.im/post/5e97ac34f265da47aa3f6dca '深入探索Gradle自动化构建技术（二、Groovy筑基篇）')

> Groovy 是一门语言，而 DSL 是一种特定领域的配置文件，Gradle 是基于 Groovy 的一种框架工具，而 gradlew 则是 gradle 的一个兼容包装工具。

#### 2. 构建过程
Gradle 的构建过程分为三个阶段：**初始化**、**配置**、**执行**。






来源：[深度探索Gradle自动化构建技术](https://juejin.im/post/5e9c46c8518825737f1a7b4c  '作者：jsonchao')

---------





---------
### 三、踩坑记录：
#### 1. AndroidManifest
- activity label属性中文设置时报manifest编码不规范错误；----> 通过strings引入


#### 2. Build
- as 直接run到手机时会默认添加testOnly=true 属性，这会导致某些机型没法直接安装调试 ----> gradle.properties  中添加android.injected.testOnly=false

---------
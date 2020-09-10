## Kotlin
> kotlin学习记录，参考资源 [Kotlin 教程](https://www.runoob.com/kotlin/kotlin-tutorial.html "菜鸟教程")、[学习 Kotlin](https://www.kotlincn.net/docs/reference/ "Kotlin 中文社区")、《第一行代码》(第三版)、[码上开学](https://kaixue.io/ "Hencoder")
>
> > *Java 和  Kotlin 命名的小插曲 ~*
> > Java 就是著名的爪哇岛，该岛盛产咖啡。据说一群研究出 Java 语言的牛人们在为它命名时闻到香浓的咖啡味，于是决定采用此名称。
> > Kotlin 源于芬兰湾的 Kotlin 岛。

### Kotlin 之于 Android
**新建支持 Kotlin 的 Android 项目**

- File -> New -> New Project...
- Choose your project -> Phone and Tablet -> Empty Acticity
- Configure your project -> Language 选择 [Kotlin]

1. IDE 自动创建 `MainActivity.kt`
2. 项目中的 2 个`build.gradle` 多了 Kotlin 的依赖：
	
	- 根目录的 `build.gradle`:
	
	```java
	buildscript {
		ext.kotlin_version = '1.3.41'
		repositories {
			...
		}
		dependencies {
			classpath 'com.android.tools.build:gradle:3.5.0-beta05'
			classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
		}
	}
	```
	- app目录下的 `build.gradle`:
	
	```java
	apply plugin: 'com.android.application'
   apply plugin: 'kotlin-android'
	...
	android {
   	...
   }
   dependencies {
	 	implementation fileTree(dir: 'libs', include: ['*.jar'])
	 	implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
		...
	}
	```

### 基本语法
#### 1. 变量
##### 1.1. 与 Java 的不同
```Kotlin
var a : Int = 2 // 普通变量；
val b : Int = 1 // 只读变量，只能赋值一次，之后不能修改；
// 👆 类似 final int b = 1;
```
> var 是 variable 的缩写，val 是 value 的缩写。

***这里与 Java 中的变量的几点不同：***

- 变量声明用 var 和 val；
- 类型与变量名换了位置，中间用冒号分隔；
- 结尾不需要分号。

```Kotlin
class Student {
	var name : String
	// 👆 这么写 IDE 会报错：
	// Property must be initialized or be abstract
	
	var v : View = null
	// 👆 这样写 IDE 仍然会报错：
	// Null can not be a value of a non-null type View
}
```
因为 Kotlin 的变量是没有默认值的，属性需要在声明的同时初始化，除非你把它声明成抽象的。

##### 1.2. Kotlin 的空安全设计

在 Kotlin 里面，所有的变量默认都是不允许为空的，如果你给它赋值 null，就会报错，像上面那样。
不过，还是有些场景，变量的值真的无法保证空与否，比如你要从服务器取一个 JSON 数据，并把它解析成一个 User 对象：
```Kotlin
class User {
	var name : String? = null  //可以在类型右边加一个 ? 号，解除它的非空限制
}
```
这种类型之后加 ? 的写法，在 Kotlin 里叫 **可空类型**。

不过，当我们使用了可空类型的变量后，会有新的问题：
由于对空引用的调用会导致空指针异常，所以 Kotlin 在可空变量直接调用的时候 IDE 会报错：
```Kotlin
var view: View? = null
view.setBackgroundColor(Color.RED)
// 👆 这样写会报错，Only safe (?.) or non-null asserted (!!.) calls are allowed on a nullable receiver of type View?
```
「可能为空」的变量，Kotlin 不允许用。那怎么办？
```Kotlin
if (view != null) {
    view.setBackgroundColor(Color.RED)
    // 👆 这样写会报错，Smart cast to 'View' is impossible, because 'view' is a mutable property that could have been changed by this time
} 
```
这个报错的意思是即使你检查了非空也不能保证下面调用的时候就是非空，因为在多线程情况下，其他线程可能把它再改成空的。

那么 Kotlin 里是这么解决这个问题的呢？它用的不是 . 而是 ?.：
```Kotlin
view?.setBackgroundColor(Color.RED)
```
这个写法同样会对变量做一次非空确认之后再调用方法，这是 Kotlin 的写法，并且它可以做到线程安全，因此这种写法叫做「safe call」。
还有一种双感叹号的用法：
```Kotlin
view!!.setBackgroundColor(Color.RED)
```
意思是告诉编译器，我保证这里的 view 一定是非空的，编译器你不要帮我做检查了，有什么后果我自己承担。这种「肯定不会为空」的断言式的调用叫做 「non-null asserted call」。

**综上：**
- 变量需要手动初始化，如果不初始化的话会报错；
- 变量默认非空，所以初始化赋值 null 的话报错，之后再次赋值为 null 也会报错；
- 变量用 ? 设置为可空的时候，使用的时候因为「可能为空」又报错。

关于空安全，最重要的是记住一点：**所谓「可空不可空」，关注的全都是使用的时候，即「这个变量在使用时是否可能为空」。**

##### 1.3. 延迟初始化

```Kotlin
lateinit var view: View
```
`lateinit` 告诉编译器我没法第一时间就初始化，但我肯定会在使用它之前完成初始化的。换句话说，加了这个 `lateinit` 关键字，这个变量的初始化就全靠你自己了，编译器不帮你检查了。
延迟初始化对变量的赋值次数没有限制，你仍然可以在初始化之后再赋其他的值给 view。

##### 1.4. 类型推断

```Kotlin
var name: String = "Mike"
👇
var name = "Mike"
```
这个特性叫做「类型推断」，它跟 Groovy 或 JS 中的动态类型是不一样的。
**「动态类型」是指变量的类型在运行时可以改变；而「类型推断」是你在代码里不用写变量类型，编译器在编译的时候会帮你补上。因此，Kotlin 是一门静态语言。**

##### 1.5. 可见性

Java 里的 public、protected、private 这些表示变量可见性的修饰符，在 Kotlin 里变量默认就是 public 的，而对于其他可见性修饰符，TODO：追加修饰符记录于 3.2

#### 2. 函数
##### 2.1 与 Java 的不同

> Kotlin 除了变量声明外，函数的声明方式也和 Java 的方法不一样。Java 的方法（method）在 Kotlin 里叫函数（function）。对任何编程语言来讲，变量就是用来存储数据，而函数就是用来处理数据。

```Kotlin
fun cook(name : String) : Food {
	...
}
```
- 以 fun 关键字开头；
- 返回值写在了函数和参数的后面。

没有返回值时，Java 用 `void` 申明， Kotlin 用 `Unit` 并且可以省略：
```Kotlin
fun test(str : String) : Unit {}
// Unit 返回类型可以省略
fun test(str : String) {}
```

函数参数也可以有可空的控制，根据前面说的空安全设计，在传递时需要注意：
```Kotlin
// 👇可空变量传给不可空参数，报错
var name : String? = "123"
fun cook(str : String) : Food {}
cook(name)

// 👇可空变量传给可空参数，正常
var name : String? = "123"
fun cook(str : String?) : Food {}
cook(name)

// 👇不可空变量传给不可空参数，正常
var name : String = "123"
fun cook(str : String) : Food {}
cook(name)
```

##### 2.2. 可见性

函数如果不加可见性修饰符的话，默认的可见范围和变量一样也是 public 的，*但有一种情况例外*，这里简单提一下，*就是遇到了 override 关键字的时候*，下面会讲到（3.2）。

##### 2.3. 属性的 `getter` / `setter` 函数

常见的，在 Java 里 field 经常带有 `getter` / `setter` 函数：
```Java
public class Student {
	private String name;
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
}
```
它们的作用就是可以自定义函数内部实现来达到「钩子」的效果，比如下面这种：
```Java
public class Student {
	private String name;
	
	public void setName(String name) {
		this.name = "Cute " + name;
	}
	
	public String getName(){
		return this.name + " giao";
	}
}
```

在 Kotlin 里，这种 getter / setter 是怎么运作的呢？
```Kotlin
class Student {
    var name = "Mike"
    fun run() {
        name = "Mary"
        // 👆的写法实际上是👇这么调用的
        // setName("Mary")
        // 建议自己试试，IDE 的代码补全功能会在你打出 setn 的时候直接提示 name 而不是 setName
        
        println(name)
        // 👆的写法实际上是👇这么调用的
        // print(getName())
        // IDE 的代码补全功能会在你打出 getn 的时候直接提示 name 而不是 getName
    }
}
```
那么我们如何来操作前面提到的「钩子」呢？看下面这段代码：
```Kotlin
class Student {
    var name = "Mike"
        👇
        get() {
            return field + " nb"
        }
        👇   👇 
        set(value) {
            field = "Cute " + value
        }
}
```
*格式上和 Java 有一些区别：*
- getter / setter 函数有了专门的关键字 get 和 set
- getter / setter 函数位于 var 所声明的变量下面
- setter 函数参数是 value

除此之外还多了一个叫 field 的东西。这个东西叫做「Backing Field」，中文翻译是**幕后字段**或**后备字段**

具体来说，举个栗子：
```Kotlin
class Kotlin {
  var name = "kaixue.io"
}
```
在编译后的字节码大致等价于这样的 Java 代码：
```Java
public final class Kotlin {
   @NotNull
   private String name = "kaixue.io";

   @NotNull
   public final String getName() {
      return this.name;
   }

   public final void setName(@NotNull String name) {
      this.name = name;
   }
}
```
> 上面的那个 String name 就是 Kotlin 帮我们自动创建的一个 Java field。这个 field 对编码的人不可见，但会自动应用于 getter 和 setter，因此它被命名为「Backing Field」，它相当于每一个 var 内部的一个变量。

1.1中讲过 val 是只读变量，只读的意思就是说 val 声明的变量不能进行重新赋值，也就是说不能调用 setter 函数，因此，val 声明的变量是不能重写 setter 函数的，但它可以重写 getter 函数：
```Kotlin
val name = "Mike"
	get(){
		return field + " giao"
	}
```
*val 所声明的只读变量，在取值的时候仍然可能被修改*，这也是和 Java 里的 final 的不同之处。


#### 3. 类型
##### 3.1. 基本类型

在 Kotlin 中，万物皆对象，Kotlin 中使用的基本类型有：数字、字符、布尔值、数组与字符串。
```Kotlin
var number: Int = 1 // 👈还有 Double Float Long Short Byte 都类似

var c: Char = 'c'

var b: Boolean = true

var array: IntArray = intArrayOf(1, 2) // 👈类似的还有 FloatArray DoubleArray CharArray 等，intArrayOf 是 Kotlin 的 built-in 函数

var str: String = "string"
```
**这里有两个地方和 Java 不太一样：**
- Kotlin 里的 Int 和 Java 里的 int 以及 Integer 不同，主要是在装箱方面不同。
  Java 里的 int 是 unbox 的，而 Integer 是 box 的：
  ```Java
  int a = 1;
  Integer b = 2; // 👈会被自动装箱 autoboxing
  ```
  Kotlin 里，Int 是否装箱根据场合来定：
  ```Kotlin
  var a: Int = 1 // unbox
  var b: Int? = 2 // box
  var list: List<Int> = listOf(1, 2) // box
  ```
Kotlin 在语言层面简化了 Java 中的 int 和 Integer，但是我们对是否装箱的场景还是要有一个概念，因为这个牵涉到程序运行时的性能开销。
因此在日常的使用中，对于 Int 这样的**基本类型，尽量用不可空变量。**

- Java 中的数组和 Kotlin 中的数组的写法也有区别：
  ```Java
  int[] array = new int[] {1, 2};
  ```
  而在 Kotlin 里，上面的写法是这样的：
  ```Kotlin
  var array : intArray = intArrayOf(1, 2)
  // 👆这种也是 unbox 的
  ```
  简单来说，原先在 Java 里的基本类型，类比到 Kotlin 里面，条件满足如下之一就不装箱：
  - 不可空类型;
  - 使用 IntArray、FloatArray 等。

##### 3.2. 类和对象

用 MainActivity.kt 和 MainActivity.java 作对比：
```Java
interface Impl {}
```
```Kotlin
class MainActivity : AppcompatActivity(), Impl {
	override fun onCreate(savedInstanceState : Bundle?){
		setContentView(R.layout.activity_main)
	}
}
```
```Java
public class MainActivity extends AppcompatActivity implements Impl {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);
	}
}
```
- 首先是类的可见性，Java 中的 `public` 在 Kotlin 中可以省略，Kotlin 的类默认是`public` 的；
- 类的继承的写法，Java 里用的是 `extends`，而在 Kotlin 里使用 `:`，但其实 `:` 不仅可以表示继承，还可以表示 Java 中的 `implement`；
- 构造方法的写法不同。
  - Java 里省略了默认的构造函数：
    ```Java
    public class MainActivity extends AppCompatActivity {
      // 👇默认构造函数
      public MainActivity() {
      }
    }
    ```
  - Kotlin 里我们注意到 `AppCompatActivity` 后面的 ()，这其实也是一种省略的写   法，等价于：
    ```Kotlin
    class MainActivity constructor() : AppCompatActivity() {
                          👆
    }
    ```
    不过其实更像 Java 的写法是这样的：
    ```Kotlin
    class MainActivity : AppCompatActivity {
    	constructor(){
    	}
    }
    ```
    Kotlin 把构造函数单独用了一个 `constructor` 关键字来和其他的 `fun` 做区分。
  - override 的不同
    - Java 里面 `@Override` 是注解的形式；
    - Kotlin 里的 `override` 变成了关键字；
    - Kotlin 省略了 `protected` 关键字，也就是说，Kotlin 里的 `override` 函数的可见性是继承自父类的。
      如果要关闭 override 的遗传性，只需要这样即可：
      ```Kotlin
      open class MainActivity : AppcompatActivity() {
          // 👇加了 final 关键字，作用和 Java 里面一样，关闭了 override 的遗传性
      	final override fun onCreate(savedInstanceState : Bundle?){
      	   ...
      	}
      }
      ```

除了以上这些明显的不同之外，还有一些不同点从上面的代码里看不出来，但当你写一个类去继承 `MainActivity` 时就会发现：
  - Kotlin 里的 `MainActivity` 无法继承：
    ```Kotlin
    // 👇写法会报错，This type is final, so it cannot be inherited from
	class NewActivity: MainActivity() {
	}
    ```
    原因是 Kotlin 里的类默认是 `final` 的，而 Java 里只有加了 `final` 关键字的类才是 `final` 的。**我们可以使用 `open` 解除 `final` 限制。**
	```Kotlin
    open class MainActivity : AppCompatActivity() {}
    
	class NewActivity: MainActivity() {
	}
	```
	但是要注意，此时 NewActivity 仍然是 `final` 的，也就是说，`open` 没有父类到子类的遗传性。
	
  - Kotlin 里除了新增了 `open` 关键字之外，也有和 Java 一样的 `abstract` 关键字，
	声明好一个类之后，实例化在 Java 中使用 `new` 关键字。而在 Kotlin 中，没有 `new` 关键字：
	```Java
	void main() {
		Activity activity = new NewActivity();
	}
	```
	```Kotlin
	fun main() {
		var activity : Activity = NewActivity()
	}
	```
	

**综上， Java 和 Kotlin 中关于类的声明主要关注以下几个方面：**
- 类的可见性和开放性；
- 构造方法；
- 继承；
- override 函数。

##### 3.3. 类型的判断和强转

> 刚才讲的实例化的例子中，我们实际上是把子类对象赋值给父类的变量，这个概念在 Java 里叫多态，Kotlin 也有这个特性，但在实际工作中我们很可能会遇到需要使用子类才有的函数。

在 Java 里，需要先使用 `instanceof` 关键字判断类型，再通过强转来调用：
```Java
void main() {
    Activity activity = new NewActivity();
    if (activity instanceof NewActivity) {
        ((NewActivity) activity).action();
    }
}
```
Kotlin 使用 `is` 关键字进行「类型判断」，并且因为编译器能够进行类型推断，可以帮助我们省略强转的写法：
```Kotlin
fun main() {
    var activity: Activity = NewActivity()
    if (activity is NewActivity) {
        // 👇的强转由于类型推断被省略了
        activity.action()
    }
}
```
不进行类型判断，直接进行强转调用呢？可以使用 `as` 关键字：
```Kotlin
fun main() {
    var activity: Activity = NewActivity()
    //如果强转成一个错误的类型，程序就会抛出一个异常。
    (activity as NewActivity).action()
}
```
安全的强转，可以更优雅地处理强转出错的情况，我们可以使用 `as?` 来解决：
```Kotlin
fun main() {
    var activity: Activity = NewActivity()
    // 👇'(activity as? NewActivity)' 之后是一个可空类型的对象，所以，需要使用 '?.' 来调用
    (activity as? NewActivity)?.action()
}
```
**Notice：**
```Kotlin
// 👇 强转，转型失败直接报错
activity as NewActivity

// 👇 可空转型，转型失败得到一个空对象
activity as? NewActivity

// 👇 activity 为空时没事，但是不为空转型失败会抛异常 ClassCastException
activity as NewActivity?

// 👇 activity 空或转型失败，得到一个空对象
activity as? NewActivity?
```

> 记录 Android 开发学习过程中的各种姿势，淦！
> 放弃幻想，准备斗争！

------------
# 一、Android
## 1. Android 基础
### 1.1. UI
#### 1.1.1. 触摸事件的分发
- 被分发的对象：**触摸屏幕产生的点击事件**，被包装为 `MotionEvent`；
- 事件的动作：按下（`Down`）、滑动（`Move`）、抬起（`Up`）、取消（`Cancel`）；
- 分发者：`Activity`、`ViewGroup`、`View`；
- 分发过程涉及的方法：`dispatchTouchEvent()`、`onInteceptTouchEvent()`、`onTouchEvent()`、`requestDisallowInterceptTouchEvent()`;

#### 1.1.2 ListView & RecyclerView

|              |                         UI                         |    缓存    |
| :----------: | :------------------------------------------------: | :--------: |
|   ListView   | headerView、footerView、remoteView、clickListenter | RecycleBin |
| RecyclerView |           itemDecoration、layoutManager            |  Recycler  |



### 1.2 组件

#### 1.2.1 Activity

- 生命周期
- 启动流程

__1）`activity`__

```java
ActivityThread#performLaunchActivity(ActivityClientRecord r, Intent customIntent) {
    ...
    // 0.创建ContextImpl
    ContextImpl appContext = createBaseContextForActivity();
    Activity activity = null;
    // 1.实例化 activity
    try {
        java.lang.ClassLoader cl = appContext.getClassLoader();
        actvity = mInstrumentation.newActivity(cl, component.getClassName(), r.intent)
    }
    
    try {
        if (activity != null) {
            // 2.activity#attach() 数据初始化
            activity.attach(appContext, this, getInstrumentation(), r.token,
                        r.ident, app, r.intent, r.activityInfo, title, r.parent,
                        r.embeddedID, r.lastNonConfigurationInstances, config,
                        r.referrer, r.voiceInteractor, window, r.configCallback,
                        r.assistToken);
            // 3.onCreate()
            if (r.isPersistable()) {
                mInstrumentation.callActivityOnCreate(activity, r.state, r.persistentState);
            } else {
                mInstrumentation.callActivityOnCreate(activity, r.state);
            }
        }
    }
}
```

> 这个过程做了三件事：
>
> 1. 实例化`Activity`；
> 2. 调用`attach()`，初始化数据；
> 3. 调用`onCreate()` -> 通过`setContentView(resId)`加载布局，为界面显示做准备。

__2) `PhoneWindow`__

 `activity`借助窗口`Window`的实现类 `PhoneWindow` 和  `View`  解耦:

```java
// Activity.java
final void attach(...) {
    // 创建 PhoneWindow
    mWindow = new PhoneWindow(this, window, activityConfigCallback);
    mWindow.setCallback(this);
    mWindow.setWindowManager(
        (WindowManager)context.getSystemService(Context.WINDOW_SERVICE),
        mToken, mComponent.flattenToString(),
        (info.flags & ActivityInfo.FLAG_HARDWARE_ACCELERATED) != 0);
}
```

view 相关的工作由窗口`Window`管理

`onCreate()`中的`setContentView()`加载布局文件，直接调用到 `PhoneWindow` 的 `setContentView()` 

```java
//Activity.java
public void setContentView(@LayoutRes int layoutResID) {
    getWindow().setContentView(layoutResID);
    initWindowDecorActionBar();
}
```

考虑到 `Activity` 有不同的主题，不同的主题就有不同的布局结构，所以得在加载布局之前设置一个顶层的 view ↓↓↓

__3) `DecorView`__

```java
//PhoneWindow.java
@Override
public void setContentView(int layoutResID) {
    if (mContentParent == null) {
        installDecor();
    } 

    // 加载应用自己的布局
    if (!hasFeature(FEATURE_CONTENT_TRANSITIONS)) {
        mLayoutInflater.inflate(layoutResID, mContentParent);
    }
}

private void installDecor() {
    if (mDecor == null) {
        mDecor = generateDecor(-1);
    } else {
        mDecor.setWindow(this);
    }
    if (mContentParent == null) {
        mContentParent = generateLayout(mDecor);       
    }
}

// 根据不同的主题设置不同的布局结构
protected DecorView generateDecor(int featureId) {
    return new DecorView(context, featureId, this, getAttributes());
}
```

以上：

	1. `activity`负责统筹

 	2. 窗口 `PhoneWindow` 负责管理各个 `View`
 	3. `DecorView` 最顶层的view，负责展示主题布局

_谁负责绘制呢？_

__4) `ViewRootImpl`__

`onCreate()`调用后，就会调用`onResume()`

```java
@Override
public void handleResumeActivity() {
    //onResume
    final ActivityClientRecord r = performResumeActivity(token, finalStateRequest, reason);
    //addView
    if (r.window == null && !a.mFinished && willBeVisible) {
        r.window = r.activity.getWindow();
        View decor = r.window.getDecorView();
        ViewManager wm = a.getWindowManager();
        WindowManager.LayoutParams l = r.window.getAttributes()
        // 初始化 ViewRootImpl
        wm.addView(decor, l);
}


//WindowManagerGlobal.java
public void addView() {
    synchronized (mLock) {
        root = new ViewRootImpl(view.getContext(), display);

        view.setLayoutParams(wparams);

        mViews.add(view);
        mRoots.add(root);
        mParams.add(wparams);

        try {
            root.setView(view, wparams, panelParentView);
        } 
    }
}

public ViewRootImpl(Context context, Display display) {
    mContext = context;
    mWindowSession = WindowManagerGlobal.getWindowSession();
    mThread = Thread.currentThread();
}
```

`ViewRootImpl` 也被创建出来了，而这个`ViewRootImpl`中，有两个变量值得关注一下：

- `mWindowSession`。类型为`IWindowSession`，是一个Binder对象，用于进程间通信。其在服务器端的实现为Session，可以通过它来完成WMS相关的工作。
- `mThread`。设置了线程变量为当前线程，也就是实例化`ViewRootImpl`时候的线程。一般进行不同线程更新UI的时候，就会判断当前线程和`mThread`是否相等，如果不同，则会抛出异常。

接下来，就是调用`ViewRootImpl`的`setView()`方法:

```java
//ViewRootImpl.java
public void setView() {
    synchronized (this) {
     //绘制
     requestLayout();

     //调用WMS的addWindow方法
     res = mWindowSession.addToDisplay(mWindow, mSeq, mWindowAttributes,
                        getHostVisibility(), mDisplay.getDisplayId(), mWinFrame,
                        mAttachInfo.mContentInsets, mAttachInfo.mStableInsets,
                        mAttachInfo.mOutsets, mAttachInfo.mDisplayCutout, mInputChannel);

     //设置this(ViewRootImpl)为view(decorView)的parent
	 view.assignParent(this);
    }
}
```

> 主要有三个功能：
>
> 1. 触发绘制（具体包括测量、布局、绘制）
>
> 2. 通过 Binder 调用 WMS 的`addWindow`方法
>
>     `addToDisplay`方法最终会WMS所在进程的`addWindow`方法，为窗口分配`Surface`，而这个`Surface`就是负责显示最终的界面，并最终会绘制到屏幕上。
>
> 3. 设置`ViewRootImpl`为`decorView`的 parent
>
>     这样设置之后，子view 请求绘制的时候（requestLayout），就能一直通过 parent 最终找到 ViewRootImpl，然后由ViewRootImpl 来负责所有View的绘制工作。整个调用过程是：
>
>     View.requestLayout -> DecorView.requestLayout -> ViewRootImpl.requestLayout

### 1.3 数据持久化

### 1.4 交互



-------


## 2. Android 进阶
### 2.1 自定义
#### 2.1.1 自定义View
#### 2.1.1 自定义ViewGroup
#### 2.1.3 自定义相机
- Camera：5.0之前的`Camera1`、5.0之后的`Camera2`，通过`Parameters`设置相机参数（预览及拍照图像、对焦、方向等）；
- SurfaceView：展示`Surface`数据，持有`SurfaceHolder`；
- SurfaceHolder：通过`SurfaceCallback`监听`Surface`的动作；
- PictureCallback：`camera#takePicture()`回调原始图片data（byte[]）。



### 2.2 适配

#### 2.2.1 屏幕适配

> [一个低成本屏幕适配方案](https://mp.weixin.qq.com/s/d9QCoBP6kV9VSWvVldVVwA   "字节跳动方案")

- **传统 `dp` 适配**

    Android中的 `dp` 会在渲染前转为 `px` ，**计算公式**：

    - px = density * dp
    - density = dpi / 160
    - px = dp * (dpi / 160)

    其中，*`dpi` 是根据设备的实际分辨率和尺寸来计算的*，每个设备都可能不一样。

    

    **屏幕尺寸、分辨率、像素密度三者关系**

    通常手机的**分辨率**是 **宽 x 高**，**屏幕大小**单位为 **寸**，三者关系：
    $$
    dpi = \frac{\sqrt{(宽² + 高²)(单位：px)}}{屏幕尺寸(单位：inch)}
    $$
    举个栗子：屏幕分辨率为 1080 * 1920，屏幕尺寸为 5 吋，那么 dpi 为 440.

    

    *这会存在什么问题呢？*

    假设我们UI *设计图是按屏幕宽度为360dp来设计的*，那么在上述设备上，*屏幕宽度其实为1080/(440/160)=392.7dp*，也就是屏幕是比设计图要宽的。这种情况下， 即使使用dp也是无法在不同设备上显示为同样效果的。 同时还存在部分设备屏幕宽度不足360dp，这时就会导致按360dp宽度来开发实际显示不全的情况。

    而且上述屏幕尺寸、分辨率和像素密度的关系，很多设备并没有按此规则来实现， 因此dpi的值非常乱，没有规律可循，从而导致使用dp适配效果差强人意。

    

- **新的适配方案**

    首先来梳理下我们的需求，一般我们设计图都是以固定的尺寸来设计的。比如以分辨率 1920px * 1080px 来设计，以 `density` 为 3 来标注，也就是屏幕其实是 640dp * 360dp。

    如果我们**想在所有设备上显示完全一致，其实是不现实的，因为屏幕高宽比不是固定的**，16:9、4:3 甚至其他宽高比层出不穷，宽高比不同，显示完全一致就不可能了。

    但是通常下，**我们只需要以宽或高一个维度去适配**，比如我们Feed是 *上下滑动的，只需要保证在所有设备中宽的维度上显示一致*即可，再比如一个 *不支持上下滑动的页面，那么需要保证在高这个维度上都显示一致，尤其不能存在某些设备上显示不全的情况*。同时考虑到现在基本都是以dp为单位去做的适配，如果新的方案不支持dp，那么迁移成本也非常高。

    因此，总结下大致需求如下：

    1. **支持以宽或者高一个维度去适配，保持该维度上和设计图一致；**
    2. **支持dp和sp单位，控制迁移成本到最小。**

    

    **找兼容突破口**

    从`dp`和`px`的转换公式 ：**px = dp * density**

    可以看出，如果设计图宽为360dp，*想要保证在所有设备计算得出的px值都正好是屏幕宽度的话，我们只能修改  `density` 的值。*

    

    通过阅读源码，我们可以得知，`density` 是 `DisplayMetrics` 中的成员变量，而 `DisplayMetrics` 实例通过 `Resources#getDisplayMetrics` 可以获得，而`Resouces`通过`Activity`或者`Application`的`Context`获得。

    

    先来熟悉下 `DisplayMetrics` 中和适配相关的几个变量：

    - DisplayMetrics#density 就是上述的 density
    - DisplayMetrics#densityDpi 就是上述的 dpi
    - DisplayMetrics#scaledDensity 字体的缩放因子，正常情况下和 density 相等，但是调节系统字体大小后会改变这个值

    

    **那么是不是所有的dp和px的转换都是通过 DisplayMetrics 中相关的值来计算的呢？**

    首先来看看*布局文件中dp的转换*，最终都是调用 `TypedValue#applyDimension(int unit, float value, DisplayMetrics metrics)` 来进行转换:

    ```java
    public static float applyDimension(int unit, float value, DisplayMetrics metrics) {
    	switch (unit) {
    		case COMPLEX_UNIT_PX:
                return value;
            case COMPLEX_UNIT_DIP:
                return value * metrics.density;
            case COMPLEX_UNIT_SP:
                return value * metrics.scaledDensity;
            // 以下分支可忽略
            case COMPLEX_UNIT_PT:
                return value * metrics.xdpi * (1.0f/72);
            case COMPLEX_UNIT_IN:
                return value * metrics.xdpi;
            case COMPLEX_UNIT_MM:
                return value * metrics.xdpi * (1.0f/25.4f);
        }
        return 0;
    }
    ```

    这里用到的DisplayMetrics正是从Resources中获得的。

    再看看*图片的decode*，`BitmapFactory#decodeResourceStream`方法:

    ```java
    public static Bitmap decodeResourceStream(Resources res, TypedValue value, InputStream is, Rect pad, 											Options opts) {
        // ... 省略不相关的
        if (opts.inTargetDensity == 0 && res != null) {
            opts.inTargetDensity = res.getDisplayMetrics().densityDpi;
        }
        return decodeStream(is, pad, opts);
    }
    ```

    可见也是通过 DisplayMetrics 中的值来计算的。

    还有些其他dp转换的场景，基本都是通过 DisplayMetrics 来计算的，这里不再详述。

    因此，想要满足上述需求，我们只需要修改 DisplayMetrics 中和 dp 转换相关的变量即可。

    

- **最终方案**

    假设设计度宽度是 360dp， 以宽度为维度来适配：

    **注意**

    - `DisplayMetrics#scaledDensity` 的特殊性

        将`DisplayMetrics#scaledDensity`和`DisplayMetrics#density`设置为同样的值，用户在系统中修改了字体大小失效了，但是我们还不能直接用原始的`scaledDensity`，直接用的话可能导致某些文字超过显示区域，因此我们可以**通过计算之前`scaledDensity`和`density`的比获得现在的`scaledDensity`**。

    - 切换字体

        如果在系统设置中切换字体，再返回应用，字体并没有变化。于是还得监听下字体切换，调用 `Application#registerComponentCallbacks` 注册下 `onConfigurationChanged` 监听即可。

    ```java
    public class AutoResize {
        private static float sNoncompatDensity;
        private static float sNoncompatScaledDensity;
        
        private static void setCustomDensity(@NonNull Activity activity, @NonNull final Application 												application) {
            final DisplayMetrics appDisplayMetrics = application.getDisplayMetrics();
            
            if (sNoncompatDensity == 0) {
            	sNoncompatDensity = appDisplayMetrics.density;
                sNoncompatScaledDensity = appDisplayMetrics.scaledDensity;
                application.registerComponentCallbacks(new ComponentCallbacks(){
                   @override
                    public void onConfigurationChanged(Configuration newConfig){
                        if (newConfig != null && newConfig.fontScale > 0) {
                            sNoncompatScaledDensity = application.getResources().getDisplayMetrics()
                                .scaledDensity;
                        }
                    }
                    @override
                    public void onLowMemory(){
                        
                    }
                });
            }
            final float targetDensity = appDisplayMetrics.widthPixels / 360; // 根据设计图的宽度适配
            final float targeScaledDensity = targetDensity * (sNoncompatScaledDensity/sNoncompatDensity);
            final int tagetDensityDpi = (int)(160 * targetDensity);
            
            appDisplayMetrics.density = targetDensity;
            appDisplayMetrics.scaledDensity = targetScaledDensity;
            appDisplayMetrics.densityDpi = targetDensityDpi;
            
            final DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
            activityDisplayMetrics.density = targetDensity;
            activityDisplayMetrics.scaledDensity = targetScaledDensity;
            activityDisplayMetrics.densityDpi = targetDensityDpi;
        }
    }
    ```

    以上代码只是以设计图宽360dp去适配的，如果要以高维度适配，可以再扩展下代码即可。



### 2.3 优化

#### 2.3.1 端到端网络优化

> [美团点评移动网络优化](https://www.jianshu.com/p/5fe6412bb739 "Alvin简书")

1. *导致网络不可用*

- GFW拦截；

- DNS劫持，端口封禁等；

- 网络基础建设差；

2. *导致网络加载时间过长*
- 移动设备为省电进行通信芯片预热；
  
- 网络请求跨运营商，物理链路长；
  
- HTTP基于Socket，请求发起会三次握手，断开时四次挥手；

3. *HTTP/HTTPS协议数据安全问题*

4. *多域名问题*
- 需连接不同服务，不利于复用；
  
- 频繁的DNS解析；

**(一) 解决方案之提速**

a）*短连方案一、域名合并*
请求时域名收编，经过SLB（Service Load Balancing）域名还原后，分发到相应业务服务器；
优点：

- 域名统一，减少DNS解析，降低劫持风险；
- 针对同一域名，可利用keep-alive复用连接；
- 客户端和服务端友好，无感。

b）*短连方案二、IP直连*
在客户端建立DNS服务，针对域名拉取IP列表，并对IP跑马测试，将后续连接替换为测试结果最快的IP连接。
优点：

- 摒弃传统DNS解析，摆脱劫持；
- 自建DNS更新时机可控；
- IP列表更换方便。

缺点：

- HTTPS证书和域名绑定，需单独处理证书校验。

**(二) 解决方案之可靠**

为进一步提升端到端成功率，需要*建立长连通道*。
c）首先想到 *HTTP/2技术*，它具有异步连接多路复用，头部压缩，请求响应管线化等优点。
弊端：

- 请求基于DNS，有劫持风险；
- 不同域名建立不同连接；
- 客户端与服务器是公网链路，网络通道难以优化；
- 业务改造大，部署成本高；
- 网络协议可定制空间小。

d）**推荐代理长连模式**
在客户端和业务服务器间架设代理长连服务器，客户端和代理服务器建立TCP长连通道，代理服务器再向业务服务器发送请求，结果通过长连通道返回客户端。
优势：

- 客户端和代理服务器IP直连，不需DNS解析；
- 代理服务器和业务服务器内网通信，减少对公网DNS依赖：
- 代理服务器部署代价小。

**自建长连通道周期**：
  1、中转服务器建设，架设完整链路；
  2、长连通道加密建设；
  3、代理服务器与业务服务器之间专线建设，减少公网干扰；
  4、自动降级Failover建设，极端情况下可建立UDP通道，腾讯WNS备用长连通道或绕过长连通道发起HTTP公网请求；
  5、多地部署长连接入点，就近接入。

**通道切换动态化**



### 2.4 开发架构

开发架构的本质是什么，维基百科对软件架构的描述如下：

> 软件架构是一个系统的草图。软件架构描述的对象是直接构成系统的抽象组件。各个组件之间的连接则用来明确和相对细致地描述组件之间的通讯。
>
> 在实现阶段，这些抽象组件被细化为实际的组件，比如具体某个类或者对象。在面向对象领域中，组件之间的连接通常用接口来实现。拆分开来就是三条：
>
> 1. 针对的是一个完整系统，此系统可以实现某种功能。
> 2. 系统包含多个模块，模块间有一些关系和连接。
> 3. 架构是实现此系统的实施描述：模块责任、模块间的连接。



*为啥要做开发架构设计呢？*

1. 模块化责任具体化，使得每个模块专注自己内部。
2. 模块间的关联简单化，减少耦合。
3. 易于使用、维护性好
4. 提高开发效率



具体到Android开发中，开发架构就是描述 **视图层**、**逻辑层**、**数据层** 三者之间的关系和实施：

- **视图层**：用户界面，即界面的展示、以及交互事件的响应。
- **逻辑层**：为了实现系统功能而进行的必要逻辑。
- **数据层**：数据的获取和存储，含本地、server。



> Android中的开发架构：MVC、MVP、MVVM

#### 2.4.1 MVC

> **View**：对应界面布局，与用户交互的载体
> **Model**：数据模型
> **Controller**：业务逻辑，数据处理，UI处理

`Activity`基本上是`View`和`Controller`的组合体，既要负责视图加载，又要处理业务逻辑，负担重。

所以更应该说是 View-Model 模式，大部分都是通过 Activity 来协调连接的。

#### 2.4.2 MVP

> View：对应于 Activity/Fragment 和 xml，负责布局加载与用户交互
>
> Model：数据模型
>
> Presenter：负责 View 和 Model 之间的交互，处理业务逻辑

面向接口开发，依赖倒置。

抽出 View 接口，并且与 Presenter 互相持有引用，隔离了 Model；

UI 驱动型，通过 UI 事件触发对数据的处理；

复杂的业务会导致大量的 UI、Presenter 接口和实现，代码不美观。

#### 2.4.3 MVVM

> View：对应于 Activity/Fragment 和 xml，负责布局加载与用户交互
>
> Model：数据模型
>
> ViewModel：负责完成 View 于 Model 间的交互，处理业务逻辑

MVVM 的目标和思想与 MVP 类似，利用**数据绑定**(Data Binding)、**依赖属性**(Dependency Property)、**命令**(Command)、**路由事件**(Routed Event)等新特性，打造了一个更加灵活高效的架构。

- 数据驱动

    在 MVVM 中，数据和业务逻辑处于一个独立的 ViewModel 中，ViewModel 只要关注数据和业务逻辑，不需要和 UI 或者控件打交道。由数据驱动 UI 去自动更新，UI 的改变又同时自动反馈到数据，数据成为主导因素，这样使得在业务逻辑处理只要关心数据，方便而且简单很多。

- 低耦合

    MVVM模式中，*数据是独立于 UI* 的，ViewModel 只负责处理和提供数据，UI想怎么处理数据都由UI自己决定，ViewModel 不涉及任何和UI相关的事也不持有UI控件的引用，即使控件改变（TextView 换成 EditText）ViewModel 几乎不需要更改任何代码，专注自己的数据处理就可以了，如果是MVP遇到UI更改，就可能需要改变获取UI的方式，改变更新UI的接口，改变从UI上获取输入的代码，可能还需要更改访问 UI 对象的属性代码等等。

    *UI 更新方便*，我们可以在工作线程中直接修改 ViewModel 的数据（只要数据是线程安全的），剩下的数据绑定框架帮你搞定。

    *复用方便*，一个 ViewModel 复用到多个View中，同样的一份数据，用不同的UI去做展示，对于版本迭代频繁的UI改动，只要更换View层就行，对于如果想在UI上的做AbTest 更是方便的多。

- 团队协作与单元测试

    由于View 和ViewModel之间是松散耦合的，可以由两个人分工来做，一个是处理业务和数据，一个是专门的UI处理。

    不管是UI的单元测试还是业务逻辑的单元测试，都是低耦合的。

[MVVM-Architecture](https://github.com/yztcit/MVVM-Architecture "github客户端实践")



#### 2.4.4 Android Jetpack AAC

> Jetpack 是一个由多个库组成的套件，可帮助开发者遵循最佳做法，减少样板代码并编写可在各种 Android 版本和设备中一致运行的代码，让开发者精力集中编写重要的代码。
>
> Android Architecture Component（AAC）， 即**Android架构组件**。

Jetpack MVVM 推荐架构：

<img src="https://upload-images.jianshu.io/upload_images/5777290-41d63e72a0e0fa86.image?imageMogr2/auto-orient/strip|imageView2/2/w/960/format/webp" alt="Jetpack MVVM" style="zoom: 67%;" />

##### 2.4.4.1 Lifecycle [^1]

> 协助开发者管理 `Activity` 和  `Fragment`生命周期，是 LiveData 和 ViewModel 的基石。

```java
// 引入依赖
// 非AndroidX
implementation "android.arch.lifecycle:extensions:1.1.1"
// AndroidX
// appcompat依赖了androidx.fragment，而androidx.fragment下依赖了ViewModel和 LiveData，LiveData内部又依赖了Lifecycle。
implementation 'androidx.appcompat:appcompat:1.2.0'
```

单独引用

```java
//根目录的 build.gradle
    repositories {
        google()
        ...
    }

//app的build.gradle
    dependencies {
        def lifecycle_version = "2.2.0"
        def arch_version = "2.1.0"

        // ViewModel
        implementation "androidx.lifecycle:lifecycle-viewmodel:$lifecycle_version"
        // LiveData
        implementation "androidx.lifecycle:lifecycle-livedata:$lifecycle_version"
        // 只有Lifecycles (不带 ViewModel or LiveData)
        implementation "androidx.lifecycle:lifecycle-runtime:$lifecycle_version"
    
        // Saved state module for ViewModel
        implementation "androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version"

        // lifecycle注解处理器
        annotationProcessor "androidx.lifecycle:lifecycle-compiler:$lifecycle_version"
        // 替换 - 如果使用Java8,就用这个替换上面的lifecycle-compiler
        implementation "androidx.lifecycle:lifecycle-common-java8:$lifecycle_version"

    //以下按需引入
        // 可选 - 帮助实现Service的LifecycleOwner
        implementation "androidx.lifecycle:lifecycle-service:$lifecycle_version"
        // 可选 - ProcessLifecycleOwner给整个 app进程 提供一个lifecycle
        implementation "androidx.lifecycle:lifecycle-process:$lifecycle_version"
        // 可选 - ReactiveStreams support for LiveData
        implementation "androidx.lifecycle:lifecycle-reactivestreams:$lifecycle_version"
        // 可选 - Test helpers for LiveData
        testImplementation "androidx.arch.core:core-testing:$arch_version"
    }
```



##### 2.4.4.2 LiveData [^2]



##### 2.4.4.3 ViewModel [^3]



##### 2.4.4.4 DataBinding [^4]



## 3. 开源框架

------------



------------
# 二、Gradle自动化构建
## 1.  简介
Gradle不仅仅是一款强大的构建工具，看起来更像一个编程框架。其组成可以细分为如下三方面：
1. Groovy核心语法：包括 Groovy 基本语法、闭包、数据结构、面向对象等；
2. Android DSL（build script block）：Android所特有的，我们按 block 块来处理业务；
3. Gradle API：包含 Project、Task、Setting 等。

Gradle 的语法是以 Groovy 为基础的，同时它还有自己的API。

[Groovy 筑基](https://juejin.im/post/5e97ac34f265da47aa3f6dca '深入探索Gradle自动化构建技术（二、Groovy筑基篇）')

> Groovy 是一门语言，而 DSL 是一种特定领域的配置文件，Gradle 是基于 Groovy 的一种框架工具，而 gradlew 则是 gradle 的一个兼容包装工具。

## 2. 构建过程
Gradle 的构建过程分为三个阶段：**初始化**、**配置**、**执行**。






来源：[深度探索Gradle自动化构建技术](https://juejin.im/post/5e9c46c8518825737f1a7b4c  '作者：jsonchao')

---------





---------
# 三、踩坑记录：
## 1. AndroidManifest
- activity label属性中文设置时报manifest编码不规范错误；----> 通过strings引入


## 2. Build
- as 直接run到手机时会默认添加testOnly=true 属性，这会导致某些机型没法直接安装调试 ----> gradle.properties  中添加android.injected.testOnly=false

---------

***********************

[^1]: https://www.jianshu.com/p/728b2345bf0b  “Jetpack AAC完整解析（一）Lifecycle 完全掌握！"
[^2]:https://www.jianshu.com/p/7ec67fe217fa  “Jetpack AAC完整解析（二）LiveData 完全掌握！”
[^3]: https://www.jianshu.com/p/f96d2673c32e "Jetpack AAC完整解析（三）ViewModel 完全掌握！"
[^4]: https://www.jianshu.com/p/1034acdb513a "Jetpack AAC完整解析（五）DataBinding 架构完善！"


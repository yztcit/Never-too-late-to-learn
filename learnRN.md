> React Native 学习 [学习资料](https://reactnative.cn/docs/getting-started "React Native 中文网")

## 1. 环境搭建

工具：node＋Android SDK + JDK１.８

### 1.1 Node

> 注意 Node 的版本应大于等于 12，而 JDK 的版本必须是 1.8（目前不支持 1.9 及更高版本，注意 1.8 版本官方也直接称 8 版本）。安装完 Node 后建议设置 npm 镜像（淘宝源）以加速后面的过程（或使用科学上网工具）。
>
> **不要使用 cnpm！cnpm 安装的模块路径比较奇怪，packager 不能正常识别！**

```markdown
# 使用nrm工具切换淘宝源
npx nrm use taobao

# 如果之后需要切换回官方源可使用
npx nrm use npm
```

### 1.2 Yarn

**Yarn**是 Facebook 提供的替代 npm 的工具，可以加速 node 模块的下载；

安装完 yarn 之后就可以用 yarn 代替 npm 了，例如用`yarn`代替`npm install`命令，用`yarn add 某第三方库名`代替`npm install 某第三方库名`。

```mark
npm install -g yarn
```

### 1.3 Android Studio # SDK

#### 1.3.1 SDK安装

在 SDK Manager 中选择"SDK Platforms"选项卡，然后在右下角勾选"Show Package Details"。展开`Android 10 (Q)`选项，确保勾选了下面这些组件（重申你必须使用稳定的代理软件，否则可能都看不到这个界面）：

- `Android SDK Platform 29`
- `Intel x86 Atom_64 System Image`（官方模拟器镜像文件，使用非官方模拟器不需要安装此组件

SDK Manager 还可以在 Android Studio 的"Preferences"菜单中找到。具体路径是**Appearance & Behavior** → **System Settings** → **Android SDK**。

点击"SDK Tools"选项卡，勾中右下角的"Show Package Details"。展开"Android SDK Build-Tools"选项，确保选中了 React Native 所必须的`29.0.2`版本。可以同时安装多个其他版本。

然后还是在"SDK Tools"选项卡，点击"NDK (Side by side)"，同样勾中右下角的"Show Package Details"，选择`20.1.5948944`版本进行安装。

#### 1.3.2 Windows环境变量配置

React Native 需要通过环境变量来了解你的 Android SDK 装在什么路径，从而正常进行编译。

打开`控制面板` -> `系统和安全` -> `系统` -> `高级系统设置` -> `高级` -> `环境变量` -> `新建`，创建一个名为`ANDROID_HOME`的环境变量（系统或用户变量均可），指向你的 Android SDK 所在的目录

**把一些工具目录添加到环境变量 Path：**

打开`控制面板` -> `系统和安全` -> `系统` -> `高级系统设置` -> `高级` -> `环境变量`，选中**Path**变量，然后点击**编辑**。点击**新建**然后把这些工具目录路径添加进去：platform-tools、emulator、tools、tools/bin

```mark
%ANDROID_HOME%\platform-tools
%ANDROID_HOME%\emulator
%ANDROID_HOME%\tools
%ANDROID_HOME%\tools\bin
```



## 2.创建第一个RN项目

> 如果之前全局安装过旧的`react-native-cli`命令行工具，使用`npm uninstall -g react-native-cli`卸载掉它以避免一些冲突。

使用 React Native 内建的命令行工具来创建一个名为"XXX"的新项目。这个命令行工具不需要安装，可以直接用 node 自带的`npx`命令来使用：

> **必须要看的注意事项一**：请`不要`单独使用常见的关键字作为项目名（如 class, native, new, package 等等）。请`不要`使用与核心模块同名的项目名（如 react, react-native 等）。请`不要`在目录、文件名中使用中文、空格等特殊符号。

> **必须要看的注意事项二**：请不要在某些权限敏感的目录例如 System32 目录中 init 项目！会有各种权限限制导致不能运行！

> **必须要看的注意事项三**：请`不要`使用一些移植的终端环境，例如`git bash`或`mingw`等等，这些在windows下可能导致找不到环境变量。请使用系统自带的命令行（CMD或powershell）运行。

```mark
npx react-native init proName
```

可以使用`--version`参数（注意是`两`个杠）创建指定版本的项目。注意版本号必须精确到两个小数点。

```mark
npx react-native init proName --version X.XX.X
```

还可以使用`--template`来使用一些社区提供的模板，例如带有`TypeScript`配置的：

```mark
npx react-native init proName --template react-native-template-typescript
```



**编译并运行应用，使用模拟器或真机调试**

确保你先运行了模拟器或者连接了真机，然后在你的项目目录中运行`yarn android`或者`yarn react-native run-android`：

```mark
yarn android
# 或者
yarn react-native run-android
```

此命令会对项目的原生部分进行编译，同时在另外一个命令行中启动`Metro`服务对 js 代码进行实时打包处理（类似 webpack）。`Metro`服务也可以使用`yarn start`命令单独启动。

`npx react-native run-android`只是运行应用的方式之一。你也可以在 Android Studio 中直接运行应用。



## 3.基础知识
> React Native 是一个使用React和应用平台的原生功能来构建 Android 和 iOS 应用的开源框架，使用与 React 组件相同的 API 结构

### 3.1 核心组件与原生组件

React Native中 React 组件通过 JavaScript调用原生视图，运行时为这些组件创建Android和iOS原生试图。

- React Natvie组件是对原生视图的封装，这些平台支持的组件称为**原生组件**；

- React Native 中一组基本、随时可用的组件称为[**核心组件**](https://reactnative.cn/docs/components-and-apis 'API');
- 自定义（原生）组件。

*主要使用的核心组件*：

| ReactNative UI Component | Android View   | iOS View         | Web Analog            | 说明                                                         |
| ------------------------ | -------------- | ---------------- | --------------------- | ------------------------------------------------------------ |
| `<View>`                 | `<ViewGroup>`  | `<UIView>`       | non-scrolling`<div>`  | A container that supports layout with flexbox, style, some touch handling, and accessibility controls |
| `<Text>`                 | `<TextView>`   | `<UITextView>`   | `<p>`                 | Displays, styles, and nests strings of text and even handles touch events |
| `<Image>`                | `<ImageView>`  | `<UIImageView>`  | `<img>`               | Displays different types of images                           |
| `<ScrollView>`           | `<ScrollView>` | `<UIScrollView>` | `<div>`               | A generic scrolling container that can contain multiple components and views |
| `<TextInput>`            | `<EditText>`   | `<UITextField>`  | `<input type="text">` | Allows the user to enter text                                |

### 3.2 React 基础

> 核心概念：`components 组件`、`JSX`、 `props属性`、`state状态`

#### 3.2.1 导入和导出

- 导入：`import xxx from 'xxx'`；
- 导出：`export default xxx`

```rea
import React from 'react';
improt { Text } from 'react-native';

const Cat = () => {
	return <Text>A cat~</Text>;
};

export default Cat;
```

上面return语句是一种简化React元素的写法，这种语法叫做[`JSX`](https://zh-hans.reactjs.org/docs/jsx-in-depth.html#gatsby-focus-wrapper "JSX指南")。

#### 3.2.2 Props 属性

`Props`是“properties”的缩写，可用于定制组件。

```react
import React from 'react';
improt { Text, View } from 'react-native';

const Cat = (props) => {
	return (
		<View>
			<Text>Hello, {props.name}!</Text>
		</View>
	);
}

const Cafe = () => {
	return (
		<View>
			<Cat name="World"/>
			<Cat name="RN"/>
		</View>
	);
}

export default Cafe;
```

> 在指定`style`属性的宽高时所用到的双层括号`{{ }}`。在 JSX 中，引用 JS 值时需要使用`{}`括起来。在你需要传递非字符串值（比如数组或者数字）的时候会经常用到这种写法：`<Cat food={["fish", "kibble"]} /> age={2}`。然而我们在 JS 中定义一个对象时，本来***也\***需要用括号括起来：`{width: 200, height: 200}`。因此要在 JSX 中传递一个 JS 对象值的时候，就必须用到两层括号：`{{width: 200, height: 200}}`。

#### 3.2.3 State 状态

> 按惯例来说，props 用来配置组件的第一次渲染（初始状态）。Use state to keep track of any component data that you expect to change over time. The following example takes place in a cat cafe where two hungry cats are waiting to be fed. Their hunger, which we expect to change over time (unlike their names), is stored as state. To feed the cats, press their buttons—which will update their state.
>
> 可以使用`useState`来记录各种类型的数据： strings, numbers, Booleans, arrays, objects。例如你可以这样来记录猫咪被爱抚的次数：`const [timesPetted, setTimesPetted] = useState(0)`。`useState`实质上做了两件事情：

- 创建一个“状态变量”，并赋予一个初始值；
- 同时创建一个函数用于设置此状态变量的值——`[<取值>, <设值>] = useState(<初始值>)`。

```react
const Cafe = () => {
    return (
        <>
        	<Cat name="Tom"/>
        </>
    );
}
```

> 上面的`<>`和`</>`是一对 JSX 标签，称为[Fragments（片段）](https://zh-hans.reactjs.org/docs/fragments.html)。由于 JSX 的语法要求根元素必须为单个元素，如果我们需要在根节点处并列多个元素，在此前不得不额外套一个没有实际用处的`View`。但有了 Fragment 后就不需要引入额外的容器视图了。

- [精心整理的资源列表](https://github.com/jondot/awesome-react-native)
- [中文资源列表](https://github.com/reactnativecn/react-native-guide)


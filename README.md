# Never-too-late-to-learn(CSDN blogs)
## [Android学习集](https://blog.csdn.net/xiaole0313/article/details/51714223 "如何自学Android, 教大家玩爆Android")

### _* 自定义view（学习资源：扔物线---henCoder）_
### [1-1 绘制基础](https://hencoder.com/ui-1-1/)

    总结：
         ·自定义绘制的方式是重写绘制方法，最常用的是onDraw()方法；
         ·绘制的关键是canvas的使用；
            *绘制类方法：drawXXX() 关键参数paint;
            *辅助类方法：裁剪和几何变换。
         ·使用不同的绘制方法控制遮盖关系，提高UI绘制性能；    
[Demo 1-1](https://github.com/yztcit/PracticeDraw1 "绘制基础")

### [1-2 Paint 详解](http://hencoder.com/ui-1-2/)

>  需要 **牢(要)牢(会)掌(背)握(诵)** _1-1 绘制基础_ 中的方法。Canvas 的 drawXXX 方法配合Paint的几个常用方法即可满足常见的绘制需求，但是Paint功能的完全掌握，可以绘制出更加细致、酷炫的效果

     Paint 的API大致可分为四类：
         ·颜色
         ·效果
         ·drawText()相关
         ·初始化
[Demo 1-2](https://github.com/yztcit/PracticeDraw2 "Paint 详解")

### [1-3 drawText() 文字绘制](https://hencoder.com/ui-1-3/)  

### 1. `canvas` 文字绘制  
#### 1.1. `drawText()` 绘制单行文本  
#### 1.2. `drawTextRun()` 针对一些特殊文字习惯  
#### 1.3. `drawTextOnPath()` 根据指定 Path 绘制文本  
#### 1.4. `StaticLayout` 绘制简单多行文本  
### 2. `paint` 对文字绘制的辅助  
#### 2.1. 设置文字显示类  
##### 2.1.1. `setTextSize()` 设置文字大小  
##### 2.1.2. `setTypeFace()` 设置字体  
##### 2.1.3. `setFakeBoldText()` 伪粗体 （因为它并不是通过选用更高 weight 的字体让文字变粗，而是通过程序在运行时把文字给「描粗」了）  
##### 2.1.4. `setStrikeThruText()` 是否加删除线  
##### 2.1.5. `setUnderlineText()` 是否加下划线  
##### 2.1.6. `setTextSkewX()` 设置文字倾斜度  
##### 2.1.7. `setTextScale()` 设置文字横向缩放，即文字变胖或者变瘦  
##### 2.1.8. `setLetterSpacing()` 设置字符间距  
##### 2.1.9. `setFontFeatureSettings()` 用CSS的 font-feature-settings 方式设置文字样式 文档[这里][font-feature-settings]  
##### 2.1.10. `setTextAlign()` 设置文字对齐方式  
##### 2.1.11. `setTextLocale()` 设置地域  
##### 2.1.12. `setTextHinting()` 是否启动文字微调（随着手机屏幕像素密度提高，几乎用不上了）  
##### 2.1.13. `setElegantTextHeight()` 针对特殊的文字，让「大高个」字体优雅的显示  
##### 2.1.14. `setSubpixelText()` 是否开启次像素级的抗锯齿（ sub-pixel anti-aliasing ）类似setTextHinting，基本没必要使用  
##### 2.1.15. `setLinearText()` 原文 `Helper for setFlags(), setting or clearing the LINEARTEXTFLAG bit`  
#### 2.2. 测量文字尺寸类  
##### 2.2.1. `float getFontSpacing()` 获取推荐的行距，即推荐的两行文字的 baseline 的距离  
##### 2.2.2. `FontMetircs getFontMetrics()` 获取 Paint 的 FontMetrics  
> _getFontSpacing() 的结果并不是通过 FontMetrics 的标准值计算出来的，而是另外计算出来的一个值，它能够做到在两行文字不显得拥挤的前提下缩短行距，以此来得到更好的显示效果。所以如果你要对文字手动换行绘制，多数时候应该选取 getFontSpacing() 来得到行距，不但使用更简单，显示效果也会更好._
##### 2.2.3. `getTextBounds()` 获取文字的显示范围  
##### 2.2.4. `float measureText` 测量文字的宽度并返回  
> _measureText() 测出来的宽度总是比 getTextBounds() 大一点点；前者测量实际占用宽度，后者测量显示范围_
##### 2.2.5. `getTextWidths()` 获取字符串中每个字符的宽度，并把结果填入参数 widths  
##### 2.2.6. `int breakText()` breakText() 是在给出宽度上限的前提下测量文字的宽度。如果文字的宽度超出了上限，那么在临近超限的位置截断文字  
##### 2.2.7. 光标相关  
###### 2.2.7.1. `getRunAdvance()` 对于一段文字，计算出某个字符处光标的 x 坐标  
###### 2.2.7.2. `getOffsetForAdvance` 给出一个位置的像素值，计算出文字中最接近这个位置的字符偏移量（即第几个字符最接近这个坐标）  
##### 2.2.8. `hasGlyph()` 检查指定的字符串中是否是一个单独的字形 (glyph）。最简单的情况是，string 只有一个字母（比如  a）  

> 明确六条线: `baseline`   `top` | `bottom` | `ascent` | `descent` | `leading`  
   
[Demo 1-3](https://github.com/yztcit/PracticeDraw3 "文字绘制")

-------------
[font-feature-settings]:https://www.w3.org/TR/css-fonts-3/#font-feature-settings-prop "CSS 文字样式"

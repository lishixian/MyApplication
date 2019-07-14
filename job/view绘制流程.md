Android View的绘制流程

View 绘制中主要流程分为measure，layout， draw 三个阶段。

> measure ：根据父 view 传递的 MeasureSpec 进行计算大小。
     在measure 方法，核心就是调用onMeasure( ) 进行View的测量。在onMeasure( )里面，获取到最小建议值，如果父类传递过来的模式是MeasureSpec.UNSPECIFIED，也就是父View大小未定的情况下，使用最小建议值，如果是AT_MOST或者EXACTLY模式，则设置父类传递过来的大小。
然后调用setMeasuredDimension 方法进行存储大小。
     MeasureSpec 封装了从父View 传递给到子View的布局需求。每个MeasureSpec代表宽度或高度的要求。每个MeasureSpec都包含了size（大小）和mode（模式）。
我觉得这是measureSpeac 最好的解释了。
后面两句不难理解。MeasureSpec 一个32位二进制的整数型，前面2位代表的是mode，后面30位代表的是size。mode 主要分为3类，分别是

- EXACTLY：父容器已经测量出子View的大小。对应是 View 的LayoutParams的match_parent 或者精确数值。
- AT_MOST：父容器已经限制子view的大小，View 最终大小不可超过这个值。对应是 View 的LayoutParams的wrap_content
- UNSPECIFIED：父容器不对View有任何限制，要多大给多大，这种情况一般用于系统内部，表示一种测量的状态。(这种不怎么常用，下面分析也会直接忽略这种情况)

layout ：根据 measure 子 View 所得到的布局大小和布局参数，将子View放在合适的位置上。
measure（） 方法中我们已经测量出View的大小，根据这些大小，我们接下来就需要确定 View 在父 View 的位置进行排版布局，这就是layout 作用。


draw ：把 View 对象绘制到屏幕上
draw（）作用就是绘制View 的背景，内容，绘制子View,还有前景跟滚动条
第一步：drawBackground(canvas)： 作用就是绘制 View 的背景。
第三步：onDraw(canvas) ：绘制 View 的内容。View 的内容是根据自己需求自己绘制的，所以方法是一个空方法，View的继承类自己复写实现绘制内容。
第三步：dispatchDraw（canvas）：遍历子View进行绘制内容。在 View 里面是一个空实现，ViewGroup 里面才会有实现。在自定义 ViewGroup 一般不用复写这个方法，因为它在里面的实现帮我们实现了子 View 的绘制过程，基本满足需求。
第四步：onDrawForeground(canvas)：对前景色跟滚动条进行绘制。
第五步：drawDefaultFocusHighlight(canvas)：绘制默认焦点高亮



原文：https://blog.csdn.net/sinat_27154507/article/details/79748010




























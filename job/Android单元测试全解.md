onView(withId(R.id.main_text)).perform(
                typeText("Hello MainActivity!"), closeSoftKeyboard());

withId(R.id.main_text)：通过ID找到对应的组件，并将其封装成一个Matcher
onView()：将窗口焦点给某个组件，并返回ViewInteraction实例
perform()：该组件需要执行的任务，传入ViewAction的实例，可以有多个，意味着用户的多种操作
typeText()：输入字符串任务，还有replaceText方法也可以实现类似的效果，不过没有输入动画
closeSoftKeyboard()：关闭软键盘


原文：https://blog.csdn.net/to_perfect/article/details/80738867



# DawnIsland
这是a岛的一个Android客户端，你可以在[原帖](https://adnmb2.com/t/23527306)看到这个客户端的一小部分历史。
我们希望在客户端实现一些特别的小功能，同时让它更好看。
目前的开发者一共有三位，你可以在这个项目的commit中找到其中两位，也欢迎任何人加入。
# 开发者
yanrou、aliaseasy 
### 协作者
かいわたつや、Mike Philemon、Greg Bray、一条小夏烟、琴轩三岁啦、いずまたかし、SHP1cTa、rfm2sE0、耶系少年丶、淡写♂莪的爱、匿名（13个重复）
# 来源
看到设计稿好看就做了
# 开源库
本项目使用了以下依赖（链接以后慢慢写）

okhttp

gson

legacy

glide

jsoup

multitype

photoview

bugly

SmartRefreshLayout

retrofit

xpopup

recyclerview-animators

mmkv-static

utilcodex

DslTablayout

ShadowLayout

transformationlayout

# 技术指导
除了上面直接使用到的库，一些其他的开源代码、文章同样给与了不少帮助
@aliaseasy98 开发者之一，虽然感谢开发者有点奇怪，但是说真的，没有他这个项目不会有现在那么好，我们一起整理了架构、探讨了各种问题的解决方案。
@drakeet 纯纯写作开发者，我们使用了他的multitype，同时他指导了我们如何处理事件分发，以及提供诸多优秀的解决思路，他精益求精的思想也不断影响着我。
@loyea 蓝岛作者，他在项目最初期就鼓励我继续做下去并提供了开发者文档，并且分享了蓝岛的一些功能的实现方式。
@seven233 紫岛作者，黎明岛大量复制粘贴了紫岛的代码，诸多功能的实现细节都参考了紫岛的处理方式，包括但不限于引用处理、涂鸦界面等等
@圣诞树上的星星 他催促我发布了第一个内测版本
@bug触发器 他处理了所有内测邮件，帮助我们整理了所有bug，并详细描述了复现方法，大大提高了我们debug的效率
## 文章
### 键盘解决方案
https://www.notion.so/A-dfca6a884cb0426c9ff616af1791f4c8#85076782446f45b5bc8867131bd496a3
我们参照这个repo编写了键盘高度的处理机制，让键盘的收缩与ui的配合看起来无比丝滑。

### Android10的图片存储
[https://stackoverflow.com/questions/8854359/exception-open-failed-eacces-permission-denied-on-android/13569364#13569364](https://stackoverflow.com/questions/8854359/exception-open-failed-eacces-permission-denied-on-android/13569364#13569364)
虽然看起来很傻，但是这个问题真的困扰了我两天

### 段落级span
https://www.jianshu.com/p/2e3889eaec63
感谢这篇文章的指引，以此为起点，黎明岛支持优秀的行间距和段间距

### API
[https://www.zybuluo.com/ovear/note/151481](https://www.zybuluo.com/ovear/note/151481)
https://github.com/Mfweb/adao_member/blob/0ced0706c0694a4c605e448703a271228ed5e55c/AdaoMemberSystem/app.js
我们依据这两个文档找到了完整的a岛api

### 圆角标签
https://juejin.im/entry/5993f4d96fb9a024865cdf5d
https://www.notion.so/A-dfca6a884cb0426c9ff616af1791f4c8#02a99723257446d79c2b1b96bbde66d1
参照这两篇文章，我们实现了卡片右上角那个好看的标签

### 进度恢复
https://github.com/wenhelinlu/spark/wiki/RecyclerView%E9%AB%98%E7%BA%A7%E5%B8%83%E5%B1%80%E5%AE%9E%E7%8E%B0%E7%9A%84%E6%96%87%E7%AB%A0%E9%98%85%E8%AF%BB%E7%95%8C%E9%9D%A2%E6%81%A2%E5%A4%8D%E9%98%85%E8%AF%BB%E4%BD%8D%E7%BD%AE%E7%9A%84%E5%AE%9E%E7%8E%B0%E6%96%B9%E5%BC%8F%E5%8F%8A%E9%9C%80%E8%A6%81%E6%B3%A8%E6%84%8F%E7%9A%84%E7%82%B9
精准进度恢复（还没做）基于此实现

# 开源协议
还没写，禁止商业使用，可以修改本代码，但使用本项目代码的项目同样需要开源并署名。
# 感谢
首先，感谢参与内测的肥肥（这里是名单（还没写））。
感谢提供图标的匿名肥肥。
感谢整理了所有板块图标的肥肥
当然，还要感谢最开始绘制ui的肥肥，即便黎明岛现在看起来以及和它不太像了，但它确实是促使这个项目开始的原因。
由于匿名版的特性这里无法列出所有有贡献的人，你可以发邮件到rourou.miao@foxmail.com联系我来为自己所作的贡献署名。

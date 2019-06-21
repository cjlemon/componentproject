
组件化设计思路  

通过动态修改apply plugin: 'com.android.application'和
apply plugin: 'com.android.library'实现module的动态切换  

修改apply plugin则通过自定义gralde插件完成。自定义gradle还需要完成
的一些事情：
1. 判断当前module是否host
2. 更新module的依赖
3. 更具当前gradle命令切换module的角色，满足能直接分别调试
4. 切换module的工作代码目录，library 和 host的会有区别（比如host需要一个入口Application而library不需要）

ui的跳转通过一个Router完成，每个依赖module有一个host，每个Activity有一个
相对应的path，通过app的一个固定scheme组成一个uri。Activity的path，和module的
host通过注解的形式注入，通过annotationProcessor自动生成跳转逻辑

library有自己的特色服务，主要是自己需要处理的一些事情，包括提供fragment以及
一些数据交互

每个library的入口生命周期通过继承一个AppLike类，通过asm编译器识别目录并
实现动态在Application中插入字节码，在AppLike中进行一些工作的初始化，比如注册自己的路由到
host的RouterManager中，注入自己的服务

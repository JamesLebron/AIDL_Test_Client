# AIDL_Test_Client
IPC 之AIDL client Demo

## Android IPC AIDL Binder 个人总结

`某些观点纯属个人理解和总结`

* 1.为什么要设计ADIL这门语言?

    设计这门语言的目的是为了实现进程间通信，尤其是在涉及多进程并发情况下的进程间通信

* 2.支持的数据类型?

  * Java中的八种基本数据类型，包括 byte，short，int，long，float，double，boolean，char。
  * String 类型。
  * CharSequence类型。
  * List类型：List中的所有元素必须是AIDL支持的类型之一，或者是一个其他AIDL生成的接口，或者是定义的parcelable。List可以使用泛型。

* 3.关于tag 标签的理解? in out inout?

  * AIDL中的定向 tag 表示了在跨进程通信中数据的流向，其中 in 表示数据只能由客户端流向服务端， out 表示数据只能由服务端流向客户端，而 inout 则表示数据可在服务端与客户端之间双向流通。
其中
  * 数据流向是针对在客户端中的那个传入方法的对象而言的。
  * in 为定向 tag 的话表现为服务端将会接收到一个那个对象的完整数据，但是客户端的那个对象不会因为服务端对传参的修改而发生变动；
  * out 的话表现为服务端将会接收到那个对象的的空对象，但是在服务端对接收到的空对象有任何修改之后客户端将会同步变动；
  * inout 为定向 tag 的情况下，服务端将会接收到客户端传来对象的完整信息，并且客户端将会同步服务端对该对象的任何变动。

* 4.如何正确打包?

    在main目录创建aidl文件夹,然后在gradle 的android 标签下配置以下便签
 最好将java文件和aidl文件同时放在aidl 文件夹下面,这样方便移植 like this :book.java book.aidl
 ```JavaScript
sourceSets {
    main {
        java.srcDirs = ['src/main/java', 'src/main/aidl']
    }
}
```
* 5.manifest 中的service 便签的配置信息
   * android:enabled : 如果为true，则这个service可以被系统实例化，如果为false，则不行。默认为true
   * android:exported : 如果为true，则其他应用的组件也可以调用这个service并且可以与它进行互动，如果为false，则只有与service同一个应用或者相同user ID的应用可以开启或绑定此service。它的默认值取决于service是否有intent filters。如果一个filter都没有，就意味着只有指定了service的准确的类名才能调用，也就是说这个service只能应用内部使用——其他的应用不知道它的类名。这种情况下exported的默认值就为false。反之，只要有了一个filter，就意味着service是考虑到外界使用的情况的，这时exported的默认值就为true
   * android:icon : 一个象征着这个service的icon
   * android:isolatedProcess : 如果设置为true，这个service将运行在一个从系统中其他部分分离出来的特殊进程中，我们只能通过Service API来与它进行交流。默认为false。
   * android:label : 显示给用户的这个service的名字。如果不设置，将会默认使用<application>的label属性。
   * android:name : 这个service的路径名，例如“com.lypeer.demo.MyService”。这个属性是唯一一个必须填的属性。
   * android:permission : 其他组件必须具有所填的权限才能启动这个service。
   * android:process : service运行的进程的name。默认启动的service是运行在主进程中的。
 ```JavaScript
<service
    android:enabled=["true" | "false"] 如果为true，则这个service可以被系统实例化，如果为false，则不行。默认为true
    android:exported=["true" | "false"]
    android:icon="drawable resource"
    android:isolatedProcess=["true" | "false"]
    android:label="string resource"
    android:name="string"
    android:permission="string"
    android:process="string" >
</service>
```


* 6.IntentService 和 HandlerThread的理解

    IntentService是一个service,其内部封装了hanlder,可直接在onHandIntent 里面执行 耗时的异步任务,任务执行完毕会自动调用stopself,
如果多次调用,会把任务加入 messageQueue 消息队列,任务会串行执行 
使用场景:比如下载任务.可多次调用下载,任务会依次执行
HandlerThread是一个线程,只不过这个线程中有自己的looper,messageQueue,可多次通过hanlder(这个hanlder在构造函数默认把looper传进去了)发送任务消息,
handlerThread收到消息之后,就会自己的线程中执行(异步线程),如果没有任务消息,就进行等待,Handler 多次发送消息,一样会把消息加入到messageQueue,任务会
顺序执行,如果想退出HandlerThread,可调用HandlerThread.quit();
使用场景:定期做异步任务,比如有个股票插件,定期去调用接口(查询数据库),查询之后可把数据返回,如果没有查询,该线程就等待,
这样就避免了定期做任务的时候,每次都要去开启一个线程,造成资源的浪费


* 7.延伸问题,如何尽可能的保证Service 不被kill掉?
    http://mp.weixin.qq.com/s/y99YvYrjEc03zK8kMeBraw

* 8.IPC 概念?Android 中哪些方式可进行IPC ?

    IPC是Inter-Process Communication的缩写，含义为进程间通信或者跨进程通信，是指两个进程之间进行数据交换的过程

* 9.哪些方式可以进行IPC? http://blog.csdn.net/u012760183/article/details/51397014

   * Binder,AIDL,Messenger,
   * Bundle(在四大组件的manifest上定义process标签就可以创建新的进程),
   * 文件(shareprefrence)(容易造成数据丢失),
   * Socket(适用于网络数据,不实用),
   * ContentProvider 在数据源访问方面功能强大 支持一对多并发操作,可以理解为约束版的AIDL
   * BrocastReceiver 耗费资源,并且只能传递Bundle 数据

* 10.AIDL(android interface definition language)和Messenger和Binder的区别?这三种方式的使用场景?

    以下全部是自己的理解:
    首先AIDL 是一种语言,翻译过来就是Android 接口定义语言
    再说binder,binder是一种Android 进行IPC 的机制,什么是机制,可以理解为内部(底层)实现,
    IBinder是一个接口,齐内部才是真正操作和沟通底层的东西,
    从应用层来分析,我们始终操作的是IBinder,再说狭义的话,onServiceConnected 之后,始终会返回一个IBinder
    可是IBinder 是一个接口,我们拿到是没有用的,必须要实现这个接口,那怎么实现这种接口呢?

  * a.binder 就实现了IBinder 接口 (怎么做?XXXService  里面用一个BBBClass继承Binder就可以了,然后将其返回,在client 里面将返回的IBinder强转为XXXService.BBBClass,然后再用BBBClass.getService即可调用该Service的方法)

  * b.利用AIDL,借助这个工具，你可以很轻松地实现IPC通信机制，根据需要灵活定义接口(怎么做?首先定义AIDL文件BookManager,编译,在Service里面 实现 这个BookManager.sub(这个sub是自动生成的,其内部是继承了binder),然后将其返回,在cilent 里面 BookManager.Stub.asInterface(Ibinder)即可)

  * c.使用Messenger,Messenger对AIDL做了封装.可以更简单的做IPC.(怎么做?在XXXService 里面新建一个Messenger和Handler,在Messenger的构造函数传入Handler
在返回的时候,直接返回Messenger.getBinder,在Client 里面,绑定之后直接实例化一个Meesenger,将返回的IBinder作为构造函数传入Messenger,只需要Messenger.send(Message)就可以发送消息了.)

    使用场景

  * a.只有当你需要来自不同应用的客户端通过IPC（进程间通信）通信来访问你的服务时，并且想在服务里处理多线程的业务，这时就需要使用AIDL。(多进程,并发)
  * b.如果你不需要同时对几个应用进程IPC操作，你最好通过实现Binder接口来创建你的接口。(单进程)
  * c.如果你仍需要执行IPC操作，但不需要处理多线程，使用Messenger来实现接口即可。(多进程,无并发)



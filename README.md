EasyMessenger
======

一款用于Android平台的基于Binder的进程间通信库，采用`annotationProcessor`生成IPC通信需要的代码。`EasyMessenger`相对于`AIDL`具备如下优势：

- 采用Java声明接口，更方便
- 接口方法支持重载
- 同时支持同步和异步通信

`EasyMessenger`目前支持如下数据类型：

- boolean, byte, char, short, int, long, float, double
- boolean[], byte[], char[], int[], long[], float[], double[]
- String, String[]
- Parcelable, Parcelable[]
- Serializable
- ArrayList(泛型参数只能是简单类型或者Parcelable)
- enum(需要实现parcelable)

## 下载
--------

Client和Server工程均需引用下面的依赖.

```gradle
implementation 'cn.zmy:easymessenger-lib:0.3'
annotationProcessor 'cn.zmy:easymessenger-compiler:0.3'
```

## 开始使用
--------

### Server端
--------

Server实现需要提供给Client功能，例如:

```java
@BinderServer
public class FunctionImpl
{
    //必须是pubic
    //方法名称、参数数量、类型、顺序必须和client的接口一致
    public int add(int num1, int num2)
    {
        return num1 + num2;
    }
}
```

注意，实现类上面标注了`@BinderServer`注解，表示这个类是一个Server的实现。

build项目之后会生成`FunctionImplProvider`类,这是一个`ContentProvider`，其命名规则为：Server实现类的名称 + Provider。

接下来，需要将生成的`ContentProvider`在`AndroidManifest.xml`中予以声明：

```xml
<provider
    android:authorities="your-authorities"
    android:name="xx.xx.FunctionImplProvider"
    android:exported="true"/>
```

请记住`android:authorities`的值，它是Client和Server之间进行通信的钥匙。

### Client端
--------

Client只需要照着Server的实现，声明同样签名的接口方法即可：

```java
@BinderClient
public interface ClientInterface
{
    int add(int num1, int num2);
}
```

其上面标注了`@BinderClient`注解，表示类是一个Client接口。

build项目之后，会生成`ClientInterfaceHelper`类，开发者也正是通过这个生成Helper类来和Server进行IPC通信的。Helper类的命名规则为：Client接口的名称 + Helper。接下来看一下Client如何使用Helper发起IPC请求。

```java
//使用之前需要初始化,需要传递application类型的context
ClientInterfaceHelper.instance.__init(appContext);
    
//同步IPC示例。在IPC完成之前，线程会阻塞
int result = ClientInterfaceHelper.instance.add(1, 2);
    
//异步IPC示例。线程不会阻塞
ClientInterfaceHelper.instance.addAsync(1, 2, new IntCallback()
{
    @Override
    public void onSuccess(int result)
    {
        //调用成功
    }

    @Override
    public void onError(Exception ex)
    {
        //调用失败
    }
});
```

## 限制

1. `EasyMessenger`目前只支持下面的数据类型：

- boolean, byte, char, short, int, long, float, double
- boolean[], byte[], char[], int[], long[], float[], double[]
- String, String[]
- Parcelable, Parcelable[]
- Serializable
- ArrayList(泛型参数只能是简单类型或者Parcelable)
- enum(需要实现parcelable)

2. `ContentProvider`的限制

由于`EasyMessenger`使用`ContentProvider`来获取Server的Binder的代理，而`ContentProvider`会先于`Application#onCreate`初始化，所以对于一些初始化代码可能需要放置于`Application#attachBaseContext`中。

## License
-------

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

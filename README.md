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
- ArrayList
- enum(需要实现parcelable)

下载
--------

```
implementation 'cn.zmy:easymessenger-lib:0.1'
annotationProcessor 'cn.zmy:easymessenger-compilier:0.1'
```

开始使用
--------

### Client

声明接口:

```
@BinderClient
public interface ClientInterface
{
    int add(int num1, int num2);
}
```

build之后，会生成`ClientInterfaceHelper`类，开发者也正是通过这个Helper类进行IPC通信。

```
//使用之前需要初始化
ClientInterfaceHelper.instance.__init(context, 
    new ComponentName("{server_package}", "{server_service_name}"));
    
//同步IPC调用
int result = ClientInterfaceHelper.instance.add(1, 2);
    
//异步IPC调用
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

### Server

实现接口:

```
@BinderServer
public class FunctionImpl
{
    //必须是pubic
    //方法名称、参数数量、类型、顺序必须和client的接口一致
    public int add(int num1, int num2)
    {
        
    }
}
```

build之后会生成`FunctionImplBinder`,将这个Binder和Service绑定：

```java
public class ServerService extends Service
{
    @Override
    public IBinder onBind(Intent intent)
    {
        return new FunctionImplBinder(new FunctionImpl());
    }
}
```

License
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
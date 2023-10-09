## IM通信

即时通信基础框架主要涉及到两个方面，其一为Tcp基础上的自定义协议，其二为常规Http协议。其中自定义协议通信框架为Netty，使用自定义Codec对数据流编解码

自定义协议需要综合考虑网络传输效率、传输安全性、协议拓展性等，因此通常涉及如下几个字段：魔数、版本号、序列化算法、加密算法、指令、控制位、数据类型、正文长度、正文

①魔数：魔数用于第一时间检验是否为自定义协议，例如Java字节码魔数0xCAFEBABE

②版本号：C/S架构中，对于协议更新通常需要版本号来控制

③序列化算法：常规的序列化算法有ProtoBuf与Json，前者在网络传输的效率较高

④加密算法：加密算法用于保障正文内容的安全性，本项目主采用AES加密算法，同时可以进行拓展

⑤指令与控制位：用于连接握手、断开连接、心跳检测、正文发布等等操作，控制位用于辅助

⑥数据类型：少部分情况下，为保证网络包的精简性，数据类型可能会为一至若干字节

⑦正文长度：用于解决Tcp传包产生的粘合切割问题（半包粘包）

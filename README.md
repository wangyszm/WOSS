# WOSS
信息采集

    该项目主要将用户上网生成的纪录收集、整理，通过网络发送并存入数据库。
    项目接口是现成的，个人独立完成其Java代码实现。采用oracle数据库做存储介质。
    收集与整理部分：利用集合Map集合存储读取的用户信息，key值存储用户登录的ip地址，匹配上线与下线记录成功后将用户信息写入pojo类中并存入List集合。
    传输部分：通过内部类实现开启线程，利用socket与ServerSocket发送、接收List集合
    存储部分：利用Oracle的JDBC完成。由于数据量较大，采取批处理方法，每1000条记录存储一次，使得存储时间由原来的20秒缩短至1秒。
    
    BIDR文件为需要读取并整理的目标文件。
    Client实现了读取、整理并发送至Server的流程，其中Gather为收集与整理的具体过程。
    Server实现了接收Client发送的数据并存入数据库的流程，其中DBStroe为JDBC的具体实现。

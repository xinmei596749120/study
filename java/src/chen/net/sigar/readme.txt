Sigar可以获得系统的如下介个方面的信息：
1.操作系统的信息，包括：dataModel、cpuEndian、name、version、arch、machine、description、patchLevel、vendor、vendorVersion、vendorName、vendorCodeName
2.CPU信息，包括：基本信息（vendor、model、mhz、cacheSize）和统计信息（user、sys、idle、nice、wait）
3.内存信息，物理内存和交换内存的总数、使用数、剩余数；RAM的大小
4.进程信息，包括每个进程的内存、CPU占用数、状态、参数、句柄等。
5.文件系统信息，包括名称、容量、剩余数、使用数、分区类型等
6.网络接口信息，包括基本信息和统计信息。
7.网络路由和链接表信息。

1、官网下载sigar（https://sourceforge.net/projects/sigar/download#!/sigar/1.6/hyperic-sigar-1.6.4.zip）
2、解压文件，把文件sigar.jar提取出来，放入到你自己的系统中
3、把sigar-amd64-winnt.dll、sigar-x86-winnt.dll、sigar-x86-winnt.lib文件放入你的jdk文件夹的bin目录下（或放入你的path环境变量里配置的任何一个地址下）
4、完成
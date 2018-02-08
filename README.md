# P2P-file-transfer
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;使用P2P的思想，在客户端之间直接建立TCP连接。让客户端不仅是Bit的消费者，也是Bit的生产者，减少服务器的传输负担，从而提高传输效率。
<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在P2P体系下，有著名文件分发协议BitTorrent，它的基本思想是：BT客户端向tracker发送文件下载请求，tracker返回多个在进行该文件下载/上传的BT客户端IP，
该BT客户端根据一定的策略和这些IP建立TCP连接并相互交换文件片段，可以说，客户端承担了更大的责任。
<hr>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;该项目可以说是一个按照P2P的思想重新实现的文件分发，不同于BT的策略。这里的基本思想是：客户端向服务器发送下载请求，并打开自己的文件接收服务器
服务器接收到请求后根据自己所持有的文件列表，按照一定的策略选取出最佳的几个客户端，并向这几个客户端发送“文件发送”命令和接收方IP，然后就是文件的单方面传输。
可以说，确实按照P2P思想让客户端直接通信。但仅仅是文件的传输，客户端之间还是不进行交流，而发送者的调度由服务器决定，而非客户端。

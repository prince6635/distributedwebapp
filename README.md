## 架构演进：
* 单一应用架构 => 垂直应用架构
* 垂直 => 分布式 (SOA)

分布式系统所依赖的基础设施包括服务框架、消息中间件、数据访问中间件、配置中心、分布式缓存系统、持久化存储(关系数据库、nosql数据库)、搜索引擎、CDN网络、负载均衡系统、运维自动化系统、硬件虚拟化及镜像管理系统、分布式文件系统、日志收集系统、监控系统、离线计算、实时计算、数据仓库等等。

Source code: <https://github.com/prince6635/distributedwebapp>

### SOA
* RPC － RPC的实现包括客户端和服务端，即服务的调用方以及服务的提供方，服务调用方发送RPC请求到服务提供方，服务提供方根据调用方提供的参数执行请求方法，将执行结果返回给调用方，一次RPC调用完成。
	* TCP协议实现：高性能，高并发
	* HTTP协议：跨平台
	* Examples:
		* 01_ImplementProtocolBySocket: understand how socket works
		* 01_SerializationMethods: different serilization/deserialization methods
		* 01_ImplementRPCByTCP: use TCP to implement RPC
		* 01_ImplementRPCByHTTP: use HTTP to implement RPC
		* 01_ImplementRPCByRESTfulHTTP: use RESTful HTTP to implement RPC
* 服务路由 － service discovery
	* 服务负载均衡
		* 单个服务的负载均衡：ngix，LVS
		* 多个服务点负载均衡 ＝> 服务配置中心
			* provider去服务配置中心注册自己的服务
			* consumer去服务配置中心查询自己要用的服务
			* consumer再通过RPC去调用provider的服务
		* Zookeeper让服务配置更简单，hadoop下的子项目，是针对大型分布系统的可靠的协调系统
		![zoopkeeper1](Images/zookeeper1.jpg)
		=> 借助Zookeeper实现负载均衡：
		![zoopkeeper2](Images/zookeeper2.jpg)
		* 负载均衡算法：
			* 轮询－Round Robin
			* 随即－Random
			* 源地址哈希法－Hash：适合有状态的服务调用
			* 加权轮询－Weight Round Robin
			* 加权随机－Weight Random
			* 最小连接数－Least Connections
		* 多机房负载均衡：通过服务路由配置，尽可能防止跨机房的服务调用 (consumer->provider)
	* 服务升级：尤其是需要修改参数列表的情况，
		* A, 对使用原来方法的代码都进行修改,然后和服务端一起发布。这仅仅从理论上来说可行,因为依赖关系太多。
		* B, 通过版本号来解决,这是比较常用的方式,使用老方法的系统继续调用原来版本的服务,而需要使用新方法的系统则使用新版服务。
		* C, 在设计方法上考虑参数的扩展性,这是一个可行的方式,但是不太好,因为参数列表可扩展一般意味着采用类似map的方式来传递参数,这样使得参数校验会比较麻烦。
	* 服务管理
		* 服务的管理(服务上线下线、服务路由、服务限流和降级、服务归组、机房规则、服务授权)
			* 稳定性管理：
				* 依赖管理－用日志分析统计各服务间的依赖关系
				* 服务分级－比如下单服务优先级一定高于论坛浏览服务，有时候要弃车保帅
				* 优雅降级－避免故障的传递将一个非核心链路的问题扩大,引起核心节点故障,最终形成多米诺骨牌效应,使得整个集群都不能对外提供服务。
				* 开关－用全局变量控制在流量高峰期关闭非核心服务对同一个service provider的调用来保证核心服务正常
		* 服务相关信息的收集和展现(服务基本信息、服务质量的好坏、服务的容量和水位、服务依赖和被依赖、服务的机房分布、服务调用统计、服务提供的方法和参数、服务负责人、服务的统计报表、服务运行状态监视)
	* Examples: 02_ServiceRouteAndLoadBalance
* SOA架构所面临问题
	* 就是如何解决集成服务应用普遍存在的一致性问题,举例来说,同时调用多个服务,当其中一个服务调用失败时,其他服务已经处理执行的结果该如何进行回滚,这在单机本地调用的情况下使用事务比较好处理,而分布式环境下的事 务将问题复杂化,并且性能开销难以承受,因此,只有在极端情况下才会考虑强一致性,一般情况下更多的关注最终一致性。
	* 另外一个就是安全问题,面向企业的平台级的SOA架构,需要对参数传递、响应内容以及各种用户私有信息的交互,有着更严格的且特殊的安全需求,如何构建一个安全的SOA架构体系,也给技术人员带来了很大的挑战。
* SOA常见的攻击手段
	* XSS－跨站脚本攻击，依赖于受信任的用户
	* CSRF-跨站请求伪造，伪装受信任的用户->见例子
		* cookie设置为HttpOnly: CSRF攻击很大程度上是利用了浏览器的cookie,"HttpOnly"属性可以避免通过程序(如JavascriptS脚本、Applet等)读取到cookie信息。
		* 增加token: 不要把用户验证信息都存在于cookie中,在请求中放入攻击者所不 能伪造的信息,并且该信息不存在于cookie之中。以参数的形式加入一个随机产生的token,并在服务端进行token校验,如果请求中没有token或者token内容不正确.
		* 通过Referer识别: 根据HTTP协议,在HTTP头中有一个字段叫Referer,它记录了该HTTP请求的来源地址。在通常 情况下,访问一个安全受限页面的请求都来自于同一个网站。
	* SQL注入攻击, 防御方法：
		* 使用预编译语句: 预编译语句PreparedStatement是java.sql中的一个接口,继承自Statement接口。
		* 使用ORM框架: 防止SQL注入的关键在于对一些关键字符进行转义,而常见的一些ORM框架,如 ibatis、hibernate等.
		* 避免密码明文存放: 对存储的密码进行单向Hash,如使用MD5对密码进行摘要,而非直接存储明文密码.
		* 处理好相应的异常: 后台的系统异常,很可能包含了一些如服务器版本、数据库版本、编程语言等等的信息,甚至 是数据库连接的地址及用户名密码,所以一定要重定向到相应的错误处理页面,而不是任由其直接输出到页面上。
	* 文件上传漏洞: 上传恶意的可执行文件或脚本->很多类型的文件,起始的几个字节内容是固定的 (Magic number)
	* DDoS攻击: Distributed Denial of Service,即分布式拒绝服务攻击,目前最为强大、最难以防御.
		* 最基本的DoS攻击就是利用合理的客户端请求来占用过多的服务器资源,从而使合法用户无法得到服务器的响应,只对CPU速度、内存或者网络带宽等等各项性能指标不高的情况下,它的效果是明显的。
		* DDos在攻击开始前,攻击者会提前控制大量的用户计算机,称之为“肉鸡”,并通过指令使大量的肉鸡在同一时刻对某个 主机进行访问,从而达到瘫痪目标主机的目的。
		* 常见的攻击手段：
			* 依赖蛮力的ICMP Flood、UDP Flood等等,随着硬件性能的 提升,需要的机器规模越来越大,组织大规模的攻击越来越困难,现在已经不常见
			* 还有就是依赖协议特征以及具体的软件漏洞进行的攻击,如Slowloris攻击,Hash碰撞攻击等 等,这类攻击主要利用协议以及软件漏洞发起攻击,需要在特定环境下才会出现
			* 更多的攻击者采用的是前面两种的混合方式,即利用了协议、系统的缺陷,又具备了海量的流量, 如SYN Flood(利用TCP对三次握手)、DNS Query Flood(向被攻击的服务器发送海量的域名解析请求)等等。
			* CC(Challenge Collapsar),基于应用层HTTP协议,也被称为HTTP Flood。攻击者通过控制的大量“肉鸡”或者利用从互联网上搜寻的大量匿名的HTTP代理,模拟正常用户给网站发起请求直到该网站拒绝服务为止。这些精心构造的HTTP请求往往有意避开CDN等缓存,进行多次DB查询操作或者是一次请求返回大量的数据,加速系统资源消耗,从而拖垮后端的业务处理系统,甚至连相关存储以及日志收集系统也无法幸免。
	* Examples:
		* 03_ValidateFileTypesForAttacks
* SOA安全算法
	* 数字摘要: 也称为消息摘要,它是一个唯一对应一个消息或文本的固定长度的值,它由一个单向 Hash函数对消息进行计算而产生。用于检查消息是否被篡改过。
		* MD5: MD5即Message Digest Algorithm 5(信息摘要算法5) - 128位
		* SHA: SHA的全称是Secure Hash Algorithm,即安全散列算法。SHA-1是基于MD4算法的,现在已成为 公认的最安全的散列算法之一,并被广泛使用。160位，故比MD5慢。
	* 对称加密(symmetrical encryption):主要是用来验证传输内容是否又被篡改!!!
	数据发送方将明文(原始数据)和加密密钥一起经过特殊加密算法处理后,生成复杂的加密密文进行发送,数据接收方收 到密文后,若想读取原文,则需要使用加密使用的密钥及相同算法的逆算法对加密的密文进行解密,才能使其恢复成可读明文。在对称加密算法中,使用的密钥只有一个,发送和接收双方 都使用这个密钥对数据进行加密和解密,这就要求加密和解密方事先都必须知道加密的密钥。
		* DES: 64位，容易被暴力破解->3DES
		* AES: 被广泛应用的对称加密算法，128,192,256位
	* 非对称加密(asymmetrical encryption):主要用来验证请求发送和接收方两者的身份信息!!!
	需要两个密钥,一个称为公开密钥(public key), 即公钥,另一个称为私有密钥(private key),即私钥。公钥与私钥需要配对使用,因为加密和解密使用的是两个不同的密钥,所以这种算法称为 非对称加密算法。 非对称加密算法实现机密信息交换的基本过程是:甲方生成一对密钥并将其中的一把作为公钥 向其它人公开,得到该公钥的乙方使用该密钥对机密信息进行加密后再发送给甲方,甲方再使用自己保存的另一把专用密钥,即私钥,对加密后的信息进行解密。
		* RSA, ![RSA](Images/RSA_algorithm.jpg)
	* Examples:
		* 03_EncryptionAlgorithms
* SOA安全认证
	* 数字签名：对非对称加密技术与数字摘要技术的综合运用,指的是将通信内容的摘要信息使用
发送者的私钥进行加密,然后将密文与原文一起传输给信息的接收者,接收者通过发送者的公
钥解密被加密的摘要信息,然后使用与发送者相同的摘要算法,对接收到的内容采用相同的方
式产生摘要串,与解密的摘要串进行对比,如果相同,则说明接收到的内容是完整的,在传输
过程中没有受到第三方篡改,否则则说明通信内容已被第三方修改。
发送者加密和接受者验证的过程：![Digital Signature](Images/DigitalSignature.jpg)
	* 数字证书(Digital Certificate): 但需要考虑性能上的成本和需要申请CA证书
		* 验证过程：![Digital Certificate](Images/DigitalCertificate.jpg)
		* 证书管理：
			* Keytool
			* OpenSSL
		* 为什么需要认证：![Why Digital Certificate](Images/WhyDigitalCertificate.jpg)
	* 摘要认证：Digest Authentication
		* 对于非敏感数据更多的关注其真实性和准确性,因此,主要是在通信过程中保障数据不被篡改。
		* 但由于服务端与客户端采用 的是相同的secret,一旦secret泄露,通信的安全则无法保障。
	* 签名认证：Signature Authentication
		* 结合参数的排序和私钥，![Signature Authentication in Server](Images/SignatureAuthentication_Server.jpg)
		![Signature Authentication in Client](Images/SignatureAuthentication_Client.jpg)
	* Examples:
		* 04_DigitalCertificate
		* 04_DigestAndSignatureAuthentication
* HTTPS: ![HTTPS Protocol](Images/HTTPSProtocol.jpg)
SSL协议数据传输是通过对称加密算法来实现加密的,密钥为双方约定好的加密密钥。
* OAuth: ![OAuth Protocol](Images/OAuthProtocol.jpg)
	* 平台厂商：比如facebook，微信，微博，淘宝；第三方应用：比如豆瓣，知乎；当用户在豆瓣上发表文章后想获得微信的授权转载到朋友圈，这时候微信就可以提供一个基于OAuth和SOA的授权API让豆瓣来获得用户的授权，而且不需要让用户在豆瓣提供自己微信的用户名和密码就可以访问用户微信的data；

### 分布式系统
* 分布式缓存: ![Distributed Cache](Images/DistributedCache.jpg)
	* memcache: 就是一张巨大的hash table，用LRU算法淘汰过多的数据
		* 分布式memcache: ![memcache](Images/memcache.jpg)
		* 防止memcache的“雪崩效应” -> 一致性哈希算法: ![Consistent Hash](Images/ConsistentHash.jpg), 比如把2^32分成4份，每份2^8，则对0到2^8-1到所有请求都落在node1上，依次类推。
* 分布式session: 传统的应用服务器,如tomcat、jboss等等,其自身所实现的session管理大部分都是基于单机的,对于大型分布式网站来说,支撑其业务的远远不止是一台服务器,而是一个分布式集 群,请求在不同服务器之间跳转,需要保持服务器之间的session同步。传统网站一般通过将 一部分数据存储在cookie中,来规避分布式环境下session的操作,这样做弊端很多,一方面 cookie的安全性一直广为诟病,并且,cookie存储数据的大小是有限制的,随着移动互联网 的发展,很多情况下还得兼顾移动端的session需求,使得采用cookie来进行session同步的方式弊端更为凸显。分布式session正是在这种情况下应运而生的。![Distributed Session](Images/DistributedSession.jpg)
	* 业务强依赖缓存,缓存需做到容灾:
		1. 双机房互相备份
		2. 数据复制多份,单台缓存失效,集群间能够自动复制和备份
		3. 数据库留有余量
		4. 万兆网卡
* 持久化存储 (RDBMS & NoSQL):
	* MySQL
		* 业务拆分: ![mysql1](Images/mysql1.jpg)
		* 数据复制: ![mysql2](Images/mysql2.jpg)
		* 读写分离: ![mysql3](Images/mysql3.jpg)
		* dual-master架构: ![mysql4](Images/mysql4.jpg)
		stand by master也要跟master的data保持一致。MySQL会通过binary log纪录当前copy的server id来避免循环copy。
			* 如果需要对当前master维护，则首先停止写入，变成只读，修改配置文件以防重启后失效，然后等待stand by master把当前master的所有data同步后就开启stand by master的write。
			* 如果当前master突然宕机，此时stand by master宕data不一定以及同步了，就要copy当前master上的binary log，然后对比stand by master的log，知道data同步后才开启write。
		* 分库分表 ![mysql5](Images/mysql5.jpg)
		假设将原来的单库单表order拆分成256个库,每个库包含1024个表,那么,按照前面所提到的路由策略,对于userid=262145的访问, 路由的计算过程如下:
		```
		中间变量=262145%(256*1024)=1
		库=取整(1/1024)=0
		表=1%1024=1 这意味着,对于userid=262145的订单记录的查询和修改,将被路由到第0个库的第1个表中执行。
		```
		* 分库分表带来的限制
			1. 条件查询、分页查询受到限制,查询必须带上分库分表所带上的id
			2. 事务可能跨多个库,数据一致性无法通过本地事务实现,无法使用外键
			3. 分库分表规则确定以后,扩展变更规则需要迁移数据 - 相当于给一个高速上行驶的汽车换轮胎
		* Examples: None
	* HBase - NoSQL, ![hbase](Images/hbase.jpg)
	<http://www.uml.org.cn/sjjm/201212141.asp>, mysql和hbase表结构的区别：
	![hbase example](Images/hbase_example.jpg)
		* 性能取舍：MySQL
			1. 主从同步,master与slave之间数据存在延时同步,一致性由强一致性变为最终一致性
			2. 分库分表,牺牲了查询的灵活性,必须带上分库分表所依赖的关键属性,牺牲了诸如外键、多表关联查询等RDBMS的传统特性.
			3. 系统扩展复杂,数据库库、表路由规则的变更,数据迁移的成本高.
			4. 业务拆分后,原先一个库中的表,可能被拆分到多个库中,使得原本简单的事务控制发展为分布式事务.
		* 架构: ![hbase architecture](Images/hbase_architecture.jpg)
		* rowkey的设计: 比如设计订单表，假设需要从卖家维度来查看某商品已售出的订单,并且按照下单时间区间来进行查询,那么,订单表可以这样设计: rowkey:seller_id + auction_id + create_time列族:order_info(auction_title,price,user_id)使用卖家id+商品id+交易时间作为表的rowkey,列族为order,该列族包含三 列,即商品标题、价格、购买者id,由于HBase的行是按照rowkey来排序的, 这样,通过rowkey进行范围查询,可以缩小scan的范围。
		![hbase rowkey](Images/hbase_rowkey.jpg)
		假设需要从购买者维度来进行订单数据的查询,展现用户购买过的商品,并且按照购买时间进行查询分页,那么,rowkey的设计又不同了: rowkey:user_id + create_time列族:order_info(auction_id,auction_title,price,seller_id)这样,通过买家id+交易时间区间,便能够查询出用户在某个时间范围内购买 所产生的订单。
		* rowkey也可以有二级索义
		* hbase的使用场景和限制: 与传统的关系型数据库相比,HBase有更好的伸缩能力,更适合于海量数据的存储和处理,并且,由于多个region server的存在,使得HBase能够 多个节点同时写入,显著提高了写入性能,并且是可扩展的。但是,HBase本身能够支持的查询维度有限,难以支持复杂查询,如group by、order by、join等等,这些特点使得它的应用场景受到了限制。当然,这也并非是不可弥补的硬伤,通过后面章节所介绍的搜索引擎,构建索引,可以在一定程度上 解决HBase复杂条件组合查询的问题。
	* Redis - 既可以做cache，又可以做key－value store来存persistent data
		* redis是一个高性能的key-value数据库,与其他很多key-value数据库不同之处在于,redis不仅支持简单的键值对类型的存储,它还支持其他的一系列丰富的数据存储结构,包括strings、hashs、lists、sets、sorted sets等等,并在这些数据结构类型上定义了一套强大的API。通过定义不同的存储结构,redis可以很轻易完成很多其他key-value数据难以完成的任务,如排序、去重等等。
		* 使用场景：相较于传统的关系型数据库,redis有更好的读写吞吐能力,能够支撑更高的并发数,而相较于其他的key-value类型的数据库,redis能够提供更为丰富的数据类型的支持,能够更灵活的满足业务需求。redis能够高效率的实现诸如排序取topN、访问计数器、队列系统、数据排重等等业务需求,并且,通过将服务器设置为cache-only,还能够提供高性能的缓存服务,相较于memcache来说,在性能差别不大的情况下,它能够支持更为丰富的数据类型。
	* 消息系统: 在分布式系统中,消息系统的应用十分广泛,消息可以作为应用间通信的一种方式,消息被保存在队列中,直到被接收者取出,由于消息发送者不需要同步等待消息接收者的响应,消息的异步接收降低了系统集成的耦合度,提升了分布式系统协作的效率,使得系统能够更快的响应用户,提供更高的吞吐,当系统处于峰值压力时,分布式消息队列还能够作为缓冲,削峰填谷,缓解集群的压力,避免整个系统被压垮。开源的消息系统有很多,包括apache的ActiveMQ,apache的 Kafka,RabbitMQ,memcacheQ等等。
		* JMS: J2EE提出消息服务规范,它是一组java应用程序接口,它提供消息的创建、消息的发送、消息接收、消息读取等等一系列服务。
			* JMS消息模型
				* P2P (点对点): 基于queue(队列)的,消息生产者发送消息到队列,消息消费者从队列中接收消息,队列的存在,使得消息的异步传输称为可能,P2P模型在点对点的情况下进行消息传递时采用。
				![p2p](Images/p2p.jpg)
				* pub/sub (发布/订阅): 发布/订阅模型定义了如何向一个内容节点发布和订阅消息,这个内容节点称为topic(主题),主题可以认为是消息传递的中介,消息发布者将消息发布到某个主题,而消息订阅者则从主题订阅消息,主题使得消息的订阅者与消息的发布者互相保持独立,不需要进行接触即可保证消息的传递,发布/订阅模型在消息的一对多广播时采用。
				![pub_sub](Images/pub_sub.jpg)
			* JMS模型的限制: 我们需要结合这两种的优点。
			![jms_limitation](Images/jms_limitation.jpg)
			* 我们需要的消息模型 (对JMS模型限制的改进)：即应用1和应用2得到相同的消息，如果它们订阅的一样，同时在应用1的集群里面每个机器的消息是不重复的。
			![message_model](Images/message_model.jpg)
				* 大型分布式系统对于消息系统的需求:
					1. 消息发送和消息接收都是集群
					2. 同一个消息的接收方可能有多台机器甚至是多个集群来进行消息的处理
					3. 不同集群对于同一条消息的处理都不能相互干扰
		* 持久订阅和非持久订阅, ![message_subscribtion](Images/message_subscribtion.jpg)
		* 如何保障消息的一致性
			* 不靠谱的做法
				```
				function(){
					//业务操作
					//调用服务,将数据写入数据库
					//发送消息
				}
				1.业务操作在前,发送消息在后,如果业务失败还行,如果业务成功,此时系统宕机,消息则发送失败 2.如果业务成功,应用也正常,此时消息系统宕机,消息没收到,也会导致消息收不到
				```

				```
				function(){
					//发送消息
					//业务操作
					//调用服务或者写数据库
				}
				这种方式更不靠谱,业务如果失败,消息却已经发出。
				```
			* 正确做法：增加“消息中间件＋消息存储”来给消息一个状态，比如业务操作时宕机，就可以让消息中间价去轮询消息发布者看发生了什么，从而防止数据的丢失。
			![message_consistency1](Images/message_consistency1.jpg)
			![message_consistency2](Images/message_consistency2.jpg)
	* Examples:
		* 06_HBase
		* 06_Redis
* 垂直化搜索引擎: 跟google和baidu等互联网搜索引擎存在着一些差别,垂直化的搜索引擎主要针对企业内部的自有数据的检索,而不像google和baidu等搜索引擎平台,采用网络爬虫对全网 数据进行抓取,从而建立索引并提供给用户进行检索。在分布式系统中,垂直化的搜索引擎是一个非常重要的角色,它即能够满足用户对于全文检索、模糊匹配的需求,解决数据库like查询效率低下的问题,又能够解决分布式环境下,由于采用分库分表、或者是使用NOSQL数据库,导致无法进行多表关联或者进行复杂查询的问题。
	* Lucene
		* 倒排索引(inverted index): 也称为反向索引,几乎所有的搜索引擎都会使用到倒排索引的数据结构,它将文档中的词作为关键字,建立词与文档的映射关系,通过对倒排索引的检索,可以根据词快 速获取包含这个词的文档列表。
		* 分词: 就是将句子或者段落进行切割,从中提取出包含固定语义的词。对于英语来说,语言的基本单位就是单词,因此,分词特别容易,只需要根据空格/符号/段落进行分割,并且排除停止词(stop word),提取词干,即可完成,但是对于中文来说就不那么容易了,中文是以字为最小单位,多个字连在一起才能构成一个表达具体含义的词,中文的句子和段落都有一个明显的标点符号分割, 唯独词没有一个形式上的分割符,固需要一个合适的中文分词工具,以便建立倒排索引 (比如“几个工厂的工人”可以分词“几个工厂的“，”工人”或者”几个”，“工厂的工人”)。提取词干是西方语言特有的处理步骤,比如英文中的单词有单复数的变形,-ing和-ed的变形,但 是在搜索引擎中,应该当做同一个词。
		* 分词器(analyzer): 文档在被索引之前,需要经过分词器处理,以提取关键的语义单元,建立索引,并剔除无用的信息,如停止词等,以提高查询的准确性。中文分词与西文分词区别在于,中文对于词的提取更为复杂。 常用的中文分词器包括一元分词、二元分词、词库分词等等。
		* 停止词(stop word): 在英语中包含了a、the、and这样使用频率很高的词,如果这些词都被建到索引中进行索引的话,搜索引擎就没有任何意义了,因为几乎所有的文档都会包含这些词,对于中文来说也是如此,中文里面也有一些出现频率很高的词,如“在”、“这”、“了”、“于”等等, 这些词没有具体含义,区分度低,搜索引擎对这些词进行索引没有任何意义,因此,停止词需要被忽略掉。
		* 排序: 当输入一个关键字进行搜索时,可能会命中许多文档,搜索引擎给用户的价值就是快速的找到需要的文档,因此,需要将相关度更大的内容排在前面,以便用户能够更快的筛选出有价值的内容,这时,就需要有适当的排序算法。一般来说,命中标题的文档将比命中内容的文档有更高的相关性,命中多次的文档比命中一次的文档有更高的相关性,商业化的搜索引擎的排序规则十分复杂,搜索结果的排序融入了广告、竞价排名等因素,由于牵涉的利益广泛,一般属于核心的商业机密。
		* 其他概念：词(term)，域(field，类似数据库表中的column)，文档(document，类似数据库中的表，如存老师信息的表就是一个document，而其中的name，age等就是fields)，查询(一系列term的条件组合)
		* 建立索引
		![lucene_buildindex](Images/lucene_buildindex.jpg)
		* 索引搜索过程
		![lucene_search](Images/lucene_search.jpg)
		* 索引优化: lucene的索引是由段(segment)组成的,每个段可能又包含多个索引文件,每个段包含了一个或者多个Document,段结构使得lucene可以很好的支持增量索引,新增的Document将被添加到新的索引段当中。但是,当越来越多的段被添加到索引当中,索引文件也就越来越多,一般来说,操作系统对于进程打开的文件句柄数是有限的,当一个进程打开太多的文件时,会抛出too many open files异常,并且,执行搜索任务时,lucene必须分别搜索每个段,然后将各个段的搜索结果合并,这样,查询的性能就会降低。=> 因为前面说到我们产生索引碎片,而这些索引碎片即使进行了碎片合并而减少碎片数,但是一旦当碎片达到一定大小后就不适合继续进行合并,否则合并代价很大,所以我们无法避免的会因为碎片问题而导致更新实时性和查询QPS性能损耗问题。我们的解决的办法就是通过一段时间对具体业务全部源数据进行一次构建全量索引DUMP工作,用构建好的新的全量主索引去替换原来老的主索引和磁盘索引,从而让实时更新、搜索服务性能恢复到最佳。
		* Lucene的IndexReader和IndexWriter都具有隔离性 => Lucene或其他搜索引擎实时性和查询性能的矛盾。
			* IndexReader.open后才能看到之前对索引的修改
			* IndexWriter只有在Commit后，在用IndexReader.open打开才能看到修改的内容
		* 实时更新:
		 	* 引入原因
			 	* 倒排索引是有一定的格式的,而这个格式一旦写入是非常难以改变,那么如何能够增量建索引呢?Lucene使用段这个概念解决了这个问题,对于每个已经生成的段,其倒排索引结构不会再改变,而增量添加的文档添加到新的段 中,段之间在一定的时刻进行合并,从而形成新的倒排索引结构。
				* 然而也正因为Lucene的索引读写的隔离性,使得Lucene的索引不够实时,如果想Lucene实时,则必须在新添文档后对IndexWriter进行commit,在搜索的时候IndexReader需要重新的打开,然而当索引在硬盘上的时候,尤其是索引非常大的时候,IndexWriter的commit操作和IndexReader的open操作都是非常慢的,根本达不到实时性的需要。
			* 规则：新增索义一定会被先加到内存索义中，达到一定大小在加入合并中索义列表，最后合并到硬盘
			![lucene_realtime1](Images/lucene_realtime1.jpg)
			![lucene_realtime2](Images/lucene_realtime2.jpg)
			![lucene_realtime3](Images/lucene_realtime3.jpg)
			* 这步一定要是原子性的，要加锁，要么每步都成功，最后commit，要么就要统统rollback，而且这个时候用户是不能search的。
			![lucene_realtime4](Images/lucene_realtime4.jpg)
			![lucene_realtime5](Images/lucene_realtime5.jpg)
		* 搜索引擎的分布式
			* dump就是从data store拉数据后分词，建立索引存到dump server中
			* merger server就是对结果进行merge处理然后分发给client
			![lucene_distributed1](Images/lucene_distributed1.jpg)
			=>
			![lucene_distributed2](Images/lucene_distributed2.jpg)
	* Solr: 好处就是可以用xml定义schema来存索引或别的配置；同时可以用类sql来查询，不像Lucence这两项都是硬编码实现。
	![lucene_solr](Images/lucene_solr.jpg)
	* Examples:
		* 07_Lucene, 目前在维护的开源中文分词有IKAAnalyzer

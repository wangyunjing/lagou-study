com.lagou.pojo.SqlCommandType: 用于标识Sql属于那种类型，不同类型调用SqlSession的不同方法

com.lagou.pojo.MapperProxyFactory: 代理对象工厂，不同的SqlSession生成不同的代理对象，所以需要一个工厂

com.lagou.pojo.MapperProxy: 代理对象，用于透传SqlSession给MapperMethod使用

com.lagou.pojo.MapperMethod: 代理真正执行类，用于缓存一些判断数据


# 测试代码
com.lagou.test.IPersistenceTest.insert

com.lagou.test.IPersistenceTest.update

com.lagou.test.IPersistenceTest.delete
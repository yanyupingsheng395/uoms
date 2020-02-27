1. spring boot版本升级
   升级spring boot版本 (2.0.4.RELEASE ->2.1.12.RELEASE)
   解决spring-data-redis低版本API的setIfAbsent不支持设置key过期时间的缺陷。
   
   升级此版本需要在yml中增加:
   spring.main. allow-bean-definition-overriding=true
   此参数在低版本中默认值为true,在2.1.12中默认值修改为false
   
2. 活动运营
   计划执行，文案改为在java中完成变量的替换。   
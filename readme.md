#uoms
用户运营系统 user operation manager system

# 使用到的java相关组件

| java组件名称 | 功能说明 |  版本 | 
| ------ | ------ | ------ |
| spring-boot | spring boot | 2.0.4.RELEASE |
| mybatis-spring-boot-starter | mybatis | 1.3.2 |
| mapper-spring-boot-starter | mybatis通用mapper插件 | 1.1.7 |
| ~~pagehelper~~ | ~~通用分页插件~~ | ~~1.2.5~~ |
| shiro-spring | shiro和spring集成的工具包 | 1.4.0 |
| shiro-redis | shiro存储信息到redis的工具包 | 2.8.24 |
| shiro-thymeleaf | shiro和thymeleaf集成 | 2.0.0 |
| fastjson | json的序列化和反序列化 | 1.2.31 |
| javacsv | java操作CSV文件的工具包 | 2.0 |
| druid | 数据源连接池 | 1.1.10 |
| reflections | 反射工具包 | 0.9.11 |
| guava | Java增强工具包 | 25.1-jre |
| jasypt | 加解密算法工具包 | 2.1.0 |
| dozer | 对象深复制的工具包 | 5.5.1 |

#使用到的js组件

| JS组件名称 | 功能说明 |  版本 | 
| ------ | ------ | ------ |
| jquery.min.js | jquery | 2.1.4 |
| bootstrap.min.js | bootstrap | 3.3.7 |
| perfect-scrollbar.min.js | 滚动条插件 | 1.4.0 |
| bootstrap-notify.min.js | bootstrap notify | 3.1.3 |
| common.js | 封装一些公共组件 | # |
| bootstrap-datepicker | bootstrap 日期插件 | 1.6.1 |
| bootstrap-datetimepicker | bootstrap 日期插件 | 4.17.37 |
| bootstrap-select | bootstrap select | 1.13.7 |
| bootstrap-table | bootstrap table| 1.14.2 |
| bootstrap-switch | bootstrap switch | 3.3.4 |
| echarts | 图表插件 | 3.3.4 |
| ion-rangeslider | 进度条 | 2.3.0 |
| jsmind | 思维导图 | # |

#前端js规约
1. 按钮样式

| 按钮名称 | 按钮样式 | 按钮图标 |
| ------ | ------ | ------ |
| 新增 | class="btn btn-primary” | mdi-plus |
| 返回 | class="btn btn-default" | mdi-reply |
| 刷新 | class="btn btn-primary" | mdi-refresh |
| 查询 | class="btn btn-primary" | mdi-magnify |
| 删除 | class="btn btn-danger" | mdi-window-close |
| 变更/更新 | class="btn btn-info " | mdi-redo |
| 修改 | class="btn btn-warning" | mdi-pencil |
| 查看 | class="btn btn-primary" | mdi-eye |
| 执行/检查 | class="btn btn-success" | mdi-check |

#系统构建
##修改版本号
versions:set -DnewVersion=1.04


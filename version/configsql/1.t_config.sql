prompt Importing table t_config...
set feedback off
set define off
insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('md.dayoperation.frequent.dual', '7', '频繁推荐最小间隔天', '99', 'MODEL', 'dayoperation');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('system.name', '用户成长系统', '登录页系统的名称', '1', 'APPS', null);

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('system.logo.url', '/images/logo.png', '系统logo的url', '2', 'APPS', null);

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.daily.pathactive.list', 'UAC_01,UAC_02,UAC_03', '每日运营开启的活跃度组', '3', 'APPS', null);

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.daily.sms.cunponurl', 'https://tb.cn.hn/t8n', '每日运营短信测试默认填充的优惠券链接', '0', 'APPS', null);

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.daily.sms.couponname', '5元券', '每日运营短信测试默认填充的优惠券名称', '0', 'APPS', null);

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.daily.sms.prodname', '测试商品', '每日运营短信测试默认填充的商品名称', '0', 'APPS', null);

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.daily.sms.produrl', 'https://tb.cn.hn/t8n', '每日运营短信测试默认填充的商品链接', '0', 'APPS', null);

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('md.dayoperation.dual.samples', '20', '日运营间隔计算样本数阈值', '99', 'MODEL', 'dayoperation');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('md.dayoperation.joint.samples', '5', '日运营连带率计算样本数阈值', '99', 'MODEL', 'dayoperation');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('md.festival.ordernum', '30', '季节订单数阈值', '99', 'MODEL', 'festival');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('md.festival.summer_keywords', '凉快|短袖', '夏季判定关键字', '99', 'MODEL', 'festival');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('md.festival.winter_keywords', '保暖|羊毛', '冬季判定关键字', '99', 'MODEL', 'festival');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('md.uservalue.income.mchangerate', '5', '用户价值收入贡献均值变化率', '99', 'MODEL', 'user_value');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('md.uservalue.pricesense.zerop', '0.3', '用户价值价格敏感度折扣为0的占比', '99', 'MODEL', 'user_value');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('md.uservalue.pricesense.tgroup', '0.1', '用户价值价格敏感度折扣组阈值', '99', 'MODEL', 'user_value');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('md.uservalue.pricesense.range', '0.2', '用户价值价格敏感度极差的阈值', '99', 'MODEL', 'user_value');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('md.uservalue.potential.meanrate', '0.1', '用户价值价值潜力均值变化率', '99', 'MODEL', 'user_value');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('md.festival.cv', '0.8', '季节占比变异系数阈值', '99', 'MODEL', 'festival');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.activity.sms.prodUrl', 'https://tb.cn.hn/t8n', '活动运营短信测试默认填充的商品链接', '199', 'APPS', null);

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.activity.sms.price', '10元', '活动运营短信测试默认填充的商品单价', '199', 'APPS', null);

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.activity.sms.prodName', '测试商品', '活动运营短信测试默认填充的商品名称', '199', 'APPS', null);

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.daily.default.effectDays', '3', '每日运营效果统计默认天数', '0', 'APPS', null);

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.rptPollingMins', '5', '获取状态报告轮询时间 数字，单位为分钟', '54', 'APPS', 'push');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.moPollingMins', '5', '获取上行消息轮询时间 数字，单位为分钟', '53', 'APPS', 'push');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.batchPollingMins', '5', '批量发送轮询时间 数字，单位为分钟', '52', 'APPS', 'push');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.dailyPollingMins', '5', '单条推送轮询时间 数字，单位为分钟', '51', 'APPS', 'push');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.repeatPushDays', '8', '避免重复推送的天数 默认为7', '1', 'APPS', 'push');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.pushFlag', 'Y', '是否开启推送 Y表示开启 N表示关闭', '2', 'APPS', 'push');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.pushMethod', 'AI', '默认推送方式 IMME立即推送 AI智能推送 默认为AI', '4', 'APPS', 'push');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.smsLengthLimit', '61', '短信内容的长度限制 一般设置为70-签名长度-退订方式长度（不同供应商规则不一样）', '5', 'APPS', 'push');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.productUrl', 'https://detail.tmall.com/item.htm?id=$PRODUCT_ID', '商品明细页链接模板 $PRODUCT_ID为变量，填充平台商品IDEPB_PRODUCT_ID', '6', 'APPS', 'push');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.isTestEnv', 'Y', '短链是否返回测试链接 Y表示是 N表示否', '7', 'APPS', 'push');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.demoShortUrl', 'https://tb.cn.hn/t8n', '测试短链接', '8', 'APPS', 'push');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.shortUrlLen', '26', '短链接在文案中的长度', '9', 'APPS', 'push');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.prodNameLen', '12', '商品名称在短链中的长度', '10', 'APPS', 'push');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.couponSendType', 'A', '优惠券发放方式（A：自行领取 B：系统发送）', '11', 'APPS', 'push');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.couponNameLen', '10', '优惠券名称在文案中的长度', '12', 'APPS', 'push');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.priceLen', '8', '商品价格在文案中的长度', '13', 'APPS', 'push');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.pushVendor', 'NONE', ' 短信供应商 可选的值有CHUANGLAN 创蓝253 MONTNETS 梦网云通信 NONE 默认实现(不实际推送) ', '14', 'APPS', 'push');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.openNightSleep', 'N', '是否开启夜间休眠，休眠期间不推送，待休眠结束再推送 Y表示是 N表示否', '15', 'APPS', 'push');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.nightStart', '22', '休眠开始时间 如21表示从21点开始', '16', 'APPS', 'push');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.nightEnd', '8', '休眠结束时间，如8表示8点开始休眠结束', '17', 'APPS', 'push');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.OpenCallback', 'Y', '是否开启获取上行消息和状态报告 Y表示是 N表示否', '18', 'APPS', 'push');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.clAccount', '***', '创蓝253账号', '30', 'APPS', 'push');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.clPassword', '***', '创蓝253密码', '31', 'APPS', 'push');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.clRequestServerUrl', 'http://smssh1.253.com/msg/send/json', '创蓝253发送地址', '32', 'APPS', 'push');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.clPullMoUrl', 'http://smssh1.253.com/msg/pull/mo', '创蓝253获取上行消息地址', '33', 'APPS', 'push');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.clReportRequestUrl', 'http://smssh1.253.com/pull/report', '创蓝253获取状态报告地址', '34', 'APPS', 'push');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.montnetsAccount', '***', '梦网云通信账号', '40', 'APPS', 'push');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.montnetsPassword', '***', '梦网云通信密码', '41', 'APPS', 'push');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.montnetsMasterIpAddress', 'api02.monyun.cn:7901/sms/v2/std/', '梦网云通信发送地址', '42', 'APPS', 'push');

insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.push.smsPrice', '0.042', '单条短信价格', '55', 'APPS', 'push');

prompt Done.

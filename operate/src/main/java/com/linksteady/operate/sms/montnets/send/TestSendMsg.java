package com.linksteady.operate.sms.montnets.send;

import com.google.common.collect.Lists;
import com.linksteady.operate.sms.montnets.domain.Message;
import java.util.Arrays;
import java.util.List;

/**
 * @author hxcao
 * @date 2019-10-11
 */
public class TestSendMsg {

    private static String userid = "JS4895";

    private static String pwd = "812316";

    private static String masterIpAddress = "61.145.229.28:7902";

    public static void main(String[] args) {
//        SendSms sendSms = new SendSms(userid, pwd, true, masterIpAddress);
////        List<Message> messagesList1 = message1();
//        List<Message> messagesList2 = message2();
////        List<Message> messagesList3 = message3();
//
//        messagesList2.forEach(m->{
//            try {
//                sendSms.singleSend(m);
//                Thread.sleep(10_000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
    }

//    private static List<Message> message1() {
//        List<Message> messages = Lists.newArrayList();
//        List<String> contents = Lists.newArrayList();
//        contents.add("你熟悉的【绿茶卸妆水】，在你看不到的地方想你哦，我帮她告诉你 回T退");
//        contents.add("记得【烟酰胺精华】在等你哟，无论何时何地，这里总有你的专属福利，领券→ https://dwz.cn/5fW2IeO7 回T退");
//        contents.add("记得【烟酰胺精华】在等你哦，无论何时何地，请在这里留下足迹 回T退");
//        contents.add("最近有没有好好养颜呢，一点心意10元券 https://dwz.cn/rBKoJjR0 ，有空回来看看，我一直都在 回T退");
//        contents.add("把【烟酰胺精华】带走呗，先领专属15元券→ https://dwz.cn/xoY01HTL ，手牵手一起走 回T退");
//        List<String> mobiles = Arrays.asList("15810081993", "13263311348", "18310987082", "17611261991", "16619722453",
//                "18165382686", "18911899103");
//
//        mobiles.stream().forEach(x->{
//            contents.stream().forEach(y->{
//                Message message = new Message();
//                message.setMobile(x);
//                message.setContent(y);
//                messages.add(message);
//            });
//        });
//        return messages;
//    }

    /**
     *
     * @return
     */
//    private static List<Message> message2() {
//        List<Message> messages = Lists.newArrayList();
//        List<String> contents = Lists.newArrayList();
//        contents.add("你的专属5元券→  https://dwz.cn/5fW2IeO7 已就绪，熟悉的【烟酰胺精华】正等待你到来 回T退");
//        contents.add("一份想念送给你15元券  https://dwz.cn/Hjh67pbv  ，常回来看看哟 回T退");
//        contents.add("专享25元券→ https://dwz.cn/mo1DAq4K 送给你！快快领走你心仪的【花痴套装】 回T退");
//        contents.add("始终惦记着：忙碌的你，是否忘了爱护自己的颜。送你10元券→ https://dwz.cn/272FXOxK 为颜做储备 回T退");
//        contents.add("专享20元券→ https://dwz.cn/EmA9RoDm 送给你！快快领走你心仪的【水动力套装】 回T退");
//        contents.add("一张薄券→ https://dwz.cn/lsvKV8YL 鼓励生活精致的你，来自【水动力套装】的牵挂 回T退");
//        List<String> mobiles = Arrays.asList("18435131817", "18310987082", "17611261991", "16619722453", "17718443569", "18001212184", "15810081993", "18911899103");
//
//        mobiles.stream().forEach(x->{
//            contents.stream().forEach(y->{
//                Message message = new Message();
//                message.setMobile(x);
//                message.setContent(y);
//                messages.add(message);
//            });
//        });
//        return messages;
//    }

    private static List<Message> message2() {
        List<Message> messages = Lists.newArrayList();
        List<String> contents = Lists.newArrayList();
        contents.add("记得【烟酰胺精华】在等你，无论何时何地，领券→ https://dwz.cn/5fW2IeO7 回T退");
        List<String> mobiles = Arrays.asList("17611261991", "16619722453", "17600270948", "13501154836", "15810081993");

        mobiles.stream().forEach(x->{
            contents.stream().forEach(y->{
                Message message = new Message();
                message.setMobile(x);
                message.setContent(y);
                messages.add(message);
            });
        });
        return messages;
    }

//    private static List<Message> message3() {
//        List<Message> messages = Lists.newArrayList();
//        List<String> contents = Lists.newArrayList();
//        contents.add("你熟悉的【绿茶卸妆水】，在你看不到的地方想你哦，我帮她告诉你 回T退");
//        contents.add("记得【烟酰胺精华】在等你哟，无论何时何地，这里总有你的专属福利，领券→ https://dwz.cn/5fW2IeO7 回T退");
//        contents.add("记得【烟酰胺精华】在等你哦，无论何时何地，请在这里留下足迹 回T退");
//        contents.add("最近有没有好好养颜呢，一点心意10元券 https://dwz.cn/rBKoJjR0 ，有空回来看看，我一直都在 回T退");
//        contents.add("把【烟酰胺精华】带走呗，先领专属15元券→ https://dwz.cn/xoY01HTL ，手牵手一起走 回T退");
//        List<String> mobiles = Arrays.asList("17718443569", "13501154836");
//
//        mobiles.stream().forEach(x->{
//            contents.stream().forEach(y->{
//                Message message = new Message();
//                message.setMobile(x);
//                message.setContent(y);
//                messages.add(message);
//            });
//        });
//        return messages;
//    }
}
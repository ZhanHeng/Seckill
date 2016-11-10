/**
 * Created by lenovo on 2016/11/10.
 */
//存放主要的交互逻辑代码

var seckill = {
    //封装秒杀相关的URL
    URL:{
        now:function () {
            return '/seckill/time/now';
        },
        exposer:function (seckillId) {
            return '/seckill/'+seckillId+'/exposer';
        },
        excution:function (seckillId,md5) {
            return '/seckill/'+seckillId+'/'+md5+'/execution';
        }
    },
    validatePhone : function(phone){
        if(phone && phone.length == 11 && !isNaN(phone)){       //验证手机号
            return true;
        }else{
            return false;
        }
    },
    handlerSeckill : function (seckillId,node) {
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        $.post(seckill.URL.exposer(seckillId),{},function (result) {
            //在回调函数中执行交互流程
            if (result && result['success']){
                var exposer = result['data'];
                if(exposer['exposed']){
                    //开启秒杀,获取秒杀地址
                    var md5 = exposer['md5'];
                    var killUrl = seckill.URL.excution(seckillId,md5);
                    console.log("killUrl = " +killUrl);
                    //绑定一次点击事件
                    $('#killBtn').one('click',function () {
                        //执行秒杀请求
                        //1.先禁用按钮 2.发送请求执行秒杀
                        $(this).addClass('disable');
                        $.post(killUrl,{},function (result) {
                            if (killUrl && result['success']){
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                //显示秒杀结果
                                node.html('<span class="label label-success">'+stateInfo+'</span>');
                            }
                        });
                    });
                    node.show();
                }else {
                    //未开启秒杀
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    //重新计算计时逻辑
                    seckill.countDownTime(seckillId,now,start,end);
                }
            }else{
                console.log("result = " +result);
            }
        });
    },
    countDownTime: function (seckillId,nowTime,startTime,endTime) {  //时间判断
        var seckillBox = $('#seckill-box');
        if(nowTime>endTime){            //秒杀结束
            seckillBox.html('秒杀结束');
        }else if (nowTime<startTime){                    //秒杀未开始，计时操作
            var killTime = new Date ( startTime + 1000) ;//加一秒，防止用户计时时间偏移
            seckillBox.countdown(killTime,function (event) {
                var format = event.strftime('秒杀计时: %D天 %H时 %M分 %S秒');
                seckillBox.html(format);
            }).on('finish.countdown',function () {  //时间结束后回调
                //获取秒杀地址，控制显示逻辑,执行秒杀
                seckill.handlerSeckill(seckillId,seckillBox);
            });
        }else{
            //秒杀开始
            seckill.handlerSeckill(seckillId,seckillBox);
        }
    },
    //详情页秒杀逻辑
    detail:{
        //详情页初始化
        init : function (params) {
            //手机登录验证，计时交互
            //规划交互流程，在Cookie中查找手机号
            var killPhone = $.cookie('killPhone');
            //验证手机号
            if (!seckill.validatePhone(killPhone)){
                //绑定手机号,控制输出
                $('#killPhoneModal').modal({
                    show : true ,           //显示弹出层
                    backdrop : 'static' ,  //禁止位置关闭
                    keyboard : false        //关闭键盘事件
                });
                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killPhoneKey').val();
                    console.log('inputPhone = ' + inputPhone);//TODO
                    if(seckill.validatePhone(inputPhone)){
                        //电话写入Cookie , 有效时间7天，只在/seckill路径下有效
                        $.cookie('killPhone',inputPhone,{expires:7,path:'/seckill'});
                        //刷新页面
                        window.location.reload();
                    }else{
                        $('#killPhoneMessage').hide().html('<label for="" class="label label-danger">手机号错误</label>').show(300);
                    }
                });
            }
            //已经登陆
            //计时交互
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            $.get(seckill.URL.now(),{},function (result) {
                if (result && result['success']){
                    var nowTime = result['data'];
                    //时间判断
                    seckill.countDownTime(seckillId,nowTime,startTime,endTime);
                }else{

                }
            })
        }
    }
}
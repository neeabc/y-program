app.controller('seckillGoodsController', function ($scope, $location, $interval, seckillGoodsService) {

    $scope.findList = function () {
        seckillGoodsService.findList().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    $scope.findOne = function () {
        seckillGoodsService.findOne($location.search()['id']).success(
            function (response) {
                $scope.entity = response;

                console.log("结束时间:" + new Date($scope.entity.endTime).getTime);
                console.log("当前时间:" + new Date().getTime);


                // Math.floor - 返回一个小于或的等于给定参数的最大整数
                allsecond = Math.floor((new Date($scope.entity.endTime).getTime() - (new Date().getTime())) / 1000);

                console.log("计算结果:" + allsecond);

                // $interval - 定时器
                // $interval(要执行的函数, 间隔-单位毫秒, 运行次数); - 参数3可以缺省,代表无限执行
                // $interval.cancel(time);
                time = $interval(function () {
                    if (allsecond > 0) {
                        allsecond = allsecond - 1;
                        $scope.timeString = converTimeString(allsecond);// 转格式
                    } else {
                        $interval.cancel(time);
                        alert("秒杀已结束");
                    }
                }, 1000);

            }
        );
    }

    // 格式转换 xxx天 xx:xx:xx
    converTimeString = function (allsecond) {
        var days = Math.floor(allsecond / (60 * 60 * 24));// 天数
        var hours = Math.floor((allsecond - days * 60 * 60 * 24) / (60 * 60));// 时
        var minutes = Math.floor((allsecond - days * 60 * 60 * 24 - hours * 60 * 60) / 60);// 分
        var seconds = allsecond - days * 60 * 60 * 24 - hours * 60 * 60 - minutes * 60;// 秒
        var timeString = "";
        if (days > 0) {
            timeString = days + "天 ";
        }
        return timeString + hours + ":" + minutes + ":" + seconds;
    }

    // 提交订单
    $scope.submitOrder = function () {
        seckillGoodsService.submitOrder($scope.entity.id).success(
            function (response) {
                if (response.success) {
                    alert("下单成功, 请在1分钟内完成支付");
                    location.href = "pay.html";
                } else {
                    alert(response.message);
                }
            }
        );
    }

});
function beforeUpload() {
    var fileName = document.getElementById('file').files[0].name;
    if(!fileName.endsWith(".xls")) {
        $MB.n_warning("仅支持xls格式的文件！");
        return;
    }
    $('#upload_file_name' ).text(document.getElementById('file').files[0].name).attr('style', 'display:inline-block;');
}

function pushMessage() {
    let formData = new FormData();
    if(sendFlag){
        let file = document.getElementById('file').files;
        if(document.getElementById('file').files.length === 0) {
            $MB.n_warning("请上传数据！");
            return false;
        }
        if(file[0].size > 10485760) {
            $MB.n_warning("上传数据不能超过10M！");
            return false;
        }
        formData.append("couponId",$("#couponId").val());
        formData.append("file", document.getElementById('file').files[0]);
        $.ajax({
            url: "/qywxDaily/uploadCoupon",
            type: "POST",
            data: formData,
            contentType: false,
            processData: false,
            success: function (data) {
                if (data.code === 200) {
                    $MB.n_success("数据提交成功！");
                } else {
                    $MB.n_danger(data.msg);
                }
            },
            error: function (data) {
                $MB.n_danger(data.msg);
            }
        });
    }else{
        formData.append("couponId",$("#couponId").val());
        $.ajax({
            url: "/qywxDaily/couponToSequence",
            type: "POST",
            data: formData,
            contentType: false,
            processData: false,
            success: function (data) {
                if (data.code === 200) {
                    $MB.n_success("数据生成成功！");
                } else {
                    $MB.n_danger(data.msg);
                }
            },
            error: function (data) {
                $MB.n_danger(data.msg);
            }
        });
    }
}
$(function () {
    $("#uploadForm").submit(upload);
})

function upload() {
    $.ajax({
        url: "http://upload-z1.qiniup.com",
        method: "post",
        //不让上传的文件转为字符串
        processData: false,
        //不让jquery设置文件上传类型，让浏览器自动设置
        contentType: false,
        data: new FormData($("#uploadForm")[0]),
        success: function (data) {
            if(data && data.code == 0) {
                //更新头像的访问路径
                $.post(
                    CONTEXT_PATH + "/user/header/url",
                    {"fileName": $("input[name=key]").val()},
                    function (data) {
                        data = $.parseJSON(data);
                        if (data.code  == 0) {
                            window.location.reload();
                        }else {
                            alert(data.msg);
                        }
                    }
                )
            }else {
                alert("上传失败！");
            }
        }
    });

    //处理完请求后，表单不再提交，否则没有action会报错
    return false;
}
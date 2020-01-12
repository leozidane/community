$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	//将发布框隐藏
	$("#publishModal").modal("hide");

	//获取标题和内容
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();

	//发送异步请求(post)
	$.post(
		CONTEXT_PATH + "/discuss/add",
		{"title":title, "content":content},
		function (data) {
			data = $.parseJSON(data);
			//在提示框中显示返回消息
			$("#hintBody").text(data.msg);
			$("#hintModal").modal("show");

			//两秒后，提示框消失
			setTimeout(function(){
				$("#hintModal").modal("hide");
				//刷新页面
				if (data.code == 0) {
					window.location.reload();
				}
			}, 2000);
		}
	);

}
$(function() {

	// init date tables
	var jobExecutorTable = $("#jobexecutor_list").dataTable({
		"deferRender": true,
		"processing" : true, 
	    "serverSide": true,
		"ajax": {
	        url: base_url + "/jobexecutor/pageList" ,
	        data : function ( d ) {
                d.executeName = $('#executeName').val();
            }
	    },
	    "columns": [
	                { "data": 'id', "bSortable": false, "visible" : false},
	                { "data": 'executeName', "bSortable": false},
					{ "data": 'desc', "bSortable": false},
	                { "data": 'address', "bSortable": false},
	                {
	                	"data": 'addTime', "bSortable": false,
	                	"render": function ( data, type, row ) {
	                		return data?moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss"):"";
	                	}
	                },
	                { "data": 'updateTime',"bSortable": false,
	                	"render": function ( data, type, row ) {
							return data?moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss"):"";
	                	}
	                },
	                { "data": '操作' , "bSortable": false,
	                	"render": function ( data, type, row ) {

							var temp = '<p id="'+ row.id +'" ' +
								' desc="'+ row.desc +'" ' +
								' executeName="'+ row.executeName +'" ' +
								' address="'+ row.address +'" ' +
							'>';

							temp += '<button class="btn btn-info btn-xs update" type="button">编辑</button> &nbsp;&nbsp; '+
								'<button class="btn btn-danger btn-xs delete" type="button">删除</button>'

							temp += '</p>';

							return temp;
	                	}
	                }
	            ],
	    "searching": false,
	    "ordering": true,
		"language" : {
			"sProcessing" : "处理中...",
			"sLengthMenu" : "每页 _MENU_ 条记录",
			"sZeroRecords" : "没有匹配结果",
			"sInfo" : "第 _PAGE_ 页 ( 总共 _PAGES_ 页 )",
			"sInfoEmpty" : "无记录",
			"sInfoFiltered" : "(由 _MAX_ 项结果过滤)",
			"sInfoPostFix" : "",
			"sSearch" : "搜索:",
			"sUrl" : "",
			"sEmptyTable" : "表中数据为空",
			"sLoadingRecords" : "载入中...",
			"sInfoThousands" : ",",
			"oPaginate" : {
				"sFirst" : "首页",
				"sPrevious" : "上页",
				"sNext" : "下页",
				"sLast" : "末页"
			},
			"oAria" : {
				"sSortAscending" : ": 以升序排列此列",
				"sSortDescending" : ": 以降序排列此列"
			}
		}
	});
	$('#searchBtn').on('click', function(){
		jobExecutorTable.fnDraw();
	});

	// 新增
	$("#addJobExecutor").click(function(){
		$('#addJobExecutorModel').modal({backdrop: false, keyboard: false}).modal('show');
	});
	var addJobExecutorValidate = $("#addJobExecutorModel .form").validate({
		errorElement : 'span',
		errorClass : 'help-block',
		focusInvalid : true,
		rules : {
			executeName : {
				required : true ,
				minlength: 4,
				maxlength: 100
			},
			desc : {
				required : true ,
				minlength: 4,
				maxlength: 100
			},
			address : {
				required : true ,
				minlength: 4,
				maxlength: 100
			}
		},
		messages : {
			executeName : {
				required :"请输入“执行器Name”"  ,
				minlength:"长度不应低于4位",
				maxlength:"长度不应超过100位"
			},
			desc : {
				required :"请输入“执行器描述”"  ,
				minlength:"长度不应低于4位",
				maxlength:"长度不应超过100位"
			},
			address : {
				required :"请输入“执行地址”"  ,
				minlength:"长度不应低于4位",
				maxlength:"长度不应超过100位"
			}
		},
		highlight : function(element) {
			$(element).closest('.form-group').addClass('has-error');
		},
		success : function(label) {
			label.closest('.form-group').removeClass('has-error');
			label.remove();
		},
		errorPlacement : function(error, element) {
			element.parent('div').append(error);
		},
		submitHandler : function(form) {
			$.post(base_url + "/jobexecutor/save", $("#addJobExecutorModel .form").serialize(), function(data, status) {
				if (data.code == "200") {
					ComAlert.show(1, "新增执行器成功", function(){
						window.location.reload();
					});
				} else {
					if (data.msg) {
						ComAlert.show(2, data.msg);
					} else {
						ComAlert.show(2, "新增失败");
					}
				}
			});

		}
	});
	$("#addJobExecutorModal").on('hide.bs.modal', function () {
		$("#addJobExecutorModal .form .form-group").removeClass("has-error");
		addJobExecutorValidate.resetForm();
	});

	// 删除
	$('#jobexecutor_list').on('click', '.delete', function(){
		var executeName = $(this).parent('p').attr("executeName");
		ComConfirm.show("确认删除该执行器?", function(){
			$.ajax({
				type : 'POST',
				url : base_url + '/jobexecutor/delete',
				data : {"executeName":executeName},
				dataType : "json",
				success : function(data){
					if (data.code == 200) {
						ComAlert.show(1, '删除成功');
						jobExecutorTable.fnDraw();
					} else {
						ComAlert.show(2, data.msg || '删除失败' );
					}
				},
			});
		});
	});

	// 更新
	$("#jobexecutor_list").on('click', '.update',function() {
		$("#updateJobExecutorModel .form input[name='desc']").val($(this).parent('p').attr("desc"));
		$("#updateJobExecutorModel .form input[name='executeName']").val($(this).parent('p').attr("executeName"));
		$("#updateJobExecutorModel .form input[name='address']").val($(this).parent('p').attr("address"));

		$('#updateJobExecutorModel').modal({backdrop: false, keyboard: false}).modal('show');
	});
	var updateJobExecutorValidate = $("#updateJobExecutorModel .form").validate({
		errorElement : 'span',
		errorClass : 'help-block',
		focusInvalid : true,
		rules : {
			desc : {
				required : true ,
				minlength: 4,
				maxlength: 100
			},
			address : {
				required : true ,
				minlength: 4,
				maxlength: 100
			}
		},
		messages : {
			desc : {
				required :"请输入“执行器描述”"  ,
				minlength:"长度不应低于4位",
				maxlength:"长度不应超过100位"
			},
			address : {
				required :"请输入“执行器地址”"  ,
				minlength:"长度不应低于4位",
				maxlength:"长度不应超过100位"
			}
		},
		highlight : function(element) {
			$(element).closest('.form-group').addClass('has-error');
		},
		success : function(label) {
			label.closest('.form-group').removeClass('has-error');
			label.remove();
		},
		errorPlacement : function(error, element) {
			element.parent('div').append(error);
		},
		submitHandler : function(form) {
			$.post(base_url + "/jobexecutor/update", $("#updateJobExecutorModel .form").serialize(), function(data, status) {
				if (data.code == "200") {
					ComAlert.show(1, "更新执行器成功", function(){
						window.location.reload();
					});
				} else {
					if (data.msg) {
						ComAlert.show(2, data.msg);
					} else {
						ComAlert.show(2, "更新失败");
					}
				}
			});

		}
	});
	$("#updateJobExecutorModel").on('hide.bs.modal', function () {
		$("#updateJobExecutorModel .form .form-group").removeClass("has-error");
		updateJobExecutorValidate.resetForm();
	});


});
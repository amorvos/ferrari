<!DOCTYPE html>
<html>
<head>
  	<title>调度中心</title>
  	<#import "/common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
	<!-- DataTables -->
  	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
  	<!-- daterangepicker -->
  	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker-bs3.css">
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
	<!-- header -->
	<@netCommon.commonHeader />
	<!-- left -->
	<@netCommon.commonLeft />
	
	<!-- Content Wrapper. Contains page content -->
	<div class="content-wrapper">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>执行器管理</h1>
		</section>
		
		<!-- Main content -->
	    <section class="content">
	    	<div class="row">
	            <div class="col-xs-3">
	              	<div class="input-group">
	                	<span class="input-group-addon">执行器Name</span>
	                	<input type="text" class="form-control" id="executeName" autocomplete="on" >
	              	</div>
	            </div>
	            <div class="col-xs-2">
	            	<button class="btn btn-block btn-info" id="searchBtn">搜索</button>
	            </div>
                <div class="col-xs-2">
                    <button class="btn btn-block btn-warning" id="addJobExecutor" type="button">+新增执行器</button>
                </div>
          	</div>
			
			<div class="row">
				<div class="col-xs-12">
					<div class="box">
					
			            <div class="box-body">
			              	<table id="jobexecutor_list" class="table table-bordered table-striped display" width="100%" >
				                <thead>
					            	<tr>
					                	<th>id</th>
					                  	<th class="executeName" >执行器Name</th>
                                        <th class="desc" >执行器描述</th>
					                  	<th class="address" >执行地址</th>
                                        <th class="addTime" >创建时间</th>
                                        <th class="updateTime" >更新时间</th>
					                  	<th class="操作" >操作</th>
					                </tr>
				                </thead>
				                <tbody></tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
	    </section>
	</div>
	
	<!-- footer -->
	<@netCommon.commonFooter />
</div>

<!-- 新增.执行机器 start -->
<div class="modal fade" id="addJobExecutorModel" tabindex="-1" role="dialog"  aria-hidden="true">
    <div class="modal-dialog modal-sm2">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" >新增执行器</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form" >
                    <div class="form-group">
                        <label for="lastname" class="col-sm-4 control-label">执行器Name <font color="red">*</font></label>
                        <div class="col-sm-8"><input type="text" class="form-control" name="executeName" placeholder="不能出现中文" maxlength="100" ></div>
                    </div>
                    <div class="form-group">
                        <label for="lastname" class="col-sm-4 control-label">执行器描述 <font color="red">*</font></label>
                        <div class="col-sm-8"><input type="text" class="form-control" name="desc" placeholder="执行器描述" maxlength="100" ></div>
                    </div>
                    <div class="form-group">
                        <label for="lastname" class="col-sm-4 control-label">执行地址 <font color="red">*</font></label>
                        <div class="col-sm-8"><input type="text" class="form-control" name="address" placeholder="格式如ip:port,多个地址,分隔" maxlength="100" ></div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-offset-3 col-sm-9">
                            <button type="submit" class="btn btn-primary">保存</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<!-- 新增.模态框 end -->

<!-- 更新.执行机器 start -->
<div class="modal fade" id="updateJobExecutorModel" tabindex="-1" role="dialog"  aria-hidden="true">
    <div class="modal-dialog modal-sm2">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" >编辑执行器</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form" >
                    <div class="form-group">
                        <label for="lastname" class="col-sm-4 control-label">执行器Name <font color="red">*</font></label>
                        <div class="col-sm-8"><input type="text" class="form-control" name="executeName" maxlength="100" readonly ></div>
                    </div>
                    <div class="form-group">
                        <label for="lastname" class="col-sm-4 control-label">执行器描述 <font color="red">*</font></label>
                        <div class="col-sm-8"><input type="text" class="form-control" name="desc" placeholder="执行器描述" maxlength="100" ></div>
                    </div>
                    <div class="form-group">
                        <label for="lastname" class="col-sm-4 control-label">执行地址 <font color="red">*</font></label>
                        <div class="col-sm-8"><input type="text" class="form-control" name="address" placeholder="格式如ip:port，多个地址用逗号分隔" maxlength="100" ></div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-offset-3 col-sm-9">
                            <button type="submit" class="btn btn-primary">保存</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<!-- 更新.模态框 end -->

<@netCommon.commonScript />
<@netCommon.comAlert />
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/moment.min.js"></script>
<script>var base_url = '${request.contextPath}';</script>
<script src="${request.contextPath}/static/js/jobexecutor.index.1.js"></script>
</body>
</html>

define(['util','domready'],function(util){
	var fetchfile = {};
	
	/**
	 * 初始化工作
	 */
	fetchfile.init = function(){
		var EVENTS = [{selector:'#seefiles',types:['click'],handler:fetchfile.seefiles},
		              {selector:'#handlePaths',types:['click'],handler:fetchfile.handlePaths},
		              {selector:'#downfiles',types:['click'],handler:fetchfile.downfiles},
		              {selector:'#files',types:['blur'],handler:fetchfile.handlePaths},
		              {selector:'#cutpath',types:['blur','keyup'],handler:fetchfile.handlePaths},
		              {selector:'#mergeVersion',types:['click'],handler:mergeVersion},
		              {parent:'#errorfiles',selector:'.badge',types:['click'],handler:fetchfile.downhistory}];
		util.regPageEvents(EVENTS);
		
		/**
		 * 版本合并
		 */
		function mergeVersion(){
			var version = $('#output').val().trim();
			if(version == ''){
				return ;
			}
			util.requestData('/filefetch/mergeVersion',{version:version},function(filename){
				downfile(filename);
			});
		}
	}
	/**
	 * 路径预处理
	 */
	fetchfile.handlePaths = function(){
		var pathArray=getpathArray();
		if(pathArray != null){
			var cutPath = $('#cutpath').val().trim();
			var leavePaths = [];
			for(var i=0;i<pathArray.length;i++){
				var currentPath = $.trim(pathArray[i]);
				if(currentPath != ''){
					currentPath = currentPath.replace(cutPath,'');
					if(currentPath != ''){
						leavePaths.push(currentPath);
					}
				}
			}
//			fetchfile.pathArray = leavePaths;
			//去除重复路径 add by sanri at 2017/07/20
			fetchfile.pathArray = util.CollectionUtils.uniqueSimpleArray(leavePaths);
			//得到最终路径
			$('#files').val(fetchfile.pathArray.join('\n'));
			return ;
		}
		$('#files').val('');
	}
	
	fetchfile.downhistory = function(){
		var $li = $(this).closest('li'),
			filename=$li.attr('filename');
		downfile(filename);
	}
	/**
	 * 查看服务器当前所有文件
	 */
	fetchfile.seefiles = function(){
		util.requestData('/filefetch/listAllFiles',function(files){
			if(files && files.length> 0){
				$('#errorfiles').empty();
				for(var i=0;i<files.length;i++){
					$('#errorfiles').append('<li filename="'+files[i]+'" class="list-group-item">'+files[i]+'<span class="badge">下载</span></li>');
				}
				util.dialogHtml($('#errorfiles'),'文件列表','80%','90%');
			}
		});
	}
	/**
	 * 获取当前文件列表中的文件
	 */
	fetchfile.downfiles = function(){
		if(fetchfile.pathArray){
			var files = $('#files').val().trim();
			var connName = $('#connName').val().trim();
			var version = $('#output').val().trim();
			util.requestData('/filefetch/findfiles',{files:files,connName:connName,version:version},function(ret){
				if(ret.filename){
					downfile(ret.filename);
				}
				if(ret.errorFiles && ret.errorFiles.length > 0){
					$('#errorfiles').empty();
					for(var i=0;i<ret.errorFiles.length;i++){
						$('#errorfiles').append('<li class="list-group-item">'+ret.errorFiles[i]+'</li>');
					}
					util.dialogHtml($('#errorfiles'),'错误文件','80%','90%');
				}
			}); 
			return ;
		}
		util.layer.msg('没有文件列表可供下载');
	}
	
	function downfile(filename){
		$('<form action="'+util.root+'/filefetch/downFile"><input name="t" value="'+new Date().getTime()+'" /><input name="filename" value="'+filename+'" /></form>').appendTo('body').submit().remove();
	}
	
	function getpathArray(){
		var $files = $('#files').val().trim();
		if($files != ''){
			return $files.split('\n');
		}
		return null;
	}
	
	fetchfile.init();
});
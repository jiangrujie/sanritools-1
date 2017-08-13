/**
 * 树结节的右键菜单功能,因为右键功能太多,避免造成 meta.js 太过庞大,把右键功能分离
 */
define(['util','dialog','sqlclient/meta','template','generate','sqlclient/relation','sqlclient/projectbuild','steps','contextMenu','icheck'],function(util,dialog,metatree,template,generate,relation,projectbuild){
	var menu = {};
	
	var api = {
			buildJavaBean:'/code/build/javabean',
			downFile:'/code/downFile',
			addData:'/sqlclient/writeData',
			addMultiData:'/sqlclient/writeMultiData',
			transfer:'/sqlclient/transfer'
	}
	
	/**
	 * 菜单初始化
	 */
	menu.init = function(){
		//初始化右键菜单
    $.contextMenu({
      selector:'#metatree li',
      zIndex:4,
      items:{		//bug 不能每个 item 都加 visible,必须要有一个没有 visible
        insert:{name:'添加数据...',icon:'add',callback:addData,visible:tableVisible},
        insertMulti:{name:'添加大量数据...',icon:'add',callback:addMultiData,visible:tableVisible},
        insertRelation:{name:'添加关联数据...',icon:'add',callback:addRelationData,visible:tableVisible},
        javaBean:{name:'java pojo 生成...',icon:'copy',visible:tableVisible,callback:buildJavaBean},
        columns:{name:'获取表字段',icon:'cut',visible:tableVisible,items:{
        	simple:{name:'以逗号拼接获取',icon:'copy',callback:listColumns},
        	diy:{name:'自定义设置...',icon:'copy',callback:diyTableColumns}
        }},
        dataTransf:{name:'数据转移',icon:'copy',visible:tableVisible,callback:dataTranfer},
        projectBuild:{name:'项目构建...',icon:'copy',visible:dbVisible,callback:buildProject},
        codeBuild:{name:'项目代码构建',icon:'copy',visible:dbVisible,callback:buildProject},
        buildRelation:{name:'建立表关系',icon:'copy',visible:dbVisible,callback:buildRelation},
        sep1: '---------',
        property:{name:'属性',icon:'cut',callback:showConnInfo}
      }
    });
    
    bindEvents();
	}
    
  /**
   * 只有连接节点可见
   */
  function connVisible(key,opts){
  	var treeNode = findTreeNode(key, opts);
  	if(treeNode && treeNode.nodeType == 'conn'){
			return true;
		}
  	return false;
  }
  
  /**
   * 只有在数据库节点上可见
   */
  function dbVisible(key,opts){
  	var treeNode = findTreeNode(key, opts);
  	if(treeNode && treeNode.nodeType == 'db'){
			return true;
		}
  	return false;
  }
  
 	/**
	 * 只有表节点可见
	 */
	function tableVisible(key,opts){
		var treeNode=findTreeNode(key, opts);
		if(treeNode && treeNode.nodeType == 'table'){
			return true;
		}
		return false;
	}
	
	/**
	 * 构建表之间关系
	 */
	function buildRelation(key,opts){
		var treeNode=findTreeNode(key, opts);
		if(!treeNode){
			return ;
		}
		if(true){
			layer.msg('功能未实现');
			return ;
		}
		metatree.current.db = treeNode.originName;
		metatree.current.conn = treeNode.getParentNode().originName;
		relation.create();
	}
	
	/**
	 * 显示连接信息
	 */
	function showConnInfo(key,opts){
		var treeNode=findTreeNode(key, opts);
		if(!treeNode){
			return ;
		}
		if(treeNode.nodeType == 'conn'){
			//显示连接信息
			var connName = treeNode.originName;
			var connMeta = metatree.meta.connMeta[connName].connInfo;
			if(connMeta){
				$('#connInfo').find('.form-group').each(function(){
					var $ptext = $(this).find('p'),
						key = $ptext.attr('name');
					$ptext.text(connMeta[key]);
				});
				var build=dialog.create('连接属性')
				.setContent($('#connInfo'))
				.setWidthHeight('400px','400px')
				.build();
				
			}
		}
	}
	
	/**
	 * 添加数据,会默认生成随机数据
	 */
	function addData(key,opts){
		var tableColumns = privateTableColumns(key,opts);
		if(!tableColumns){
			layer.msg('没有找到表格列');
			return ;
		}
		var treeNode=findTreeNode(key, opts);
		//选中表格,设置 metatree.current.table 
		metatree.current.table = treeNode.originName;
		var columns = [];
		for(var i=0;i<tableColumns.length;i++){
			columns[i] = {};
			//加入初始值,给每一列
			columns[i].name = tableColumns[i];
			columns[i].initValue = generate.num(10,false,true);
		}
		var randomDataHtml = template('randomdata',{columns:columns});
		$('#adddata').find('tbody').html(randomDataHtml);
		var build=dialog.create('添加数据['+treeNode.originName+']')
		.setContent($('#adddata'))
		.setWidthHeight('99%','90%')
		.addBtn({type:'yes',text:'确定',handler:function(index, layero){
			var params = {
					connName:metatree.current.conn,
					database:metatree.current.db,
					tableName:metatree.current.table,
					dataMap:{}
			};
			$('#adddata').find('tbody>tr').each(function(){
				var finallyVal = $(this).find('span.finaly-value').text().trim(),
					columnname = $(this).attr('columnname'),
					checked = $(this).find(':checkbox').is(':checked');
				if(checked){
					params.dataMap[columnname]= finallyVal;
				}
			});
			util.requestData(api.addData,params,function(ret){
				layer.close(index);
				layer.msg(ret);
			});
		}})
		.build();
		
		//美化复选框
		$('#adddata').find(':checkbox').iCheck({
      checkboxClass: 'icheckbox_square-green',
      radioClass: 'iradio_square-green'
		});
	}
	
	/**
	 * 添加大量数据
	 * 1.前端对于数据生成不做处理,后台自动根据字段类型生成
	 * 2.对某些字段有固定值需指定
	 * 3.排除字段需要指定
	 */
	function addMultiData(key,opts){
		var tableColumns = privateTableColumns(key,opts);
		if(!tableColumns){
			layer.msg('没有找到表格列');
			return ;
		}
		var treeNode=findTreeNode(key, opts);
		//选中表格,设置 metatree.current.table 
		metatree.current.table = treeNode.originName;
		var randomDataHtml = template('randommultidata',{columns:tableColumns});
		$('#addmultidata').find('tbody').html(randomDataHtml);
		var build=dialog.create('添加大量数据['+treeNode.originName+']')
		.setContent($('#addmultidata'))
		.setWidthHeight('400px','80%')
		.addBtn({type:'yes',text:'确定',handler:function(index, layero){
			var params = {
					connName:metatree.current.conn,
					database:metatree.current.db,
					tableName:metatree.current.table,
					dataMap:{}
			};
			//查询添加数量 
			params.count = $('#addmultidata').find('input[name=count]').val().trim();
			$('#addmultidata').find('tbody>tr').each(function(){
				var finallyVal = $(this).find('input[name=fixedval]').text().trim(),
					columnname = $(this).attr('columnname'),
					checked = $(this).find(':checkbox').is(':checked');
				if(checked){
					params.dataMap[columnname]= finallyVal;
				}
			});
			util.requestData(api.addMultiData,params);
			layer.close(index);
			layer.msg('正在等待后台生成');
		}}).build();
		
		//美化复选框
		$('#addmultidata').find(':checkbox').iCheck({
      checkboxClass: 'icheckbox_square-green',
      radioClass: 'iradio_square-green'
		});
	}
	
	/**
	 * 列出表字段,
	 * 以逗号拼接获取
	 */
	function listColumns(key,opts){
		var tableColumns = privateTableColumns(key,opts);
		if(tableColumns){
			layer.alert(tableColumns.join(','));
		}
	}
	
	/*
	 * 抽出方法,查找表格列
	 */
	function privateTableColumns(key, opts){
		var treeNode=findTreeNode(key, opts);
		if(!treeNode){
			return undefined;
		}
		if(treeNode.children && treeNode.children.length > 0){
			var tableColumns = [];
			for(var i=0;i<treeNode.children.length;i++){
				tableColumns.push(treeNode.children[i].originName);
			}
			return tableColumns;
		}
		return undefined;
	}
	
	/**
	 * 自定义获取表字段
	 */
	function diyTableColumns(key,opts){
		var tableColumns = privateTableColumns(key,opts);
		if(!tableColumns){
			layer.msg('没有表格列');
			return ;
		}
		var treeNode=findTreeNode(key, opts);
		var buildDialog = dialog.create('自定义表 ['+treeNode.originName+'] 字段获取')
				.setContent($('#diyTableColumns'))
				.setWidthHeight('300px','270px')
				.addBtn({type:'yes',text:'确定',handler:function(index, layero){
					var setting = util.serialize2Json($('#diyTableColumns>form').serialize());
		  		if(treeNode.children && treeNode.children.length > 0){
		  			var result = tableColumns.join(setting.prefix+setting.join+setting.suffix);
		  			//替换特殊字符 
		  			result = result.replace(/\</g,'&lt;').replace(/\>/g,'&gt;');
		  			//替换换行符
		  			result = result.replace(/\\n/g,'<br/>');
		  			layer.alert(result);
		  			layer.close(index);
		  		}
				}})
				.build();
	}
	
	/**
	 * 随机关联添加数据
	 * 暂定思路:
	 * 	1.选取需要添加数据的表
	 * 	2.加载之前已经设定好的关联关系(存文件)
	 * 	3.重新设定关联关系或跳过
	 * 	4.根据关系生成数据
	 */
	function addRelationData(key,opts){
		var treeNode=findTreeNode(key, opts);
		if(!treeNode){
			layer.msg('没有选中节点');
			return ;
		}
		//构建对话框
		var buildDialog = dialog.create('['+treeNode.originName+'] 添加关联数据')
		.setContent($('#relationdata'))
		.setWidthHeight('80%','98%').build();
		
		require(['steps'],function(){
			$('#relationdata').steps({
				labels:{
					finish:'完成',
					previous:'上一步',
					next:'下一步'
				},
				headerTag: "h4",
			  bodyTag: "section",
			  transitionEffect: "slideLeft",
			  autoFocus: true,
			  onStepChanging:changeStep
			});
		});
		
		/*
		 * 添加关联数据下一步 
		 */
		function changeStep(){
			
			return true;
		}
	}
	
	
	
	/**
	 * 构建 java bean 
	 */
	function buildJavaBean(key,opts){
		var treeNode=findTreeNode(key, opts);
		if(!treeNode){
			layer.msg('没有选中节点');
			return ;
		}
		//所有字段追加进排除框中
		$('#generatepojoexclude').empty();
		if(treeNode.children && treeNode.children.length > 0){
			for(var i=0;i<treeNode.children.length;i++){
				var child = treeNode.children[i];
				$('#generatepojoexclude').append('<label class="checkbox-inline"><input name="excludecolumn" type="checkbox" value="'+child.originName+'">'+child.originName+'</label>');
			}
		}
		
		//构建对话框
		var buildDialog = dialog.create('['+treeNode.originName+'] java pojo 生成')
		.setContent($('#generatepojo'))
		.setWidthHeight('500px','90%')
		.addBtn({type:'yes',text:'生成',handler:function(index){
			var packageName = $('#generatepojo').find('input[name=packageName]').val().trim();
			var baseEntity = $('#generatepojo').find('input[name=baseEntity]').val().trim();
			var model = $('#generatepojo').find(':radio:checked').val();
			var interfaces = [],excludeColumns = [];
			$('#generatepojo').find(':checkbox[name=interfaces]:checked').each(function(){
				interfaces.push($(this).val());
			});
			$('#generatepojoexclude').find(':checkbox[name=excludecolumn]:checked').each(function(){
				excludeColumns.push($(this).val());
			});
			var params = {
					connName:metatree.current.conn,
					dbName:metatree.current.db,
					tableName:treeNode.originName,
					packageName:packageName,
					baseEntity:baseEntity,
					model:model,
					interfaces:interfaces,
					excludeColumns:excludeColumns
			}
			util.requestData(api.buildJavaBean,params,function(filename){
				$('<iframe id="pojo_'+filename+'" src="'+util.root+api.downFile+'?typeName=pojo&fileName='+filename+'" style="display:none"></iframe>').appendTo($('body'))
				setTimeout(function(){
					$('iframe#pojo_'+filename).remove();
				},1000);
				layer.close(index);
			});
		}}).build();
		//必须显示之后,才能调用 checkbox 和 radio 美化
		require(['icheck'],function(){
			$(':radio,:checkbox',$('#generatepojo')).iCheck({
  			checkboxClass: 'icheckbox_square-green',
  			radioClass: 'iradio_square-green'
  		});
		});
	}
	
	/**
	 * 数据转移功能
	 */
	function dataTranfer(key,opts){
		var treeNode=findTreeNode(key, opts);
		if(!treeNode){
			layer.msg('没有选中节点');
			return ;
		}
		metatree.current.table = treeNode.originName;
		metatree.current.db = treeNode.getParentNode().originName;
		metatree.current.conn = treeNode.getParentNode().getParentNode().originName;

		layer.prompt({title:'处理类',value:'com.sanri.app.jdbc.datatransfer.impl.TestTransferImpl'},function(value, index, elem){
			if(!value){
				layer.msg('需要提供处理类');
				return ;
			}
			var params = $.extend({},metatree.current,{handlerClazz:value});
			util.requestData(api.transfer,params);
		  layer.close(index);
		  layer.msg('正在进行数据转移');
		});
	}
	
	/**
	 * 构建项目代码
	 */
	function buildProject(key,opts){
		var treeNode=findTreeNode(key, opts);
		if(!treeNode){
			layer.msg('没有选中节点');
			return ;
		}
		//构建项目代码生成数据
		if(!treeNode.children || treeNode.children.length == 0){
			layer.msg('当前库没有打开,无法生成');
			return ;
		}
		metatree.current.db = treeNode.originName;
		metatree.current.conn = treeNode.getParentNode().originName;
		projectbuild.init();		//开始项目构建 
	}
	
	/**
	 * 查找到当前右击的树节点
	 */
	function findTreeNode(key,opts){
		if(opts.$trigger ){
			var treeId = opts.$trigger.attr('id');
	    return metatree.getTreeNodeByTreeId(treeId);
		}
		return null;
	}
	
	/**
	 * 页面事件绑定
	 */
	function bindEvents(){
		var events = [{parent:'#adddata',selector:'.btn',types:['click'],handler:changeRandomData}];
		util.regPageEvents(events);
		
		/*
		 * 修改随机生成的数据 
		 */
		function changeRandomData(){
			var $tr = $(this).closest('tr'),
					$fixedTd = $tr.find('td:last'),
					$finallyspan = $tr.find('span.finaly-value'),
					$currTd = $(this).closest('td');
			var genMethod = $currTd.attr('generate');
			var genValue = 0;
			
			//加入选中样式
			$tr.find('.btn').removeClass('selected');
			$(this).addClass('selected');
			
			//生成数据
			var $input = $currTd.find('input');
			var inputVal = undefined;
			if($input && $input.size() > 0){
				inputVal= $input.val().trim();
			}
			if(genMethod == 'fixed'){
				//固定值不用生成
				genValue = inputVal;
			}else{
				var fun = generate[genMethod];
				if(fun){
					genValue = fun.apply(generate,[inputVal]);
				}else{
					//找不到函数使用默认值,不修改
				}
			}
			//填充到 fixed 值和最终值
			$tr.find('span.finaly-value').text(genValue);
		}
	}
	
	
	return menu;
});
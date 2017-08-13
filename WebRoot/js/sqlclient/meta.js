define(['util','sqlclient/code','dialog','ztree'],function(util,code,dialog){
	var ztree,$ztree = $('#metatree'),nowTreeNodes;
	var metatree = {
			//当前选中的连接,库,表,列
			current:{conn:undefined,db:undefined,table:undefined,column:undefined},
			defConn:'default',
			meta:{},
			ztreeConfig : {
					view:{dblClickExpand : false,showLine:true,selectedMulti:false,
						nameIsHTML:true		//名称支持 html 格式
						},
					data:{
						key:{name:'name'},		//名称的键
						simpleData:{enable:true,idKey:'id',pIdKey:'pid',rootPId:-1}
					},
					callback:{
						beforeClick:nodeBeforeClick,
						onClick:nodeClick,
						onExpand:nodeExpand,
						onDblClick:nodeDblClick
					}
			}
	};
	/*
	 * 后台请求 api
	 */
	var api = {
			connections:'/sqlclient/connections',
			connectionInfo:'/sqlclient/connectionInfo',
			schemas:'/sqlclient/schemas',
			tables:'/sqlclient/loadSchema'
	}
	
	/**
	 * 元数据初始化
	 */
	metatree.init = function(){
		//初始化所有连接的节点
		util.requestData(api.connections,function(names){
			var ztreeNodes = [];
			if(names){
				metatree.meta.connections = names;		//保存所有的连接信息
				metatree.meta.connMeta={};
				for(var i=0;i<names.length;i++){
					var connName = names[i];
					metatree.meta.connMeta[connName] = {};
					var connTreeNodeInfo = {id:connName+'_conn',pid:-1,nodeType:'conn',name:(i+1)+'.'+connName,originName:connName};
					ztreeNodes.push(connTreeNodeInfo);
				}
			}
			//只有连接节点进行初始化,以后的结点都是在树基础上添加 的
			ztree = $.fn.zTree.init($ztree, metatree.ztreeConfig, ztreeNodes);
			//双击默认连接节点
			var defTreeNode = ztree.getNodeByParam('id',metatree.defConn+'_conn');
			metatree.ztreeConfig.callback.onDblClick.call(ztree,null,'conn',defTreeNode);
		});
		bindEvents();
	}
	
	/**
	 * 获取当前选中的 database 对象
	 */
	metatree.getCurrentDb = function(){
		var dbname = this.current.db;
		var conn = this.current.conn;
		var metaInfo = this.meta.connMeta[conn];
		if(metaInfo && metaInfo.databases){
			var currDatabase = null;
			//找到当前数据库 meta 元素
			for(var i=0;i<metaInfo.databases.length;i++){
				var database = metaInfo.databases[i];
				if(database.instance == dbname){
					currDatabase = database;
					break;
				}
			}
			if(currDatabase){
				return currDatabase;
			}
		}
		return undefined;
	}
	
	
	/**
	 * 根据树节点 id 获取节点
	 */
	metatree.getTreeNodeByTreeId = function(treeId){
		var treeNode = ztree.getNodeByTId(treeId);
		return treeNode;
	}
	
	/**
	 * 根据连接加载所有的数据库节点
	 */
	function loadDbTreeNodes(connName){
		var connTreeNode = ztree.getNodeByParam('id',connName+'_conn');
		if(metatree.meta.connMeta[connName].databases && metatree.meta.connMeta[connName].databases.length > 0){
			//如果数据已经加载过了,则无需重复加载 ,暂时只改了这,以后所有的请求都得判断是否有数据 TODO 
			appendDbTreeNode(metatree.meta.connMeta[connName].databases);
		}else{
			util.requestData(api.schemas,{connName:connName},function(databases){
				metatree.meta.connMeta[connName].databases = databases;
				appendDbTreeNode(databases);
			});
		}
		
		/**
		 * 追加 db 节点
		 */
		function appendDbTreeNode(databases){
			var ztreeNodes = [];
			for(var i=0;i<databases.length;i++){
				var database = databases[i];			//id 加上 connName 才不会重复
				var dbTreeNodeInfo = {id:database.instance+'_db_'+connName,nodeType:'db',name:(i+1)+'.'+database.instance,originName:database.instance};
				ztreeNodes.push(dbTreeNodeInfo);
			}
			//追加节点到连接节点
			ztree.addNodes(connTreeNode,0,ztreeNodes);
			//修改连接结点名称
			var parentNodeName  = connTreeNode.name + '['+databases.length+']';
			connTreeNode.name = parentNodeName;
			ztree.updateNode(connTreeNode);
			
			//查询当前连接的默认库,并双击默认库
			util.requestData(api.connectionInfo,{name:connName},function(connInfo){
				metatree.meta.connMeta[connName].connInfo = connInfo;
				//获取默认库的 节点
				var defDbNode = ztree.getNodeByParam('id',connInfo.database+'_db_'+connName);
				metatree.ztreeConfig.callback.onDblClick.call(ztree,null,'conn',defDbNode);
			});
		}
	}
	
	/**
	 * 根据需要加载的库,加载所有表及列信息
	 */
	function loadTableTreeNodes(connName,dbName){
		var connTreeNode = ztree.getNodeByParam('id',connName+'_conn');
		var dbTreeNode = ztree.getNodeByParam('id',dbName+'_db_'+connName);
		util.requestData(api.tables,{connName:connName,schemaName:dbName},function(schema){
			var ztreeNodes = [];
			var tables = schema.tables;
			//替换 database 数据,原先一次性加载,只加载了默认库的,这里需要将原来没有加上的表格加上 add by sanri at 2017/07/07
			for(var i=0;i<metatree.meta.connMeta[connName].databases.length;i++){
				var databaseName = metatree.meta.connMeta[connName].databases[i].instance;
				if(schema.instance  == databaseName){
					metatree.meta.connMeta[connName].databases[i] = schema;
					break;
				}
			}
			//加载表格和列
			for(var j=0;j<tables.length;j++){
				var table = tables[j];
				var currTableName = formatTableName(j,table);	//表名和列名加上 connName 才不会导致重复的 id 
				var tableTreeNode = {id:table.tableName+'_table_'+connName,name:currTableName,originName:table.tableName,nodeType:'table'};
				ztreeNodes.push(tableTreeNode);
				for(var k=0;k<table.columns.length;k++){
					var column = table.columns[k];
					var currentColumnName = formatColumnName(k, column);
					var columnTreeNode = {id:column.columnName+'_column_'+connName,name:currentColumnName,originName:column.columnName,pid:tableTreeNode.id,nodeType:'column'};
					ztreeNodes.push(columnTreeNode);
				}
			}
			//追加节点到库节点
			ztree.addNodes(dbTreeNode,0,ztreeNodes);
			//修改连接结点名称
			var parentNodeName  = dbTreeNode.name + '['+tables.length+']';
			dbTreeNode.name = parentNodeName;
			ztree.updateNode(dbTreeNode);
			
		});
		
		/*
     * 格式化 表名和字段名
     * 连接名
     */
    function formatTableName(index,table){
      var columns = table.columns || [];
      return (index + 1)+'.'+table.tableName+'('+(table.comments || '未说明')+')['+columns.length+']';
    }
    function formatColumnName(index,column){
      return (index + 1) + '.'+column.columnName+'('+(column.comments || '未说明')+')';
    }
	}
	
	metatree.relayout = function(){
		
	}
	
	/**
	 * 节点展开
	 * 只有用户点击的时候才会触发展开函数
	 */
	function nodeExpand(event, treeId, treeNode){
		
	}
	
	/**
	 * 节点点击之前
	 */
	function nodeBeforeClick(){
		
	}
	
	/**
	 * 点击节点
	 * 问题:双击节点时会认为是两次单击,对于双击事件会触发一次,单击事件会触发两次单击事件
	 * 使用 setTimeout 解决 
	 * 思路:如果真的是单击,则在 250 ms 后定时器执行,同时销毁定时器和执行单击事件
	 * 如果是双击,则此方法进两次,在第一次需要 250ms 执行单击任务,但在250 ms 内,定时器由在第二次进来这个方法时销毁定时器
	 */
	function nodeClick(event, treeId, treeNode){
		if(metatree.clickTimeout){
//			console.log('取消单击事件:'+metatree.clickTimeout);
			window.clearTimeout(metatree.clickTimeout);					//取消单击延时未执行的方法
			metatree.clickTimeout = null;
		}else{
			metatree.clickTimeout = window.setTimeout(function(){
				//执行单击事件
//				console.log('单击事件:'+metatree.clickTimeout);
				metatree.clickTimeout = null;
				//单击只对表格节点进行处理,查询当前表的 10条数据,并使当前的 选中库和连接为点击的父级,有可能并没有指到当前库和连接
				if(treeNode.nodeType == 'table'){
					var dbNode = treeNode.getParentNode();
					var connNode = dbNode.getParentNode();
					
					//指定当前连接和库
					metatree.current = {
							conn:connNode.originName,
							db:dbNode.originName,
							table:treeNode.originName
					}
					//调用 code sql 输入 sql ,并执行
					var newSqlLine = 'select * from '+metatree.current.table+' limit 0,10';
					code.appendSql(newSqlLine);
					code.executeSqls(metatree.current.conn,metatree.current.db,newSqlLine);
				}
			},250);
//			console.log('两秒后执行任务:'+metatree.clickTimeout)
		}
	}
	
	/**
	 * 节点双击
	 */
	function nodeDblClick(event, treeId, treeNode){
		if(!treeNode){
			//点击空白区域返回 add by sanri at 2017/07/07
		}
		if(treeNode.children && treeNode.children.length > 0){
			ztree.expandNode(treeNode);
			return ;
		}
		//根据类型加载节点
		if(treeNode.nodeType == 'conn' ){
			//选中当前连接
			metatree.current.conn = treeNode.originName;
			loadDbTreeNodes(treeNode.originName);
		}else if(treeNode.nodeType == 'db'){
			var connNode = treeNode.getParentNode();
			//同时选中连接和库
			metatree.current.conn = connNode.originName;
			metatree.current.db = treeNode.originName;
			//加载库的所有表数据
			loadTableTreeNodes(connNode.originName,treeNode.originName);
		}
	}
	
	function bindEvents(){
		var events = [{selector:'#search',types:['blur'],handler:filterNodes}];
		
		util.regPageEvents(events);
	}
	
	/**
	 * 使用关键字过滤节点
	 */
	function filterNodes(){
		var keyword = $(this).val().trim();
		if(!keyword){
			metatree.init();
		}
		// 这个方法获取到的是符合条件的结点,并没有按照树形结构去查询节点数据,需要一级一级去获取上级节点,并组装
		var ztreeNodes =  ztree.getNodesByFilter(filter,false);
		var ztreeNodesSimple = [],connOrDbNodesSimple = [];
		//先把所有的库和连接找到
		var connIds = '',dbIds = '';
		for(var i=0;i<ztreeNodes.length;i++){
			var currNode = ztreeNodes[i],tableNode,dbOrConnNode,dbNode,connNode;
			switch(currNode.nodeType){
			case 'column':
				tableNode = currNode.getParentNode();
			case 'table':
				if(!tableNode){
					tableNode = currNode;
				}
				dbOrConnNode = tableNode.getParentNode();
				if(dbOrConnNode.nodeType == 'db'){
					dbNode = dbOrConnNode;
					connNode = dbOrConnNode.getParentNode();
				}else{
					connNode = dbOrConnNode;
				}
				break;
			case 'db':
				dbNode = currNode;
				connNode = currNode.getParentNode();
				break;
			case 'conn':
				connNode = currNode;
				break;
			}
			
			if(dbNode){
				if(dbIds.indexOf(dbNode.id) == -1){
					var dbNodeSimple = {id:dbNode.id,name:dbNode.name,originName:dbNode.originName,pid:dbNode.pid,nodeType:dbNode.nodeType};
					connOrDbNodesSimple.push(dbNodeSimple);
					dbIds += dbNodeSimple.id+'$';
				}
			}
			if(connIds.indexOf(connNode.id) == -1){
				var connNodeSimple = {id:connNode.id,name:connNode.name,originName:connNode.originName,pid:connNode.pid,nodeType:connNode.nodeType};
				connOrDbNodesSimple.push(connNodeSimple);
				connIds += connNodeSimple.id + '$';
			}
		}
		ztreeNodesSimple = ztreeNodesSimple.concat(connOrDbNodesSimple);			//连接库和连接数据节点
		//下面的 for 循环只是为了把所有的表找到
		var tableNodeIds = '';
		for(var i=0;i<ztreeNodes.length;i++){
			var currNode = ztreeNodes[i],tableNode = null;
			if(currNode.nodeType == 'column'){
				tableNode = currNode.getParentNode();
			}else if(currNode.nodeType == 'table'){
				tableNode = currNode;
			}
			// 如果没有这个表,则把这个表加入
			if(tableNode != null && tableNodeIds.indexOf(tableNode.id) == -1){
				ztreeNodesSimple.push(tableNode);
				tableNodeIds += tableNode.id+'$';
			}
		}
		//重新渲染 ztree ,目前是重新初始化
		ztree = $.fn.zTree.init($ztree, metatree.ztreeConfig, ztreeNodesSimple);
		//展开找到的库和连接节点 
		for(var i=0;i<connOrDbNodesSimple.length;i++){
			var needExpandNode = connOrDbNodesSimple[i];
			if(needExpandNode.nodeType == 'db'){
				var expandDbNode = ztree.getNodeByParam('id',needExpandNode.id);
				ztree.expandNode(expandDbNode,true);
			}else if(needExpandNode.nodeType == 'conn'){
				var expandConnNode = ztree.getNodeByParam('id',needExpandNode.id);
				ztree.expandNode(expandConnNode,true);
			}
		}
		/*
		 * 过滤器函数,用于过滤节点数据
		 */
		function filter(node){
			if(node.originName && node.originName.indexOf(keyword) !=-1){
				return true;
			}
			return false;
		}
	}
	
	return metatree;
});
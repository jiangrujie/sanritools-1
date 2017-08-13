/**
 * 树节点 id 表示规则为 连接名称_节点深度_节点名称_随机数 这样就绝对不会重复
 */
define(['util','dialog','template','ztree','contextMenu'],function(util,dialog,template){
	var zkclient = {
			meta:{connections:[],connInfos:{}},
			current:{conn:undefined,nodePath:undefined},
			ztreeConfig : {
				//nameIsHTML 名称支持 html 格式
				view:{dblClickExpand : false,showLine:true,selectedMulti:false,nameIsHTML:true},
				data:{
					key:{name:'name'},		//名称的键
					simpleData:{enable:true,idKey:'id',pIdKey:'pid',rootPId:-1}
				},
				callback:{
					onClick:fetchNodeData,
					onDblClick:fetchChildNodes
				}
		}
	};
	var api = {
			createConn:'/zoo/createConn',			//创建连接
			connInfo: '/zoo/connInfo',				//显示连接详细信息
			connections:'/zoo/connections',		//获取所有连接
			childNodes:'/zoo/childNodes',			//获取子节点
			nodeData:'/zoo/nodeData',					//获取节点数据 
			nodeMeta:'/zoo/nodeMeta',				//获取节点元数据
			nodeACLs:'/zoo/nodeACLs'
	}
	var $ztree = $('#zooconntree'),ztree = undefined;
	
	/**
	 *  ztree 客户端初始化
	 *  加载初始化数据,绑定事件
	 */
	zkclient.init = function(){
		util.requestData(api.connections,function(conns){
			zkclient.meta.connections = conns || [];
			//获取所有连接的连接属性
			for(var i=0;i<zkclient.meta.connections.length;i++){
				var connName = zkclient.meta.connections[i];
				util.requestData(api.connInfo,{connName:connName},function(connInfo){
					zkclient.meta.connInfos[connName] = connInfo;
				});
			}
			initZTree();
		});
		return this;
	}
	
	/**
	 * 初始化 ztree 
	 */
	function initZTree(){
		if(zkclient.meta.connections && zkclient.meta.connections.length  > 0){
			var ztreeNodes = [];
			for(var i=0;i<zkclient.meta.connections.length;i++){
				var connName = zkclient.meta.connections[i];
				ztreeNodes.push({id:connName+'_0_',pid:'-1',name:connName,nodeType:'conn',deep:0});
			}
			ztree = $.fn.zTree.init($ztree, zkclient.ztreeConfig, ztreeNodes);
			
			//加载默认连接的第一级子节点
			var defaultConnName = 'default';
			if($.inArray(defaultConnName,zkclient.meta.connections) != -1){
				loadNode(defaultConnName);
			}
			return ;
		}
		layer.msg('无连接初始化');
	}

	/**
	 * 双击时抓取子节点
	 */
	function fetchChildNodes(event, treeId, treeNode){
		if(!treeNode){
			return ;
		}
		//如果有子节点，无需重复加载，后面再考虑强制刷新 TODO 
		if(treeNode.children && treeNode.children.length > 0){
			ztree.expandNode(treeNode);
			return ;
		}
		if(treeNode.nodeType == 'conn'){
			//连接节点，直接抓取根数据
			loadNode(treeNode.name);
		}else{
			var connName = treeNode.connName;
			loadNode(connName, treeNode.id);
		}
	}
	
	/**
	 * 单击时抓取节点数据
	 * 需要区分是单击还是双击
	 */
	function fetchNodeData(event, treeId, treeNode){
		if(zkclient.clickTimeout){
//		console.log('取消单击事件:'+metatree.clickTimeout);
		window.clearTimeout(zkclient.clickTimeout);					//取消单击延时未执行的方法
		zkclient.clickTimeout = null;
	}else{
		zkclient.clickTimeout = window.setTimeout(function(){
			//执行单击事件
//			console.log('单击事件:'+metatree.clickTimeout);
			zkclient.clickTimeout = null;

			//处理单 击时的数据
			if(treeNode.nodeType == 'conn'){
				//连接节点，直接抓取根数据
				return ;
			}
			var nodePath = getXpath(treeNode);
			//获取节点数据
			util.requestData(api.nodeData,{connName:treeNode.connName,nodePath:nodePath},function(nodeData){
				$('#nodeTabContent>.tab-pane>textarea').val(nodeData);
			});
			//获取节点 ACL 权限信息
			util.requestData(api.nodeACLs,{connName:treeNode.connName,nodePath:nodePath},function(nodeACLs){
				var htmlCode = template('nodeacls',{acls:nodeACLs});
				$('#nodeACL').find('tbody').html(htmlCode);
			});
			//获取节点元数据信息
			util.requestData(api.nodeMeta,{connName:treeNode.connName,nodePath:nodePath},function(stat){
				var $tbody = $('#nodeattr tbody').empty();
				if(stat){
					var index = 0;
					var htmlCodes = [];
					for(var key in stat){
						var currAttr = {index:++index,key:key,value:stat[key]};
						switch(key){
						case 'version':
							currAttr['remark'] = '数据版本';
							break;
						case 'cversion':
							currAttr['remark'] = '子节点版本';
							break;
						case 'aversion':
							currAttr['remark'] = 'ACL 版本';
							break;
						case 'dataLength':
							currAttr['remark'] = '数据长度';
							break;
						case 'ctime':
							currAttr['remark'] = '节点创建时间';
							currAttr['value'] = stat[key] + '('+util.FormatUtil.dateFormat(stat[key],'yyyy-MM-dd HH:mm:ss')+')';
							break;
						case 'mtime':
							currAttr['remark'] = '节点最后一次被修改的时间';
							currAttr['value'] = stat[key] + '('+util.FormatUtil.dateFormat(stat[key],'yyyy-MM-dd HH:mm:ss')+')';
							break;
						case 'numChildren':
							currAttr['remark'] = '子节点个数';
							break;
						case 'emphemeralOwner':
							currAttr['remark'] = '节点拥有者会话ID';
							break;
						}
						var htmlCode = template('attrTemplate',currAttr);
						htmlCodes.push(htmlCode);
					}
					$tbody.append(htmlCodes.join(''));
				}
			});
		
		},250);
//		console.log('两秒后执行任务:'+metatree.clickTimeout)
	}
}
	
	/**
	 * 双击节点加载第一级子节点
	 * connName: 连接名称
	 * nodeId: 节点id 
	 * 当nodeId 为空时表示双击根节点
	 */
	function loadNode(connName,nodeId){
		var nodePath = undefined;
		if(!nodeId){
			nodePath = '/';
		}else{
			var currTreeNode = ztree.getNodeByParam('id',nodeId);
			if(currTreeNode){
				nodePath = getXpath(currTreeNode);
			}
		}
		
		if(nodePath){
			var parentNode = undefined;
			if(nodeId){
				parentNode = ztree.getNodeByParam('id',nodeId);
			}else{
				parentNode = ztree.getNodeByParam('id',connName+'_0_');
			}
			//加载子节点数据
			util.requestData(api.childNodes,{connName:connName,nodePath:nodePath},function(childs){
				if(!childs || childs.length == 0 ){
					return ;
				}
				renderNode(connName, parentNode, childs);
			});
		}
	}
	
	/**
	 * 渲染节点
	 * connName: 连接名称
	 * parentId : 父级节点编号 
	 * childeNodes : 子节点序列 Array,没有时传空数组 
	 */
	function renderNode(connName,parentTreeNode,childs){
		var childNodes = [];
		var childDeep = parentTreeNode.deep + 1;
		for(var i=0;i<childs.length;i++){
			var currNodeId = connName+'_'+childDeep+'_'+childs[i] + Math.round(Math.random() * 100);
			childNodes.push({id:currNodeId ,name:childs[i],deep:childDeep,nodeType:'dataNode',connName:connName});
		}
		ztree.addNodes(parentTreeNode,childNodes);
	}
	
	/**
	 * 获取节点路径,使用 / 做为分隔,以 / 开头
	 */
	function getXpath(treeNode){
		var nodes = treeNode.getPath();
		if(nodes){
			var xPath = [];
			for(var i=0;i<nodes.length;i++){
				if(nodes[i].nodeType == 'conn'){
					continue;
				}
				xPath.push(nodes[i].name);
			}
			return '/'+xPath.join('/');
		}
		return '/';		//如果当前节点是根节点,返回 / 
	}
	
	return zkclient.init();
});
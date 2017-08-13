/**
 * Created by sanri on 2017/3/19.
 */
define(['layer'],function(layer){
    layer.config({
        path:'../../plugins/layer/'        //相对于当前文件路径
    });
    /**
     * 获取项目根路径
     */
    function getRootPath(){
        var curWwwPath=top.document.location.href,
            pathName=top.document.location.pathname;

        var pos=curWwwPath.indexOf(pathName),
            localhostPaht=curWwwPath.substring(0,pos),
            projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);

        return(localhostPaht+projectName);
    }

    var root = getRootPath();

    /**
     * StringUtil 工具类
     */
    var StringUtil = (function(){
        /*
         * 把EXT_ORG_TYPE这种下划线格式的数据转换成extOrgType驼峰式
         * */
        function _2aB(name) {
            var result = '';
            // 快速检查
            if (name == '') {
                // 没必要转换
                return "";
            } else if (name.indexOf("_") == '-1') {
                // 不含下划线，全部转小写
                return name.toLowerCase();
            }
            // 用下划线将原始字符串分割
            var camels = name.split("_");
            for (var i = 0, len = camels.length; i < len; i++) {
                var camel = camels[i];
                // 跳过原始字符串中开头、结尾的下换线或双重下划线
                if (camel == '') {
                    continue;
                }
                // 处理真正的驼峰片段
                if (result.length == 0) {
                    // 第一个驼峰片段，全部字母都小写
                    result += camel.toLowerCase();
                } else {
                    // 其他的驼峰片段，首字母大写
                    result += (camel.substring(0, 1).toUpperCase());
                    result += (camel.substring(1).toLowerCase());
                }
            }
            return result.toString();
        }
        /*
         * 把extOrgType驼峰式的数据转换成EXT_ORG_TYPE下划线格式
         * */
        function aB2_(name) {
            //  var name="extOrgType";
            var result = '';
            if (name != '' && name.length > 0) {
                // 循环处理其余字符
                for (var i = 0; i < name.length; i++) {
                    var s = name.substring(i, i + 1);
                    // 在大写字母前添加下划线
                    if (s == s.toUpperCase()) {
                        result += ("_");
                    }
                    // 其他字符直接转成大写
                    result += (s.toUpperCase());
                }
            }
            return result.toString();
        }

        /**
         * 判断字符串是否为空
         */
        function isBlank(source){
            if(source == null || source === undefined ){
                return true;
            }
            if(typeof source !== 'string'){
                throw new Error('判断为空,只支持字符串类型');
            }
            return source.replace(/\s*/,'').length == 0;
        }

        return {
            _2aB:_2aB,
            aB2_:aB2_,
            isBlank:isBlank
        }
    })();
    
    /**
     * 事件注册方法
     * 入参格式
     * selector 和 parent 支持字符串(jquery 选择器)和对象(jquery 对象)
     * [{selector:jquery选择器,types:[事件1,事件2],handler:function,parent:jquery 选择器}] parent 如果存在的话,会注册动态事件
     */
    function regPageEvents(cfgArr){
        if(!cfgArr){
            return ;
        }
        while(cfgArr[0]){
            (function(cfg){
                if(typeof cfg.parent  == 'string'){
                  cfg.parent = $(cfg.parent);
                }
                if(typeof cfg.selector == 'string'){
                  //如果是动态事件,这里不能再取为对象了 fix bug at 2016/10/04
                  if(!cfg.parent){
                    cfg.selector = $(cfg.selector);
                  }
                }
                for(var i = 0, l = cfg.types.length; i < l; i++) {
                    if(cfg.parent){
                        //注册动态事件
                        cfg.parent.on(cfg.types[i],cfg.selector, function(e) {
                            if($.isFunction(cfg.handler)) {
                                cfg.handler.apply(this, arguments);
                                e.stopPropagation();    //停止冒泡
                            }
                        });
                        continue;
                    }
                    cfg.selector.bind(cfg.types[i], function(e) {
                        if($.isFunction(cfg.handler)) {
                            cfg.handler.apply(this, arguments);
                            e.stopPropagation();
                        }
                    });
                }
            })(cfgArr.shift());
        }
    }
    

    /**
     * 格式化工具
     */
    var FormatUtil = (function(){
        var defaultDateFormat = 'yyyy-MM-dd';
        /**
         *
         * @param time long 型日期,或 Date 对象
         * @param format 格式,支持
         *  yyyy 年
         *  MM 月
         *  dd 日
         *  HH 小时
         *  mm 分钟
         *  ss 秒
         */
        function dateFormat(time,format){
            if(StringUtil.isBlank(format)){
                format = defaultDateFormat;
            }
            if(typeof time == 'number'){
                time = new Date(time);
            }
            var tf = function(i){return (i < 10 ? '0' : '') + i ;};
            return format.replace(/yyyy|MM|dd|HH|mm|ss/g, function(a){
                switch(a){
                    case 'yyyy':
                        return tf(time.getFullYear());
                        break;
                    case 'MM':
                        return tf(time.getMonth() + 1);
                        break;
                    case 'mm':
                        return tf(time.getMinutes());
                        break;
                    case 'dd':
                        return tf(time.getDate());
                        break;
                    case 'HH':
                        return tf(time.getHours());
                        break;
                    case 'ss':
                        return tf(time.getSeconds());
                        break;
                }
            });
        }

        /**
         * 格式化字符串
         * @param source {0}xxx{1}
         * @param 后面接的参数会依次替换 {0},{1}
         */
        function stringFormat(){
            if(arguments.length < 1 || StringUtil.isBlank(arguments[0])){
                return '';
            }
            //先要保存原来的 arguments
            var paramsMap=[];
            for(var i=1;i<arguments.length;i++){
                paramsMap.push(arguments[i]);
            }
            return arguments[0].replace(/\{(\d+)\}/g,function(m,i){
                return paramsMap[i];
            });
        }

        return {
            dateFormat:dateFormat,
            stringFormat:stringFormat
        }
    })();
    
    /**
     * 集合工具类
     * uniqueSimpleArray 对数组元素进行去重操作(里面是简单元素)
     */
    var CollectionUtils = (function(){
    	return {
    		/**
    		 * 清除重复数据,字符串数组 
    		 * @param array
    		 * @returns
    		 */
    		uniqueSimpleArray:function(array){
    			if(array && $.isArray(array)){
    				var unique = [];
    				for(var i=0;i<array.length;i++){
    					if($.inArray(array[i],unique) == -1){
    						unique.push(array[i]);
    					}
    				}
    				return unique;
    			}
    			return [];
    		}
    	}
    })();

    /**
     *
     * 将url请求参数序列化成json对象，如果有相同名称的参数将使用seperator分隔存放于一个属性中
     * @param param 需要处理的url请求字符串，形如：a=1&b=2&c=3
     * @param seperator 多个属性的分隔符，默认','
     * @param vFilter 每一组键值对的过滤器，用于值的预处理
     * @return 序列化后的对象
     */
    var serialize2Json = function(param, seperator, vFilter) {
        seperator = seperator || ",";
        param = (function() {
            try {
                return decodeURIComponent(param);
            } catch(ex) {
                return param;
            }
        })();
        vFilter = vFilter || $.noop;

        var ret = {},
            pairsArr = param.split('&'), pair, idx, key, value;
        $.each(pairsArr, function() {
            idx = this.indexOf("=");
            if(-1 === idx) {
                return true;
            }
            key = this.substring(0, idx);
            value = this.substring(idx + 1, this.length);

            value = vFilter.call(param, key, value) || value;

            if(ret[key]) {
                if(-1 === ret[key].indexOf(value)) {
                    ret[key] = ret[key] + seperator + value;
                }
            } else {
                ret[key] = value;
            }
        });

        return ret;
    };

    /**
     * 对象转键值对
     * 只支持简单对象 {key:value,key:value} 形式
     */
    var object2KeyValue = function(object){
        if(object){
            var keyValue = '';
            for(var key in object){
                keyValue += (encodeURIComponent(key)+'='+encodeURIComponent(object[key]));
            }
        }
        return '';
    };

//    /**
//     *  打开一个对话框
//     * @param url
//     * @param params
//     * @param width
//     * @param height
//     * @param ok
//     * @param cancel
//     */
//    function dialog(url,params,title,width,height,ok,cancel){
//        layer.open({
//            type: 2,
//            title: title,
//            shadeClose: true,
//            shade: 0.8,
//            area: [width, height],
//            content: url
//        });
//    }
    
    /**
     * 用 html 元素打开一个对话框
     */
    function dialogHtml(element,title,width,height){
    	layer.open({
			  type: 1,
			  shade: false,
			  title: title, //不显示标题
			  content: element, //捕获的元素，注意：最好该指定的元素要存放在body最外层，否则可能被其它的相对元素所影响
			  area: [width, height]
    	});
    }

    /**
     * ajax 请求
     */
    function ajax(options,success,error){
        if(!error || !$.isFunction(error)){
            error = function(xhr,textStatus, errorThrown){
                if(window.console){
                    console.log(textStatus);
                    console.error(errorThrown);
                }
            }
        }
        var options = $.extend({type:'post',
            async:false,
            contentType:'application/json',
            dataType:'json',
            error:error,
            timeout:500000,                   //默认 5 秒后超时
            success:success},options);
        if(options.url.startsWith('http')){  //如果以 http 打头,则为跨域请求
            if(options.isJsonp){                //使用 jsonp 跨域
                options.dataType='jsonp';
            }
            //如果不使用 jsonp 跨域 ,则 chrome 浏览器 和后台都需要设置
            /*
             * 后台设置
             *   response.addHeader("Access-Control-Allow-Origin", "*");
             * chrome 设置
             *  目标加入 --disable-web-security
             */
        }else{
            options.url = root+options.url;   //本地请求
        }
        if(typeof options.data == 'object'){
            options.data = JSON.stringify(options.data);
        }
        $.ajax(options);
    }

    /**
     * 从服务器上获取数据
     * 统一的错误处理
     */
    function requestData(url,params,success){
        var _success = success,_data = params;
        if(typeof params == 'function'){
            _success = params;
            _data = {};
        }
        ajax({url:url,data:_data,async:true},function(data){
            _success(data);
        },function(xhr,textStatus, errorThrown){
            var msg = FormatUtil.stringFormat('<h1>请求出错</h1>请求 url:{0}<br/>请求参数:{1}<br/>结果{2}<br/>',url,JSON.stringify(_data),textStatus);
            //layer.alert(msg);
        });
    }

    /**
     * 美化元素,这个会查找本身及其以下的所有元素都进行美化
     * @param $obj
     */
    function icheck($obj){
        if(typeof $obj == 'string'){
            $obj = $($obj);
        }
        require(['icheck'],function(){
            if($obj.is(':radio,:checkbox')){
                $obj.iCheck({
                    checkboxClass: 'icheckbox_square-green',
                    radioClass: 'iradio_square-green'
                });
                return ;
            }
            $obj.find('input:radio,input:checkbox').iCheck({
                checkboxClass: 'icheckbox_square-green',
                radioClass: 'iradio_square-green'
            });
        });
    }

    /**
     * 只能美化当前选中的选择框
     * @param $obj
     * @param options
     */
    function chosen($obj,options){
        if(!options || !options.valueField || !options.textField){
            throw new Error('请先提供 valueField 和 textField 配置');
        }
        if(typeof $obj == 'string'){
            $obj = $($obj);
        }
        var data = options.data || [];
        if(options.ajax){
            //请求数据
            var url = options.ajax.url,
                data = options.ajax.data,
                field = options.ajax.field;
            requestData(url,data,function(d){
                if(!field){
                    data = d;
                }else{
                    data = d[field];
                }
            });
        }
        if(data && $.isArray(data)){
            for(var i=0;i<data.length;i++){
                $obj.append('<option value="'+data[i][options.valueField]+'">'+data[i][options.textField]+'</option>');
            }
        }
        require(['chosen'],function(){
            $obj.chosen({
                disable_search_threshold : 10,
                no_results_text : '没有匹配记录',
                width:'120px',
                placeholder_text_multiple:'请选择',
                placeholder_text_single:'请选择',
                search_contains:true    //模糊查找
            });
        });
    }

    /**
     * 美化日期控件
     * @param $obj
     */
    function datetimepicker($obj){
        if(typeof $obj == 'string'){
            $obj = $($obj);
        }
        require(['datetimepicker'],function(){
            require(['../plugins/locales'],function(locale){
                locale.load('datetimepicker');
                $obj.datetimepicker({
                    language:  'zh',
                    weekStart: 1,
                    todayBtn:  1,
                    autoclose: 1,
                    todayHighlight: 1,
                    startView: 2,
                    forceParse: 0,
                    showMeridian: 1
                });
            });
        });
    }


    //exports.root = getRootPath();
    //exports.StringUtil = StringUtil;
    //exports.FormatUtil = FormatUtil;
    //exports.serialize2Json = serialize2Json;
    //exports.dialog = dialog;
    //exports.icheck=icheck;

    return {
        root:getRootPath(),
        StringUtil:StringUtil,
        FormatUtil:FormatUtil,
        CollectionUtils:CollectionUtils,
        serialize2Json:serialize2Json,
        dialogHtml:dialogHtml,
        icheck:icheck,
        chosen:chosen,
        ajax:ajax,
        requestData:requestData,
        datetimepicker:datetimepicker,
        regPageEvents:regPageEvents,
        /**
         * params : Object
         */
        downFile:function(url,params){
        	var keyValueParam = '?t='+new Date().getTime();
        	if(params){
        		for(var key in params){
        			keyValueParam += ('&'+key+'='+params[key]);
        		}
        	}
        	var id = Math.round(Math.random() * 1000000);
        	$('<iframe id="filedown_iframe_'+id+'" src="'+root+url+keyValueParam+'" style="display:none"></iframe>').appendTo($('body'))
  				setTimeout(function(){
  					$('iframe#filedown_iframe_'+id).remove();
  				},1000);
        }
    }

});
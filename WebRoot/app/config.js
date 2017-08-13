/**
 * Created by sanri on 2017/4/15.
 */
require.config({
   baseUrl:'/newsanritools/js',    
   paths:{
       'layer':'../plugins/layer/layer',      /*相对于 baseUrl 路径*/
       'jquery':'jquery.1.9.1.min',
       'chosen':'../plugins/chosen/chosen.jquery.min',
       'icheck':'../plugins/icheck/icheck.min',
       'datetimepicker':'../plugins/datetimepicker/bootstrap-datetimepicker.min',
       'bootstrap':'../js/bootstrap.min',
       'scrolltabs':'../plugins/scrolltabs/scrolltabs',
       'codemirror/lib':'../plugins/codemirror',
       'contextMenu':'../plugins/contextMenu/jquery.contextMenu.min',
       'position':'../plugins/contextMenu/jquery.ui.position.min',
       'ztree':'../plugins/ztree/jquery.ztree.all',
       'zclip':'../js/jquery.zclip',
       'datatable':'../plugins/datatable/jquery.dataTables.min',
       'jsonview':'../plugins/jsonview/jquery.jsonview.min',
       'echarts':'../plugins/echarts/echarts.min',
       'steps':'../plugins/steps/jquery.steps',
       'template':'../plugins/template',
       'javabrush':'../plugins/syntaxhighlighter/brush/shBrushJava',
       'xmlbrush':'../plugins/syntaxhighlighter/brush/shBrushXml',
       'highlighter':'../plugins/syntaxhighlighter/shCore',
       'autocomplete':'../plugins/autocomplete/jquery.autocomplete.min',
       'storage':'jquery.storageapi.min',
       'migrate':'jquery.migrate'					//修复 jquery 升级后废弃的 api
   },
   shim:{
       'icheck':{
           deps:['jquery'],
           exports:'icheck'
       },
       'layer':{
           deps:['jquery'],
           exports:'layer'
       },
       'bootstrap':{
           deps:['jquery']
       },
       'javabrush':{
      	 deps:['jquery','highlighter']
       },
       'xmlbrush':{
      	 deps:['jquery','highlighter']
       },
//       'scrolltabs':{deps:['util']},
       'ztree':{deps:['jquery']},
       'zclip':{deps:['jquery']},
       'chosen':{deps:['jquery']},
       'jsonview':{deps:['jquery']},
       'steps':{deps:['jquery']},
       'contextMenu':{deps:['jquery','position']},
       'autocomplete':{deps:['migrate']},
       'migrate':{deps:['jquery']}
   },
//   packages: [{
//	     name: 'codemirror',
//	     location: '../plugins/codemirror/',
//	     main: '../codemirror'
//	 },{
//     name: "mode_sql",
//     location: "../plugins/codemirror/mode/sql",
//     main: '../sql/sql'
// }]
   
});

///**
// * 扩展 jquery 的 $.browser 以免引起插件引用低版本的 jquery 去掉的方法而出错
//*/
//require(['jquery'],function($){
//	$.fn.browser = {
//			opera:false
//	};
//})

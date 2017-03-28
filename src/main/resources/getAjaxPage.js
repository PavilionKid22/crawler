
//调用接口
system = require('system')   
address = system.args[1];//获得命令行第二个参数
var url = address;
//下载网页
var page = require('webpage').create();//创建一个page实例
page.open(url, function (status) {//打开一个页面
    //Page is loaded!   
    if (status !== 'success') {   
        console.log('Unable to post!');   
    } else {
        console.log(page.content);   
    }      
    phantom.exit();   
});
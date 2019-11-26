sed -n '3p' /var/log/yum.log     //将yum.log的第3行输出
sed -n '3,9p' /var/log/yum.log   //将3和9行的文字输出

#在文件第一行添加happy,文件结尾添加new year
-e 允许多项编辑
-i 修改文件内容
sed -e '1i happy' -e '$a new year' readme.txt    //界面上显示
sed -i -e '1i happy' -e '$a new year' readme.txt //真实写入 

sed -n '$p' yum.log        //显示最后一行的内容
sed '1,4i hahaha' yum.log  //在文件第一行和第四行的每行下面添加hahaha

sed  '3,9d' /var/log/yum.log  //删除第3到第9行,只是不显示而已

ifconfig |sed -n '2p'   //显示第2行
	inet addr:192.168.133.66  Bcast:192.168.133.255  Mask:255.255.255.0

-n 取消默认的完整输出，只要需要的
-s 用一个字符串替换另一个 	
ifconfig |sed -n '2p' | sed -r 's#.*r:(.*) B.*k:(.*)#\1 \2#g'  //(.*)表示匹配的项,之后可以用\1取出第一个括号内匹配的内容，\2取出第二个括号内匹配的内容  
	192.168.133.66  255.255.255.0

	
sed 's/\s*=\s*.*//g'
这个就是替换命令s，格式为 s/.../.../g，最后的g表示全部替换，否则只替换第一次匹配的。
只替换符合 \s*=\s*.* 这种正则匹配的字符串，\s*=\s*.* 表示 sxxx=sxxx.xxx 这种形式(x表示任意字符)。	

#打印匹配内容：ro.build.version.incremental
sed -n '/ro.build.version.incremental/p' build.prop	
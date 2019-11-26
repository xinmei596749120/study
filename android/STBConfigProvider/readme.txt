串口数据命令的查询：

查数据库的值:
插入 insert
删除 delete
更新 update
查询 query

	//查询整个数据库
	content query --uri content://settings/secure
	content query --uri content://stbconfig/authentication
	
	//修改某个值
	content update --uri content://stbconfig/authentication --bind value:s:123456 --where "name='password'"	
	//插入某个值
	content insert --uri content://stbconfig/authentication --bind name:s:aaa --bind value:s:bbb
	//删除某一个值
	content delete -- uri content://stbconfig/authentication --where "name='new_setting'"
	
	//进入数据库目录查看	
	sqlite3 <dbpath>
	.dump
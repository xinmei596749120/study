������������Ĳ�ѯ��

�����ݿ��ֵ:
���� insert
ɾ�� delete
���� update
��ѯ query

	//��ѯ�������ݿ�
	content query --uri content://settings/secure
	content query --uri content://stbconfig/authentication
	
	//�޸�ĳ��ֵ
	content update --uri content://stbconfig/authentication --bind value:s:123456 --where "name='password'"	
	//����ĳ��ֵ
	content insert --uri content://stbconfig/authentication --bind name:s:aaa --bind value:s:bbb
	//ɾ��ĳһ��ֵ
	content delete -- uri content://stbconfig/authentication --where "name='new_setting'"
	
	//�������ݿ�Ŀ¼�鿴	
	sqlite3 <dbpath>
	.dump
sed -n '3p' /var/log/yum.log     //��yum.log�ĵ�3�����
sed -n '3,9p' /var/log/yum.log   //��3��9�е��������

#���ļ���һ�����happy,�ļ���β���new year
-e �������༭
-i �޸��ļ�����
sed -e '1i happy' -e '$a new year' readme.txt    //��������ʾ
sed -i -e '1i happy' -e '$a new year' readme.txt //��ʵд�� 

sed -n '$p' yum.log        //��ʾ���һ�е�����
sed '1,4i hahaha' yum.log  //���ļ���һ�к͵����е�ÿ���������hahaha

sed  '3,9d' /var/log/yum.log  //ɾ����3����9��,ֻ�ǲ���ʾ����

ifconfig |sed -n '2p'   //��ʾ��2��
	inet addr:192.168.133.66  Bcast:192.168.133.255  Mask:255.255.255.0

-n ȡ��Ĭ�ϵ����������ֻҪ��Ҫ��
-s ��һ���ַ����滻��һ�� 	
ifconfig |sed -n '2p' | sed -r 's#.*r:(.*) B.*k:(.*)#\1 \2#g'  //(.*)��ʾƥ�����,֮�������\1ȡ����һ��������ƥ������ݣ�\2ȡ���ڶ���������ƥ�������  
	192.168.133.66  255.255.255.0

	
sed 's/\s*=\s*.*//g'
��������滻����s����ʽΪ s/.../.../g������g��ʾȫ���滻������ֻ�滻��һ��ƥ��ġ�
ֻ�滻���� \s*=\s*.* ��������ƥ����ַ�����\s*=\s*.* ��ʾ sxxx=sxxx.xxx ������ʽ(x��ʾ�����ַ�)��	

#��ӡƥ�����ݣ�ro.build.version.incremental
sed -n '/ro.build.version.incremental/p' build.prop	
## 安全检查信息管理模块数据库设计文档
##

### 目录
1.	介绍
1.	数据库详细设计
1.	附录

## 
###　介绍
安全检查信息管理模块数据库采用mysql。

## 
###　数据库详细设计

#### 检查内容表 check_content
字段			|类型		 |约束		  | 说明
------------|------------|------------|------------
report_content_id 	|int（11）		 |主键		  | 检查内容的id
report_content 	|varchar（200）		 |	无	  | 检查内容

#### 检查项表 check_item
字段			|类型		 |约束		  | 说明
------------|------------|------------|------------
report_item_id 	|int（11）	 |主键		  | 检查项id
report_item 	|varchar（200）	 |		  | 检查项
table_sequence 	|int（11）		 |	无	  | 所在检查内容中具体序号
check_content_id 	|int（11）		 |	无	  | 所属检查内容的id

#### 检查记录表 check_record
字段			|类型		 |约束		  | 说明
------------|------------|------------|------------
repord_id 	|int（11）	 |主键		  | 检查记录id
report_id 	|int（11）	 |无		  | 安全检查表id
check_item 	|varchar（200）	 |无		  | 检查项
passed 	|varchar（1）	 |无		  | 检查项是否通过，1 为通过， 0 为未通过
pic_url 	|varchar（200）	 |无		  | 检查项图片url
note 	|varchar（200）	 |无		  | 检查项备注

#### 安全检查信息表 report
字段			|类型		 |约束		  | 说明
------------|------------|------------|------------
report_id 	|int（11）	 |主键		  | 安全检查表id
report_name 	|varchar（200）	 |无		  | 安全检查表名字
report_creator 	|varchar（200）	 |无		  | 安全检查表创建者
report_create_time 	|varchar（19）	 |无		  | 安全检查表创建时间

#### 备注(个人时间原因)
1.	所有表的字段的非主键约束未标注，DDL中已注明（jinshan.sql）。
1.	关于check_item表，可以优化，具体为新增一个check_item_id字段并增加响应的约束。

## 
###　附录

#### mysql文件已转储为jinshan.sql，导入即可使用。